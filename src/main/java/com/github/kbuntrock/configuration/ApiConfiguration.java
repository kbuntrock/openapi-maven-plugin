package com.github.kbuntrock.configuration;

import com.github.kbuntrock.utils.Cloner;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

public class ApiConfiguration extends CommonApiConfiguration {

    /**
     * A list of location to find api endpoints. A location could be a class or a package
     */
    @Parameter(required = true)
    private List<String> locations;

    @Parameter
    private String filename = "spec-open-api";

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
        if (attachArtifact) {
            merged.setAttachArtifact(attachArtifact);
        }
        if (!DEFAULT_SUCCESSFUL_OPERATION_DESCRIPTION.equals(defaultSuccessfulOperationDescription)) {
            merged.setDefaultSuccessfulOperationDescription(defaultSuccessfulOperationDescription);
        }
        if (defaultProduceConsumeGuessing) {
            // TODO : to update if the default value change.
            merged.setDefaultProduceConsumeGuessing(defaultProduceConsumeGuessing);
        }
        if (!springPathEnhancement) {
            merged.setSpringPathEnhancement(springPathEnhancement);
        }
        return merged;
    }

}
