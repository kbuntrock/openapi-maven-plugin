package io.github.kbuntrock.resources.dto.genericity.extendsMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public class GenericExtendsList<T> extends ArrayList<UUID> {

	public GenericExtendsList<T> doSomething(List<T> rows) {
		return this;
	}
}
