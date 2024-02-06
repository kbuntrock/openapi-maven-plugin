package io.github.kbuntrock.model;

import io.github.kbuntrock.yaml.model.ExternalDocs;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import static java.util.Comparator.nullsLast;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Endpoint implements Comparable<Endpoint> {

	private String path;

	private OperationType type;

	private String name;

	private List<ParameterObject> parameters;

	private Integer responseCode;
	private DataObject responseObject;
	private List<String> responseFormats;

	private boolean deprecated = false;

	/**
	 * Used to identify uniquely a endpoint. Aggregation of the returned type, the name and the parameters types.
	 */
	private String identifier;

    private final Method method;

    public Endpoint(Method method) {
        this.method = method;
    }

	public Optional<String> getComputedDescription() {
		if (method.isAnnotationPresent(Operation.class)) {
			final String description = method.getAnnotation(Operation.class).description();
			return !description.isEmpty() ? Optional.of(description) : Optional.empty();
		}
		return Optional.empty();
	}

	public Optional<String> getComputedSummary() {
		if (method.isAnnotationPresent(Operation.class)) {
			final String summary = method.getAnnotation(Operation.class).summary();
			return !summary.isEmpty() ? Optional.of(summary) : Optional.empty();
		}
		return Optional.empty();
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

	public boolean getComputedDeprecated() {
		// Swagger takes precedence ?
		if (method.isAnnotationPresent(Operation.class)) {
			return method.getAnnotation(Operation.class).deprecated();
		}
		return deprecated;
	}

    public String getComputedName() {
        // Swagger takes precedence ?
		return getSwaggerName()
				.orElse(name);
    }


    private Optional<String> getSwaggerName() {
        if (method.isAnnotationPresent(io.swagger.v3.oas.annotations.tags.Tag.class)) {
            return Optional.of(method.getAnnotation(io.swagger.v3.oas.annotations.tags.Tag.class).name());
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
}
