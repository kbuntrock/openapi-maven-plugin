package io.github.kbuntrock.configuration;

import io.github.kbuntrock.configuration.library.Library;
import io.github.kbuntrock.configuration.library.TagAnnotation;
import io.github.kbuntrock.utils.Cloner;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

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
        if (attachArtifact != null) {
            merged.setAttachArtifact(attachArtifact);
        }
        if (defaultSuccessfulOperationDescription != null) {
            merged.setDefaultSuccessfulOperationDescription(defaultSuccessfulOperationDescription);
        }
        if (defaultProduceConsumeGuessing != null) {
            merged.setDefaultProduceConsumeGuessing(defaultProduceConsumeGuessing);
        }
        if (springPathEnhancement != null) {
            merged.setSpringPathEnhancement(springPathEnhancement);
        }
        if (loopbackOperationName != null) {
            merged.setLoopbackOperationName(loopbackOperationName);
        }
        if (operationId != null) {
            merged.setOperationId(operationId);
        }
        if (library != null) {
            merged.setLibrary(library);
        }
        if (tagAnnotations != null && !tagAnnotations.isEmpty()) {
            merged.setTagAnnotations(tagAnnotations);
        } else if(Library.JAXRS.name().equals(merged.getLibrary().toString().toUpperCase())) {
            merged.setTagAnnotations(new ArrayList<>());
            merged.getTagAnnotations().add(TagAnnotation.JAXRS_PATH.getAnnotationClassName());
        }
        if (freeFields != null) {
            merged.setFreeFields(freeFields);
        }
        merged.operationIdHelper = new OperationIdHelper(merged.operationId);
        return merged;
    }

}
