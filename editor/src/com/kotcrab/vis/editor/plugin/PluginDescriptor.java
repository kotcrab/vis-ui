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

package com.kotcrab.vis.editor.plugin;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StreamUtils;
import com.kotcrab.vis.editor.util.vis.EditorException;
import org.apache.commons.io.IOUtils;

import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Represents plugin descriptor that is going to be loaded into VisEditor
 * @author Kotcrab
 */
public class PluginDescriptor {
	public static final String PLUGIN_ID = "Plugin-Id";
	public static final String PLUGIN_NAME = "Plugin-Name";
	public static final String PLUGIN_DESCRIPTION = "Plugin-Description";
	public static final String PLUGIN_PROVIDER = "Plugin-Provider";
	public static final String PLUGIN_VERSION = "Plugin-Version";
	public static final String PLUGIN_COMPATIBILITY = "Plugin-Compatibility";
	public static final String PLUGIN_LICENSE = "Plugin-License";

	public FileHandle file;
	public String folderName;

	public String id;
	public String name;
	public String description;
	public String provider;
	public String version;
	public int compatibility;

	/* Optional */
	public String license;

	public Array<Class> pluginClasses = new Array<>();
	public Array<FileHandle> libs = new Array<>();

	public PluginDescriptor (FileHandle file, Manifest manifest) throws EditorException {
		this.file = file;
		folderName = file.parent().name();
		libs.addAll(file.parent().child("lib").list());

		Attributes attributes = manifest.getMainAttributes();

		id = attributes.getValue(PLUGIN_ID);
		name = attributes.getValue(PLUGIN_NAME);
		description = attributes.getValue(PLUGIN_DESCRIPTION);
		provider = attributes.getValue(PLUGIN_PROVIDER);
		version = attributes.getValue(PLUGIN_VERSION);
		String comp = attributes.getValue(PLUGIN_COMPATIBILITY);
		String licenseFile = attributes.getValue(PLUGIN_LICENSE);

		if (id == null || name == null || description == null || provider == null || version == null || comp == null)
			throw new EditorException("Missing one of required field in plugin manifest, plugin: " + file.name());

		try {
			compatibility = Integer.valueOf(comp);
		} catch (NumberFormatException e) {
			throw new EditorException("Failed to parse compatibility code, value must be integer!", e);
		}

		if (licenseFile != null && !licenseFile.isEmpty()) {
			JarFile jar = null;
			try {
				jar = new JarFile(file.file());
				JarEntry licenseEntry = jar.getJarEntry(licenseFile);
				license = StreamUtils.copyStreamToString(jar.getInputStream(licenseEntry));
			} catch (Exception e) {
				throw new EditorException("Failed to read license file for plugin: " + file.name(), e);
			} finally {
				IOUtils.closeQuietly(jar);
			}
		}
	}
}
