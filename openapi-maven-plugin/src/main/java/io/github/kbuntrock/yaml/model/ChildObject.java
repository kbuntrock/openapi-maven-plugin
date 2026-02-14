package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import io.github.kbuntrock.model.DataObject;

public class ChildObject {

	private BeanPropertyDefinition propertyDefinition;
	private DataObject dataObject;

	public ChildObject(BeanPropertyDefinition propertyDefinition, DataObject dataObject) {
		this.propertyDefinition = propertyDefinition;
		this.dataObject = dataObject;
	}

	public BeanPropertyDefinition getPropertyDefinition() {
		return propertyDefinition;
	}

	public DataObject getDataObject() {
		return dataObject;
	}

	public String getName() {
		return propertyDefinition.getName();
	}
}
