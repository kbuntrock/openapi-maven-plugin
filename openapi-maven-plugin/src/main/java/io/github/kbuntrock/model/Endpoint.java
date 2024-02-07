package io.github.kbuntrock.model;

import io.github.kbuntrock.yaml.model.ExternalDocs;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import static java.util.Comparator.nullsLast;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Endpoint implements Comparable<Endpoint> {

	private String path;

	private OperationType type;

	private String name;

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


	public Optional<ExternalDocs> getComputedExternalDocs() {
		if (method.isAnnotationPresent(Operation.class)) {
			ExternalDocumentation externalDocumentation = method.getAnnotation(Operation.class).externalDocs();
			if (!externalDocumentation.description().isEmpty() || !externalDocumentation.url().isEmpty()) {
				return Optional.of(new ExternalDocs(
						!externalDocumentation.url().isEmpty() ? externalDocumentation.url() : null,
						!externalDocumentation.description().isEmpty() ? externalDocumentation.description() : null));
			}
		}
		return Optional.empty();
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

	public boolean getDeprecated() {
		return deprecated;
	}
	public void setDeprecated(final boolean deprecated) {
		this.deprecated = deprecated;
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
