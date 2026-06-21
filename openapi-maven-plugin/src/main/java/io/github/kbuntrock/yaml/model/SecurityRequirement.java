package io.github.kbuntrock.yaml.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SecurityRequirement extends LinkedHashMap<String, List<String>> {

	public void addRequirement(String name, List<String> scopes) {
		this.put(name, scopes != null ? scopes : new ArrayList<>());
	}

	public void addRequirement(String name) {
		this.put(name, new ArrayList<>());
	}
}
