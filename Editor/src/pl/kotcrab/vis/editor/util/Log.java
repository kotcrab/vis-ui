/**
 * Copyright 2014 Pawel Pastuszak
 * 
 * This file is part of VisEditor.
 * 
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

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
