package demo;

import java.lang.InterruptedException;
import java.lang.Thread;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;

public class MemoryTest {
	private final static Logger LOGGER = Logger.getGlobal();
	public static void main(final String[] args) throws java.lang.InterruptedException {
    SetupLogging();

		final Vector<byte[]> v = new Vector<byte[]>();
		final int MB = 1024 * 1024;
		int step = 10;

		while (true) {
			final Runtime rt = Runtime.getRuntime();
			final int totalMemory = (int)(rt.totalMemory() / MB);
			final int freeMemory = (int)(rt.freeMemory() / MB);
			final int maxMemory = (int)(rt.maxMemory() / MB);

			if (maxMemory - totalMemory < 100) {
				if (freeMemory < 40) {
					LOGGER.fine("Release " + v.size() + " blocks.");
					v.clear();
				  step = 1;
				} else {
					step = 10;
				}
			} else {
				step = (int)((maxMemory - totalMemory) / 10);
				if (step > freeMemory) {
					step = freeMemory;
				}
			}

			LOGGER.fine("FREE = " + freeMemory + "MB; TOTAL = " + totalMemory + "MB; MAX = " + maxMemory + "MB; STEP = "
					+ step + " MB");
			final byte b[] = new byte[step * MB];
			v.add(b);
		}
	}

	private static void SetupLogging() {
		System.setProperty("java.util.logging.SimpleFormatter.format", 
            "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
		Level logLevel = Level.INFO;
    if (System.getProperties().containsKey("logLevel")) {
		  logLevel = Level.parse(System.getProperty("logLevel"));
		} else if (System.getenv("logLevel") != null) {
			logLevel = Level.parse(System.getenv("logLevel"));
		} else if (System.getenv("logLevel".toUpperCase()) != null) {
			logLevel = Level.parse(System.getenv("logLevel".toUpperCase()));
		}

		LOGGER.setLevel(logLevel);
		System.out.println("Log Level is set to: " + LOGGER.getLevel());
		System.out.println("JAVA_OPTS = " + System.getenv("JAVA_OPTS"));

		final ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(logLevel);
		LOGGER.addHandler(handler);
		LOGGER.setUseParentHandlers(false);		
	}
}
