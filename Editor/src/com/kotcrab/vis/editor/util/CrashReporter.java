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

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.App;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

public class CrashReporter {
	private static final String TAG = "CrashReporter";
	private static final String PATH = "http://apps.kotcrab.com/vis/crash/report.php";
	public static boolean reportSent;

	private StringBuilder crashReport;
	private File logFile;
	private String report;

	public CrashReporter (File logFile) throws IOException {
		this.logFile = logFile;
		this.crashReport = new StringBuilder();

		printHeader();
		printThreadInfo();
		printLog();

		report = crashReport.toString();
	}

	public void sendReport () throws IOException {
		if (App.ERROR_REPORTS) {

			//don't send multiple reports from one instance of application
			if (reportSent == false) {
				Log.info(TAG, "Sending crash report");
				HttpURLConnection connection = (HttpURLConnection) new URL(PATH + "?filename=" + logFile.getName()).openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				OutputStream os = connection.getOutputStream();

				os.write(report.getBytes());
				os.flush();
				os.close();

				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				String s;
				while ((s = in.readLine()) != null)
					Log.debug(TAG, "Server response: " + s);
				in.close();


				Log.info(TAG, "Crash report sent");
			}


			reportSent = true;
		} else
			Log.warn(TAG, "Application requested to send report but error reports are disabled, ignoring.");
	}

	private void printHeader () {
		println("--- VisEditor Crash Report ---");
		println("VisEditor " + App.VERSION);
		println("VersionCode: " + App.VERSION_CODE + " Snapshot: " + App.SNAPSHOT);
		println();

		println("Java: " + System.getProperty("java.version") + " " + System.getProperty("java.vendor"));
		println("Java VM: " + System.getProperty("java.vm.name"));
		println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch"));
		println();

	}

	private void printThreadInfo () {
		println("--- Threads ---");

		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

		for (Thread t : threadSet) {
			if (t.isDaemon()) {
				println("Skipping daemon thread: " + t.getName());
			} else {
				println("Thread: " + t.getName());
				for (StackTraceElement e : t.getStackTrace()) {
					crashReport.append("\t");
					println(e.toString());
				}
			}

			println();
		}

		println("---------------");
		println();
	}

	private void printLog () throws IOException {
		println("--- Log file (last 200 lines) ---");

		ReversedLinesFileReader reader = new ReversedLinesFileReader(logFile);
		Array<String> logLines = new Array<>();

		for (int i = 0; i < 200; i++) {
			String line = reader.readLine();
			if (line == null) break;
			logLines.add(line);
		}

		logLines.reverse();

		for (String s : logLines)
			println(s);

		println("---------------------------------");
		println();
	}

	private void println () {
		crashReport.append('\n');
	}

	private void println (String s) {
		crashReport.append(s);
		crashReport.append('\n');
	}
}
