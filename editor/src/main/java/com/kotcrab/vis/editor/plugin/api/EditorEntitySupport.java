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

package com.kotcrab.vis.editor.plugin.api;

import com.artemis.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetsUIModule;
import com.kotcrab.vis.editor.module.project.assetsmanager.ContentItemProperties;
import com.kotcrab.vis.editor.module.project.assetsmanager.FileItem;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.util.scene2d.VisDragAndDrop;
import com.kotcrab.vis.editor.util.vis.SortedEntityEngineConfiguration;
import com.kotcrab.vis.runtime.util.EntityEngine;

/**
 * @author Kotcrab
 */
public interface EditorEntitySupport {
	/** Called after injecting modules. */
	default void init () {

	}

	/**
	 * Called when support should register it's {@link EntityEngine} systems and managers.
	 */
	default void registerSystems (SortedEntityEngineConfiguration config) {

	}

	/**
	 * Checks whether this extensions supports given asset directory. This is called when VisEditor searches for matching
	 * EditorEntitySupport for currently tested file.
	 * @return true if supported, false otherwise
	 */
	boolean isSupportedDirectory (FileHandle file, String relativePath);

	/** Returns {@link ContentItemProperties} for given file, that is displayed in {@link AssetsUIModule} */
	ContentItemProperties getContentItemProperties (FileHandle file, String relativePath, String extension);

	/** Called when EditorEntitySupport must create drop source for assets drag and drop */
	Source createDropSource (VisDragAndDrop dragAndDrop, FileItem item);

	Entity processDropPayload (EntityEngine engine, EditorScene scene, Object payload);

	EntityProxy resolveProxy (Entity entity);
}
