package io.github.kbuntrock.context;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public final class ApiContext {

    private final ProjectContext projectContext;

    public ApiContext(final ProjectContext projectContext) {
        this.projectContext = projectContext;
    }

    public Log getLogger() {
        return projectContext.getLogger();
    }

    public ClassLoader getClassLoader() {
        return projectContext.getClassLoader();
    }

    public MavenProject getProject() {
        return projectContext.getProject();
    }
}
