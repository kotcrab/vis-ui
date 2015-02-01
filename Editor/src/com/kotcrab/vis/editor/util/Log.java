/*
 * Copyright 2014-2015 Pawel Pastuszak
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

package com.kotcrab.vis.editor.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log utility, log events are redirected to listener and printed to standard output
 * @author Pawel Pastuszak
 */
public class Log {
	public static final int OFF = 5;
	public static final int DEBUG = 4;
	public static final int INFO = 3;
	public static final int WARN = 2;
	public static final int ERROR = 1;
	public static final int FATAL = 0;

	private static int logLevel = INFO;

	private static boolean debugInterrupted = false;

	private static LoggerListener listener = new DefaultLogListener();
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm]");

	public static void init () {
		System.setErr(new Interceptor(System.err));

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException (Thread t, Throwable e) {
				Log.exception(e);
			}
		});
	}

	// Standard log

	public static void debug (String msg) {
		if (logLevel >= DEBUG) print("[Debug]" + msg);
	}

	public static void info (String msg) {
		if (logLevel >= INFO) print("[Info]" + msg);
	}

	public static void warn (String msg) {
		if (logLevel >= WARN) print("[Warn]" + msg);
	}

	public static void error (String msg) {
		if (logLevel >= ERROR) printErr("[Error]" + msg);
	}

	public static void fatal (String msg) {
		if (logLevel >= FATAL) printErr("[Fatal]" + msg);
	}

	//Log with tag

	public static void debug (String tag, String msg) {
		if (logLevel >= DEBUG) print("[Debug][" + tag + "] " + msg);
	}

	public static void info (String tag, String msg) {
		if (logLevel >= INFO) print("[Info][" + tag + "] " + msg);
	}

	public static void warn (String tag, String msg) {
		if (logLevel >= WARN) print("[Warn][" + tag + "] " + msg);
	}

	public static void error (String tag, String msg) {
		if (logLevel >= ERROR) printErr("[Error][" + tag + "] " + msg);
	}

	public static void fatal (String tag, String msg) {
		if (logLevel >= FATAL) printErr("[Fatal][" + tag + "] " + msg);
	}

	private static void print (String msg) {
		msg = getTimestamp() + msg;
		listener.log(msg);
		System.out.println(msg);
	}

	private static void printErr (String msg) {
		msg = getTimestamp() + msg;
		listener.err(msg);
		System.err.println(msg);
	}

	public static void exception (Throwable e) {
		if (e instanceof InterruptedException && debugInterrupted == false) return;

		e.printStackTrace();
		listener.exception(ExceptionUtils.getStackTrace(e));
	}


	private static String getTimestamp () {
		return dateFormat.format(new Date());
	}

	public static void setListener (LoggerListener listener) {
		Log.listener = listener;
	}

	public static int getLogLevel () {
		return logLevel;
	}

	public static void setLogLevel (int logLevel) {
		Log.logLevel = logLevel;
	}

	public static boolean isDebugInterrupted () {
		return debugInterrupted;
	}

	public static void setDebugInterrupted (boolean debugInterrupted) {
		Log.debugInterrupted = debugInterrupted;
	}

	private static class Interceptor extends PrintStream {
		public Interceptor (OutputStream out) {
			super(out, true);
		}

		@Override
		public void print (String s) {
			super.print(s);
		}
	}

	private static class DefaultLogListener implements LoggerListener {
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
}


