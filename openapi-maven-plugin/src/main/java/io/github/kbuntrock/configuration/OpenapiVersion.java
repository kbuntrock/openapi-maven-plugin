package io.github.kbuntrock.configuration;

import io.github.kbuntrock.MojoRuntimeException;

public enum OpenapiVersion {
	V3_0,
	V3_1,
	V3_2;

	public static OpenapiVersion fromString(String version) {
		if(version.equals("3.0") || version.startsWith("3.0.")) {
			return V3_0;
		} else if(version.equals("3.1") || version.startsWith("3.1.")) {
			return V3_1;
		} else if(version.equals("3.2") || version.startsWith("3.2.")) {
			return V3_2;
		}
		throw new MojoRuntimeException("This openapi specification version is not supported by this plugin:" + version);
	}
}
