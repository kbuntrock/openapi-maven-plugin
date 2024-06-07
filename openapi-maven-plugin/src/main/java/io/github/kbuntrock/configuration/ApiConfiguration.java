package io.github.kbuntrock.configuration;

import io.github.kbuntrock.configuration.library.Library;
import io.github.kbuntrock.configuration.library.TagAnnotation;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugins.annotations.Parameter;

public class ApiConfiguration extends CommonApiConfiguration {

	private static final String DEFAULT_FILENAME = "spec-open-api.yml";
	/**
	 * A list of location to find api endpoints. A location could be a class or a package
	 */
	@Parameter(required = true)
	private List<String> locations;
	@Parameter
	private String filename = DEFAULT_FILENAME;

	protected String baseFreeField;
	@Parameter
	private boolean mergeFreeFields;

	private OperationIdHelper operationIdHelper;

	public List<String> getLocations() {
		return locations;
	}

	public void setLocations(final List<String> locations) {
		this.locations = locations;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	public OperationIdHelper getOperationIdHelper() {
		return operationIdHelper;
	}

	public void setOperationIdHelper(final OperationIdHelper operationIdHelper) {
		this.operationIdHelper = operationIdHelper;
	}

	public boolean isMergeFreeFields() {
		return mergeFreeFields;
	}

	public void setMergeFreeFields(final boolean mergeFreeFields) {
		this.mergeFreeFields = mergeFreeFields;
	}

	public String getBaseFreeField() {
		return baseFreeField;
	}

	/**
	 * Create a ApiConfiguration version based on the common configuration + the modified values overrided in the child config
	 *
	 * @param commonApiConfiguration the common api configuration
	 * @return the merged configuration
	 */
	public ApiConfiguration mergeWithCommonApiConfiguration(final CommonApiConfiguration commonApiConfiguration) {
		final CommonApiConfiguration copy = new CommonApiConfiguration(commonApiConfiguration);
		final ApiConfiguration merged = new ApiConfiguration();
		// Copy properties
		merged.tag = copy.tag;
		merged.operation = copy.operation;
		merged.attachArtifact = copy.attachArtifact;
		merged.defaultSuccessfulOperationDescription = copy.defaultSuccessfulOperationDescription;
		merged.defaultProduceConsumeGuessing = copy.defaultProduceConsumeGuessing;
		merged.pathEnhancement = copy.pathEnhancement;
		merged.pathPrefix = copy.pathPrefix;
		merged.fileFormat = copy.fileFormat;
		merged.loopbackOperationName = copy.loopbackOperationName;
		merged.operationId = copy.operationId;
		merged.freeFields = copy.freeFields;
		merged.library = copy.library;
		merged.tagAnnotations = copy.tagAnnotations;
		merged.whiteList = copy.whiteList;
		merged.blackList = copy.blackList;
		merged.enumConfigList = copy.enumConfigList;
		merged.extraSchemaClasses = copy.extraSchemaClasses;
		merged.customResponseTypeAnnotation = copy.customResponseTypeAnnotation;
		merged.defaultErrors = copy.defaultErrors;
		merged.openapiModels = copy.openapiModels;
		merged.modelsAssociations = copy.modelsAssociations;
		merged.defaultNonNullableFields = copy.defaultNonNullableFields;
		merged.nonNullableAnnotation = copy.nonNullableAnnotation;
		merged.nullableAnnotation = copy.nullableAnnotation;
		merged.nonDocumentableParameterClasses = copy.nonDocumentableParameterClasses;
		// End copy properties

		merged.setLocations(locations);
		merged.setFilename(filename);
		merged.setMergeFreeFields(mergeFreeFields);

		if(!tag.getSubstitutions().isEmpty()) {
			merged.getTag().setSubstitutions(tag.getSubstitutions());
		}
		if(!operation.getSubstitutions().isEmpty()) {
			merged.getOperation().setSubstitutions(operation.getSubstitutions());
		}
		if(attachArtifact != null) {
			merged.setAttachArtifact(attachArtifact);
		}
		if(defaultSuccessfulOperationDescription != null) {
			merged.setDefaultSuccessfulOperationDescription(defaultSuccessfulOperationDescription);
		}
		if(defaultProduceConsumeGuessing != null) {
			merged.setDefaultProduceConsumeGuessing(defaultProduceConsumeGuessing);
		}
		if(pathEnhancement != null) {
			merged.setPathEnhancement(pathEnhancement);
		}
		if(pathPrefix != null) {
			merged.setPathPrefix(pathPrefix);
		}
		if(fileFormat != null) {
			merged.setFileFormat(fileFormat);
		}
		if("json".equals(fileFormat) && DEFAULT_FILENAME.equals(filename)){
			merged.filename = DEFAULT_FILENAME.replace(".yml", ".json");
		}
		if(loopbackOperationName != null) {
			merged.setLoopbackOperationName(loopbackOperationName);
		}
		if(operationId != null) {
			merged.setOperationId(operationId);
		}
		if(library != null) {
			merged.setLibrary(library);
		}
		if(tagAnnotations != null && !tagAnnotations.isEmpty()) {
			merged.setTagAnnotations(tagAnnotations);
		} else if(Library.JAVAX_RS.name().equals(merged.getLibrary().toString().toUpperCase())) {
			merged.setTagAnnotations(new ArrayList<>());
			merged.getTagAnnotations().add(TagAnnotation.JAVAX_RS_PATH.getAnnotationClassName());
		} else if(Library.JAKARTA_RS.name().equals(merged.getLibrary().toString().toUpperCase())) {
			merged.setTagAnnotations(new ArrayList<>());
			merged.getTagAnnotations().add(TagAnnotation.JAKARTA_RS_PATH.getAnnotationClassName());
		}
		if(freeFields != null) {
			merged.setFreeFields(freeFields);
			if(mergeFreeFields && commonApiConfiguration.freeFields != null) {
				merged.baseFreeField = commonApiConfiguration.freeFields;
			}
		}
		if(whiteList != null) {
			merged.setWhiteList(whiteList);
		}
		if(blackList != null) {
			merged.setBlackList(blackList);
		}
		if(extraSchemaClasses != null && !extraSchemaClasses.isEmpty()) {
			merged.setExtraSchemaClasses(extraSchemaClasses);
		}
		if(customResponseTypeAnnotation != null) {
			merged.setCustomResponseTypeAnnotation(customResponseTypeAnnotation);
		}
		if(defaultErrors != null) {
			merged.setDefaultErrors(defaultErrors);
		}
		if(openapiModels != null) {
			merged.setOpenapiModels(openapiModels);
		}
		if(modelsAssociations != null) {
			merged.setModelsAssociations(modelsAssociations);
		}
		if(defaultNonNullableFields != null) {
			merged.setDefaultNonNullableFields(defaultNonNullableFields);
		}
		if(nonNullableAnnotation != null) {
			merged.setNonNullableAnnotation(nonNullableAnnotation);
		}
		if(nullableAnnotation != null) {
			merged.setNullableAnnotation(nullableAnnotation);
		}

		merged.operationIdHelper = new OperationIdHelper(merged.operationId);

		for(int i = 0; i < merged.tagAnnotations.size(); i++) {
			if("requestmapping".equals(merged.tagAnnotations.get(i).toLowerCase())) {
				merged.tagAnnotations.set(i, TagAnnotation.SPRING_MVC_REQUEST_MAPPING.getAnnotationClassName());
			} else if("restcontroller".equals(merged.tagAnnotations.get(i).toLowerCase())) {
				merged.tagAnnotations.set(i, TagAnnotation.SPRING_REST_CONTROLLER.getAnnotationClassName());
			}
		}

		if(nonDocumentableParameterClasses != null) {
			merged.setNonDocumentableParameterClasses(nonDocumentableParameterClasses);
		}
		return merged;
	}

}
