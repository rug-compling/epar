package epar.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import epar.util.RecUtil;
import epar.util.StringPool;

public class BinaryRule {
	
	public static enum HeadPosition {
		LEFT, RIGHT;
		
		@Override
		public String toString() {
			if (this == LEFT) {
				return "l";
			} else {
				return "r";
			}
		}
	}

	public final String leftChildCategory;

	public final String rightChildCategory;

	public final String parentCategory;

	public final BinaryRule.HeadPosition headPosition;

	public BinaryRule(String leftChildCategory, String rightChildCategory,
			String parentCategory, BinaryRule.HeadPosition headPosition) {
		this.leftChildCategory = leftChildCategory;
		this.rightChildCategory = rightChildCategory;
		this.parentCategory = parentCategory;
		this.headPosition = headPosition;
	}

	public static List<BinaryRule> read(String line) {
		List<BinaryRule> rules = new ArrayList<BinaryRule>();
		Scanner scanner = new Scanner(line);
		String leftChildCategory = StringPool.get(scanner.next());
		RecUtil.expect(",", scanner);
		String rightChildCategory = StringPool.get(scanner.next());
		RecUtil.expect(":", scanner);
		RecUtil.expect("[", scanner);

		while (true) {
			RecUtil.expect("REDUCE", scanner);
			RecUtil.expect("BINARY", scanner);
			String head = scanner.next();
			HeadPosition headPosition;

			if ("LEFT".equals(head)) {
				headPosition = HeadPosition.LEFT;
			} else if ("RIGHT".equals(head)) {
				headPosition = HeadPosition.RIGHT;
			} else {
				scanner.close();
				throw new RuntimeException("Invalid head position indicator: "
						+ head);
			}

			String parentCategory = StringPool.get(scanner.next());
			rules.add(new BinaryRule(leftChildCategory, rightChildCategory,
					parentCategory, headPosition));

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