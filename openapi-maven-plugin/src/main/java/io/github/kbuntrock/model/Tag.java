package io.github.kbuntrock.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.nullsLast;

/**
 * A group of endpoints, found in the same rest controller annotated class
 */
public class Tag implements Comparable<Tag> {

	private final List<Endpoint> endpoints = new ArrayList<>();
	private String name;
	private String description;
	private final Class<?> clazz;
	private String computedName;

	public Tag(final Class<?> clazz) {
		this.name = clazz.getSimpleName();
		this.clazz = clazz;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setComputedName(final String computedName) {
        this.computedName = computedName;
	}

    public String getComputedName() {
        return computedName;
    }

	public Collection<Endpoint> getEndpoints() {
		return endpoints;
	}

	public Collection<Endpoint> getSortedEndpoints() {
		return endpoints.stream().sorted().collect(Collectors.toList());
	}

	public void addEndpoints(final List<Endpoint> endpoint) {
		this.endpoints.addAll(endpoint);
	}

	public Class<?> getClazz() {
		return clazz;
	}

	@Override
	public String toString() {
		return "Tag{" +
			"name='" + name + '\'' +
			", endpoints=" + endpoints.stream().map(Endpoint::toString).collect(Collectors.joining(", ")) +
			'}';
	}

	@Override
	public int compareTo(final Tag o) {
		return Comparator
			.comparing((Tag t) -> t.computedName, nullsLast(String::compareTo))
			.thenComparing(t -> t.name, nullsLast(String::compareTo))
			.thenComparing(t -> t.clazz.getCanonicalName())
			.compare(this, o);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
