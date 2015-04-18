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

package com.kotcrab.vis.editor.plugin;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.util.jar.Manifest;

public class PluginDescriptor {
	public static final String PLUGIN_ID = "Plugin-Id";
	public static final String PLUGIN_PROVIDER = "Plugin-Provider";
	public static final String PLUGIN_VERSION = "Plugin-Version";
	public static final String PLUGIN_CLASS = "Plugin-Class";
	public static final String PLUGIN_DEPENDENCIES = "Plugin-Dependencies";

	public FileHandle file;
	public String id;
	public String provider;
	public String version;
	public String clazz;
	public Array<String> deps = new Array<>();

	public PluginDescriptor (FileHandle file, Manifest mf) {
		this.file = file;

		id = mf.getMainAttributes().getValue(PLUGIN_ID);
		provider = mf.getMainAttributes().getValue(PLUGIN_PROVIDER);
		version = mf.getMainAttributes().getValue(PLUGIN_VERSION);
		clazz = mf.getMainAttributes().getValue(PLUGIN_CLASS);
		String depString = mf.getMainAttributes().getValue(PLUGIN_DEPENDENCIES);
		if (depString.equals("") == false) deps.addAll(depString.split(" "));
	}
}
