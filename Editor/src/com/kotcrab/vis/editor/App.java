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

import com.kotcrab.vis.editor.event.EventBus;
import com.kotcrab.vis.editor.util.Log;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;

public class App {
	public static final String TAG = "App";

	public static final int VERSION_CODE = 2;
	public static final int COMPATIBILITY_CODE = 2;
	public static final String VERSION = "0.0.2-SNAPSHOT";

	public static final boolean ERROR_REPORTS = false;
	public static final boolean OPENGL_CRASH_BEFORE_EXIT_MESSAGE = true;
	public static final boolean SNAPSHOT = VERSION.contains("SNAPSHOT");

	public static final String JAR_FOLDER_PATH = getJarPath();

	private static final String USER_HOME_PATH = System.getProperty("user.home") + File.separator;
	public static final String APP_FOLDER_PATH = USER_HOME_PATH + ".viseditor" + File.separator;

	public static EventBus eventBus;

	public static void init () {
		new File(APP_FOLDER_PATH).mkdir();

		Log.init();

		checkCharset();

		eventBus = new EventBus();

		if (ERROR_REPORTS == false)
			Log.warn(TAG, "Error reports are disabled!");
	}

	/** Checks if proper charset is set, if not tries to change it, if that fails method will throw IllegalStateException */
	private static void checkCharset () {
		if (Charset.defaultCharset().name().equals("UTF-8") == false) {
			Log.error(TAG, "UTF-8 is not default charset, trying to change...");

			try {
				System.setProperty("file.encoding", "UTF-8");
				Field charset = Charset.class.getDeclaredField("defaultCharset");
				charset.setAccessible(true);
				charset.set(null, null);
				Log.warn(TAG, "Charset change successful, run with VM argument: -Dfile.encoding=UTF-8 to avoid this.");
			} catch (Exception e) {
				String charsetChangeFailed = "UTF-8 charset is not default for this system and attempt to change it failed, " +
						"cannot continue! Run with VM argument: -Dfile.encoding=UTF-8 to fix this.";
				JOptionPane.showMessageDialog(null, charsetChangeFailed, "Fatal error", JOptionPane.ERROR_MESSAGE);
				throw new IllegalStateException(charsetChangeFailed);
			}
		}
	}

	public static String getJarPath () {
		try {
			URL url = App.class.getProtectionDomain().getCodeSource().getLocation();
			String path = URLDecoder.decode(url.getFile(), "UTF-8");
			path = path.substring(1, path.lastIndexOf('/')); // remove jar name from path

			if (path.endsWith("Editor/target/classes")) //launched from ide, remove classes from path
				path = path.substring(0, path.length() - "/classes".length());

			path = path.replace("/", File.separator);
			return path + File.separator;
		} catch (UnsupportedEncodingException e) {
			Log.exception(e);
		}

		throw new IllegalStateException("Failed to get jar path, cannot continue!");
	}

	static void startNewInstance () {
		try {
			String java = System.getProperty("java.home") + "/bin/java";

			List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
			StringBuilder vmArgsOneLine = new StringBuilder();
			for (String arg : vmArguments) {
				if (arg.contains("-agentlib") == false)
					vmArgsOneLine.append(arg).append(" ");
			}

			final StringBuilder cmd = new StringBuilder("\"" + java + "\" " + vmArgsOneLine);

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

			Runtime.getRuntime().exec(cmd.toString());
		} catch (Exception e) {
			Log.exception(e);
		}
	}
}
