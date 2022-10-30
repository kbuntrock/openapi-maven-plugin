package io.github.kbuntrock.configuration;

import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugins.annotations.Parameter;

public class Tag {

	@Parameter
	private List<Substitution> substitutions = new ArrayList<>();

	public List<Substitution> getSubstitutions() {
		return substitutions;
	}

	public void setSubstitutions(List<Substitution> substitutions) {
		this.substitutions = substitutions;
	}
}
