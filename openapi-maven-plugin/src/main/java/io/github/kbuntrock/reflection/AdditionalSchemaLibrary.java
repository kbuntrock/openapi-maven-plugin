package io.github.kbuntrock.reflection;

import io.github.kbuntrock.model.DataObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Used to stored dataObject which we initially didn't want to save in the schema section, but we are forced to do it
 * in order to handle recursivity (typically generic recursive objects)
 *
 * @author Kevin Buntrock
 */
public final class AdditionalSchemaLibrary {

	private final Map<String, DataObject> map = new LinkedHashMap<>();

	/**
	 * Register a {@link DataObject} by its signature for inclusion in the schema section.
	 *
	 * @param signature
	 *            unique signature
	 * @param dataObject
	 *            the associated data object
	 */
	public void addDataObject(final String signature, final DataObject dataObject) {
		map.put(signature, dataObject);
	}

	/**
	 * Access all registered additional schema entries in insertion order.
	 *
	 * @return a map of signature to {@link DataObject}
	 */
	public Map<String, DataObject> getMap() {
		return map;
	}
}
