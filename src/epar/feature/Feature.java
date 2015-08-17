package epar.feature;

import java.util.List;

public abstract class Feature {
	
	public static Feature fromParts(List<String> parts) {
		if (parts.size() == 2) {
			return new Feature1(parts.get(0), parts.get(1));
		} else if (parts.size() == 3) {
			return new Feature2(parts.get(0), parts.get(1), parts.get(2));
		} else if (parts.size() == 4) {
			return new Feature3(parts.get(0), parts.get(1), parts.get(2), parts.get(3));
		} else if (parts.size() == 5) {
			return new Feature4(parts.get(0), parts.get(1), parts.get(2), parts.get(3), parts.get(4));
		}
		
		throw new IllegalArgumentException();
	}

}
