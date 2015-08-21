package epar.util;

import java.util.HashMap;
import java.util.Map;

public class SymbolPool {

    private static final Map<String, Short> map = new HashMap<>();
    
    public static final short NONE = 0;

    private static short nextID = 1;

    public static Short get(String string) {
        if (!map.containsKey(string)) {
            map.put(string, nextID);
            return nextID++;
        }

        return map.get(string);
    }

}
