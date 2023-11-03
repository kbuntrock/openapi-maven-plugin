package io.github.kbuntrock.model;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.Substitution;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.nullsLast;

/**
 * A group of endpoints, found in the same rest controller annotated class
 */
public class Tag implements Comparable<Tag> {

	private final SortedSet<Endpoint> endpoints = new TreeSet<>();
	private String name;
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

	public String computeConfiguredName(final ApiConfiguration apiConfiguration) {
		if(computedName == null) {
			computedName = getName();
			for(final Substitution substitution : apiConfiguration.getTag().getSubstitutions()) {
				computedName = computedName.replaceAll(substitution.getRegex(), substitution.getSubstitute());
			}
		}
		return computedName;
	}

	public Collection<Endpoint> getEndpoints() {
		return endpoints;
	}

	public void addEndpoint(final Endpoint endpoint) {
		this.endpoints.add(endpoint);
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Tag tag = (Tag) o;

		if (!Objects.equals(name, tag.name)) return false;
		return Objects.equals(computedName, tag.computedName);
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

	@Override
	public int compareTo(Tag o) {
		return Comparator
				.comparing((Tag t) -> t.computedName, nullsLast(String::compareTo))
				.thenComparing(t -> t.name, nullsLast(String::compareTo))
				.compare(this, o);
	}
}
