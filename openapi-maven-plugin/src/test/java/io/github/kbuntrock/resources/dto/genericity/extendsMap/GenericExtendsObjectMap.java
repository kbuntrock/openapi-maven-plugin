package io.github.kbuntrock.resources.dto.genericity.extendsMap;

import java.util.HashMap;
import java.util.List;


public class GenericExtendsObjectMap<T> extends HashMap<String, Object> {

	public GenericExtendsObjectMap<T> rows(List<T> rows) {
		this.put("rows", rows);
		return this;
	}

	public <G> GenericExtendsObjectMap<T> lines(List<G> lines) {
		this.put("lines", lines);
		return this;
	}
}
