package io.github.kbuntrock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Keep track of tags and explore them to find every DataObject which should end up in the components/schemas section
 */
public class TagLibrary {

	public static final String METHOD_GET_PREFIX = "get";
	public static final int METHOD_GET_PREFIX_SIZE = METHOD_GET_PREFIX.length();
	public static final String METHOD_IS_PREFIX = "is";
	public static final int METHOD_IS_PREFIX_SIZE = METHOD_IS_PREFIX.length();

	private final List<Tag> tags = new ArrayList<>();
	private final Set<DataObject> schemaObjects = new HashSet<>();
	private final Set<String> exploredSignatures = new HashSet<>();

	public void addTag(final Tag tag) throws MojoFailureException {
		tags.add(tag);
		exploreTagObjects(tag);
		System.currentTimeMillis();
	}

	/**
	 * Analyse all endpoints of a tag (aka a rest controller) to extract all objects which will be written in the schema section : parameters or response.
	 *
	 * @param tag a rest controller
	 */
	private void exploreTagObjects(final Tag tag) {
		for(final Endpoint endpoint : tag.getEndpoints()) {
			if(endpoint.getResponseObject() != null) {
				exploreDataObject(endpoint.getResponseObject());
			}

			for(final ParameterObject parameterObject : endpoint.getParameters()) {
				exploreDataObject(parameterObject);
			}
		}
	}

	private void exploreDataObject(final DataObject dataObject) {
		// We don't want to explore several times the same type of objects
		if(!exploredSignatures.add(dataObject.getSignature())) {
			return;
		}
		// Generically typed objects are almost never written in the schema section (only when a recursive
		if(dataObject.isReferenceObject()) {
			if(schemaObjects.add(dataObject)) {
				inspectObject(dataObject);
			}
		} else if(dataObject.isGenericallyTyped()) {
//            // Eventually analyse instead the generic types
			if(dataObject.getGenericNameToTypeMap() != null) {
				for(final Map.Entry<String, Type> entry : dataObject.getGenericNameToTypeMap().entrySet()) {
					final DataObject genericObject = new DataObject(dataObject.getContextualType(entry.getValue()));
					exploreDataObject(genericObject);
				}
			}
			inspectObject(dataObject);
		} else if(dataObject.isJavaArray()) {
			exploreDataObject(dataObject.getArrayItemDataObject());
		}
	}

	private void inspectObject(final DataObject explored) {
		if(explored.getJavaClass().isEnum()) {
			return;
		}
		final List<Field> fields = ReflectionsUtils.getAllNonStaticFields(new ArrayList<>(), explored.getJavaClass());
		for(final Field field : fields) {
			if(field.isAnnotationPresent(JsonIgnore.class)) {
				// Field is tagged ignore. No need to document it.
				continue;
			}
			final DataObject dataObject = new DataObject(explored.getContextualType(field.getGenericType()));
			exploreDataObject(dataObject);
		}
		if(explored.getJavaClass().isInterface()) {
			final Method[] methods = explored.getJavaClass().getMethods();
			for(final Method method : methods) {

				if(method.getParameters().length == 0
					&& ((method.getName().startsWith(METHOD_GET_PREFIX) && method.getName().length() != METHOD_GET_PREFIX_SIZE) ||
					(method.getName().startsWith(METHOD_IS_PREFIX)) && method.getName().length() != METHOD_IS_PREFIX_SIZE)) {
					final DataObject dataObject = new DataObject(explored.getContextualType(method.getGenericReturnType()));
					exploreDataObject(dataObject);
				}
			}
		}

	}

	public List<Tag> getTags() {
		return tags;
	}

	public Set<DataObject> getSchemaObjects() {
		return schemaObjects;
	}
}
