package com.github.kbuntrock.configuration;

import com.github.kbuntrock.utils.Cloner;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;
import java.util.stream.Collectors;

public class ApiConfiguration extends CommonApiConfiguration {

    /**
     * A list of location to find api endpoints. A location could be a class or a package
     */
    @Parameter(required = true)
    private List<String> locations;

    @Parameter
    private String filename = "spec-open-api";

    private OperationIdHelper operationIdHelper;

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public OperationIdHelper getOperationIdHelper() {
        return operationIdHelper;
    }

    public void setOperationIdHelper(OperationIdHelper operationIdHelper) {
        this.operationIdHelper = operationIdHelper;
    }

    /**
     * Create a ApiConfiguration version based on the common configuration + the modified values overrided in the child config
     *
     * @param commonApiConfiguration the common api configuration
     * @return the merged configuration
     */
    public ApiConfiguration mergeWithCommonApiConfiguration(final CommonApiConfiguration commonApiConfiguration) {
        CommonApiConfiguration copy = Cloner.INSTANCE.deepClone(commonApiConfiguration);
        ApiConfiguration merged = new ApiConfiguration();
        Cloner.INSTANCE.copyPropertiesOfInheritedClass(copy, merged);
        merged.setLocations(locations);
        merged.setFilename(filename);
        if (!tag.getSubstitutions().isEmpty()) {
            merged.getTag().setSubstitutions(tag.getSubstitutions());
        }
        if (!operation.getSubstitutions().isEmpty()) {
            merged.getOperation().setSubstitutions(operation.getSubstitutions());
        }
        if (!attachArtifact) {
            merged.setAttachArtifact(attachArtifact);
        }
        if (!DEFAULT_SUCCESSFUL_OPERATION_DESCRIPTION.equals(defaultSuccessfulOperationDescription)) {
            merged.setDefaultSuccessfulOperationDescription(defaultSuccessfulOperationDescription);
        }
        if (!defaultProduceConsumeGuessing) {
            merged.setDefaultProduceConsumeGuessing(defaultProduceConsumeGuessing);
        }
        if (!springPathEnhancement) {
            merged.setSpringPathEnhancement(springPathEnhancement);
        }
        if (loopbackOperationName) {
            merged.setLoopbackOperationName(loopbackOperationName);
        }
        if (!DEFAULT_OPERATION_ID.equals(operationId)) {
            merged.setOperationId(operationId);
        }
        if (!DEFAULT_LIBRARY.equalsIgnoreCase(library)) {
            merged.setLibrary(library);
        }
        if (!DEFAULT_TAG_ANNOTATIONS.stream().collect(Collectors.joining()).equals(tagAnnotations.stream().collect(Collectors.joining()))) {
            merged.setTagAnnotations(tagAnnotations);
        }
        merged.operationIdHelper = new OperationIdHelper(merged.operationId);
        return merged;
    }

}
