package com.github.kbuntrock;

import com.github.kbuntrock.model.Tag;
import com.github.kbuntrock.utils.Logger;
import com.github.kbuntrock.utils.ReflectionsUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.reflections.scanners.Scanners.TypesAnnotated;

public class SpringResourceParser {

    private final Log logger = Logger.INSTANCE.getLogger();

    private final List<String> apiLocations;

    private final ClassLoader projectClassLoader;

    private final boolean test;

    public SpringResourceParser(ClassLoader projectClassLoader, List<String> apiLocations) {
        this(projectClassLoader, apiLocations, false);
    }

    public SpringResourceParser(ClassLoader projectClassLoader, List<String> apiLocations, boolean test) {
        this.apiLocations = apiLocations;
        this.projectClassLoader = projectClassLoader;
        this.test = test;
    }

    public TagLibrary scanRestControllers() throws MojoFailureException {
        TagLibrary library = new TagLibrary(projectClassLoader);
        for (String apiLocation : apiLocations) {
            logger.info("Scanning : " + apiLocation);

            ConfigurationBuilder configurationBuilder = ReflectionsUtils.getConfigurationBuilder();
            configurationBuilder.filterInputsBy( new FilterBuilder().includePackage(apiLocation));
            configurationBuilder.setScanners(TypesAnnotated);

            Reflections reflections = new Reflections(configurationBuilder);


            // Find directly or inheritedly annotated by RequestMapping classes.
            Set<Class<?>> classes = reflections.get(TypesAnnotated.with(RequestMapping.class).asClass(projectClassLoader));
            SpringClassAnalyser springClassAnalyser = new SpringClassAnalyser(projectClassLoader);
            for (Class clazz : classes) {
                Optional<Tag> optTag = springClassAnalyser.getTagFromClass(clazz);
                if(optTag.isPresent()) {
                    library.addTag(optTag.get());
                }
            }
        }
        return library;
    }
}
