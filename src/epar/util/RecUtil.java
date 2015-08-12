package epar.util;

import java.util.Iterator;

public class RecUtil {

	public static void expect(String expectedToken, Iterator<String> it) {
		expect(expectedToken, it.next());
	}

	public static void expect(String expectedToken, String token) {
		if (!expectedToken.equals(token)) {
			throw new RuntimeException("Unexpected token: " + token
					+ ", expected " + expectedToken);
		}
	}

}
