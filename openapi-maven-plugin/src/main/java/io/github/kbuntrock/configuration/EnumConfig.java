package io.github.kbuntrock.configuration;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author Kévin Buntrock
 */
public class EnumConfig {

	@Parameter
	private String canonicalName;
	@Parameter
	private String valueField;

	public EnumConfig() {
	}

	public EnumConfig(final EnumConfig enumConfig) {
		this.canonicalName = enumConfig.canonicalName;
		this.valueField = enumConfig.valueField;
	}

	public String getCanonicalName() {
		return canonicalName;
	}

	public void setCanonicalName(final String canonicalName) {
		this.canonicalName = canonicalName;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(final String valueField) {
		this.valueField = valueField;
	}
}
