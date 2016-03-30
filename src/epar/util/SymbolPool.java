package epar.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolPool {

    private static final Map<String, Integer> STRING_TO_ID = new HashMap<>();

    private static final Map<Integer, String> ID_TO_STRING = new HashMap<>();
    
    public static final int NONE = 0;

    private static int nextID = 1;

    public static int getID(String string) {
        if (!STRING_TO_ID.containsKey(string)) {
            if (nextID == 0) {
                throw new RuntimeException("Symbol pool overflow!");
            }
            
            STRING_TO_ID.put(string, nextID);
            ID_TO_STRING.put(nextID, string);
            return nextID++;
        }

        return STRING_TO_ID.get(string);
    }

    public static String getString(int id) {
        return ID_TO_STRING.get(id);
    }
    
    public static int join(List<Integer> ids, String glue) {
        List<String> symbols = new ArrayList<>();
        return getID(StringUtil.join(symbols, glue));
    }

}
