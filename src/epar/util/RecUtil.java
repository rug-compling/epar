package epar.util;

import java.util.Iterator;
import java.util.Scanner;

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

    public static void expectEnd(Scanner scanner) {
        if (scanner.hasNext()) {
            throw new RuntimeException("Unexpected token " + scanner.next()
                    + " beyond end of parsed expression");
        }
    }

}
