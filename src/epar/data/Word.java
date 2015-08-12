package epar.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import epar.util.StringPool;

public class Word {
	
	public final String form;
	
	public final String pos;
	
	public final List<String> categories;
	
	public Word(String form, String pos, List<String> categories) {
		this.form = form;
		this.pos = pos;
		this.categories = categories;
	}
	
	public static Word read(String line) {
		Scanner scanner = new Scanner(line);
		String form = StringPool.get(scanner.next());
		String pos = StringPool.get(scanner.next());
		List<String> categories = new ArrayList<String>();
		
		while (scanner.hasNext()) {
			categories.add(StringPool.get(scanner.next()));
		}
		
		scanner.close();
		return new Word(form, pos, categories);
	}

}
