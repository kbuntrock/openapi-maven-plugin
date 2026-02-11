package io.github.kbuntrock.context;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.NullableConfiguration;
import io.github.kbuntrock.configuration.library.Library;
import io.github.kbuntrock.configuration.library.reader.ClassLoaderHelper;
import io.github.kbuntrock.reflection.AdditionalSchemaLibrary;
import io.github.kbuntrock.reflection.annotation.MergeAnnotationsHelper;
import io.github.kbuntrock.reflection.annotation.regular.RegularMergedAnnotationsHelper;
import io.github.kbuntrock.reflection.annotation.spring.SpringMergeAnnotationsHelper;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public final class ApiContext {

	private final ProjectContext projectContext;
	private final AdditionalSchemaLibrary additionalSchemaLibrary;

	private ApiConfiguration apiConfiguration;
	private NullableConfiguration nullableConfiguration;
	private OpenApiTypeResolver openApiTypeResolver;

	private MergeAnnotationsHelper mergeAnnotationsHelper;

	public ApiContext(final ProjectContext projectContext, final AdditionalSchemaLibrary additionalSchemaLibrary) {
		this.projectContext = projectContext;
		this.additionalSchemaLibrary = additionalSchemaLibrary;
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

	public AdditionalSchemaLibrary getAdditionnalSchemaLibrary() {
		return additionalSchemaLibrary;
	}

	public ApiConfiguration getApiConfiguration() {
		return apiConfiguration;
	}

	public void setApiConfiguration(ApiConfiguration apiConfiguration) {
		this.apiConfiguration = apiConfiguration;
		if(Library.SPRING_MVC == apiConfiguration.getLibrary()) {
			this.mergeAnnotationsHelper = new SpringMergeAnnotationsHelper(projectContext.getClassLoaderHelper());
		} else {
			this.mergeAnnotationsHelper = new RegularMergedAnnotationsHelper(projectContext.getClassLoaderHelper());
		}

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
