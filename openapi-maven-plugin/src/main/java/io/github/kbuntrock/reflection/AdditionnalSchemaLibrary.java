package io.github.kbuntrock.reflection;

import io.github.kbuntrock.model.DataObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MUST be reset between each api scan.
 * <p>
 * Used to stored dataObject which we initially didn't want to save in the schema section, but we are forced to do it
 * in order to handle recursivity (typically generic recursive objects)
 *
 * @author Kevin Buntrock
 */
public final class AdditionnalSchemaLibrary {

    private static Map<String, DataObject> map = new LinkedHashMap<>();

    public static void reset() {
        map.clear();
    }

    public static void addDataObject(final String signature, final DataObject dataObject) {
        map.put(signature, dataObject);
    }

    public static Map<String, DataObject> getMap() {
        return map;
    }
}
