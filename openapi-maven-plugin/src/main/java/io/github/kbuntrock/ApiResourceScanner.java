package io.github.kbuntrock;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.CommonApiConfiguration;
import io.github.kbuntrock.configuration.library.reader.ClassLoaderHelper;
import io.github.kbuntrock.context.ApiContext;
import io.github.kbuntrock.javadoc.ClassDocumentation;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
import org.apache.maven.plugin.MojoFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * High-level scanner that discovers REST controller classes and builds a {@link TagLibrary}
 * from them according to an {@link ApiConfiguration}.
 * <p>
 * Responsibilities:
 * - Configure and run ClassGraph to find classes annotated with any of the configured tag annotations.
 * - Apply class-level whitelist/blacklist filters (regex) on discovered classes.
 * - Delegate per-class endpoint analysis to {@link JavaClassAnalyser}.
 * - Attach additional schema classes explicitly listed in configuration.
 * - Resolve stable, human-friendly schema reference names at the end of the scan.
 * <p>
 * Filtering notes:
 * - Entries in the whitelist/blacklist that contain only a class pattern are handled here.
 * - Method-level filtering (class|method) is enforced in {@link JavaClassAnalyser}.
 */
public class ApiResourceScanner {

	private final ApiContext context;

	private final ApiConfiguration apiConfiguration;
	private final OpenApiTypeResolver openApiTypeResolver;
	private final Map<String, ClassDocumentation> javadocMap;

	/** Class-level allow/deny lists. Method-level rules are enforced later in JavaClassAnalyser. */
	private final List<Pattern> whiteListPatterns = new ArrayList<>();
	private final List<Pattern> blackListPatterns = new ArrayList<>();

	public ApiResourceScanner(final ApiContext context, final Map<String, ClassDocumentation> javadocMap) {
		this.context = context;
		this.apiConfiguration = context.getApiConfiguration();
		this.openApiTypeResolver = context.getOpenApiTypeResolver();
		this.javadocMap = javadocMap;

		// Build class-level whitelist: take the left part before the separator when present.
		// Skip entries that start directly with the separator (they target only methods).
		if(apiConfiguration.getWhiteList() != null) {
			for(final String whiteEntry : apiConfiguration.getWhiteList()) {
				final String regex = whiteEntry.split(CommonApiConfiguration.SEPARATOR_CLASS_METHOD)[0];
				if(!whiteEntry.startsWith(CommonApiConfiguration.SEPARATOR_CLASS_METHOD)) {
					whiteListPatterns.add(Pattern.compile(regex));
				}
			}
		}
		// Build class-level blacklist: take entries that contain only one segment (class pattern).
		if(apiConfiguration.getBlackList() != null) {
			for(final String blackEntry : apiConfiguration.getBlackList()) {
				final String[] regexArray = blackEntry.split(CommonApiConfiguration.SEPARATOR_CLASS_METHOD);
				if(regexArray.length == 1) {
					blackListPatterns.add(Pattern.compile(regexArray[0]));
				}
			}
		}
	}

	/**
	 * Scan configured locations for REST controllers and build the {@link TagLibrary}.
	 * <p>
	 * Steps:
	 * - Scan the plugin classpath for class, method, and annotation information.
	 * - From the scan, collect classes annotated with any configured tag annotations.
	 * - Apply class-level white/black lists, then analyse each class with {@link JavaClassAnalyser}.
	 * - Register additional schema classes listed explicitly in the configuration.
	 * - Finalize by resolving schema reference names for readability.
	 *
	 * @return a populated {@link TagLibrary}
	 * @throws MojoFailureException
	 *             if scanning or analysis fails
	 */
	public TagLibrary scanRestControllers() throws MojoFailureException {

		final TagLibrary library = new TagLibrary(context, javadocMap);

		final ClassGraph classGraph = new ClassGraph()
			.enableMethodInfo()
			.enableClassInfo()
			.enableAnnotationInfo()
			.ignoreClassVisibility()
			.ignoreMethodVisibility()
			.ignoreParentClassLoaders()
			.addClassLoader(context.getClassLoader());

		try(ScanResult classScanResult = classGraph.scan()) {
			library.setCurrentScanResult(classScanResult);
			for(final String apiLocation : apiConfiguration.getLocations()) {
				context.getLogger().info("Scanning : " + apiLocation);
				// Collect classes that bear any of the configured tag annotations (e.g., Spring controllers).
				String[] annotationNames = apiConfiguration.getTagAnnotations().toArray(new String[0]);
				Set<Class<?>> restControllerClasses = classScanResult
					.getClassesWithAnyAnnotation(annotationNames)
					.stream()
					.filter(onLocation(apiLocation, context.getClassLoaderHelper()))
					.map(ClassInfo::loadClass)
					.collect(Collectors.toSet());

				context.getLogger().info("Found " + restControllerClasses.size() + " annotated classes with [ " +
					String.join(", ", apiConfiguration.getTagAnnotations()) + " ]");

				// Analyse each controller class and populate the TagLibrary.
				final JavaClassAnalyser javaClassAnalyser = new JavaClassAnalyser(context, apiConfiguration, classScanResult,
					openApiTypeResolver);
				for(final Class<?> restControllerClass : restControllerClasses) {
					if(validateWhiteList(restControllerClass) && validateBlackList(restControllerClass)) {
						javaClassAnalyser.getTagFromClass(restControllerClass).ifPresent(library::addTag);
					}
				}

				// Look for extra configuration classes that might define security schemes
				Set<Class<?>> securityClasses = classScanResult
					.getClassesWithAnyAnnotation("io.swagger.v3.oas.annotations.security.SecurityScheme",
						"io.swagger.v3.oas.annotations.security.SecuritySchemes")
					.stream()
					.filter(onLocation(apiLocation, context.getClassLoaderHelper()))
					.map(ClassInfo::loadClass)
					.collect(Collectors.toSet());

				for(final Class<?> securityClass : securityClasses) {
					// We only process if it hasn't already been processed as a rest controller and if it passes white/black list
					if(!restControllerClasses.contains(securityClass) && validateWhiteList(securityClass)
						&& validateBlackList(securityClass)) {
						javaClassAnalyser.readSecuritySchemesFromClass(securityClass, library);
					}
				}

				// Add extra data objects to the schema section (objects not explicitly referenced by endpoints).
				for(final String className : apiConfiguration.getExtraSchemaClasses()) {
					try {
						library.addExtraClass(context.getClassLoader().loadClass(className));
					} catch(final ClassNotFoundException e) {
						throw new MojoRuntimeException("Cannot load extra class " + className, e);
					}
				}
			}
		} finally {
			library.setCurrentScanResult(null);
		}

		// Assign short, stable names to schema references after all scans are completed.
		library.resolveSchemaReferenceNames();

		return library;
	}

	/**
	 * Build a predicate that constrains results to the configured location, which may
	 * be a fully qualified class name or a package prefix.
	 */
	private static Predicate<ClassInfo> onLocation(final String apiLocation, final ClassLoaderHelper classLoaderHelper) {
		if(classLoaderHelper.isClass(apiLocation)) {
			return classInfo -> classInfo.getName().equals(apiLocation);
		}
		return classInfo -> classInfo.getPackageName().equals(apiLocation)
			|| classInfo.getPackageName().startsWith(apiLocation + ".");
	}

	/**
	 * Class-level whitelist: when empty, allow all classes. Otherwise, only allow matches.
	 */
	private boolean validateWhiteList(final Class<?> restControllerClass) {
		if(whiteListPatterns.isEmpty()) {
			return true;
		}
		return whiteListPatterns.stream()
			.anyMatch(whitePattern -> whitePattern.matcher(restControllerClass.getCanonicalName()).matches());
	}

	/**
	 * Class-level blacklist: when empty, allow all classes. Otherwise, exclude matches.
	 */
	private boolean validateBlackList(final Class<?> restControllerClass) {
		if(blackListPatterns.isEmpty()) {
			return true;
		}
		return blackListPatterns.stream()
			.noneMatch(blackPattern -> blackPattern.matcher(restControllerClass.getCanonicalName()).matches());
	}
}
