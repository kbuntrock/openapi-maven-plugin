package io.github.kbuntrock;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.CommonApiConfiguration;
import io.github.kbuntrock.configuration.JavadocConfiguration;
import io.github.kbuntrock.configuration.NullableConfiguration;
import io.github.kbuntrock.context.ApiContext;
import io.github.kbuntrock.context.ProjectContext;
import io.github.kbuntrock.javadoc.ClassDocumentation;
import io.github.kbuntrock.javadoc.JavadocParser;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.reflection.AdditionnalSchemaLibrary;
import io.github.kbuntrock.utils.CollectionUtils;
import io.github.kbuntrock.utils.FileUtils;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
import io.github.kbuntrock.yaml.YamlWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.rtinfo.RuntimeInformation;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Maven Mojo that generates OpenAPI specification files for one or more APIs of the current project.
 * <p>
 * It scans compiled classes using the project classpath, discovers REST resources according to the configured
 * {@link ApiConfiguration}s, optionally enriches the output with parsed Javadoc, and writes one OpenAPI document
 * per configured API into {@code ${project.build.directory}} (or a temporary file in test mode).
 * </p>
 * <p>
 * Goal name: {@code documentation}<br/>
 * Default phase: {@code compile}<br/>
 * Requires dependency resolution: {@code compile+runtime}<br/>
 * Thread-safe: {@code true}
 * </p>
 * <p>
 * Configuration can be defined in the POM or passed via system properties (for example
 * {@code -Dopenapi.locations=...}). When properties are used, an implicit {@link ApiConfiguration} is created
 * from these values.
 * </p>
 */
@Mojo(name = "documentation", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class DocumentationMojo extends AbstractMojo {

	/**
	 * The current Maven project.
	 */
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	MavenProject project;
	/**
	 * Common configuration applied to all declared APIs.
	 */
	@Parameter
	private CommonApiConfiguration apiConfiguration = new CommonApiConfiguration();
	/**
	 * The list of API configurations to document. One OpenAPI file is produced per entry.
	 */
	@Parameter
	private List<ApiConfiguration> apis;
	/**
	 * Javadoc parsing configuration. If enabled, parsed Javadoc enriches the generated specification.
	 */
	@Parameter
	private JavadocConfiguration javadocConfiguration;
	/**
	 * Output directory for generated OpenAPI files. Defaults to {@code ${project.build.directory}}.
	 */
	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;
	/**
	 * Whether to send anonymous analytics for the first API configuration execution.
	 */
	@Parameter(defaultValue = "true", property = "openapi.analytics")
	protected boolean analytics;

	/**
	 * COMMAND LINES / SETTINGS / POM PROPERTIES
	 */
	@Parameter(property = "openapi.locations")
	private List<String> locations;

	@Parameter(property = "openapi.filename")
	private String filename;

	@Parameter(property = "openapi.tagAnnotations")
	private List<String> tagAnnotations;

	@Parameter(property = "openapi.library")
	protected String library;

	@Parameter(property = "openapi.javadoc.locations")
	protected List<String> javadocScanLocation;

	@Parameter(property = "openapi.javadoc.scanEnabled", defaultValue = "true")
	protected Boolean javadocScanEnabled = true;

	@Inject
	private MavenProjectHelper projectHelper;

	@Parameter(defaultValue = "${plugin}", readonly = true, required = true)
	private PluginDescriptor pluginDescriptor;

	@Inject
	private RuntimeInformation runtimeInformation;

	private boolean testMode = false;

	private final ProjectContext context = new ProjectContext();

	/**
	 * Executes the mojo: prepares logging and class loading, validates and parses configuration/Javadoc,
	 * scans the compiled project, and writes the resulting OpenAPI documents.
	 *
	 * @throws MojoExecutionException
	 *             if an unrecoverable error occurs during execution
	 * @throws MojoFailureException
	 *             if the configuration is invalid or no content can be generated
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		try {
			final long debut = System.currentTimeMillis();

			context.initLogger(getLog());
			// Prepare the class loader
			ClassLoader projectClassLoader = createProjectDependenciesClassLoader();
			context.initClassLoader(projectClassLoader);

			// Validate the configuration, parse the javadoc, parse the compiled code, and write the documentation.
			// This is the method to call in unit tests
			documentProject();

			context.getLogger().info("Openapi spec generation took " + (System.currentTimeMillis() - debut) + "ms.");

		} catch(final MojoRuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex.getCause());
		}
	}

	/**
	 * Runs the documentation workflow and returns the list of generated files.
	 * <p>
	 * Extracted to facilitate unit testing.
	 * </p>
	 *
	 * @return the list of generated OpenAPI files
	 * @throws MojoFailureException
	 *             when configuration validation fails or nothing can be documented
	 * @throws MojoExecutionException
	 *             on unexpected execution errors
	 */
	public List<File> documentProject() throws MojoFailureException, MojoExecutionException {
		context.setProject(project);

		// Log the java version
		final String version = System.getProperty("java.version");
		context.getLogger().debug("Running on java " + version);

		validateConfiguration();
		Map<String, ClassDocumentation> javadocMap = scanJavadoc();
		return scanProjectResourcesAndWriteSpec(javadocMap);
	}

	/**
	 * Validates plugin configuration:
	 * <ul>
	 * <li>Builds an {@link ApiConfiguration} from system properties when applicable</li>
	 * <li>Ensures at least one API is configured and initializes default values</li>
	 * <li>Verifies locations are provided for each API</li>
	 * <li>Checks that each API has a distinct output filename</li>
	 * </ul>
	 *
	 * @throws MojoFailureException
	 *             if any validation rule fails
	 */
	private void validateConfiguration() throws MojoFailureException {
		createPropertyApiConfiguration();
		if(apis == null || apis.isEmpty()) {
			throw new MojoFailureException("At least one api configuration element should be configured");
		}
		this.getApiConfiguration().initDefaultValues();

		if(apis.stream().map(ApiConfiguration::getLocations).anyMatch(locations -> locations == null || locations.isEmpty())) {
			throw new MojoFailureException("At least one location element should be configured");
		}
		if(apis.stream().map(ApiConfiguration::getFilename).collect(Collectors.toSet()).size() != apis.size()) {
			throw new MojoFailureException(
				"At least two openapi documentations have a colliding filename. Please set different ones if you wish to generate multiple documentations.");
		}
	}

	/**
	 * Builds an implicit {@link ApiConfiguration} from system properties when they are present.
	 * <p>
	 * If {@code openapi.locations} is defined (via command line, {@code settings.xml}, or POM properties),
	 * an API configuration is synthesized using the provided properties: {@code openapi.locations},
	 * {@code openapi.library}, {@code openapi.filename}, and {@code openapi.tagAnnotations}. The synthesized
	 * configuration is appended to the list of APIs to generate.
	 * </p>
	 */
	private void createPropertyApiConfiguration() {
		if(locations != null && !locations.isEmpty()) {
			if(this.apis == null) {
				this.apis = new ArrayList<>();
			}
			ApiConfiguration apiConf = new ApiConfiguration();
			apiConf.setLocations(locations);
			if(StringUtils.isNotEmpty(library)) {
				apiConf.setLibrary(library);
			}
			if(StringUtils.isNotEmpty(filename)) {
				apiConf.setFilename(filename);
			}
			if(!CollectionUtils.isEmpty(tagAnnotations)) {
				apiConf.setTagAnnotations(tagAnnotations);
			}
			this.apis.add(apiConf);
		}
	}

	/**
	 * Scans compiled project resources and writes all OpenAPI documents.
	 *
	 * @param javadocMap
	 *            optional map of parsed Javadoc keyed by fully qualified class name
	 * @return the generated files
	 * @throws MojoFailureException
	 *             when nothing is found to document or write errors occur
	 * @throws MojoExecutionException
	 *             on unexpected execution errors
	 */
	private List<File> scanProjectResourcesAndWriteSpec(Map<String, ClassDocumentation> javadocMap) throws MojoFailureException {

		final List<File> generatedFiles = new ArrayList<>();

		for(int i = 0; i < apis.size(); i++) {
			final ApiConfiguration initialApiConfiguration = apis.get(i);
			ApiContext apiContext = new ApiContext(context, new AdditionnalSchemaLibrary());
			final ApiConfiguration apiConfig = initialApiConfiguration.mergeWithCommonApiConfiguration(this.apiConfiguration);
			apiContext.setApiConfiguration(apiConfig);
			apiContext.setOpenApiTypeResolver(new OpenApiTypeResolver(apiContext));
			apiContext.setNullableConfiguration(new NullableConfiguration(apiConfig));

			if(i == 0) {
				// Send analytics once: only for the first API configuration
				Analytics.build(analytics, apiConfig, project, pluginDescriptor, runtimeInformation, testMode).send();
			}

			final ApiResourceScanner apiResourceScanner = new ApiResourceScanner(apiContext, javadocMap);
			context.getLogger().debug("Prepare to scan");
			final TagLibrary tagLibrary = apiResourceScanner.scanRestControllers();
			context.getLogger().debug("Scan done");

			File generatedFile = null;
			try {
				if(testMode) {
					// In test mode, write to a temporary file to avoid touching the project's target directory
					generatedFile = Files.createTempFile(
						apiConfig.getFilename().substring(0, apiConfig.getFilename().length() - ".yml".length()) + "_", ".yml")
						.toFile();
				} else {
					outputDirectory.mkdirs();
					generatedFile = new File(outputDirectory, apiConfig.getFilename());
				}
				context.getLogger().debug("Prepared to write : " + generatedFile.getAbsolutePath());

				new YamlWriter(apiContext, apiConfig, tagLibrary).write(generatedFile, tagLibrary);

				if(apiConfig.isAttachArtifact()) {
					// Compute extension and classifier-less name to attach the generated file as a Maven artifact
					final String fileExtension = com.google.common.io.Files.getFileExtension(apiConfig.getFilename());
					final int extensionSize = fileExtension.isEmpty() ? 0 : fileExtension.length() + 1;
					final String fileNameWithoutExtension = apiConfig.getFilename()
						.substring(0, apiConfig.getFilename().length() - extensionSize);
					projectHelper.attachArtifact(project, fileExtension, fileNameWithoutExtension, generatedFile);
				}

				generatedFiles.add(generatedFile);

				final int nbTagsGenerated = tagLibrary.getTags().size();

				if(nbTagsGenerated == 0) {
					// Fail fast to signal misconfiguration or incompatible Java version used during Maven build
					throw new MojoFailureException(
						"There is nothing to document. Please check if you have correctly configured the plugin or if the "
							+ "java version used by maven is high enough to read the compiled project classes (maven toolchain is not supported)");
				}

				final int nbOperationsGenerated = tagLibrary.getTags().stream().map(Tag::getEndpoints).map(Collection::size)
					.mapToInt(Integer::intValue).sum();
				context.getLogger().info(
					apiConfig.getFilename() + " : " + nbTagsGenerated + " tags and " + nbOperationsGenerated
						+ " operations generated.");
			} catch(final IOException e) {
				throw new MojoFailureException(
					"Cannot write file specification file : " + (generatedFile == null ? "temporary test file"
						: generatedFile.getAbsolutePath()),
					e);
			}
		}
		return generatedFiles;
	}

	/**
	 * Creates a classloader for the classes and dependencies of the project.
	 * <p>
	 * For more information, see https://maven.apache.org/guides/mini/guide-maven-classloading.html<br/>
	 * The plugin's {@link ClassRealm} is reused and augmented with the project's compile and runtime classpath
	 * elements so that project classes can be loaded while scanning.
	 * </p>
	 *
	 * @return the classloader to use
	 * @throws MojoExecutionException
	 *             if classpath elements cannot be resolved or URLs cannot be created
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
			context.getLogger().debug("urls for URLClassLoader: " + Arrays.asList(urlsForClassLoader));

			// Using a separate ClassWorld/ClassRealm for the project would complicate scanning because
			// plugin classes could not load project types directly. Instead, we augment the plugin ClassRealm
			// with the project's classpath URLs so both sets of classes are visible during scanning.
			final ClassRealm classRealm = (ClassRealm) DocumentationMojo.class.getClassLoader();
			for(final URL url : urlsForClassLoader) {
				classRealm.addURL(url);
			}
			return classRealm;
		} catch(final DependencyResolutionRequiredException | MalformedURLException ex) {
			throw new MojoExecutionException("Cannot create project dependencies classloader", ex);
		}

	}

	/**
	 * Determines whether Javadoc should be scanned based on the current configuration.
	 *
	 * @return true if Javadoc scan is enabled and locations are configured, false otherwise
	 */
	private boolean shouldScanJavadoc() {
		return javadocConfiguration != null
			&& !CollectionUtils.isEmpty(javadocConfiguration.getScanLocations());
	}

	/**
	 * Parses Javadoc if enabled and configured.
	 * <p>
	 * When the plugin is configured via properties only, a default scan location of {@code src/main/java}
	 * is applied unless {@code openapi.javadoc.locations} is provided. If scanning is disabled or no
	 * configuration is found, this method returns {@code null}.
	 * </p>
	 *
	 * @return a map of class FQCN to {@link ClassDocumentation}, or {@code null} if scanning is skipped
	 */
	private Map<String, ClassDocumentation> scanJavadoc() {

		if(!javadocScanEnabled) {
			context.getLogger().info("Javadoc scan is disabled.");
			return null;
		}

		if(!CollectionUtils.isEmpty(locations) && !shouldScanJavadoc()) {
			// When the plugin is configured via properties only, provide a sensible default scan location
			if(javadocConfiguration == null) {
				javadocConfiguration = new JavadocConfiguration();
			}
			if(CollectionUtils.isEmpty(javadocScanLocation)) {
				javadocConfiguration.setScanLocations(Collections.singletonList("src/main/java"));
			} else {
				// Respect explicitly provided scan locations from properties
				javadocConfiguration.setScanLocations(javadocScanLocation);
			}

		}

		if(!shouldScanJavadoc()) {
			context.getLogger().info("No javadoc configuration found: scan of javadoc skipped.");
			return null;
		}

		final long debutJavadoc = System.currentTimeMillis();

		final List<File> filesToScan = javadocConfiguration.getScanLocations().stream()
			.map(path -> FileUtils.toFile(project.getBasedir().getAbsolutePath(), path))
			.collect(Collectors.toList());
		final JavadocParser javadocParser = new JavadocParser(context, filesToScan, javadocConfiguration);
		javadocParser.scan();
		context.getLogger().info("Javadoc parsing took " + (System.currentTimeMillis() - debutJavadoc) + "ms.");

		return javadocParser.getJavadocMap();
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

	public ProjectContext getContext() {
		return context;
	}
}
