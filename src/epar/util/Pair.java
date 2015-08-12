package epar.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Pair<T, U> {
	
	public final T fst;
	
	public final U snd;
	
	public Pair(T fst, U snd) {
		this.fst = fst;
		this.snd = snd;
	}
	
	public static <T, U> List<Pair<T, U>> zip(Iterable<T> fsts, Iterable<U> snds) {
		List<Pair<T, U>> result = new ArrayList<Pair<T, U>>();
		Iterator<U> sndsIt = snds.iterator();
		
		for (T fst : fsts) {
			if (!sndsIt.hasNext()) {
				break;
			}
			
			result.add(new Pair<T, U>(fst, sndsIt.next()));
		}
		
		return result;
	}

}
