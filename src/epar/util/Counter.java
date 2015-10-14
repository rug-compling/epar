package epar.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author p264360
 * @param <T>
 */
public class Counter<T> {

    private final Map<T, Double> map = new HashMap<>();

    private int total = 0;
    
    private double max = Double.NEGATIVE_INFINITY;
    
    public void add(T element) {
        add(element, 1.0);
    }

    public void add(T element, Double count) {
        total++;

        if (map.containsKey(element)) {
            map.put(element, map.get(element) + count);
        } else {
            map.put(element, count);
        }
        
        max = Math.max(max, map.get(element));
    }

    public double getCount(T element) {
        if (map.containsKey(element)) {
            return map.get(element);
        } else {
            return 0;
        }
    }

    public int getTotal() {
        return total;
    }
    
    public double getMaxCount() {
        return max;
    }

    public Set<Map.Entry<T, Double>> entrySet() {
        return map.entrySet();
    }

}
