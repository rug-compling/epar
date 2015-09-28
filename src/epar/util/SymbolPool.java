package epar.util;

import java.util.HashMap;
import java.util.Map;

public class SymbolPool {

    private static final Map<String, Short> stringToID = new HashMap<>();

    private static final Map<Short, String> idToString = new HashMap<>();
    
    public static final short NONE = 0;

    private static short nextID = 1;

    public static short getID(String string) {
        if (!stringToID.containsKey(string)) {
            stringToID.put(string, nextID);
            idToString.put(nextID, string);
            return nextID++;
        }

        return stringToID.get(string);
    }

    public static String getString(short id) {
        return idToString.get(id);
    }
    
    public static short straighten(short originalCategory) {
        return SymbolPool.getID(SymbolPool.getString(originalCategory).replace('\\', '/'));
    }

}
