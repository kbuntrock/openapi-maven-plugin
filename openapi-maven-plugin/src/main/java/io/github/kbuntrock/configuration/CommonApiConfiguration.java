package io.github.kbuntrock.configuration;

import io.github.kbuntrock.configuration.library.Library;
import io.github.kbuntrock.configuration.library.TagAnnotation;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author Kevin Buntrock
 */
public class CommonApiConfiguration {

	public static String SEPARATOR_CLASS_METHOD = "#";

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
	protected Boolean pathEnhancement;

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

	@Parameter(required = true)
	protected List<String> whiteList;

	@Parameter(required = true)
	protected List<String> blackList;

	@Parameter
	protected List<EnumConfig> enumConfigList = new ArrayList<>();

	@Parameter
	protected List<String> extraSchemaClasses = new ArrayList<>();

	@Parameter
	protected String customResponseTypeAnnotation;

	public CommonApiConfiguration() {
	}

	public CommonApiConfiguration(final CommonApiConfiguration commonApiConfiguration) {
		this.tag = new Tag(commonApiConfiguration.tag);
		this.operation = new Operation(commonApiConfiguration.operation);
		this.attachArtifact = commonApiConfiguration.attachArtifact;
		this.defaultSuccessfulOperationDescription = commonApiConfiguration.defaultSuccessfulOperationDescription;
		this.defaultProduceConsumeGuessing = commonApiConfiguration.defaultProduceConsumeGuessing;
		this.pathEnhancement = commonApiConfiguration.pathEnhancement;
		this.loopbackOperationName = commonApiConfiguration.loopbackOperationName;
		this.operationId = commonApiConfiguration.operationId;
		this.freeFields = commonApiConfiguration.freeFields;
		this.library = commonApiConfiguration.library;
		this.customResponseTypeAnnotation = commonApiConfiguration.customResponseTypeAnnotation;
		for(final String tagAnnotation : commonApiConfiguration.tagAnnotations) {
			this.tagAnnotations.add(tagAnnotation);
		}
		if(commonApiConfiguration.whiteList != null) {
			this.whiteList = new ArrayList<>();
			this.whiteList.addAll(commonApiConfiguration.whiteList);
		}
		if(commonApiConfiguration.blackList != null) {
			this.blackList = new ArrayList<>();
			this.blackList.addAll(commonApiConfiguration.blackList);
		}
		for(final EnumConfig enumConfig : commonApiConfiguration.enumConfigList) {
			this.enumConfigList.add(new EnumConfig(enumConfig));
		}
		if(!commonApiConfiguration.extraSchemaClasses.isEmpty()) {
			this.extraSchemaClasses.addAll(commonApiConfiguration.extraSchemaClasses);
		}
	}

	public void initDefaultValues() {
		if(library == null) {
			library = DEFAULT_LIBRARY;
		}
		if(tagAnnotations.isEmpty()) {
			if(Library.SPRING_MVC.name().equals(library)) {
				tagAnnotations.add(TagAnnotation.SPRING_REST_CONTROLLER.getAnnotationClassName());
			} else if(Library.JAVAX_RS.name().equals(library)) {
				tagAnnotations.add(TagAnnotation.JAVAX_RS_PATH.getAnnotationClassName());
			} else {
				tagAnnotations.add(TagAnnotation.JAKARTA_RS_PATH.getAnnotationClassName());
			}
		}
		if(operationId == null) {
			operationId = DEFAULT_OPERATION_ID;
		}
		if(loopbackOperationName == null) {
			loopbackOperationName = true;
		}
		if(pathEnhancement == null) {
			pathEnhancement = true;
		}
		if(defaultProduceConsumeGuessing == null) {
			defaultProduceConsumeGuessing = true;
		}
		if(defaultSuccessfulOperationDescription == null) {
			defaultSuccessfulOperationDescription = "successful operation";
		}
		if(attachArtifact == null) {
			attachArtifact = true;
		}
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(final Tag tag) {
		this.tag = tag;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(final Operation operation) {
		this.operation = operation;
	}

	public boolean isAttachArtifact() {
		return attachArtifact;
	}

	public void setAttachArtifact(final boolean attachArtifact) {
		this.attachArtifact = attachArtifact;
	}

	public boolean isDefaultProduceConsumeGuessing() {
		return defaultProduceConsumeGuessing;
	}

	public void setDefaultProduceConsumeGuessing(final boolean defaultProduceConsumeGuessing) {
		this.defaultProduceConsumeGuessing = defaultProduceConsumeGuessing;
	}

	public boolean getPathEnhancement() {
		return pathEnhancement;
	}

	public void setPathEnhancement(final boolean pathEnhancement) {
		this.pathEnhancement = pathEnhancement;
	}

	public String getDefaultSuccessfulOperationDescription() {
		return defaultSuccessfulOperationDescription;
	}

	public void setDefaultSuccessfulOperationDescription(final String defaultSuccessfulOperationDescription) {
		this.defaultSuccessfulOperationDescription = defaultSuccessfulOperationDescription;
	}

	public boolean isLoopbackOperationName() {
		return loopbackOperationName;
	}

	public void setLoopbackOperationName(final boolean loopbackOperationName) {
		this.loopbackOperationName = loopbackOperationName;
	}

	public String getOperationId() {
		return operationId;
	}

	public void setOperationId(final String operationId) {
		this.operationId = operationId;
	}

	public String getFreeFields() {
		return freeFields;
	}

	public void setFreeFields(final String freeFields) {
		this.freeFields = freeFields;
	}

	public Library getLibrary() {
		return Library.getByName(library);
	}

	public void setLibrary(final String library) {
		this.library = library;
	}

	public List<String> getTagAnnotations() {
		return tagAnnotations;
	}

	public void setTagAnnotations(final List<String> tagAnnotations) {
		this.tagAnnotations = tagAnnotations;
	}

	public List<String> getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(final List<String> whiteList) {
		this.whiteList = whiteList;
	}

	public List<String> getBlackList() {
		return blackList;
	}

	public void setBlackList(final List<String> blackList) {
		this.blackList = blackList;
	}

	public List<EnumConfig> getEnumConfigList() {
		return enumConfigList;
	}

	public void setEnumConfigList(final List<EnumConfig> enumConfigList) {
		this.enumConfigList = enumConfigList;
	}

	public List<String> getExtraSchemaClasses() {
		return extraSchemaClasses;
	}

	public void setExtraSchemaClasses(final List<String> extraSchemaClasses) {
		this.extraSchemaClasses = extraSchemaClasses;
	}

	public String getCustomResponseTypeAnnotation() {
		return customResponseTypeAnnotation;
	}

	public void setCustomResponseTypeAnnotation(final String customResponseTypeAnnotation) {
		this.customResponseTypeAnnotation = customResponseTypeAnnotation;
	}
}
