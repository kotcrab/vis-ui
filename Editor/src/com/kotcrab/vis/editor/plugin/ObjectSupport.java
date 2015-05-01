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
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Serializer;
import com.kotcrab.vis.editor.module.project.ExportModule;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.project.assetsmanager.FileItem;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.ui.scene.entityproperties.ContentItemProperties;
import com.kotcrab.vis.editor.ui.scene.entityproperties.SpecificObjectTable;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.entity.Entity;

public abstract class ObjectSupport<ED extends EntityData, E extends Entity & EditorObject> {
	private int id = -1;

	public void bindModules (ProjectModuleContainer projectMC) {

	}

	public abstract Class<E> getObjectClass ();

	public abstract ED getEmptyData ();

	public abstract boolean isSupportedDirectory (String extension, String relativePath);

	public abstract ContentItemProperties getContentItemProperties (String relativePath, String ext);

	public abstract Source createDropSource (DragAndDrop dragAndDrop, FileItem item);

	public abstract Serializer<E> getSerializer ();

	public abstract void export (ExportModule module, Array<EntityData> entities, E entity);

	/**
	 * @return instance of specific object table or null if this object does not have any special properites. This must return
	 * new instance every time this method is called.
	 */
	public SpecificObjectTable getUIPropertyTable () {
		return null;
	}

	public boolean canAnalyze (FileHandle file, String relativePath) {
		return false;
	}

	public void assignId (int id) {
		if (this.id != -1) throw new IllegalStateException("Id was already assigned to this support!");
		this.id = id;
	}

	public int getId () {
		if (id == -1) throw new IllegalStateException("Id wasn't assigned yet for this support!");
		return id;
	}

}
