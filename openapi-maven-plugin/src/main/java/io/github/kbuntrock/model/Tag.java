package io.github.kbuntrock.model;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.Substitution;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A group of endpoints, found in the same rest controller annotated class
 */
public class Tag {

	private final List<Endpoint> endpoints = new ArrayList<>();
	private String name;
	private final Class<?> clazz;
	private String computedName;

	public Tag(Class<?> clazz) {
		this.name = clazz.getSimpleName();
		this.clazz = clazz;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String computeConfiguredName(ApiConfiguration apiConfiguration) {
		if(computedName == null) {
			computedName = getName();
			for(Substitution substitution : apiConfiguration.getTag().getSubstitutions()) {
				computedName = computedName.replaceAll(substitution.getRegex(), substitution.getSubstitute());
			}
		}
		return computedName;
	}

	public List<Endpoint> getEndpoints() {
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
}
