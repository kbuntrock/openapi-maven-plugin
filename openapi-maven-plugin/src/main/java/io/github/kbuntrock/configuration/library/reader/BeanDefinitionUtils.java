package io.github.kbuntrock.configuration.library.reader;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import io.github.kbuntrock.context.ApiContext;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Flow;
import io.github.kbuntrock.reflection.BeanDefinition;
import io.github.kbuntrock.yaml.model.ChildObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BeanDefinitionUtils {

	private static List<BeanDefinition> getPropertyDefinitions(ObjectMapper mapper, DataObject dataObject) {
		Class<?> clazz = dataObject.getJavaClass();
		if(dataObject.getFlow() == Flow.INPUT) {
			DeserializationConfig deserializationConfig = mapper.getDeserializationConfig();
			JavaType type = deserializationConfig.constructType(clazz);
			BeanDescription deserializationDescription = deserializationConfig.introspect(type);
			return deserializationDescription.findProperties().stream()
				.map(p -> new BeanDefinition(p, null, dataObject.getFlow()))
				.filter(BeanDefinition::couldDeserialize)
				.collect(Collectors.toList());
		} else if(dataObject.getFlow() == Flow.OUTPUT) {
			SerializationConfig serializationConfig = mapper.getSerializationConfig();
			JavaType type = serializationConfig.constructType(clazz);
			BeanDescription serializationDescription = serializationConfig.introspect(type);
			return serializationDescription.findProperties().stream().map(p -> new BeanDefinition(p, null, dataObject.getFlow()))
				.filter(BeanDefinition::couldSerialize)
				.collect(Collectors.toList());
		}

		// Mix input and output data
		SerializationConfig serializationConfig = mapper.getSerializationConfig();
		JavaType type = serializationConfig.constructType(clazz);
		BeanDescription serializationDescription = serializationConfig.introspect(type);

		DeserializationConfig deserializationConfig = mapper.getDeserializationConfig();
		BeanDescription deserializationDescription = deserializationConfig.introspect(type);

		Map<String, BeanPropertyDefinition> deserialMap = deserializationDescription.findProperties().stream()
			.collect(Collectors.toMap(BeanPropertyDefinition::getName, p -> p, (a, b) -> a, LinkedHashMap::new));

		List<BeanDefinition> list = new ArrayList<>();
		for(BeanPropertyDefinition beanDef : serializationDescription.findProperties()) {
			list.add(new BeanDefinition(beanDef, deserialMap.remove(beanDef.getName()), Flow.INPUT_OUTPUT));
		}
		for(BeanPropertyDefinition beanDef : deserialMap.values()) {
			list.add(new BeanDefinition(beanDef, null, Flow.INPUT_OUTPUT));
		}
		return list;
	}

	public static List<ChildObject> getPropertyObjectsToDocument(DataObject explored, final ApiContext context) {
		List<BeanDefinition> propertyDefinitions = getPropertyDefinitions(context.getSchemaObjectMapper(),
			explored);
		List<ChildObject> childObjects = new ArrayList<>();

		for(BeanDefinition propertyDefinition : propertyDefinitions) {
			Type genericType;
			if(propertyDefinition.hasField()) {
				genericType = propertyDefinition.getField().getAnnotated().getGenericType();
			} else if(propertyDefinition.hasGetter()) {
				genericType = propertyDefinition.getGetter().getAnnotated().getGenericReturnType();
			} else if(propertyDefinition.hasSetter()
				&& propertyDefinition.getSetter().getAnnotated().getGenericParameterTypes().length == 1) {
				genericType = propertyDefinition.getSetter().getAnnotated().getGenericParameterTypes()[0];
			} else {
				continue;
			}
			childObjects.add(new ChildObject(propertyDefinition,
				new DataObject(explored.getContextualType(genericType), context, explored.getFlow())));
		}
		return childObjects;
	}
}
