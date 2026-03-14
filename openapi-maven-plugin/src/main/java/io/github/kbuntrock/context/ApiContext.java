package io.github.kbuntrock.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
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

import java.lang.annotation.Annotation;
import java.util.Optional;

public final class ApiContext {

	private final ProjectContext projectContext;
	private final AdditionalSchemaLibrary additionalSchemaLibrary;

	private ApiConfiguration apiConfiguration;
	private NullableConfiguration nullableConfiguration;
	private OpenApiTypeResolver openApiTypeResolver;
	private ObjectMapper schemaObjectMapper;

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
		// Set the mapper in the API context so it can be configured in the future
		// See :
		// schemaObjectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
		// schemaObjectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		// schemaObjectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY);
		this.schemaObjectMapper = new ObjectMapper();

		if(Library.SPRING_MVC == apiConfiguration.getLibrary()) {
			this.mergeAnnotationsHelper = new SpringMergeAnnotationsHelper(projectContext.getClassLoaderHelper());
		} else {
			this.mergeAnnotationsHelper = new RegularMergedAnnotationsHelper(projectContext.getClassLoaderHelper());
			this.schemaObjectMapper.setAnnotationIntrospector(new SpecializedAnnotationIntrospector(this));
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

	public ObjectMapper getSchemaObjectMapper() {
		return schemaObjectMapper;
	}

	public class SpecializedAnnotationIntrospector extends JacksonAnnotationIntrospector {

		final Optional<Class> jsonbTransient;

		public SpecializedAnnotationIntrospector(final ApiContext apiContext) {
			switch(apiContext.apiConfiguration.getLibrary()) {
				case JAVAX_RS:
					jsonbTransient = ApiContext.this.projectContext.getClassLoaderHelper()
						.tryToGetByName("javax.json.bind.annotation.JsonbTransient");
					break;
				case JAKARTA_RS:
					jsonbTransient = ApiContext.this.projectContext.getClassLoaderHelper()
						.tryToGetByName("jakarta.json.bind.annotation.JsonbTransient");
					break;
				default:
					throw new RuntimeException("Unsupported library : " + apiContext.apiConfiguration.getLibrary());
			}

		}

		@Override
		public boolean hasIgnoreMarker(AnnotatedMember m) {
			if(jsonbTransient.isPresent()) {
				Annotation ann = _findAnnotation(m, jsonbTransient.get());
				return ann != null || super.hasIgnoreMarker(m);
			}
			return super.hasIgnoreMarker(m);
		}
	}
}
