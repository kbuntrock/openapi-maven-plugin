package io.github.kbuntrock.model;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.util.Comparator.nullsLast;

public class Endpoint implements Comparable<Endpoint> {

	private String path;

	private OperationType type;

	private String name;

	private String computedName;

	private List<ParameterObject> parameters;

	private Integer responseCode;
	private DataObject responseObject;
	private List<String> responseFormats;

	private boolean deprecated = false;

	/**
	 * Used to identify uniquely a endpoint. Aggregation of the returned type, the name and the parameters types.
	 */
	private String identifier;

	public String getPath() {
		return path;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public OperationType getType() {
		return type;
	}

	public void setType(final OperationType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public List<ParameterObject> getParameters() {
		return parameters;
	}

	public void setParameters(final List<ParameterObject> parameters) {
		this.parameters = parameters;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(final Integer responseCode) {
		this.responseCode = responseCode;
	}

	public DataObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(final DataObject responseObject) {
		this.responseObject = responseObject;
	}

	public List<String> getResponseFormats() {
		return responseFormats;
	}

	public void setResponseFormats(final List<String> responseFormats) {
		this.responseFormats = responseFormats;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final String identifier) {
		this.identifier = identifier;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(final boolean deprecated) {
		this.deprecated = deprecated;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Endpoint endpoint = (Endpoint) o;

		if (!Objects.equals(path, endpoint.path)) return false;
		if (type != endpoint.type) return false;
		if (!Objects.equals(name, endpoint.name)) return false;
		return Objects.equals(computedName, endpoint.computedName);
	}

	@Override
	public int hashCode() {
		int result = path != null ? path.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (computedName != null ? computedName.hashCode() : 0);
		return result;
	}

	@Override
	public int compareTo(Endpoint o) {
		return Comparator
				.comparing((Endpoint endpoint) -> endpoint.path, nullsLast(String::compareTo))
				.thenComparing(e -> e.type, nullsLast(OperationType::compareTo))
				.thenComparing(e -> e.computedName, nullsLast(String::compareTo))
				.thenComparing(e -> e.name, nullsLast(String::compareTo))
				.compare(this, o);
	}
}
