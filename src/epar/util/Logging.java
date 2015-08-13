package epar.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging {
	
	private static final Level LEVEL = Level.FINE; 

	private static final Logger LOGGER = Logger.getLogger("epar");
	
	static {
		LOGGER.setUseParentHandlers(false);
		Handler handler = new ConsoleHandler();
		handler.setLevel(LEVEL);
		LOGGER.addHandler(handler);
	}

	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void fine(String message) {
		LOGGER.fine(message);
	}

}
