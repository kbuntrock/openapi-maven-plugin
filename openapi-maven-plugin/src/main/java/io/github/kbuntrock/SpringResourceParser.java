package io.github.kbuntrock;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.library.Library;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.utils.Logger;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.reflections.scanners.Scanners.TypesAnnotated;

public class SpringResourceParser {

    private final Log logger = Logger.INSTANCE.getLogger();

    private final ApiConfiguration apiConfiguration;

    public SpringResourceParser(final ApiConfiguration apiConfiguration) {
        this.apiConfiguration = apiConfiguration;
    }

    public TagLibrary scanRestControllers() throws MojoFailureException {
        TagLibrary library = new TagLibrary();

        Library framework = apiConfiguration.getLibrary();
        List<Class<? extends Annotation>> annotatedElementList = new ArrayList<>();
        for (String annotationName : apiConfiguration.getTagAnnotations()) {
            annotatedElementList.add(framework.getByClassName(annotationName));
        }

        for (String apiLocation : apiConfiguration.getLocations()) {
            logger.info("Scanning : " + apiLocation);

            ConfigurationBuilder configurationBuilder = ReflectionsUtils.createConfigurationBuilder();
            configurationBuilder.filterInputsBy(new FilterBuilder().includePackage(apiLocation));

            configurationBuilder.setScanners(TypesAnnotated);
            Reflections reflections = new Reflections(configurationBuilder);
            Set<Class<?>> classes = reflections.get(TypesAnnotated.with(annotatedElementList.toArray(new AnnotatedElement[]{}))
                    .asClass(ReflectionsUtils.getProjectClassLoader()));
            
            logger.info("Found " + classes.size() + " annotated classes");

            // Find directly or inheritedly annotated by RequestMapping classes.
            SpringClassAnalyser springClassAnalyser = new SpringClassAnalyser(apiConfiguration);
            for (Class clazz : classes) {
                Optional<Tag> optTag = springClassAnalyser.getTagFromClass(clazz);
                if (optTag.isPresent()) {
                    library.addTag(optTag.get());
                }
            }
        }
        return library;
    }
}
