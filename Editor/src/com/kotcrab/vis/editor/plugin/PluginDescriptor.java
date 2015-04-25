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
import com.kotcrab.vis.editor.util.EditorException;

import java.util.jar.Manifest;

public class PluginDescriptor {
	public static final String PLUGIN_ID = "Plugin-Id";
	public static final String PLUGIN_NAME = "Plugin-Name";
	public static final String PLUGIN_DESCRIPTION = "Plugin-Description";
	public static final String PLUGIN_PROVIDER = "Plugin-Provider";
	public static final String PLUGIN_VERSION = "Plugin-Version";
	public static final String PLUGIN_COMPATIBILITY = "Plugin-Compatibility";

	public FileHandle file;
	public String folderName;

	public String id;
	public String name;
	public String description;
	public String provider;
	public String version;
	public int compatibility;

	public Array<Class> pluginClasses = new Array<>();
	public Array<FileHandle> libs = new Array<>();

	public PluginDescriptor (FileHandle file, Manifest mf) throws EditorException {
		this.file = file;
		folderName = file.parent().name();
		libs.addAll(file.parent().child("lib").list());

		id = mf.getMainAttributes().getValue(PLUGIN_ID);
		name = mf.getMainAttributes().getValue(PLUGIN_NAME);
		description = mf.getMainAttributes().getValue(PLUGIN_DESCRIPTION);
		provider = mf.getMainAttributes().getValue(PLUGIN_PROVIDER);
		version = mf.getMainAttributes().getValue(PLUGIN_VERSION);
		String comp = mf.getMainAttributes().getValue(PLUGIN_COMPATIBILITY);

		if (id == null || name == null || description == null || provider == null || version == null || comp == null)
			throw new EditorException("Missing one of required field in plugin manifest, plugin: " + file.extension());

		try {
			compatibility = Integer.valueOf(comp);
		} catch (NumberFormatException ex) {
			throw new EditorException("Failed to parse compatibility code, value must be integer!", ex);
		}
	}
}
