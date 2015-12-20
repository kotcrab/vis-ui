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
import com.kotcrab.vis.editor.assets.*;
import com.kotcrab.vis.editor.assets.transaction.AssetTransactionGenerator;
import com.kotcrab.vis.editor.assets.transaction.generator.*;
import com.kotcrab.vis.editor.extension.AssetType;
import com.kotcrab.vis.editor.extension.DefaultExporter;
import com.kotcrab.vis.editor.extension.SpriterAssetsFileSorter;
import com.kotcrab.vis.editor.extension.SpriterUIContextGenerator;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.PolygonTool;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.RotateTool;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.ScaleTool;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.SelectionTool;
import com.kotcrab.vis.editor.plugin.api.impl.ReflectionToolProvider;
import com.kotcrab.vis.editor.module.Module;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetDirectoryDescriptor;
import com.kotcrab.vis.editor.plugin.EditorEntitySupport;
import com.kotcrab.vis.editor.plugin.api.*;
import com.kotcrab.vis.editor.plugin.api.ContainerExtension.ExtensionScope;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Holds plugins loaded by {@link PluginLoaderModule}. Others modules (even from different containers like 'project' or 'scene') then can access them.
 * @author Kotcrab
 */
public class ExtensionStorageModule extends EditorModule {
	private Array<EditorEntitySupport> entitiesSupport = new Array<>();

	public void addEntitySupport (EditorEntitySupport support) {
		entitiesSupport.add(support);
	}

	public Array<EditorEntitySupport> getEntitiesSupports () {
		return entitiesSupport;
	}

	// ----------------

	private Array<ExporterPlugin> exporterPlugins = new Array<>();

	public void addExporterPlugin (ExporterPlugin exporterPlugin) {
		exporterPlugins.add(exporterPlugin);
	}

	public Array<ExporterPlugin> getExporterPlugins () {
		return exporterPlugins;
	}

	// ----------------

	private Array<ContainerExtension<?>> containerExtensions = new Array<>();

	public void addContainerExtension (ContainerExtension<?> extension) {
		containerExtensions.add(extension);
	}

	// ----------------

	private Array<ResourceLoader> resourceLoaders = new Array<>();

	public void addResourceLoader (ResourceLoader loader) {
		resourceLoaders.add(loader);
	}

	// ----------------

	private Array<ToolProvider<?>> toolProviders = new Array<>();

	public void addToolProvider (ToolProvider<?> provider) {
		toolProviders.add(provider);
	}

	public Array<ToolProvider<?>> getToolProviders () {
		return toolProviders;
	}

	// ----------------

	private Array<AssetTypeStorage> assetTypeStorages = new Array<>();

	public void addAssetTypeStorage (AssetTypeStorage storage) {
		assetTypeStorages.add(storage);
	}

	// ----------------

	private Array<AssetsUIContextGeneratorProvider> assetsContextGenProviders = new Array<>();

	public void addAssetsContextGeneratorProvider (AssetsUIContextGeneratorProvider contextProvider) {
		assetsContextGenProviders.add(contextProvider);
	}

	public Array<AssetsUIContextGeneratorProvider> getAssetsContextGeneratorsProviders () {
		return assetsContextGenProviders;
	}

	// ----------------

	private Array<AssetsFileSorter> assetsFileSorters = new Array<>();

	public void addAssetsFileSorter (AssetsFileSorter sorter) {
		assetsFileSorters.add(sorter);
	}

	public Array<AssetsFileSorter> getAssetsFileSorters () {
		return assetsFileSorters;
	}

	// ----------------

	private Array<AssetDescriptorProvider<?>> assetDescriptorProviders = new Array<>();

	public void addAssetDescriptorProvider (AssetDescriptorProvider<?> provider) {
		assetDescriptorProviders.add(provider);
	}

	// ----------------

	private Array<AssetTransactionGenerator> assetTransactionGens = new Array<>();

	public void addAssetTransactionGenerator (AssetTransactionGenerator generator) {
		assetTransactionGens.add(generator);
	}

	public Array<AssetTransactionGenerator> getAssetTransactionGenerator () {
		return assetTransactionGens;
	}

	// ----------------

	private Array<AssetDirectoryDescriptor> assetDirectoryDescriptors = new Array<>();

	public Array<AssetDirectoryDescriptor> getAssetDirectoryDescriptors () {
		return assetDirectoryDescriptors;
	}

	public Array<AssetDescriptorProvider<?>> getAssetDescriptorProviders () {
		return assetDescriptorProviders;
	}

	// ----------------

	@Override
	public void init () {
		addDefaultExtensions();

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

	private void addDefaultExtensions () {
		exporterPlugins.add(new DefaultExporter());

		assetTypeStorages.add(new AssetType());
		assetsContextGenProviders.add(SpriterUIContextGenerator::new);
		assetsFileSorters.add(new SpriterAssetsFileSorter());

		assetDescriptorProviders.add(new BmpFontDescriptorProvider());
		assetDescriptorProviders.add(new ParticleDescriptorProvider());
		assetDescriptorProviders.add(new MusicDescriptorProvider());
		assetDescriptorProviders.add(new SoundDescriptorProvider());
		assetDescriptorProviders.add(new TextureRegionDescriptorProvider());
		assetDescriptorProviders.add(new AtlasRegionDescriptorProvider());
		assetDescriptorProviders.add(new TtfFontDescriptorProvider());
		assetDescriptorProviders.add(new ShaderDescriptorProvider());
		assetDescriptorProviders.add(new SpriterDescriptorProvider());

		assetTransactionGens.add(new AtlasRegionAssetTransactionGenerator());
		assetTransactionGens.add(new MusicAssetTransactionGenerator());
		assetTransactionGens.add(new SoundAssetTransactionGenerator());
		assetTransactionGens.add(new ParticleAssetTransactionGenerator());
		assetTransactionGens.add(new BmpFontAssetTransactionGenerator());
		assetTransactionGens.add(new TextureRegionAssetTransactionGenerator());
		assetTransactionGens.add(new TtfAssetTransactionGenerator());

		toolProviders.add(new ReflectionToolProvider<>(SelectionTool.class));
		toolProviders.add(new ReflectionToolProvider<>(RotateTool.class));
		toolProviders.add(new ReflectionToolProvider<>(ScaleTool.class));
		toolProviders.add(new ReflectionToolProvider<>(PolygonTool.class));
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
