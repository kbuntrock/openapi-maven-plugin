package io.github.kbuntrock.configuration;

import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugins.annotations.Parameter;

public class Tag {

	@Parameter
	private List<Substitution> substitutions = new ArrayList<>();

	public Tag() {
	}

	public Tag(final Tag tag) {
		for(final Substitution substitution : tag.getSubstitutions()) {
			substitutions.add(new Substitution(substitution));
		}
	}

	public List<Substitution> getSubstitutions() {
		return substitutions;
	}

	public void setSubstitutions(final List<Substitution> substitutions) {
		this.substitutions = substitutions;
	}
}
