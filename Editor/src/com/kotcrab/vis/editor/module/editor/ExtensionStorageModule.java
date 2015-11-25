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

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.assets.AssetType;
import com.kotcrab.vis.editor.module.Module;
import com.kotcrab.vis.editor.module.project.DefaultExporter;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetDirectoryDescriptor;
import com.kotcrab.vis.editor.plugin.ContainerExtension;
import com.kotcrab.vis.editor.plugin.ContainerExtension.ExtensionScope;
import com.kotcrab.vis.editor.plugin.EditorEntitySupport;
import com.kotcrab.vis.editor.plugin.ExporterPlugin;
import com.kotcrab.vis.editor.plugin.api.AssetTypeStorage;
import com.kotcrab.vis.editor.plugin.api.ResourceLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Holds plugins loaded by {@link PluginLoaderModule}. Others modules (even from different containers like 'project' or 'scene') then can access them.
 * @author Kotcrab
 */
public class ExtensionStorageModule extends EditorModule {
	private Array<EditorEntitySupport> objectSupports = new Array<>();
	private Array<ExporterPlugin> exporterPlugins = new Array<>();
	private Array<ContainerExtension<?>> containerExtensions = new Array<>();
	private Array<ResourceLoader> resourceLoaders = new Array<>();
	private Array<AssetTypeStorage> assetTypeStorages = new Array<>();

	private Array<AssetDirectoryDescriptor> assetDirectoryDescriptors = new Array<>();

	public void addObjectSupport (EditorEntitySupport support) {
		objectSupports.add(support);
	}

	public void addContainerExtension (ContainerExtension extension) {
		containerExtensions.add(extension);
	}

	public void addExporterPlugin (ExporterPlugin exporterPlugin) {
		exporterPlugins.add(exporterPlugin);
	}

	public void addResourceLoader (ResourceLoader loader) {
		resourceLoaders.add(loader);
	}

	public void addAssetTypeStorage (AssetTypeStorage storage) {
		assetTypeStorages.add(storage);
	}

	public Array<EditorEntitySupport> getEntitiesSupports () {
		return objectSupports;
	}

	public Array<ExporterPlugin> getExporterPlugins () {
		return exporterPlugins;
	}

	public Array<AssetDirectoryDescriptor> getAssetDirectoryDescriptors () {
		return assetDirectoryDescriptors;
	}

	@Override
	public void init () {
		exporterPlugins.add(new DefaultExporter());

		assetTypeStorages.add(new AssetType());

		resourceLoaders.forEach(loader -> {
			Log.debug("ExtensionStorage::ResourceLoader", "Loading " + loader.getName());
			loader.load();
		});

		assetTypeStorages.forEach(storage -> {
			try {
				for (Field field : storage.getClass().getDeclaredFields()) {
					if (Modifier.isStatic(field.getModifiers())) {
						if (field.getType().isAssignableFrom(AssetDirectoryDescriptor.class)) {
							assetDirectoryDescriptors.add((AssetDirectoryDescriptor) field.get(storage));
						}
					}
				}
			} catch (ReflectiveOperationException e) {
				throw new IllegalStateException(e);
			}
		});
	}

	@Override
	public void dispose () {
		resourceLoaders.forEach(loader -> {
			Log.debug("ExtensionStorage::ResourceLoader", "Unloading " + loader.getName());
			loader.dispose();
		});
	}

	public <T extends Module> Array<T> getContainersExtensions (Class<T> baseModuleType, ExtensionScope scope) {
		Array<T> modules = new Array<>();

		for (ContainerExtension extension : containerExtensions) {
			if (extension.getScope() == scope) {
				try {
					Constructor<?> cons = extension.getClass().getConstructor();
					Object module = cons.newInstance();
					modules.add((T) module);
				} catch (ReflectiveOperationException e) {
					throw new IllegalStateException(e);
				}
			}
		}

		return modules;
	}
}
