package phs.media_server;

import java.io.IOException;
import java.net.ServerSocket;

import junit.framework.Assert;

public class Logger {

	/**
	 * Code modes:
	 * 	- DEV: enable assert to crash app when something goes wrong, enable logs
	 * 	- TEST: disable assert, enable logs
	 * 	- PRODUCT: disable assert & logs
	 */
	public enum CodeModes {DEV,TEST,PRODUCT};
	static public final CodeModes CurrentCodeMode = CodeModes.DEV;
	
	
	private static final String TAG = "PHS_MEDIA_SERVER";
	
	public static void verifyTrue(boolean b) {
		if (CurrentCodeMode == CodeModes.DEV )
			Assert.assertTrue(b);
		else if ( CurrentCodeMode == CodeModes.TEST ) {
			logMessage("Assert false");
		}
	}
	public static void logErrorTrace() {
		System.err.println("TODO: error trace is logged");
	}

	public static void logInfo(String message) {
		logMessage(message);
	}

	private static void logMessage(String message) {
		if ( CurrentCodeMode != CodeModes.PRODUCT) {
			StackTraceElement elm = Thread.currentThread().getStackTrace()[3];
			String location = extractLocation(elm);
			System.out.println(TAG+": " + message + " == Location: " + location );
		}
	}

	public static void logError(String message) {
		if ( CurrentCodeMode != CodeModes.PRODUCT) {
			StackTraceElement elm = Thread.currentThread().getStackTrace()[3];
			String location = extractLocation(elm);
			System.err.println(TAG+": " + message + " == Location: " + location );
		}
		
	}
	
	public static void logError(Exception e) {
		logError(e.getClass().toString() + ":" + e.getMessage());
	}

	private static String extractLocation(StackTraceElement elm) {
		return elm.getClassName()+"."+elm.getMethodName()+" at line: " + elm.getLineNumber();
	}
	
	private static final int MIN_PORT = 50000;
	private static final int MAX_PORT = 65535;
	/**
	 * Find the available port in range [MIN_PORT,MAX_PORT]
	 * @return
	 */
	public static int findFreePort() {
		for(int port = MIN_PORT ; port <= MAX_PORT ; port++) {
			try {
				ServerSocket serverSocket = new ServerSocket(port);
				serverSocket.close();
				return port;
			} catch (IOException e) {
				Logger.logInfo("Port " + port + " is reserved !");
			}
		}
		return -1;
	}



}