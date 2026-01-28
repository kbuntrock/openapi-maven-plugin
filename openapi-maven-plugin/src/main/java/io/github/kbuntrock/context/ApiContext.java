package io.github.kbuntrock.context;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.NullableConfiguration;
import io.github.kbuntrock.configuration.library.reader.ClassLoaderHelper;
import io.github.kbuntrock.reflection.AdditionnalSchemaLibrary;
import io.github.kbuntrock.reflection.annotation.MergeAnnotationsHelper;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public final class ApiContext {

	private final ProjectContext projectContext;
	private final AdditionnalSchemaLibrary additionnalSchemaLibrary;

	private ApiConfiguration apiConfiguration;
	private NullableConfiguration nullableConfiguration;
	private OpenApiTypeResolver openApiTypeResolver;

	private MergeAnnotationsHelper mergeAnnotationsHelper;

	public ApiContext(final ProjectContext projectContext, final AdditionnalSchemaLibrary additionnalSchemaLibrary) {
		this.projectContext = projectContext;
		this.additionnalSchemaLibrary = additionnalSchemaLibrary;
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

	public ClassLoaderHelper getClassLoaderHelper() {
		return projectContext.getClassLoaderHelper();
	}

	public AdditionnalSchemaLibrary getAdditionnalSchemaLibrary() {
		return additionnalSchemaLibrary;
	}

	public ApiConfiguration getApiConfiguration() {
		return apiConfiguration;
	}

	public void setApiConfiguration(ApiConfiguration apiConfiguration) {
		this.apiConfiguration = apiConfiguration;
		this.mergeAnnotationsHelper = new MergeAnnotationsHelper(projectContext.getClassLoaderHelper());
	}

	public NullableConfiguration getNullableConfiguration() {
		return nullableConfiguration;
	}

	public void setNullableConfiguration(NullableConfiguration nullableConfiguration) {
		this.nullableConfiguration = nullableConfiguration;
	}

	public OpenApiTypeResolver getOpenApiTypeResolver() {
		return openApiTypeResolver;
	}

	public void setOpenApiTypeResolver(OpenApiTypeResolver openApiTypeResolver) {
		this.openApiTypeResolver = openApiTypeResolver;
	}

	public MergeAnnotationsHelper getMergeAnnotationsHelper() {
		return mergeAnnotationsHelper;
	}
}
