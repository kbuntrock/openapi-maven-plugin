package com.github.kbuntrock.configuration;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

public class ApiConfiguration {

    /**
     * A list of location to find api endpoints. A location could be a class or a package
     */
    @Parameter(required = true)
    private List<String> locations;

    @Parameter
    private String filename = "spec-open-api";

    @Parameter
    private Tag tag = new Tag();

    @Parameter
    private Operation operation = new Operation();

    @Parameter
    private boolean attachArtifact = true;

    @Parameter(required = false)
    private String defaultSuccessfulOperationDescription = "successful operation";

    /**
     * If not defined, try to guess a produce / consume value depending of the parameter/return type
     */
    @Parameter
    private boolean defaultProduceConsumeGuessing;

    /**
     * Apply the spring enhancement to path value between a class @RequestMapping and a method @RequestMapping :
     * - add a "/" between the two values if there is none
     * - add a "/" at the beginning of the operation path if there is none
     */
    @Parameter
    private boolean springPathEnhancement = true;

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

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public boolean isAttachArtifact() {
        return attachArtifact;
    }

    public void setAttachArtifact(boolean attachArtifact) {
        this.attachArtifact = attachArtifact;
    }

    public boolean isDefaultProduceConsumeGuessing() {
        return defaultProduceConsumeGuessing;
    }

    public void setDefaultProduceConsumeGuessing(boolean defaultProduceConsumeGuessing) {
        this.defaultProduceConsumeGuessing = defaultProduceConsumeGuessing;
    }

    public boolean isSpringPathEnhancement() {
        return springPathEnhancement;
    }

    public void setSpringPathEnhancement(boolean springPathEnhancement) {
        this.springPathEnhancement = springPathEnhancement;
    }

    public String getDefaultSuccessfulOperationDescription() {
        return defaultSuccessfulOperationDescription;
    }

    public void setDefaultSuccessfulOperationDescription(String defaultSuccessfulOperationDescription) {
        this.defaultSuccessfulOperationDescription = defaultSuccessfulOperationDescription;
    }
}
