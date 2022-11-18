package io.github.kbuntrock.configuration;

import org.apache.maven.plugins.annotations.Parameter;

public class Substitution {

	@Parameter
	private String type;
	@Parameter(required = true)
	private String regex;
	@Parameter
	private String substitute = "";

	public Substitution() {
	}

	public Substitution(final Substitution substitution) {
		this.type = substitution.type;
		this.regex = substitution.regex;
		this.substitute = substitution.substitute;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(final String regex) {
		this.regex = regex;
	}

	public String getSubstitute() {
		return substitute;
	}

	public void setSubstitute(final String substitute) {
		this.substitute = substitute;
	}


}
