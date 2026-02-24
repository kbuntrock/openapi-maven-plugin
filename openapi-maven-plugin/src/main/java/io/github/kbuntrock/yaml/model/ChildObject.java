package io.github.kbuntrock.yaml.model;

import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Flow;
import io.github.kbuntrock.reflection.BeanDefinition;

public class ChildObject {

	private final BeanDefinition beanDefinition;
	private final DataObject dataObject;

	public ChildObject(BeanDefinition beanDefinition, DataObject dataObject) {
		this.beanDefinition = beanDefinition;
		this.dataObject = dataObject;
	}

	public BeanDefinition getBeanDefinition() {
		return beanDefinition;
	}

	public DataObject getDataObject() {
		return dataObject;
	}

	public String getName() {
		return beanDefinition.getName();
	}

	public void mergeWithFlow(final Flow flow, final BeanDefinition beanDefinition) {
		if(dataObject.getFlow() == Flow.INPUT_OUTPUT || flow == dataObject.getFlow()) {
			// The child has already been explored / merged via another branch.
			return;
		}
		dataObject.setFlow(Flow.INPUT_OUTPUT);
		beanDefinition.merge(beanDefinition);
	}
}
