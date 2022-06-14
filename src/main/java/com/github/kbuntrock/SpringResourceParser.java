package com.github.kbuntrock;

import com.github.kbuntrock.model.Tag;
import com.github.kbuntrock.reflection.ReflectionsUtils;
import com.github.kbuntrock.utils.Logger;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.reflections.scanners.Scanners.TypesAnnotated;

public class SpringResourceParser {

    private final Log logger = Logger.INSTANCE.getLogger();

    private final List<String> apiLocations;


    public SpringResourceParser(List<String> apiLocations) {
        this.apiLocations = apiLocations;
    }

    public TagLibrary scanRestControllers() throws MojoFailureException {
        TagLibrary library = new TagLibrary();
        for (String apiLocation : apiLocations) {
            logger.info("Scanning : " + apiLocation);

            ConfigurationBuilder configurationBuilder = ReflectionsUtils.getConfigurationBuilder();
            configurationBuilder.filterInputsBy(new FilterBuilder().includePackage(apiLocation));
            configurationBuilder.setScanners(TypesAnnotated);

            Reflections reflections = new Reflections(configurationBuilder);


            // Find directly or inheritedly annotated by RequestMapping classes.
            Set<Class<?>> classesRM = reflections.get(TypesAnnotated.with(RequestMapping.class).asClass(ReflectionsUtils.getProjectClassLoader()));
            // Abstract or interfaces implemented with @RequestMapping
            Set<Class<?>> interfacesAndAsbtractsRM = classesRM.stream().filter(x -> x.isInterface() || Modifier.isAbstract(x.getModifiers())).collect(Collectors.toSet());
            // @RestController classes
            Set<Class<?>> classesRC = reflections.get(TypesAnnotated.with(RestController.class).asClass(ReflectionsUtils.getProjectClassLoader()));

            // If @RequestMapping annotated abstract class or interface IS NOT used in a scanned class annotated with @RestController :
            // it is docummented. But if it is, we only document the @RestController class (which is expected to be an implementation).

            Set<Class<?>> unimplementedRM = new HashSet<>();
            unimplementedRM.addAll(classesRM);
            for (Class<?> interfaceOrAbstractRM : interfacesAndAsbtractsRM) {
                for (Class<?> clazz : classesRC) {
                    if (clazz.isAssignableFrom(interfaceOrAbstractRM)) {
                        unimplementedRM.remove(interfaceOrAbstractRM);
                    }
                }
            }
            Set<Class<?>> classesToScan = new HashSet<>();
            classesToScan.addAll(unimplementedRM);
            classesToScan.addAll(classesRC);

            SpringClassAnalyser springClassAnalyser = new SpringClassAnalyser();
            for (Class clazz : classesToScan) {
                Optional<Tag> optTag = springClassAnalyser.getTagFromClass(clazz);
                if (optTag.isPresent()) {
                    library.addTag(optTag.get());
                }
            }
        }
        return library;
    }
}
