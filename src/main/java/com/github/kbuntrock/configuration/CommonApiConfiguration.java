package com.github.kbuntrock.configuration;

import com.github.kbuntrock.configuration.library.Library;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kevin Buntrock
 */
public class CommonApiConfiguration {

    protected static String DEFAULT_SUCCESSFUL_OPERATION_DESCRIPTION = "successful operation";

    public static String DEFAULT_OPERATION_ID = "{class_name}.{method_name}";

    protected static String DEFAULT_LIBRARY = Library.SPRING_MVC.name();

    protected static List<String> DEFAULT_TAG_ANNOTATIONS = Library.SPRING_MVC.getTagAnnotations().stream()
            .map(Library.TagAnnotation::getAnnotationClassName).collect(Collectors.toList());

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
    protected boolean defaultProduceConsumeGuessing = true;

    /**
     * Apply the spring enhancement to path value between a class @RequestMapping and a method @RequestMapping :
     * - add a "/" between the two values if there is none
     * - add a "/" at the beginning of the operation path if there is none
     */
    @Parameter
    protected boolean springPathEnhancement = true;

    /**
     * If true, return a short operation name for code generation, as described here :
     * https://loopback.io/doc/en/lb4/Decorators_openapi.html
     */
    @Parameter
    protected boolean loopbackOperationName = false;

    @Parameter
    protected String operationId = DEFAULT_OPERATION_ID;

    @Parameter
    protected String library = DEFAULT_LIBRARY;

    @Parameter
    protected List<String> tagAnnotations = getLibrary().getTagAnnotations().stream()
            .map(Library.TagAnnotation::getAnnotationClassName).collect(Collectors.toList());

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

    public boolean isLoopbackOperationName() {
        return loopbackOperationName;
    }

    public void setLoopbackOperationName(boolean loopbackOperationName) {
        this.loopbackOperationName = loopbackOperationName;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public Library getLibrary() {
        return Library.getByName(library);
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public List<String> getTagAnnotations() {
        return tagAnnotations;
    }

    public void setTagAnnotations(List<String> tagAnnotations) {
        this.tagAnnotations = tagAnnotations;
    }
}
