package io.github.kbuntrock;

import static org.reflections.scanners.Scanners.TypesAnnotated;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.CommonApiConfiguration;
import io.github.kbuntrock.configuration.library.Library;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.utils.Logger;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

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

	public TagLibrary scanRestControllers(final Map<Class<?>, Class<?>> clazzMappers) throws MojoFailureException {

		final TagLibrary library = new TagLibrary();
		TagLibraryHolder.INSTANCE.setTagLibrary(library);

		final Library framework = apiConfiguration.getLibrary();
		final List<Class<? extends Annotation>> annotatedElementList = new ArrayList<>();
		for(final String annotationName : apiConfiguration.getTagAnnotations()) {
			annotatedElementList.add(framework.getByClassName(annotationName));
		}

		for(final String apiLocation : apiConfiguration.getLocations()) {
			logger.info("Scanning : " + apiLocation);

			final ConfigurationBuilder configurationBuilder = ReflectionsUtils.createConfigurationBuilder();
			configurationBuilder.filterInputsBy(new FilterBuilder().includePackage(apiLocation));
			configurationBuilder.setClassLoaders(new ClassLoader[]{ReflectionsUtils.getProjectClassLoader()});

			configurationBuilder.setScanners(TypesAnnotated);
			final Reflections reflections = new Reflections(configurationBuilder);
			final Set<Class<?>> classes = reflections.get(TypesAnnotated.with(annotatedElementList.toArray(new AnnotatedElement[]{}))
				.asClass(ReflectionsUtils.getProjectClassLoader()));

			logger.info("Found " + classes.size() + " annotated classes with [ " +
				annotatedElementList.stream().map(Class::getSimpleName).collect(Collectors.joining(", ")) + " ]");

			// Find directly or inheritedly annotated by RequestMapping classes.
			final JavaClassAnalyser javaClassAnalyser = new JavaClassAnalyser(apiConfiguration);
			for(final Class clazz : classes) {
				if(validateWhiteList(clazz) && validateBlackList(clazz)) {
					final Optional<Tag> optTag = javaClassAnalyser.getTagFromClass(clazz, apiConfiguration.buildClazzMappers());
					if(optTag.isPresent()) {
						library.addTag(optTag.get());
					}
				}
			}

			// Possibly add extra data objets to the future schema section (objets which are not explicitely used by a endpoint)
			for(final String className : apiConfiguration.getExtraSchemaClasses()) {
				try {
					library.addExtraClass(ReflectionsUtils.getProjectClassLoader().loadClass(className), clazzMappers);
				} catch(final ClassNotFoundException e) {
					throw new MojoRuntimeException("Cannot load extra class " + className, e);
				}
			}
		}

		// When all scans are done, we set a short name for all reference objects
		library.resolveSchemaReferenceNames();

		return library;
	}

	private boolean validateWhiteList(final Class clazz) {
		if(!whiteListPatterns.isEmpty()) {
			for(final Pattern whitePattern : whiteListPatterns) {
				if(whitePattern.matcher(clazz.getCanonicalName()).matches()) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	private boolean validateBlackList(final Class clazz) {
		if(!blackListPatterns.isEmpty()) {
			for(final Pattern blackPattern : blackListPatterns) {
				if(blackPattern.matcher(clazz.getCanonicalName()).matches()) {
					return false;
				}
			}
		}
		return true;
	}
}
