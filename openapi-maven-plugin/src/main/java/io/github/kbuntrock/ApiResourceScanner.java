package io.github.kbuntrock;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.CommonApiConfiguration;
import io.github.kbuntrock.configuration.library.reader.ClassLoaderUtils;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.utils.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

/**
 * In charge of creating the tag library object based on an api configuration object.
 */
public class ApiResourceScanner {

	private final Log logger = Logger.INSTANCE.getLogger();

	private final ApiConfiguration apiConfiguration;

	private final List<Pattern> whiteListPatterns = new ArrayList<>();
	private final List<Pattern> blackListPatterns = new ArrayList<>();

	public ApiResourceScanner(final ApiConfiguration apiConfiguration) {
		this.apiConfiguration = apiConfiguration;

		if(apiConfiguration.getWhiteList() != null) {
			for(final String whiteEntry : apiConfiguration.getWhiteList()) {
				final String regex = whiteEntry.split(CommonApiConfiguration.SEPARATOR_CLASS_METHOD)[0];
				if(!whiteEntry.startsWith(CommonApiConfiguration.SEPARATOR_CLASS_METHOD)) {
					whiteListPatterns.add(Pattern.compile(regex));
				}
			}
		}
		if(apiConfiguration.getBlackList() != null) {
			for(final String blackEntry : apiConfiguration.getBlackList()) {
				final String[] regexArray = blackEntry.split(CommonApiConfiguration.SEPARATOR_CLASS_METHOD);
				if(regexArray.length == 1) {
					blackListPatterns.add(Pattern.compile(regexArray[0]));
				}
			}
		}
	}

	public TagLibrary scanRestControllers() throws MojoFailureException {

		final TagLibrary library = new TagLibrary();
		TagLibraryHolder.INSTANCE.setTagLibrary(library);

		for(final String apiLocation : apiConfiguration.getLocations()) {
			logger.info("Scanning : " + apiLocation);

			ClassGraph classGraph = new ClassGraph()
				.enableMethodInfo()
				.enableClassInfo()
				.enableAnnotationInfo()
				.ignoreClassVisibility()
				.ignoreMethodVisibility()
				.ignoreParentClassLoaders()
				.addClassLoader(ReflectionsUtils.getProjectClassLoader());
			if(ClassLoaderUtils.isClass(apiLocation)) {
				classGraph.acceptClasses(apiLocation);
			} else {
				classGraph.acceptPackages(apiLocation);
			}

			try(ScanResult classScanResult = classGraph.scan()) {
				String[] annotationNames = apiConfiguration.getTagAnnotations().toArray(new String[0]);
				Set<Class<?>> restControllerClasses = classScanResult
					.getClassesWithAnyAnnotation(annotationNames)
					.stream()
					.filter(onLocation(apiLocation))
					.map(ClassInfo::loadClass)
					.collect(Collectors.toSet());

				logger.info("Found " + restControllerClasses.size() + " annotated classes with [ " +
					String.join(", ", apiConfiguration.getTagAnnotations()) + " ]");

				// Find directly or inheritedly annotated by RequestMapping classes.
			final JavaClassAnalyser javaClassAnalyser = new JavaClassAnalyser(apiConfiguration);
				for(final Class<?> restControllerClass : restControllerClasses) {
					if(validateWhiteList(restControllerClass) && validateBlackList(restControllerClass)) {
						javaClassAnalyser.getTagFromClass(restControllerClass).ifPresent(library::addTag);
					}
				}

				// Possibly add extra data objets to the future schema section (objets which are not explicitly used by an endpoint)
				for(final String className : apiConfiguration.getExtraSchemaClasses()) {
					try {
						library.addExtraClass(ReflectionsUtils.getProjectClassLoader().loadClass(className));
					} catch(final ClassNotFoundException e) {
						throw new MojoRuntimeException("Cannot load extra class " + className, e);
					}
				}
			}
		}

		// When all scans are done, we set a short name for all reference objects
		library.resolveSchemaReferenceNames();

		return library;
	}

	private static Predicate<ClassInfo> onLocation(final String apiLocation) {
		if(ClassLoaderUtils.isClass(apiLocation)) {
			return classInfo -> classInfo.getName().equals(apiLocation);
		}
		return classInfo -> classInfo.getPackageName().startsWith(apiLocation);
	}

	private boolean validateWhiteList(final Class<?> restControllerClass) {
		if (whiteListPatterns.isEmpty()) {
			return true;
		}
		return whiteListPatterns.stream().anyMatch(whitePattern -> whitePattern.matcher(restControllerClass.getCanonicalName()).matches());
    }

	private boolean validateBlackList(final Class<?> restControllerClass) {
		if (blackListPatterns.isEmpty()) {
			return true;
		}
		return blackListPatterns.stream().noneMatch(blackPattern -> blackPattern.matcher(restControllerClass.getCanonicalName()).matches());
	}
}
