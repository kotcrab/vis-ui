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

package com.kotcrab.vis.editor;

import com.kotcrab.vis.editor.event.EventBus;
import com.kotcrab.vis.editor.util.Log;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;

public class App {
	public static final String TAG = "App";

	public static final int VERSION_CODE = 2;
	public static final String VERSION = "0.0.2-SNAPSHOT";

	public static final boolean ERROR_REPORTS = true;
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
			Log.w(TAG, "Error reports are disabled!");
	}

	/** Checks if proper charset is set, if not tries to change it, if that fails method will throw IllegalStateException */
	private static void checkCharset () {
		if (Charset.defaultCharset().name().equals("UTF-8") == false) {
			Log.err(TAG, "UTF-8 is not default charset, trying to change...");

			try {
				System.setProperty("file.encoding", "UTF-8");
				Field charset = Charset.class.getDeclaredField("defaultCharset");
				charset.setAccessible(true);
				charset.set(null, null);
				Log.l(TAG, "Success, run with VM argument: -Dfile.encoding=UTF-8 to avoid this.");
			} catch (Exception e) {
				String charsetChangeFailed = "Failed! UTF-8 charset is not default for this system and attempt to change it failed, " +
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
			path = path.substring(0, path.lastIndexOf('/')); // remove jar name from path
			return path + File.separator;
		} catch (UnsupportedEncodingException e) {
			Log.exception(e);
		}

		return null;
	}
}
