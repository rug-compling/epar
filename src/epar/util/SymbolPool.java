package epar.util;

import java.util.HashMap;
import java.util.Map;

public class SymbolPool {

    private static final Map<String, Integer> stringToID = new HashMap<>();

    private static final Map<Integer, String> idToString = new HashMap<>();
    
    public static final short NONE = 0;

    private static int nextID = 1;

    public static int getID(String string) {
        if (!stringToID.containsKey(string)) {
            if (nextID == 0) {
                throw new RuntimeException("Symbol pool overflow!");
            }
            
            stringToID.put(string, nextID);
            idToString.put(nextID, string);
            return nextID++;
        }

        return stringToID.get(string);
    }

    public static String getString(int id) {
        return idToString.get(id);
    }

}
