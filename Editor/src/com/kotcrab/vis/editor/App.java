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
import com.kotcrab.vis.editor.util.JarUtils;
import com.kotcrab.vis.editor.util.Log;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class App {
	public static final String TAG = "App";

	public static final int VERSION_CODE = 3;
	public static final int COMPATIBILITY_CODE = 2;
	public static final String VERSION = "0.1.0-SNAPSHOT";

	public static boolean buildTimestampValid = false;
	public static String buildTimestamp;

	public static final boolean OPENGL_CRASH_BEFORE_EXIT_MESSAGE = true;
	public static final boolean SNAPSHOT = VERSION.contains("SNAPSHOT");

	private static String JAR_FOLDER_PATH = JarUtils.getJarPath(App.class);

	private static final String USER_HOME_PATH = System.getProperty("user.home") + File.separator;
	public static final String APP_FOLDER_PATH = USER_HOME_PATH + ".viseditor" + File.separator;

	private static final String GDX_RELEASE_ZIP = "http://libgdx.badlogicgames.com/releases/libgdx-1.5.6.zip";

	public static EventBus eventBus;

	public static void init () {
		new File(APP_FOLDER_PATH).mkdir();

		Log.init();

		checkCharset();

		eventBus = new EventBus();

		try {
			buildTimestamp = readTimestamp();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (buildTimestamp == null)
			buildTimestamp = "unknown, not built using CI";
		else
			buildTimestampValid = true;

		Log.info("Build: " + App.buildTimestamp);
	}

	private static String readTimestamp () throws IOException {
		Class clazz = App.class;
		String className = clazz.getSimpleName() + ".class";
		String classPath = clazz.getResource(className).toString();
		if (!classPath.startsWith("jar")) {
			// Class not from JAR
			return null;
		}
		String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) +
				"/META-INF/MANIFEST.MF";
		Manifest manifest = new Manifest(new URL(manifestPath).openStream());
		Attributes attr = manifest.getMainAttributes();
		return attr.getValue("Build-Timestamp");
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

	public static String getJarFolderPath () {
		return JAR_FOLDER_PATH;
	}
}
