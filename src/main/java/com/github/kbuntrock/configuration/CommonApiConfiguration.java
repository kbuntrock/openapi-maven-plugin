package com.github.kbuntrock.configuration;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author Kevin Buntrock
 */
public class CommonApiConfiguration {

    protected String DEFAULT_SUCCESSFUL_OPERATION_DESCRIPTION = "successful operation";

    @Parameter
    protected Tag tag = new Tag();

    @Parameter
    protected Operation operation = new Operation();

    @Parameter
    protected boolean attachArtifact = true;

    @Parameter
    protected String defaultSuccessfulOperationDescription = "successful operation";

    /**
     * If not defined, try to guess a produce / consume value depending of the parameter/return type
     */
    @Parameter
    protected boolean defaultProduceConsumeGuessing;

    /**
     * Apply the spring enhancement to path value between a class @RequestMapping and a method @RequestMapping :
     * - add a "/" between the two values if there is none
     * - add a "/" at the beginning of the operation path if there is none
     */
    @Parameter
    protected boolean springPathEnhancement = true;

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
