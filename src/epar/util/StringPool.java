package epar.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Simplistic static string deduplicator. Do not put anything in here that you
 * don't want to stay around for the rest of the life of the JVM.
 * 
 * @author ke293
 * 
 */
public class StringPool {

	private static final Map<String, String> map = new HashMap<String, String>();

	public static String get(String string) {
		if (!map.containsKey(string)) {
			map.put(string, string);
			return string;
		}

		return map.get(string);
	}

}
