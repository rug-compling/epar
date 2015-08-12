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

}
