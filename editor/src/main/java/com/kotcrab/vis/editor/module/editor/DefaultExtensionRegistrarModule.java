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

package com.kotcrab.vis.editor.module.editor;

import com.kotcrab.vis.editor.assets.*;
import com.kotcrab.vis.editor.assets.transaction.AssetTransactionGenerator;
import com.kotcrab.vis.editor.assets.transaction.generator.*;
import com.kotcrab.vis.editor.extension.AssetType;
import com.kotcrab.vis.editor.extension.DefaultExporter;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.PolygonTool;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.RotateTool;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.ScaleTool;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.SelectionTool;
import com.kotcrab.vis.editor.plugin.api.ComponentTableProvider;
import com.kotcrab.vis.editor.plugin.api.ToolProvider;
import com.kotcrab.vis.editor.plugin.api.UserAddableComponentProvider;
import com.kotcrab.vis.editor.plugin.api.impl.ReflectionToolProvider;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.AutoComponentTable;
import com.kotcrab.vis.editor.ui.scene.entityproperties.components.PhysicsPropertiesComponentTable;
import com.kotcrab.vis.editor.ui.scene.entityproperties.components.RenderableComponentTable;
import com.kotcrab.vis.editor.ui.scene.entityproperties.components.TextPropertiesComponentTable;
import com.kotcrab.vis.runtime.component.*;
import com.kotcrab.vis.runtime.plugin.VisPlugin;

import java.util.function.Consumer;

/**
 * Registers default VisEditor extension. Note that plugins should use {@link VisPlugin} annotations.
 * @author Kotcrab
 */
public class DefaultExtensionRegistrarModule extends EditorModule {
	private ExtensionStorageModule extensionStorage;

	@SuppressWarnings("Convert2MethodRef")
	@Override
	public void init () {
		extensionStorage.addExporterPlugin(new DefaultExporter());

		extensionStorage.addAssetTypeStorage(new AssetType());

		Consumer<AssetDescriptorProvider<?>> descProvReg = provider -> extensionStorage.addAssetDescriptorProvider(provider);
		descProvReg.accept(new BmpFontDescriptorProvider());
		descProvReg.accept(new ParticleDescriptorProvider());
		descProvReg.accept(new MusicDescriptorProvider());
		descProvReg.accept(new SoundDescriptorProvider());
		descProvReg.accept(new TextureRegionDescriptorProvider());
		descProvReg.accept(new AtlasRegionDescriptorProvider());
		descProvReg.accept(new TtfFontDescriptorProvider());
		descProvReg.accept(new ShaderDescriptorProvider());

		Consumer<AssetTransactionGenerator> genReg = generator -> extensionStorage.addAssetTransactionGenerator(generator);
		genReg.accept(new AtlasRegionAssetTransactionGenerator());
		genReg.accept(new MusicAssetTransactionGenerator());
		genReg.accept(new SoundAssetTransactionGenerator());
		genReg.accept(new ParticleAssetTransactionGenerator());
		genReg.accept(new BmpFontAssetTransactionGenerator());
		genReg.accept(new TextureRegionAssetTransactionGenerator());
		genReg.accept(new TtfAssetTransactionGenerator());

		Consumer<ToolProvider<?>> toolReg = provider -> extensionStorage.addToolProvider(provider);
		toolReg.accept(new ReflectionToolProvider<>(SelectionTool.class));
		toolReg.accept(new ReflectionToolProvider<>(RotateTool.class));
		toolReg.accept(new ReflectionToolProvider<>(ScaleTool.class));
		toolReg.accept(new ReflectionToolProvider<>(PolygonTool.class));

		Consumer<ComponentTableProvider> compTableReg = provider -> extensionStorage.addComponentTableProvider(provider);
		compTableReg.accept(sceneMC -> new RenderableComponentTable(sceneMC));
		compTableReg.accept(sceneMC -> new AutoComponentTable<>(sceneMC, Shader.class, true));
		compTableReg.accept(sceneMC -> new AutoComponentTable<>(sceneMC, VisPolygon.class, true));
		compTableReg.accept(sceneMC -> new PhysicsPropertiesComponentTable(sceneMC));
		compTableReg.accept(sceneMC -> new AutoComponentTable<>(sceneMC, Variables.class, true));
		compTableReg.accept(sceneMC -> new AutoComponentTable<>(sceneMC, VisMusic.class, false));
		compTableReg.accept(sceneMC -> new AutoComponentTable<>(sceneMC, VisSound.class, false));
		compTableReg.accept(sceneMC -> new AutoComponentTable<>(sceneMC, VisParticle.class, false));
		compTableReg.accept(sceneMC -> new TextPropertiesComponentTable(sceneMC));

		Consumer<UserAddableComponentProvider> userAddCompReg = provider -> extensionStorage.addUserAddableComponentProvider(provider);
		userAddCompReg.accept(() -> Shader.class);
		userAddCompReg.accept(() -> VisPolygon.class);
		userAddCompReg.accept(() -> PhysicsProperties.class);
		userAddCompReg.accept(() -> Variables.class);
	}
}
