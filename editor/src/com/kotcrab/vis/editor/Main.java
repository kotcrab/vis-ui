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
import com.kotcrab.vis.editor.util.vis.LaunchConfiguration;

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

		new Lwjgl3Application(editor, config);
	}
}
