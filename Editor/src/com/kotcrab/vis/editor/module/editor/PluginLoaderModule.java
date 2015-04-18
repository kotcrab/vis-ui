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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.plugin.ObjectSupport;
import com.kotcrab.vis.editor.plugin.PluginDescriptor;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.Log;
import com.kotcrab.vis.runtime.plugin.EntitySupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class PluginLoaderModule extends EditorModule {
	private static final String TAG = "PluginLoader";
	private static final String PLUGINS_FOLDER_PATH = App.JAR_FOLDER_PATH + File.separator + "plugins";

	private PluginContainerModule pluginContainer;

	private ArrayList<PluginDescriptor> descriptors; //we need nested iterators, Array can't provide that
	private URLClassLoader classLoader;

	@Override
	public void init () {
		pluginContainer = container.get(PluginContainerModule.class);
	}

	@Override
	public void postInit () {
		FileHandle pluginsFolder = Gdx.files.absolute(PLUGINS_FOLDER_PATH);
		Log.debug(TAG, "Loading plugins from: " + pluginsFolder);

		FileHandle[] plugins = pluginsFolder.list();

		Array<FileHandle> pluginsFiles = new Array<>();

		for (FileHandle file : plugins) {
			String relativePath = FileUtils.relativize(pluginsFolder, file.path());

			if (file.extension().equals("jar") == false) {
				Log.warn(TAG, "Unknown file in plugins folder: " + relativePath);
				continue;
			}

			pluginsFiles.add(file);
		}

		try {
			loadPluginsDescriptors(pluginsFiles);
			verifyPlugins();
			loadPluginsJars();
			loadMainPluginsClasses();
		} catch (IOException | ReflectiveOperationException e) {
			Log.exception(e);
		}
	}

	private void loadPluginsDescriptors (Array<FileHandle> pluginsFiles) throws IOException {
		descriptors = new ArrayList<>();

		for (FileHandle file : pluginsFiles) {

			JarInputStream jarStream = new JarInputStream(new FileInputStream(file.file()));
			Manifest mf = jarStream.getManifest();
			jarStream.close();

			descriptors.add(new PluginDescriptor(file, mf));
		}
	}

	private void verifyPlugins () {
		for (PluginDescriptor descriptor : descriptors)
			checkCircularDependencies(descriptor.id, descriptor.deps);

		resolveLoadOrder();
	}

	private void resolveLoadOrder () {
		ArrayList<PluginDescriptor> orderedDescriptors = new ArrayList<>();

		while (true) {
			int loadedPlugins = orderedDescriptors.size();

			loadPossiblePlugins(orderedDescriptors);

			if (loadedPlugins == orderedDescriptors.size()) {
				Log.fatal("Failed to resolve load order of plugins! Unresolved plugins:");
				for (PluginDescriptor descriptor : descriptors)
					Log.fatal("\t" + descriptor);

				throw new IllegalStateException("Failed to resolve load order of plugins!");
			}

			if (descriptors.size() == 0) break;
		}

		descriptors = orderedDescriptors;
	}

	private void loadPossiblePlugins (ArrayList<PluginDescriptor> orderedDescriptors) {
		Iterator<PluginDescriptor> iterator = descriptors.iterator();
		while (iterator.hasNext()) {
			PluginDescriptor descriptor = iterator.next();

			if (isAllDependenciesLoaded(orderedDescriptors, descriptor) == false) continue;

			orderedDescriptors.add(descriptor);
			iterator.remove();
		}
	}

	private boolean isAllDependenciesLoaded (ArrayList<PluginDescriptor> orderedDescriptors, PluginDescriptor descriptor) {
		for (String dep : descriptor.deps)
			if (listContains(orderedDescriptors, dep) == false) return false;

		return true;
	}

	private void checkCircularDependencies (String base, Array<String> deps) {
		for (String id : deps) {
			if (id.equals(base))
				throw new IllegalStateException("Circular dependencies are not allowed! Base plugin: " + base);

			PluginDescriptor plugin = getDescriptorById(id);
			if (plugin.deps.size > 0) checkCircularDependencies(base, plugin.deps);
		}
	}

	private void loadPluginsJars () throws IOException, ClassNotFoundException {
		URL[] urls = new URL[descriptors.size()];

		for (int i = 0; i < urls.length; i++)
			urls[i] = new URL("jar:file:" + descriptors.get(i).file.path() + "!/");

		classLoader = URLClassLoader.newInstance(urls, ClassLoader.getSystemClassLoader());

		for (PluginDescriptor descriptor : descriptors) {
			Log.debug(TAG, "Loading: " + descriptor.id);

			JarFile jarFile = new JarFile(descriptor.file.path());
			loadJarClasses(classLoader, jarFile.entries());
		}
	}

	private void loadJarClasses (URLClassLoader classLoader, Enumeration<JarEntry> entries) throws ClassNotFoundException {
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (entry.isDirectory() || entry.getName().endsWith(".class") == false) continue;

			String className = entry.getName().substring(0, entry.getName().length() - ".class".length());
			className = className.replace('/', '.');
			classLoader.loadClass(className);
		}
	}

	private void loadMainPluginsClasses () throws ReflectiveOperationException {
		for (PluginDescriptor descriptor : descriptors) {
			Class<?> clazz = Class.forName(descriptor.clazz, true, classLoader);
			Constructor<?> cons = clazz.getConstructor();
			Object object = cons.newInstance();

			if (object instanceof ObjectSupport) {
				pluginContainer.addSupport((ObjectSupport) object);
				continue;
			}

			if (object instanceof EntitySupport)
				continue;

			Log.warn("Plugin '" + descriptor.id + "' was successfully loaded but it's main class object wasn't recognized.");
		}
	}

	private boolean listContains (ArrayList<PluginDescriptor> descriptors, String id) {
		for (PluginDescriptor descriptor : descriptors)
			if (descriptor.id.equals(id)) return true;

		return false;
	}

	private PluginDescriptor getDescriptorById (String id) {
		for (PluginDescriptor descriptor : descriptors)
			if (descriptor.id.equals(id)) return descriptor;

		throw new IllegalStateException("Could not find PluginDescriptor by id: " + id);
	}
}
