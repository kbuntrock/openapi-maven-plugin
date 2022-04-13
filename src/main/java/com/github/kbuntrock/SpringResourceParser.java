package com.github.kbuntrock;

import com.github.kbuntrock.model.Tag;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SpringResourceParser {

    private Log logger = new SystemStreamLog();

    private final List<String> apiLocations;

    private final ClassLoader projectClassLoader;

    public SpringResourceParser(ClassLoader projectClassLoader, List<String> apiLocations) {
        this.apiLocations = apiLocations;
        this.projectClassLoader = projectClassLoader;
    }

    public TagLibrary scanRestControllers() throws MojoFailureException {
        TagLibrary library = new TagLibrary();
        for (String apiLocation : apiLocations) {
            logger.info("scanning : " + apiLocation);

            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .addClassLoaders(projectClassLoader)
                    .addUrls(ClasspathHelper.forClassLoader(projectClassLoader))
                    .filterInputsBy( new FilterBuilder().includePackage(apiLocation)));


            // Find directly or inheritedly annotated by RequestMapping classes.
            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RequestMapping.class, true);
            SpringClassAnalyser springClassAnalyser = new SpringClassAnalyser();
            logger.info("subclasses");
            for (Class clazz : classes) {
                logger.info(clazz.getSimpleName());
                Optional<Tag> optTag = springClassAnalyser.getTagFromClass(clazz);
                if(optTag.isPresent()) {
                    library.addTag(optTag.get());
                }
            }
        }
        return library;
    }
}
