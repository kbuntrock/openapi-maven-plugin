package io.github.kbuntrock;


import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.CommonApiConfiguration;
import io.github.kbuntrock.configuration.EnumConfigHolder;
import io.github.kbuntrock.configuration.JavadocConfiguration;
import io.github.kbuntrock.configuration.NullableConfigurationHolder;
import io.github.kbuntrock.javadoc.JavadocMap;
import io.github.kbuntrock.javadoc.JavadocParser;
import io.github.kbuntrock.javadoc.JavadocWrapper;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.reflection.AdditionnalSchemaLibrary;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.utils.FileUtils;
import io.github.kbuntrock.utils.Logger;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
import io.github.kbuntrock.yaml.YamlWriter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "documentation", defaultPhase = LifecyclePhase.COMPILE,
	requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class DocumentationMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	MavenProject project;
	/**
	 * A common api configuration between all described apis
	 */
	@Parameter
	private CommonApiConfiguration apiConfiguration = new CommonApiConfiguration();
	/**
	 * A list of api configurations
	 */
	@Parameter(required = true)
	private List<ApiConfiguration> apis;
	/**
	 * A list of api configurations
	 */
	@Parameter
	private JavadocConfiguration javadocConfiguration;
	/**
	 * Location of the file.
	 */
	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;
	@Component
	private MavenProjectHelper projectHelper;

	private ClassLoader projectClassLoader;

	private boolean testMode = false;

	/**
	 * Execution of the documentation mojo
	 *
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		try {
			final long debut = System.currentTimeMillis();

			Logger.INSTANCE.setLogger(getLog());

			// Prepare the class loader
			projectClassLoader = createProjectDependenciesClassLoader();
			ReflectionsUtils.initiate(projectClassLoader);

			// Validate the configuration, parse the javadoc, parse the compiled code, and write the documentation.
			// This is the method to call in unit tests
			documentProject();

			getLog().info("Openapi spec generation took " + (System.currentTimeMillis() - debut) + "ms.");

		} catch(final MojoRuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex.getCause());
		}


	}

	public List<File> documentProject() throws MojoFailureException, MojoExecutionException {

		// Log the java version
		final String version = System.getProperty("java.version");
		getLog().debug("Running on java " + version);

		validateConfiguration();
		scanJavadoc();
		return scanProjectResourcesAndWriteSpec();
	}

	private void validateConfiguration() throws MojoFailureException {
		if(apis == null || apis.isEmpty()) {
			throw new MojoFailureException("At least one api configuration element should be configured");
		}
		this.getApiConfiguration().initDefaultValues();
		EnumConfigHolder.storeConfig(this.getApiConfiguration().getEnumConfigList());

		if (apis.stream().map(ApiConfiguration::getLocations).anyMatch(locations -> locations == null || locations.isEmpty())) {
			throw new MojoFailureException("At least one location element should be configured");
		}
	}

	/**
	 * Scan the project compiled resources and write all the documentations files
	 *
	 * @return the generated files
	 * @throws MojoFailureException
	 * @throws MojoExecutionException
	 */
	private List<File> scanProjectResourcesAndWriteSpec() throws MojoFailureException {

		final List<File> generatedFiles = new ArrayList<>();
		for(final ApiConfiguration initialApiConfiguration : apis) {
			AdditionnalSchemaLibrary.reset();
			final ApiConfiguration apiConfig = initialApiConfiguration.mergeWithCommonApiConfiguration(this.apiConfiguration);
			initObjectMapperFactory(apiConfig);
			NullableConfigurationHolder.storeConfig(apiConfig);

			final ApiResourceScanner apiResourceScanner = new ApiResourceScanner(apiConfig);
			getLog().debug("Prepare to scan");
			final TagLibrary tagLibrary = apiResourceScanner.scanRestControllers();
			getLog().debug("Scan done");

			File generatedFile = null;
			try {
				if(testMode) {
					generatedFile = Files.createTempFile(
						apiConfig.getFilename().substring(0, apiConfig.getFilename().length() - ".yml".length()) + "_", ".yml").toFile();
				} else {
					generatedFile = new File(outputDirectory, apiConfig.getFilename());
				}
				getLog().debug("Prepared to write : " + generatedFile.getAbsolutePath());

				new YamlWriter(project, apiConfig).write(generatedFile, tagLibrary);

				if(apiConfig.isAttachArtifact()) {
					final String fileExtension = com.google.common.io.Files.getFileExtension(apiConfig.getFilename());
					final int extensionSize = fileExtension.isEmpty() ? 0 : fileExtension.length() + 1;
					final String fileNameWithoutExtension = apiConfig.getFilename()
						.substring(0, apiConfig.getFilename().length() - extensionSize);
					projectHelper.attachArtifact(project, fileExtension, fileNameWithoutExtension, generatedFile);
				}

				generatedFiles.add(generatedFile);

				final int nbTagsGenerated = tagLibrary.getTags().size();

				if(nbTagsGenerated == 0) {
					throw new MojoFailureException(
						"There is nothing to document. Please check if you have correctly configured the plugin or if the "
							+ "java version used by maven is high enough to read the compiled project classes (maven toolchain is not supported yet)");
				}

				final int nbOperationsGenerated = tagLibrary.getTags().stream().map(Tag::getEndpoints).map(Collection::size).mapToInt(Integer::intValue).sum();
				getLog().info(
					apiConfig.getFilename() + " : " + nbTagsGenerated + " tags and " + nbOperationsGenerated + " operations generated.");
			} catch(final IOException e) {
				throw new MojoFailureException("Cannot write file specification file : " + (generatedFile == null ? "temporary test file"
					: generatedFile.getAbsolutePath()), e);
			}
		}
		return generatedFiles;
	}

	private void initObjectMapperFactory(final ApiConfiguration apiConfig) {
		OpenApiTypeResolver.INSTANCE.init(project, apiConfig);
	}

	/**
	 * Create a classloader for the classes and dependencies of the project
	 *
	 * For more information, see https://maven.apache.org/guides/mini/guide-maven-classloading.html
	 *
	 * @return the classloader to use
	 * @throws MojoExecutionException
	 */
	private ClassLoader createProjectDependenciesClassLoader() throws MojoExecutionException {
		try {
			final List<URL> pathUrls = new ArrayList<>();
			for(final String compileClasspathElements : project.getCompileClasspathElements()) {
				pathUrls.add(new File(compileClasspathElements).toURI().toURL());
			}
			for(final String runtimeClasspathElement : project.getRuntimeClasspathElements()) {
				pathUrls.add(new File(runtimeClasspathElement).toURI().toURL());
			}

			final URL[] urlsForClassLoader = pathUrls.toArray(new URL[pathUrls.size()]);
			getLog().debug("urls for URLClassLoader: " + Arrays.asList(urlsForClassLoader));

			// We could use a completely separated Classword but is had too much complexity while scanning the project classes since
			// we can't use Class loaded in the pluging Classloader. We should then use classes of the project classLoader and handle cases
			// when there are not present
			// We prefer add projet url to the plugin classLoader.
			final ClassRealm classRealm = (ClassRealm) DocumentationMojo.class.getClassLoader();
			for(final URL url : urlsForClassLoader) {
				classRealm.addURL(url);
			}
			return classRealm;
		} catch(final DependencyResolutionRequiredException | MalformedURLException ex) {
			throw new MojoExecutionException("Cannot create project dependencies classloader", ex);
		}

	}

	private void scanJavadoc() {
		if (javadocConfiguration == null
			|| javadocConfiguration.getScanLocations() == null
			|| javadocConfiguration.getScanLocations().isEmpty()
		) {
			getLog().info("No javadoc configuration found: scan of javadoc skipped.");
			return;
		}

		final long debutJavadoc = System.currentTimeMillis();

		final List<File> filesToScan = javadocConfiguration.getScanLocations().stream()
			.map(path -> FileUtils.toFile(project.getBasedir().getAbsolutePath(), path))
			.collect(Collectors.toList());
		final JavadocParser javadocParser = new JavadocParser(filesToScan, javadocConfiguration);
		javadocParser.scan();
		JavadocMap.INSTANCE.setJavadocMap(javadocParser.getJavadocMap());
		if(!JavadocConfiguration.DISABLED_EOF_REPLACEMENT.equals(javadocConfiguration.getEndOfLineReplacement())) {
			JavadocWrapper.setEndOfLineReplacement(javadocConfiguration.getEndOfLineReplacement());
		}
		getLog().info("Javadoc parsing took " + (System.currentTimeMillis() - debutJavadoc) + "ms.");
	}

	public List<ApiConfiguration> getApis() {
		return apis;
	}

	public void setApis(final List<ApiConfiguration> apis) {
		this.apis = apis;
	}

	public JavadocConfiguration getJavadocConfiguration() {
		return javadocConfiguration;
	}

	public void setJavadocConfiguration(final JavadocConfiguration javadocConfiguration) {
		this.javadocConfiguration = javadocConfiguration;
	}

	public CommonApiConfiguration getApiConfiguration() {
		return apiConfiguration;
	}

	public void setApiConfiguration(final CommonApiConfiguration apiConfiguration) {
		this.apiConfiguration = apiConfiguration;
	}

	public void setProject(final MavenProject project) {
		this.project = project;
	}

	public void setTestMode(final boolean testMode) {
		this.testMode = testMode;
	}
}
