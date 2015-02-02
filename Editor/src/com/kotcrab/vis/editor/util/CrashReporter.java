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

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class CrashReporter {
	public static void sendReport (File logFile) throws IOException {
		StringBuilder crashReport = new StringBuilder();
		crashReport.append("VisEditor Crash Report\n");
		crashReport.append("VisEditor " + App.VERSION + "\n");
		crashReport.append("VersionCode:" + App.VERSION_CODE + " Snapshot: " + App.SNAPSHOT + "\n\n");

		crashReport.append("=======Dumping threads info:=======\n");

		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

		for (Thread t : threadSet) {
			if (t.isDaemon()) {
				crashReport.append("Skipping daemon thread: " + t.getName() + "\n");
			} else {
				crashReport.append("Thread: " + t.getName() + "\n");
				for (StackTraceElement e : t.getStackTrace()) {
					crashReport.append("\t");
					crashReport.append(e.toString());
					crashReport.append("\n");
				}
			}

			crashReport.append("\n");
		}

		crashReport.append("==========Thread info end==========\n\n");

		crashReport.append("====Last 200 lines of log file:====\n");

		ReversedLinesFileReader reader = new ReversedLinesFileReader(logFile);
		Array<String> logLines = new Array<>();

		for (int i = 0; i < 200; i++) {
			String line = reader.readLine();
			if (line == null) break;
			logLines.add(line + "\n");
		}

		logLines.reverse();

		for(String s : logLines)
			crashReport.append(s);

		crashReport.append("============Log file end===========\n");
		crashReport.append("\n");

		String result = crashReport.toString();
	}
}
