package epar.feature;

public class Feature3 extends Feature {
	
	private final String template;
	
	private final String a;
	
	private final String b;
	
	private final String c;

	public Feature3(String template, String a, String b, String c) {
		this.template = template;
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	@Override
	public String toString() {
		return template + " " + a + " " + b + " " + c + " ";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		result = prime * result + ((template == null) ? 0 : template.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feature3 other = (Feature3) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (b == null) {
			if (other.b != null)
				return false;
		} else if (!b.equals(other.b))
			return false;
		if (c == null) {
			if (other.c != null)
				return false;
		} else if (!c.equals(other.c))
			return false;
		if (template == null) {
			if (other.template != null)
				return false;
		} else if (!template.equals(other.template))
			return false;
		return true;
	}

}
