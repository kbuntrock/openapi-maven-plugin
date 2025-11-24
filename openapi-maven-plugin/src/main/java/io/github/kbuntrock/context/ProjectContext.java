package io.github.kbuntrock.context;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Per-execution context for the OpenAPI Maven plugin.
 *
 * This class aggregates shared services and configuration that must be kept local
 * to a single plugin execution to maintain thread-safety across multi-module,
 * parallel maven builds.
 */
public final class ProjectContext {

    private Log logger;

    private ClassLoader classLoader;

    private MavenProject project;

    public void initLogger(Log logger) {
        if(this.logger == null) {
            this.logger = logger;
        }
    }

    public void initClassLoader(ClassLoader classLoader) {
        if(this.classLoader == null) {
            this.classLoader = classLoader;
        }
    }

    public Log getLogger() {
        return logger;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public MavenProject getProject() {
        return project;
    }

}
