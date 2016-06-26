/*
 * Copyright 2014-2016 See AUTHORS file.
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

package com.kotcrab.vis.editor.util;

import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.ui.util.OsUtils;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.List;

public class ApplicationUtils {
	public static String getRestartCommand () {
		List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
		StringBuilder vmArgsOneLine = new StringBuilder();

		if (OsUtils.isMac()) {
			vmArgsOneLine.append("-XstartOnFirstThread ");
		}

		for (String arg : vmArguments) {
			if (arg.contains("-agentlib") == false) {
				vmArgsOneLine.append(arg).append(" ");
			}
		}

		final StringBuilder cmd = new StringBuilder(PlatformUtils.getJavaBinPath() + " " + vmArgsOneLine);

		String[] mainCommand = System.getProperty("sun.java.command").split(" ");

		if (mainCommand[0].endsWith(".jar"))
			cmd.append("-jar " + new File(mainCommand[0]).getPath());
		else
			cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);

		for (int i = 1; i < mainCommand.length; i++) {
			cmd.append(" ");
			cmd.append(mainCommand[i]);
		}

		//if launching from idea, not in debug mode
		String ideaLauncher = "-Didea.launcher.bin.path=";
		int ideaLauncherStart = cmd.indexOf(ideaLauncher);
		if (ideaLauncherStart != -1) {
			cmd.insert(ideaLauncherStart + ideaLauncher.length(), "\"");
			cmd.insert(cmd.indexOf("-cp ", ideaLauncherStart) - 1, "\"");
		}

		return cmd.toString();
	}

	public static void startNewInstance () {
		try {
			CommandLine cmdLine = CommandLine.parse(getRestartCommand());
			DefaultExecutor executor = new DefaultExecutor();
			executor.setStreamHandler(new PumpStreamHandler(null, null, null));
			executor.execute(cmdLine, new DefaultExecuteResultHandler());
		} catch (Exception e) {
			Log.exception(e);
		}
	}
}
