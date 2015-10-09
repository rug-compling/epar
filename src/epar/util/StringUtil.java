package epar.util;

import java.util.Iterator;

public class StringUtil {

    public static String join(Iterable<?> pieces, String glue) {
        Iterator<?> it = pieces.iterator();

        if (!it.hasNext()) {
            return "";
        }

        StringBuilder builder = new StringBuilder(representAsString(it.next()));

        while (it.hasNext()) {
            builder.append(glue);
            builder.append(representAsString(it.next()));
        }

        return builder.toString();
    }

    private static String representAsString(Object object) {
        if (object == null) {
            return null;
        }

        return object.toString();
    }

}
