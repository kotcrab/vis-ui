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
import com.kotcrab.vis.editor.util.ApplicationUtils;
import com.kotcrab.vis.editor.util.ExceptionUtils;
import com.kotcrab.vis.editor.util.PlatformUtils;
import com.kotcrab.vis.editor.util.vis.CrashReporter;
import com.kotcrab.vis.editor.util.vis.LaunchConfiguration;
import com.kotcrab.vis.ui.util.OsUtils;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.File;
import java.io.IOException;

/**
 * Main VisEditor class
 * @author Kotcrab
 */
public class Main {
	private static Editor editor;

	public static void main (String[] args) throws Exception {
		App.init();
		if (OsUtils.isMac()) System.setProperty("java.awt.headless", "true");

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
		config.setWindowSizeLimits(1, 1, 9999, 9999);
		config.useVsync(true);
		config.setIdleFPS(2);
		config.setWindowListener(new Lwjgl3WindowAdapter() {
			@Override
			public boolean closeRequested () {
				editor.requestExit();
				return false;
			}
		});

		try {
			new Lwjgl3Application(editor, config);
			Log.dispose();
		} catch (Exception e) {
			Log.exception(e);
			Log.fatal("Uncaught exception occurred, error report will be saved");
			Log.flush();

			if (App.eventBus != null) App.eventBus.post(new ExceptionEvent(e, true));

			try {
				File crashReport = new CrashReporter(Log.getLogFile().file()).processReport();
				if (new File(App.TOOL_CRASH_REPORTER_PATH).exists() == false) {
					Log.warn("Crash reporting tool not present, skipping crash report sending.");
				} else {
					CommandLine cmdLine = new CommandLine(PlatformUtils.getJavaBinPath());
					cmdLine.addArgument("-jar");
					cmdLine.addArgument(App.TOOL_CRASH_REPORTER_PATH);
					cmdLine.addArgument(ApplicationUtils.getRestartCommand().replace("\"", "%"));
					cmdLine.addArgument(crashReport.getAbsolutePath(), false);
					DefaultExecutor executor = new DefaultExecutor();
					executor.setStreamHandler(new PumpStreamHandler(null, null, null));
					executor.execute(cmdLine);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			Log.dispose();
			System.exit(-3);
		} catch (ExceptionInInitializerError err) {
			if (OsUtils.isMac() && err.getCause() instanceof IllegalStateException) {
				if (ExceptionUtils.getStackTrace(err).contains("XstartOnFirstThread")) {
					System.out.println("Application was not launched on first thread. Restarting with -XstartOnFirstThread, add VM argument -XstartOnFirstThread to avoid this.");
					ApplicationUtils.startNewInstance();
				}
			}

			throw err;
		}
	}
}
