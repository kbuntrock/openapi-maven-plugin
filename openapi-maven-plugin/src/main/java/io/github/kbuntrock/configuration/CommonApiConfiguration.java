package io.github.kbuntrock.configuration;

import io.github.kbuntrock.configuration.library.Library;
import io.github.kbuntrock.configuration.library.TagAnnotation;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Buntrock
 */
public class CommonApiConfiguration {

    protected static String DEFAULT_SUCCESSFUL_OPERATION_DESCRIPTION = "successful operation";

    public static String DEFAULT_OPERATION_ID = "{class_name}.{method_name}";

    public static String DEFAULT_LIBRARY = Library.SPRING_MVC.name();

    public static List<String> DEFAULT_TAG_ANNOTATIONS = new ArrayList<>();

    static {
        DEFAULT_TAG_ANNOTATIONS.add(TagAnnotation.SPRING_REST_CONTROLLER.getAnnotationClassName());
    }

    @Parameter
    protected Tag tag = new Tag();

    @Parameter
    protected Operation operation = new Operation();

    @Parameter
    protected Boolean attachArtifact;

    @Parameter
    protected String defaultSuccessfulOperationDescription;

    /**
     * If not defined, try to guess a produce / consume value depending of the parameter/return type
     */
    @Parameter
    protected Boolean defaultProduceConsumeGuessing;

    /**
     * Apply the spring enhancement to path value between a class @RequestMapping and a method @RequestMapping :
     * - add a "/" between the two values if there is none
     * - add a "/" at the beginning of the operation path if there is none
     */
    @Parameter
    protected Boolean springPathEnhancement;

    /**
     * If true, return a short operation name for code generation, as described here :
     * https://loopback.io/doc/en/lb4/Decorators_openapi.html
     */
    @Parameter
    protected Boolean loopbackOperationName;

    @Parameter
    protected String operationId;

    @Parameter
    protected String freeFields;

    @Parameter
    protected String library;

    @Parameter
    protected List<String> tagAnnotations = new ArrayList<>();

    public void initDefaultValues() {
        if (library == null) {
            library = DEFAULT_LIBRARY;
        }
        if (tagAnnotations.isEmpty()) {
            if (Library.SPRING_MVC.name().equals(library)) {
                tagAnnotations.add(TagAnnotation.SPRING_REST_CONTROLLER.getAnnotationClassName());
            } else {
                tagAnnotations.add(TagAnnotation.JAXRS_PATH.getAnnotationClassName());
            }
        }
        if (operationId == null) {
            operationId = DEFAULT_OPERATION_ID;
        }
        if (loopbackOperationName == null) {
            loopbackOperationName = true;
        }
        if (springPathEnhancement == null) {
            springPathEnhancement = true;
        }
        if (defaultProduceConsumeGuessing == null) {
            defaultProduceConsumeGuessing = true;
        }
        if (defaultSuccessfulOperationDescription == null) {
            defaultSuccessfulOperationDescription = "successful operation";
        }
        if (attachArtifact == null) {
            attachArtifact = true;
        }
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

    public String getFreeFields() {
        return freeFields;
    }

    public void setFreeFields(String freeFields) {
        this.freeFields = freeFields;
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
