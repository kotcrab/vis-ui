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

import com.artemis.Entity;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Serializer;
import com.kotcrab.vis.editor.assets.AssetDescriptorProvider;
import com.kotcrab.vis.editor.assets.transaction.AssetTransactionGenerator;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetsUIModule;
import com.kotcrab.vis.editor.module.project.assetsmanager.ContentItemProperties;
import com.kotcrab.vis.editor.module.project.assetsmanager.FileItem;
import com.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.scene.entityproperties.specifictable.SpecificComponentTable;
import com.kotcrab.vis.editor.ui.scene.entityproperties.specifictable.SpecificUITable;
import com.kotcrab.vis.editor.util.gdx.VisDragAndDrop;
import com.kotcrab.vis.runtime.util.EntityEngine;
import com.kotcrab.vis.runtime.util.EntityEngineConfiguration;

/**
 * @author Kotcrab
 */
public abstract class EditorEntitySupport {
	/** Called when support can get required modules from {@link ProjectModuleContainer} */
	public void bindModules (ProjectModuleContainer projectMC) {

	}

	/**
	 * Called when support should register it's {@link EntityEngine} systems and managers. WARNING: Do not store
	 * modules from {@link SceneModuleContainer} because this method is called for each scene and scene modules are
	 * scene context sensitive. If you need to get and store modules do it in {@link #bindModules(ProjectModuleContainer)}
	 */
	public void registerSystems (SceneModuleContainer sceneMC, EntityEngineConfiguration engineConfig) {

	}

	/**
	 * Checks whether this extensions supports given asset directory. Supports should have their own main directory in project
	 * assets folder where assets for entities can be stored. This is called when VisEditor searches for matching
	 * ObjectSupport for currently tested file.
	 * @param relativePath of the file that is tested
	 * @param extension of the file that is tested
	 * @return true if supported, false otherwise
	 */
	public abstract boolean isSupportedDirectory (String relativePath, String extension);

	/** Returns {@link ContentItemProperties} for given file, that is displayed in {@link AssetsUIModule} */
	public abstract ContentItemProperties getContentItemProperties (String relativePath, String extension);

	/** Called when ObjectSupport must create drop source for assets drag and drop */
	public abstract Source createDropSource (VisDragAndDrop dragAndDrop, FileItem item);

	public abstract Entity processDropPayload (EntityEngine engine, EditorScene scene, Object payload);

	public abstract EntityProxy resolveProxy (Entity entity);

	/** @return kryo serializer used for serializing this entity */
	public abstract Array<Serializer> getSerializers ();

	public Array<Class<?>> getSerializedTypes () {
		return null;
	}

	/** This must return new instances every time this is called. */
	public Array<SpecificUITable> getUIPropertyTables () {
		return null;
	}

	/** This must return new instances every time this is called. */
	public Array<SpecificComponentTable> getComponentsUITables () {
		return null;
	}

	public Array<AssetDescriptorProvider> getAssetDescriptorProviders () {
		return null;
	}

	public Array<AssetTransactionGenerator> getAssetTransactionGenerators () {
		return null;
	}
}
