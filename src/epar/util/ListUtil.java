package epar.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {

	public static <T> List<T> listFromIterable(Iterable<T> elements) {
		List<T> list = new ArrayList<T>();
		
		for (T element : elements) {
			list.add(element);
		}
		
		return list;
	}

}
