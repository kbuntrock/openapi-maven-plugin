package io.github.kbuntrock.yaml.model;

import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.reflection.BeanDefinition;

public class ChildObject {

	private BeanDefinition propertyDefinition;
	private DataObject dataObject;

	public ChildObject(BeanDefinition propertyDefinition, DataObject dataObject) {
		this.propertyDefinition = propertyDefinition;
		this.dataObject = dataObject;
	}

	public BeanDefinition getPropertyDefinition() {
		return propertyDefinition;
	}

	public DataObject getDataObject() {
		return dataObject;
	}

	public String getName() {
		return propertyDefinition.getName();
	}
}
