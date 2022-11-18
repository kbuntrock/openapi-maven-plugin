package io.github.kbuntrock.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kévin Buntrock
 */
public final class EnumConfigHolder {

	private static final Map<String, String> map = new HashMap<>();

	public static void storeConfig(final List<EnumConfig> enumConfigList) {
		for(final EnumConfig config : enumConfigList) {
			map.put(config.getCanonicalName(), config.getValueField());
		}
	}

	public static String getValueFieldForEnum(final String canonicalName) {
		return map.get(canonicalName);
	}

}
