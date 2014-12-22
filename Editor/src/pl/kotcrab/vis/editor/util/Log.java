/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package pl.kotcrab.vis.editor.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Log utility, log events are redirected to listener and printed to standard output
 * @author Pawel Pastuszak */
public class Log {
	private static boolean debug = false;
	private static boolean deubgInterrupted = false;

	private static LoggerListener listener = new DefaultLogListener();

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm] ");

	public static boolean isDebug () {
		return debug;
	}

	public static void setDebug (boolean debug) {
		Log.debug = debug;
	}

	public static boolean isDeubgInterrupted () {
		return deubgInterrupted;
	}

	public static void setDeubgInterrupted (boolean deubgInterrupted) {
		Log.deubgInterrupted = deubgInterrupted;
	}

	public static void init () {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException (Thread t, Throwable e) {
				e.printStackTrace();
				listener.exception(ExceptionUtils.getStackTrace(e));
			}
		});
	}

	public static void exception (Exception e) {
		e.printStackTrace();
		listener.exception(ExceptionUtils.getStackTrace(e));
	}

	public static void interruptedEx (InterruptedException e) {
		if (deubgInterrupted) exception(e);
	}

	// ============STANDARD LOGGING============

	public static void l (String msg) {
		println(msg);
	}

	public static void w (String msg) {
		println("WARNING: " + msg);
	}

	public static void debug (String msg) {
		if (debug) println("DEBUG: " + msg);
	}

	public static void err (String msg) {
		printlnErr("ERROR: " + msg);
	}

	// ============LOGGING WITHOUT NEW LINE============

	public static void lNnl (String msg) {
		print(msg);
	}

	public static void wNnl (String msg) {
		print("WARNING: " + msg);
	}

	public static void debugNnl (String msg) {
		if (debug) print("DEBUG: " + msg);
	}

	public static void eNnl (String msg) {
		printErr("ERROR: " + msg);
	}

	// ============LOGGING WITH TAG============

	public static void l (String tag, String msg) {
		println("[" + tag + "] " + msg);
	}

	public static void w (String tag, String msg) {
		println("[" + tag + "] " + "WARNING: " + msg);
	}

	public static void debug (String tag, String msg) {
		if (debug) println("[" + tag + "] " + "DEBUG: " + msg);
	}

	public static void err (String tag, String msg) {
		printlnErr("[" + tag + "] " + "ERROR: " + msg);
	}

	// ==========================================

	private static void print (String msg) {
		msg = getTimestamp() + msg;
		listener.log(msg);
		System.out.print(msg);
	}

	private static void println (String msg) {
		msg = getTimestamp() + msg;
		listener.log(msg);
		System.out.println(msg);
	}

	private static void printErr (String msg) {
		msg = getTimestamp() + msg;
		listener.err(msg);
		System.err.print(msg);
	}

	private static void printlnErr (String msg) {
		msg = getTimestamp() + msg;
		listener.err(msg);
		System.err.println(msg);
	}

	public static void setListener (LoggerListener listener) {
		Log.listener = listener;
	}

	private static String getTimestamp () {
		return dateFormat.format(new Date());
	}
}

class DefaultLogListener implements LoggerListener {
	@Override
	public void log (String msg) {
	}

	@Override
	public void err (String msg) {
	}

	@Override
	public void exception (String stacktrace) {
	}
}
