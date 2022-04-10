package com.github.kbuntrock;


import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "documentation", defaultPhase = LifecyclePhase.COMPILE,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class DocumentationMojo extends AbstractMojo {

    /**
     * A list of api configurations
     */
    @Parameter(required = true)
    private List<ApiConfiguration> apiConfigurations;

    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    private ClassLoader projectClassLoader;

    public void execute() throws MojoExecutionException, MojoFailureException {
        touchExecute();

        validateConfiguration();

        scanProjectResources();
    }

    private void validateConfiguration() throws MojoFailureException {
        if (apiConfigurations == null || apiConfigurations.isEmpty()) {
            throw new MojoFailureException("At least one api configuration element should be configured");
        }
        for(ApiConfiguration apiConfiguration : apiConfigurations) {
            if (apiConfiguration.getLocations() == null || apiConfiguration.getLocations().isEmpty()) {
                throw new MojoFailureException("At least one location element should be configured");
            }
        }
    }

    private void scanProjectResources() throws MojoFailureException, MojoExecutionException {

        getLog().info("BEGINNING");
        createProjectDependenciesClassLoader();

        for(ApiConfiguration apiConfiguration : apiConfigurations){
            SpringResourceParser springResourceParser = new SpringResourceParser(getLog(), projectClassLoader, apiConfiguration.getLocations());
            springResourceParser.findRestControllers();
        }


        getLog().info("END");
    }

    /**
     * Create a classloader for the classes and dependencies of the project
     *
     * @return the classloader to use
     * @throws MojoExecutionException
     */
    private void createProjectDependenciesClassLoader() throws MojoExecutionException {
        try {
            List<URL> pathUrls = new ArrayList<>();
            for (String mavenCompilePath : project.getCompileClasspathElements()) {
                pathUrls.add(new File(mavenCompilePath).toURI().toURL());
            }

            URL[] urlsForClassLoader = pathUrls.toArray(new URL[pathUrls.size()]);
            getLog().info("urls for URLClassLoader: " + Arrays.asList(urlsForClassLoader));

            // We need to define parent classloader which is the parent of the plugin classloader, in order to not mix up
            // the project and the plugin classes.
            projectClassLoader = new URLClassLoader(urlsForClassLoader, DocumentationMojo.class.getClassLoader().getParent());
        } catch (DependencyResolutionRequiredException | MalformedURLException ex) {
            throw new MojoExecutionException("Cannot create project dependencies classloader", ex);
        }

    }

    private void touchExecute() throws MojoExecutionException {
        File f = outputDirectory;

        if (!f.exists()) {
            f.mkdirs();
        }

        File touch = new File(f, "touch.txt");

        FileWriter w = null;
        try {
            w = new FileWriter(touch);

            w.write("touch.txt");
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + touch, e);
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

}
