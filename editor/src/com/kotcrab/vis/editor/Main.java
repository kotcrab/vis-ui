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

package com.kotcrab.vis.editor;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.kotcrab.vis.editor.event.ExceptionEvent;
import com.kotcrab.vis.editor.util.PlatformUtils;
import com.kotcrab.vis.editor.util.vis.CrashReporter;
import com.kotcrab.vis.editor.util.vis.LaunchConfiguration;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.File;
import java.io.IOException;

/** @author Kotcrab */
public class Main {
	private static Editor editor;

	public static void main (String[] args) throws Exception {
		App.init();

		LaunchConfiguration launchConfig = new LaunchConfiguration();

		//TODO: needs some better parser
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equals("--scale-ui")) {
				launchConfig.scaleUIEnabled = true;
				continue;
			}

			if (arg.equals("--project")) {
				if (i + 1 >= args.length) {
					throw new IllegalStateException("Not enough parameters for --project <project path>");
				}

				launchConfig.projectPath = args[i + 1];
				i++;
				continue;
			}

			if (arg.equals("--scene")) {
				if (i + 1 >= args.length) {
					throw new IllegalStateException("Not enough parameters for --scene <scene path>");
				}

				launchConfig.scenePath = args[i + 1];
				i++;
				continue;
			}

			Log.warn("Unrecognized command line argument: " + arg);
		}

		launchConfig.verify();

		editor = new Editor(launchConfig);

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1280, 720);
		config.useVsync(true);
		config.setWindowListener(new Lwjgl3WindowAdapter() {
			@Override
			public boolean closeRequested () {
				editor.requestExit();
				return false;
			}
		});

		try {
			new Lwjgl3Application(editor, config);
		} catch (Exception e) {
			Log.exception(e);
			Log.fatal("Uncaught exception occurred, error report will be saved");
			Log.flushFile();

			if (App.eventBus != null) App.eventBus.post(new ExceptionEvent(e, true));

			try {
				File crashReport = new CrashReporter(Log.getLogFile().file()).processReport();
				if (new File(App.CRASH_REPORTING_TOOL_JAR).exists() == false) {
					Log.warn("Crash reporting tool not present, skipping crash report sending.");
				} else {
					CommandLine cmdLine = new CommandLine(PlatformUtils.getJavaBinPath());
					cmdLine.addArgument("-jar");
					cmdLine.addArgument(App.CRASH_REPORTING_TOOL_JAR);
					cmdLine.addArgument(ApplicationUtils.getRestartCommand().replace("\"", "%"));
					cmdLine.addArgument(crashReport.getAbsolutePath(), false);
					DefaultExecutor executor = new DefaultExecutor();
					executor.setStreamHandler(new PumpStreamHandler(null, null, null));
					executor.execute(cmdLine);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			System.exit(-3);
		}
	}
}
