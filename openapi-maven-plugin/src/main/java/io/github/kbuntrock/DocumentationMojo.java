package io.github.kbuntrock;


import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.CommonApiConfiguration;
import io.github.kbuntrock.configuration.JavadocConfiguration;
import io.github.kbuntrock.javadoc.JavadocMap;
import io.github.kbuntrock.javadoc.JavadocParser;
import io.github.kbuntrock.javadoc.JavadocWrapper;
import io.github.kbuntrock.reflection.AdditionnalSchemaLibrary;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.utils.FileUtils;
import io.github.kbuntrock.utils.Logger;
import io.github.kbuntrock.yaml.YamlWriter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
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
			long debut = System.currentTimeMillis();

			Logger.INSTANCE.setLogger(getLog());

			// Prepare the class loader
			projectClassLoader = createProjectDependenciesClassLoader();
			ReflectionsUtils.initiate(projectClassLoader);

			// Validate the configuration, parse the javadoc, parse the compiled code, and write the documentation.
			// This is the method to call in unit tests
			documentProject();

			getLog().info("Openapi spec generation took " + (System.currentTimeMillis() - debut) + "ms.");

		} catch(MojoRuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex.getCause());
		}


	}

	public List<File> documentProject() throws MojoFailureException, MojoExecutionException {

		// Log the java version
		String version = System.getProperty("java.version");
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

		for(ApiConfiguration apiConfiguration : apis) {
			if(apiConfiguration.getLocations() == null || apiConfiguration.getLocations().isEmpty()) {
				throw new MojoFailureException("At least one location element should be configured");
			}
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

		List<File> generatedFiles = new ArrayList<>();
		for(ApiConfiguration initialApiConfiguration : apis) {
			AdditionnalSchemaLibrary.reset();
			ApiConfiguration apiConfig = initialApiConfiguration.mergeWithCommonApiConfiguration(this.apiConfiguration);
			ApiResourceScanner apiResourceScanner = new ApiResourceScanner(apiConfig);
			getLog().debug("Prepare to scan");
			TagLibrary tagLibrary = apiResourceScanner.scanRestControllers();
			getLog().debug("Scan done");

			File generatedFile = null;
			try {
				if(testMode) {
					generatedFile = Files.createTempFile(apiConfig.getFilename() + "_", ".yml").toFile();
				} else {
					generatedFile = new File(outputDirectory, apiConfig.getFilename() + ".yml");
				}
				getLog().debug("Prepared to write : " + generatedFile.getAbsolutePath());

				new YamlWriter(project, apiConfig).write(generatedFile, tagLibrary);

				if(apiConfig.isAttachArtifact()) {
					projectHelper.attachArtifact(project, "yml", apiConfig.getFilename(), generatedFile);
				}

				generatedFiles.add(generatedFile);

				int nbTagsGenerated = tagLibrary.getTags().size();
				int nbOperationsGenerated = tagLibrary.getTags().stream().map(t -> t.getEndpoints().size())
					.collect(Collectors.summingInt(Integer::intValue));
				getLog().info(
					apiConfig.getFilename() + " : " + nbTagsGenerated + " tags and " + nbOperationsGenerated + " operations generated.");
			} catch(IOException e) {
				throw new MojoFailureException("Cannot write file specification file : " + (generatedFile == null ? "temporary test file"
					: generatedFile.getAbsolutePath()));
			}
		}
		return generatedFiles;
	}

	/**
	 * Create a classloader for the classes and dependencies of the project
	 *
	 * @return the classloader to use
	 * @throws MojoExecutionException
	 */
	private ClassLoader createProjectDependenciesClassLoader() throws MojoExecutionException {
		try {
			List<URL> pathUrls = new ArrayList<>();
			for(String compileClasspathElements : project.getCompileClasspathElements()) {
				pathUrls.add(new File(compileClasspathElements).toURI().toURL());
			}
			for(String runtimeClasspathElement : project.getRuntimeClasspathElements()) {
				pathUrls.add(new File(runtimeClasspathElement).toURI().toURL());
			}

			URL[] urlsForClassLoader = pathUrls.toArray(new URL[pathUrls.size()]);
			getLog().debug("urls for URLClassLoader: " + Arrays.asList(urlsForClassLoader));

			// We need to define parent classloader which is the plugin classloader, in order to not mix up
			// the project and the plugin classes.
			return new URLClassLoader(urlsForClassLoader, DocumentationMojo.class.getClassLoader());
		} catch(DependencyResolutionRequiredException | MalformedURLException ex) {
			throw new MojoExecutionException("Cannot create project dependencies classloader", ex);
		}

	}

	private void scanJavadoc() {
		if(javadocConfiguration != null && javadocConfiguration.getScanLocations() != null && !javadocConfiguration.getScanLocations()
			.isEmpty()) {
			long debutJavadoc = System.currentTimeMillis();
			List<File> filesToScan = new ArrayList<>();
			for(String path : javadocConfiguration.getScanLocations()) {
				filesToScan.add(FileUtils.toFile(project.getBasedir().getAbsolutePath(), path));
			}
			JavadocParser javadocParser = new JavadocParser(filesToScan);
			javadocParser.scan();
			JavadocMap.INSTANCE.setJavadocMap(javadocParser.getJavadocMap());
			if(!JavadocConfiguration.DISABLED_EOF_REPLACEMENT.equals(javadocConfiguration.getEndOfLineReplacement())) {
				JavadocWrapper.setEndOfLineReplacement(javadocConfiguration.getEndOfLineReplacement());
			}
			getLog().info("Javadoc parsing took " + (System.currentTimeMillis() - debutJavadoc) + "ms.");
		}
	}

	public List<ApiConfiguration> getApis() {
		return apis;
	}

	public void setApis(List<ApiConfiguration> apis) {
		this.apis = apis;
	}

	public JavadocConfiguration getJavadocConfiguration() {
		return javadocConfiguration;
	}

	public void setJavadocConfiguration(JavadocConfiguration javadocConfiguration) {
		this.javadocConfiguration = javadocConfiguration;
	}

	public CommonApiConfiguration getApiConfiguration() {
		return apiConfiguration;
	}

	public void setApiConfiguration(CommonApiConfiguration apiConfiguration) {
		this.apiConfiguration = apiConfiguration;
	}

	public void setProject(MavenProject project) {
		this.project = project;
	}

	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}
}
