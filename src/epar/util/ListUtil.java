package epar.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {

	public static <T> List<T> listFromIterable(Iterable<T> elements) {
		List<T> list = new ArrayList<>();
		
		for (T element : elements) {
			list.add(element);
		}
		
		return list;
	}
        
        public static boolean isContiguous(List<Integer> intList) {
            for (int i = 1; i < intList.size(); i++) {
                if (intList.get(i) != intList.get(i - 1) + 1) {
                    return false;
                }
            }
            
            return true;
        }

}
