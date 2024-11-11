package io.github.kbuntrock.utils;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The node to write in the documentation with a denormalisation on the type attribute
 *
 * @author Kévin Buntrock
 */
public class OpenApiResolvedType {

	/**
	 * The denormalized type
	 */
	private final OpenApiDataType type;

	private final String modelName;

	/**
	 * The full node to write in the documentation (type + format + anything configured)
	 */
	private final JsonNode node;

	/**
	 * True if the json node completly describe the object (false for arrays, enum and objects described from the code)
	 */
	private boolean completeNode = true;

	/**
	 * The node ready to be written in the schema section
	 * (we are forced to do this since JsonNode unwrapping is not supported yet by jackson).
	 *
	 * See :
	 * - https://github.com/FasterXML/jackson-databind/issues/3196
	 * - https://github.com/FasterXML/jackson-databind/issues/3394
	 * - https://github.com/FasterXML/jackson-databind/issues/3604
	 */
	private final Map<String, JsonNode> schemaSection = new LinkedHashMap<>();

	private final String defaultEncoding;

	public OpenApiResolvedType(final OpenApiDataType type, final JsonNode node, final String modelName, final String defaultEncoding) {
		this.type = type;
		this.node = node;
		this.modelName = modelName;
		this.defaultEncoding = defaultEncoding;
		if(node != null) {
			node.fields().forEachRemaining(entry -> {
				schemaSection.put(entry.getKey(), entry.getValue());
			});
		}
	}

	public OpenApiDataType getType() {
		return type;
	}

	public Map<String, JsonNode> getSchemaSection() {
		return schemaSection;
	}

	public boolean isCompleteNode() {
		return completeNode;
	}

	public void setCompleteNode(final boolean completeNode) {
		this.completeNode = completeNode;
	}

	public String getModelName() {
		return modelName;
	}

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public JsonNode getNode() {
		return node;
	}

	public OpenApiResolvedType copy() {
		final OpenApiResolvedType copy = new OpenApiResolvedType(type, node, modelName, defaultEncoding);
		copy.completeNode = this.completeNode;
		return copy;
	}

}
