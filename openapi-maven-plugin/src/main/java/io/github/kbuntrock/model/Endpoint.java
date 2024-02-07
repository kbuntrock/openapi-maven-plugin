package io.github.kbuntrock.model;

import static java.util.Comparator.nullsLast;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Endpoint implements Comparable<Endpoint> {

	private String path;

	private OperationType type;

	private String name;

	private String description;

	private String summary;

	private String externalDocUrl;
	private String externalDocDescription;

	private List<ParameterObject> parameters;

	private final Map<Integer, Response> responses = new HashMap<>(); // Indexed on response's code

	private boolean deprecated = false;

	/**
	 * Used to identify uniquely a endpoint. Aggregation of the returned type, the name and the parameters types.
	 */
	private String identifier;

    private final Method method;

    public Endpoint(Method method) {
        this.method = method;
    }


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

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Method getMethod() {
		return method;
	}
	public List<ParameterObject> getParameters() {
		return parameters;
	}

	public void setParameters(final List<ParameterObject> parameters) {
		this.parameters = parameters;
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

	public String getExternalDocUrl() {
		return externalDocUrl;
	}

	public void setExternalDocUrl(String externalDocUrl) {
		this.externalDocUrl = externalDocUrl;
	}

	public String getExternalDocDescription() {
		return externalDocDescription;
	}

	public void setExternalDocDescription(String externalDocDescription) {
		this.externalDocDescription = externalDocDescription;
	}
	@Override
	public int compareTo(final Endpoint o) {
		return Comparator
			.comparing((Endpoint endpoint) -> endpoint.path, nullsLast(String::compareTo))
			.thenComparing(e -> e.type)
			.thenComparing(e -> e.name)
			.compare(this, o);
	}

	public Map<Integer, Response> getResponses() {
		return responses;
	}

	public void addResponse(Response response) {
		this.responses.put(response.getCode(), response);
	}
}
