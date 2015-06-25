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

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Serializer;
import com.kotcrab.annotation.CallSuper;
import com.kotcrab.vis.editor.assets.AssetDescriptorProvider;
import com.kotcrab.vis.editor.assets.transaction.AssetTransactionGenerator;
import com.kotcrab.vis.editor.module.project.AssetsAnalyzerModule;
import com.kotcrab.vis.editor.module.project.ExportModule;
import com.kotcrab.vis.editor.module.project.ObjectSupportModule;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetsUIModule;
import com.kotcrab.vis.editor.module.project.assetsmanager.ContentItemProperties;
import com.kotcrab.vis.editor.module.project.assetsmanager.FileItem;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.ui.scene.entityproperties.specifictable.SpecificObjectTable;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.entity.Entity;
import com.kotcrab.vis.runtime.plugin.EntitySupport;

/**
 * Base class for all VisPlugins that add custom entities support for VisEditor. Writing plugin for editor also requires
 * writing VisRuntime plugin, see {@link EntitySupport}
 * @param <ED> custom entity data type (see {@link EntityData})
 * @param <E> custom entity type (see {@link Entity} and {@link EditorObject})
 * @author Kotcrab
 */
public abstract class ObjectSupport<ED extends EntityData, E extends Entity & EditorObject> {
	private int id = -1;

	/** Called when ObjectSupport can get required modules from {@link ProjectModuleContainer} */
	public void bindModules (ProjectModuleContainer projectMC) {

	}

	/** @return class of {@link EditorObject} that this plugin supports */
	public abstract Class<E> getObjectClass ();

	/** @return empty data instance this plugin */
	public abstract ED getEmptyData ();

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
	public abstract Source createDropSource (DragAndDrop dragAndDrop, FileItem item);

	/** @return kryo serializer used for serializing this entity */
	public abstract Serializer<E> getSerializer ();

	/**
	 * Called when support should export entity, this is to create filled EntityData from entity and add it to entities list.
	 * @param module ExportModule used for export operation
	 * @param entities array containing all exported entities so far, you should add your exported data here
	 * @param entity that you should export
	 */
	public abstract void export (ExportModule module, Array<EntityData> entities, E entity);

	/**
	 * @return instance of specific object table or null if this object does not have any special properties. This must return
	 * new instance every time this method is called.
	 */
	public SpecificObjectTable getUIPropertyTable () {
		return null;
	}

	/** @return {@link AssetDescriptorProvider} for {@link AssetsAnalyzerModule} or null. This is required if you want to support asset usage analyzing */
	public AssetDescriptorProvider getAssetDescriptorProvider () {
		return null;
	}

	/**
	 * @return {@link AssetTransactionGenerator} for {@link AssetsAnalyzerModule} or null. This is required if you want to support asset transaction.
	 * (renaming or moving asset file)
	 */
	public AssetTransactionGenerator getAssetTransactionGenerator () {
		return null;
	}

	/** Called when {@link ObjectSupportModule} has assigned ID for this module. */
	@CallSuper
	public void assignId (int id) {
		if (this.id != -1) throw new IllegalStateException("Id was already assigned to this support!");
		this.id = id;
	}

	@CallSuper
	public int getId () {
		if (id == -1) throw new IllegalStateException("Id wasn't assigned yet for this support!");
		return id;
	}

	@CallSuper
	public void releaseId () {
		id = -1;
	}
}
