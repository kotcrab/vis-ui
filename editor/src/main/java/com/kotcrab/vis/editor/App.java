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

import com.kotcrab.vis.editor.event.EventBusExceptionEvent;
import com.kotcrab.vis.editor.event.VisEventBus;
import com.kotcrab.vis.editor.module.editor.AppFileAccessModule;
import com.kotcrab.vis.editor.module.editor.PluginFilesAccessModule;
import com.kotcrab.vis.editor.util.JarUtils;
import com.kotcrab.vis.editor.util.PublicApi;
import org.slf4j.impl.SimpleLogger;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Provides access to version fields, basic app paths, build time stamps, and {@link VisEventBus}.
 * @author Kotcrab
 */
@PublicApi
public class App {
	private static boolean initialized;

	private static final String TAG = "App";

	/** Editor package name */
	public static final String PACKAGE = "com.kotcrab.vis.editor";

	/**
	 * VisEditor version code number, also used as project version code. Note that single VisEditor x.y.z version (eg. 0.3.0)
	 * may have multiple version codes. Version code is incremented after new editor release and after incompatible
	 * change is made to project system.
	 */
	public static final int VERSION_CODE = VersionCodes.EDITOR_040;

	/**
	 * VisEditor plugin version code. This only informs about plugin format version, even if this code matches plugin
	 * still may fail to load, for example when incompatibles changes were made to code.
	 */
	public static final int PLUGIN_COMPATIBILITY_CODE = 4;

	/** VisEditor version in text format: x.y.z, may be x.y.z-SNAPSHOT if this version is snapshot */
	public static final String VERSION = "0.4.0-SNAPSHOT";

	/** If true this version is snapshot */
	public static final boolean SNAPSHOT = VERSION.contains("SNAPSHOT");

	/** If true this version of editor was build on CI server and has valid timestamp in manifest */
	private static boolean buildTimestampValid = false;
	/** VisEditor build timestamp, only valid if this version was built on CI server, check {@link #buildTimestampValid} first! */
	private static String buildTimestamp;

	/** Path to folder that editor jar is located, if launched from Maven or IDE this will point to Maven /target/ folder. */
	public static final String JAR_FOLDER_PATH = JarUtils.getJarPath(App.class);
	static final String TOOL_CRASH_REPORTER_PATH = App.JAR_FOLDER_PATH + "tools/crash-reporter.jar";

	private static final String USER_HOME_PATH = System.getProperty("user.home") + File.separator;

	/**
	 * VisEditor folder path in `user.home` folder, actual location depends on OS. This SHOULD NOT be used by plugins
	 * see {@link PluginFilesAccessModule}.
	 * @see AppFileAccessModule
	 */
	public static final String APP_FOLDER_PATH = USER_HOME_PATH + ".viseditor" + File.separator;

	/** VisEditor main {@link VisEventBus} */
	public static final VisEventBus eventBus = new VisEventBus((exception, context) -> {
		Log.fatal("Exception when dispatching event: " + context.getSubscriber() + " to " + context.getSubscriberMethod());
		Log.exception(exception);
		App.eventBus.post(new EventBusExceptionEvent(exception, context));
	});

	/** Performs App init, called only once by VisEditor. */
	static void init () {
		if (initialized) throw new IllegalStateException("App cannot be initialized twice!");
		new File(APP_FOLDER_PATH).mkdir();

		Log.init();
		//com.esotericsoftware.minlog.Log.TRACE();
		System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");

		checkCharset();

		try {
			buildTimestamp = readTimestamp();
			if (buildTimestamp == null || buildTimestamp.equals("not built using CI")) {
				buildTimestamp = "not built using CI";
				buildTimestampValid = false;
			} else
				buildTimestampValid = true;
		} catch (IOException e) {
			Log.exception(e);
		}

		Log.info("Build: " + App.buildTimestamp);
		initialized = true;
	}

	public static String getBuildTimestamp () {
		return buildTimestamp;
	}

	public static boolean isBuildTimestampValid () {
		return buildTimestampValid;
	}

	private static String readTimestamp () throws IOException {
		Class<App> clazz = App.class;
		String className = clazz.getSimpleName() + ".class";
		String classPath = clazz.getResource(className).toString();
		if (!classPath.startsWith("jar")) {
			// class not from JAR
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
			Log.warn(TAG, "UTF-8 is not default charset, trying to change...");

			try {
				System.setProperty("file.encoding", "UTF-8");
				Field charset = Charset.class.getDeclaredField("defaultCharset");
				charset.setAccessible(true);
				charset.set(null, null);
				Log.warn(TAG, "Charset change successful, run with VM argument: -Dfile.encoding=UTF-8 to avoid this.");
			} catch (Exception e) {
				String charsetChangeFailed = "UTF-8 charset is not default for this system and attempt to change it failed, " +
						"cannot continue. Run with VM argument: -Dfile.encoding=UTF-8 to fix this.";
				JOptionPane.showMessageDialog(null, charsetChangeFailed, "Fatal error", JOptionPane.ERROR_MESSAGE);
				throw new IllegalStateException(charsetChangeFailed, e);
			}
		}
	}
}
