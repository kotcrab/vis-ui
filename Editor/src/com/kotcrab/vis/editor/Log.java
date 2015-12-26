/*
 * Copyright 2014-2015 See AUTHORS file.
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
 */

package com.kotcrab.vis.editor;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.kotcrab.vis.editor.event.ExceptionEvent;
import com.kotcrab.vis.editor.event.VisEventBus;
import com.kotcrab.vis.editor.util.ExceptionUtils;
import com.kotcrab.vis.editor.util.PublicApi;
import com.kotcrab.vis.editor.util.vis.CrashReporter;
import com.kotcrab.vis.ui.widget.file.FileUtils;

import javax.swing.JOptionPane;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * VisEditor log utility. Log events are printed to standard output and written to file.
 * @author Kotcrab
 */
@PublicApi
public class Log {
	private static boolean initialized;

	private static final boolean DEBUG_INTERRUPTED = false;

	/*** Highest log level possible, all messages will be logged. */
	public static final int TRACE = 6;
	/*** Only debug messages will be logged. Default VisEditor log level. */
	public static final int DEBUG = 5;
	/*** Only info messages will be logged. */
	public static final int INFO = 4;
	/*** Only warning messages will be logged. */
	public static final int WARN = 3;
	/*** Only error messages will be logged. Those messages are printed to System.err output. */
	public static final int ERROR = 2;
	/*** Only fatal error messages will be logged. Similar to {@link #ERROR} messages those are printed to System.err output. */
	public static final int FATAL = 1;
	/*** Lowest log level possible, all logging even fatal errors is disabled. */
	public static final int OFF = 0;

	/** Current log level set. */
	static int LOG_LEVEL = DEBUG;

	private static File logFile;
	private static PrintWriter logFileWriter;
	private static SimpleDateFormat msgDateFormat = new SimpleDateFormat("[HH:mm]");

	/** Initializes logging facility, called once by VisEditor. */
	public static void init () {
		if (initialized) throw new IllegalStateException("Log cannot be initialized twice!");
		initialized = true;

		System.setErr(new ErrorStreamInterceptor(System.err));

		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			Log.exception(e);
			Log.fatal("Uncaught exception occurred, error report will be saved");

			logFileWriter.flush();

			boolean openGlCrash = false;
			if (e.getMessage() != null && e.getMessage().contains("No OpenGL context found in the current thread."))
				openGlCrash = true;

			if (App.eventBus != null) App.eventBus.post(new ExceptionEvent(e, true));

			try {
				new CrashReporter(logFile).processReport();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			if (openGlCrash) {
				String error = "An unexpected error occurred and editor had to shutdown, please check log: " + logFile.getParent();
				if (MathUtils.randomBoolean(0.001f)) error += " ¯\\_(ツ)_/¯";
				JOptionPane.showMessageDialog(null, error);

				System.exit(-3);
			}
		});

		prepareLogFile();
	}

	/**
	 * Disposes logging facility permanently, called once by VisEditor. After calling this method you must not call
	 * any logging methods.
	 */
	public static void dispose () {
		info("Exiting");
		logFileWriter.close();
	}

	/** @return {@link FileHandle} to log file that is currently being used by this logging facility. */
	public static FileHandle getLogFile () {
		return FileUtils.toFileHandle(logFile);
	}

	/** @return current log level set. See public {@link Log} fields for possible log levels. */
	public static int getLogLevel () {
		return LOG_LEVEL;
	}

	private static void prepareLogFile () {
		File logDirectory = new File(App.APP_FOLDER_PATH, "logs");
		logDirectory.mkdir();

		SimpleDateFormat fileDateFormat = new SimpleDateFormat("yy-MM-dd");
		String fileName = fileDateFormat.format(new Date());
		String fileNameYearMonth = fileName.substring(0, 5);

		File[] files = logDirectory.listFiles();

		if(files != null) {
			//we are deleting files that are not from current month
			for (File f : files) {
				if (f.getName().contains(fileNameYearMonth) == false) f.delete();
			}
		}

		try {
			logFile = new File(logDirectory, "viseditor " + fileName + ".txt");
			logFile.createNewFile();
			logFileWriter = new PrintWriter(new FileWriter(logFile, true));
		} catch (IOException e) {
			exception(e);
		}

		logFileWriter.println();
		info("VisEditor " + App.VERSION + " (version code: " + App.VERSION_CODE + ")");
		info("Started: " + fileName);
	}

	// Standard log

	/** Logs message with trace log level. */
	public static void trace (String msg) {
		if (LOG_LEVEL >= TRACE) print("[Trace] " + msg);
	}

	/** Logs message with debug log level. */
	public static void debug (String msg) {
		if (LOG_LEVEL >= DEBUG) print("[Debug] " + msg);
	}

	/** Logs message with info log level. */
	public static void info (String msg) {
		if (LOG_LEVEL >= INFO) print("[Info] " + msg);
	}

	/** Logs message with warning log level. */
	public static void warn (String msg) {
		if (LOG_LEVEL >= WARN) print("[Warning] " + msg);
	}

	/** Logs message with error log level. */
	public static void error (String msg) {
		if (LOG_LEVEL >= ERROR) printErr("[Error] " + msg);
	}

	/** Logs message with fatal log level. */
	public static void fatal (String msg) {
		if (LOG_LEVEL >= FATAL) printErr("[Fatal] " + msg);
	}

	//Log with tag

	/** Logs message with trace log level and given tag (usually caller class name) */
	public static void trace (String tag, String msg) {
		if (LOG_LEVEL >= TRACE) print("[Trace][" + tag + "] " + msg);
	}

	/** Logs message with debug log level and given tag (usually caller class name) */
	public static void debug (String tag, String msg) {
		if (LOG_LEVEL >= DEBUG) print("[Debug][" + tag + "] " + msg);
	}

	/** Logs message with info log level and given tag (usually caller class name) */
	public static void info (String tag, String msg) {
		if (LOG_LEVEL >= INFO) print("[Info][" + tag + "] " + msg);
	}

	/** Logs message with warning log level and given tag (usually caller class name) */
	public static void warn (String tag, String msg) {
		if (LOG_LEVEL >= WARN) print("[Warning][" + tag + "] " + msg);
	}

	/** Logs message with error log level and given tag (usually caller class name) */
	public static void error (String tag, String msg) {
		if (LOG_LEVEL >= ERROR) printErr("[Error][" + tag + "] " + msg);
	}

	/** Logs message with fatal log level and given tag (usually caller class name) */
	public static void fatal (String tag, String msg) {
		if (LOG_LEVEL >= FATAL) printErr("[Fatal][" + tag + "] " + msg);
	}

	/**
	 * Logs exception stacktrace and posts {@link ExceptionEvent} to main VisEditor {@link VisEventBus}. This
	 * should be always preferred to calling {@link Throwable#printStackTrace()} directly. Exceptions are logged
	 * to {@link #FATAL} log level.
	 */
	public static void exception (Throwable throwable) {
		if (throwable instanceof InterruptedException && DEBUG_INTERRUPTED == false) return;

		if (App.eventBus != null) App.eventBus.post(new ExceptionEvent(throwable, false));
		String stack = ExceptionUtils.getStackTrace(throwable);
		fatal(stack);
	}

	private static void print (String msg) {
		msg = getTimestamp() + msg;
		logFileWriter.println(msg);
		System.out.println(msg);
	}

	private static void printErr (String msg) {
		msg = getTimestamp() + msg;
		System.err.println(msg);
	}

	private static String getTimestamp () {
		return msgDateFormat.format(new Date());
	}

	private static class ErrorStreamInterceptor extends PrintStream {
		public ErrorStreamInterceptor (OutputStream out) {
			super(out, true);
		}

		@Override
		public void print (String s) {
			super.print(s);
			if (logFileWriter != null) logFileWriter.println(s);
		}
	}
}


