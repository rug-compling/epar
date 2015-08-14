package epar.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import epar.util.RecUtil;
import epar.util.StringPool;

public class UnaryRule {

	public final String childCategory;

	public final String parentCategory;

	public UnaryRule(String childCategory, String parentCategory) {
		this.childCategory = childCategory;
		this.parentCategory = parentCategory;
	}

	public static List<UnaryRule> read(String line) {
		List<UnaryRule> rules = new ArrayList<UnaryRule>();
		Scanner scanner = new Scanner(line);
		String childCategory = StringPool.get(scanner.next());
		RecUtil.expect(":", scanner);
		RecUtil.expect("[", scanner);

		while (true) {
			RecUtil.expect("REDUCE", scanner);
			RecUtil.expect("UNARY", scanner);

			String parentCategory = StringPool.get(scanner.next());
			rules.add(new UnaryRule(childCategory, parentCategory));

			String token = scanner.next();

			if ("]".equals(token)) {
				break;
			} else {
				RecUtil.expect(",", token);
			}
		}

		scanner.close();
		return rules;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((childCategory == null) ? 0 : childCategory.hashCode());
		result = prime * result + ((parentCategory == null) ? 0 : parentCategory.hashCode());
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
		UnaryRule other = (UnaryRule) obj;
		if (childCategory == null) {
			if (other.childCategory != null)
				return false;
		} else if (!childCategory.equals(other.childCategory))
			return false;
		if (parentCategory == null) {
			if (other.parentCategory != null)
				return false;
		} else if (!parentCategory.equals(other.parentCategory))
			return false;
		return true;
	}

}
