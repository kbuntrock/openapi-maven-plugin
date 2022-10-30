package io.github.kbuntrock;

import static org.reflections.scanners.Scanners.TypesAnnotated;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.library.Library;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.utils.Logger;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

	public ApiResourceScanner(final ApiConfiguration apiConfiguration) {
		this.apiConfiguration = apiConfiguration;
	}

	public TagLibrary scanRestControllers() throws MojoFailureException {

		final TagLibrary library = new TagLibrary();

		final Library framework = apiConfiguration.getLibrary();
		final List<Class<? extends Annotation>> annotatedElementList = new ArrayList<>();
		for(final String annotationName : apiConfiguration.getTagAnnotations()) {
			annotatedElementList.add(framework.getByClassName(annotationName));
		}

		for(final String apiLocation : apiConfiguration.getLocations()) {
			logger.info("Scanning : " + apiLocation);

			final ConfigurationBuilder configurationBuilder = ReflectionsUtils.createConfigurationBuilder();
			configurationBuilder.filterInputsBy(new FilterBuilder().includePackage(apiLocation));

			configurationBuilder.setScanners(TypesAnnotated);
			final Reflections reflections = new Reflections(configurationBuilder);
			final Set<Class<?>> classes = reflections.get(TypesAnnotated.with(annotatedElementList.toArray(new AnnotatedElement[]{}))
				.asClass(ReflectionsUtils.getProjectClassLoader()));

			logger.info("Found " + classes.size() + " annotated classes with [ " +
				annotatedElementList.stream().map(Class::getSimpleName).collect(Collectors.joining(", ")) + " ]");

			// Find directly or inheritedly annotated by RequestMapping classes.
			final JavaClassAnalyser javaClassAnalyser = new JavaClassAnalyser(apiConfiguration);
			for(final Class clazz : classes) {
				final Optional<Tag> optTag = javaClassAnalyser.getTagFromClass(clazz);
				if(optTag.isPresent()) {
					library.addTag(optTag.get());
				}
			}
		}
		return library;
	}
}
