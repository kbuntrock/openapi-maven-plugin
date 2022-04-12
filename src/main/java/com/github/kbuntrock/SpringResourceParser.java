package com.github.kbuntrock;

import com.github.kbuntrock.model.Endpoint;
import com.github.kbuntrock.model.Operation;
import com.github.kbuntrock.model.Tag;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SpringResourceParser {

    private final Log logger;

    private final List<String> apiLocations;

    private final ClassLoader projectClassLoader;

    public SpringResourceParser(Log logger, ClassLoader projectClassLoader, List<String> apiLocations) {
        this.logger = logger;
        this.apiLocations = apiLocations;
        this.projectClassLoader = projectClassLoader;
    }

    public void findRestControllers() throws MojoFailureException {
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
            List<Tag> tagElements = new ArrayList<>();
            for (Class clazz : classes) {
                logger.info(clazz.getSimpleName());
                Optional<Tag> optTag = springClassAnalyser.getTagFromClass(clazz);
                if(optTag.isPresent()) {
                    tagElements.add(optTag.get());
                }
            }
        }
    }
}
