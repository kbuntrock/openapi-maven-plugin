package com.github.kbuntrock;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpringResourceParser {

    private final Log logger;

    private final List<String> apiLocations;

    private final ClassLoader projectClassLoader;

    public SpringResourceParser(Log logger, ClassLoader projectClassLoader, List<String> apiLocations) {
        this.logger = logger;
        this.apiLocations = apiLocations;
        this.projectClassLoader = projectClassLoader;
    }

    public void findRestControllers() {
        for (String apiLocation : apiLocations) {
            logger.info("scanning : " + apiLocation);

            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .addClassLoaders(projectClassLoader)
                    .addUrls(ClasspathHelper.forClassLoader(projectClassLoader))
                    .filterInputsBy( new FilterBuilder().includePackage(apiLocation)));

            logger.info("all types");
            for (String s : reflections.getAllTypes()) {
                logger.info(s);
            }

            // Find directly or inheritedly annotated by RequestMapping classes.
            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RequestMapping.class, true);

            logger.info("subclasses");
            for (Class clazz : classes) {
                logger.info(clazz.getSimpleName());
            }
        }
    }
}
