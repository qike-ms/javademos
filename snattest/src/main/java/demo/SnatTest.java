package demo;

import java.io.*;
import java.lang.InterruptedException;
import java.lang.Thread;
import java.net.*;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;


public class SnatTest {
	private final static Logger LOGGER = Logger.getGlobal();

	private final static int TOTALCON = 2000;
	public static void main(final String[] args) throws java.lang.InterruptedException {
    SetupLogging();

		OutputStream[] outputs = new OutputStream[TOTALCON];
		byte[] data = "hi".getBytes();

		for (int i = 0; i < TOTALCON; i++) {
			try (Socket socket = new Socket("216.58.193.78", 80)) {
					final OutputStream output = socket.getOutputStream();
					outputs[i] = output;

					LOGGER.info(i + "th socket opened." );
					output.write(data);
					LOGGER.info(i + "th socket wrote." );
			} catch (final UnknownHostException ex) {
					LOGGER.warning(i + " : " + "Server not found: " + ex.getMessage());
			} catch (final IOException ex) {
					LOGGER.warning(i + " : " + "I/O error: " + ex.getMessage());
			}
		}

		while(true) {
			Thread.sleep(10000);
			for (int i = 0; i < TOTALCON; i++) {
				try {
						outputs[i].write(data);
						LOGGER.info(i + "th socket wrote again." );
				} catch (final UnknownHostException ex) {
						LOGGER.warning(i + " : " + "Server not found: " + ex.getMessage());
				} catch (final IOException ex) {
						LOGGER.warning(i + " : " + "I/O error: " + ex.getMessage());
				}
			}
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
