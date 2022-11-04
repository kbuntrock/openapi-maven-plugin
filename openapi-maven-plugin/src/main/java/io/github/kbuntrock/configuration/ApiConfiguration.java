package io.github.kbuntrock.configuration;

import io.github.kbuntrock.configuration.library.Library;
import io.github.kbuntrock.configuration.library.TagAnnotation;
import io.github.kbuntrock.utils.Cloner;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugins.annotations.Parameter;

public class ApiConfiguration extends CommonApiConfiguration {

	/**
	 * A list of location to find api endpoints. A location could be a class or a package
	 */
	@Parameter(required = true)
	private List<String> locations;

	@Parameter
	private String filename = "spec-open-api.yml";

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

	/**
	 * Create a ApiConfiguration version based on the common configuration + the modified values overrided in the child config
	 *
	 * @param commonApiConfiguration the common api configuration
	 * @return the merged configuration
	 */
	public ApiConfiguration mergeWithCommonApiConfiguration(final CommonApiConfiguration commonApiConfiguration) {
		final CommonApiConfiguration copy = Cloner.INSTANCE.deepClone(commonApiConfiguration);
		final ApiConfiguration merged = new ApiConfiguration();
		Cloner.INSTANCE.copyPropertiesOfInheritedClass(copy, merged);
		merged.setLocations(locations);
		merged.setFilename(filename);

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
		}
		if(whiteList != null) {
			merged.setWhiteList(whiteList);
		}
		if(blackList != null) {
			merged.setBlackList(blackList);
		}

		merged.operationIdHelper = new OperationIdHelper(merged.operationId);

		for(int i = 0; i < merged.tagAnnotations.size(); i++) {
			if("requestmapping".equals(merged.tagAnnotations.get(i).toLowerCase())) {
				merged.tagAnnotations.set(i, TagAnnotation.SPRING_MVC_REQUEST_MAPPING.getAnnotationClassName());
			} else if("restcontroller".equals(merged.tagAnnotations.get(i).toLowerCase())) {
				merged.tagAnnotations.set(i, TagAnnotation.SPRING_REST_CONTROLLER.getAnnotationClassName());
			}
		}
		return merged;
	}

}
