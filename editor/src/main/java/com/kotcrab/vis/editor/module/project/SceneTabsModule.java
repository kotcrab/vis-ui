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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.KryoException;
import com.google.common.eventbus.Subscribe;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.event.OpenSceneRequest;
import com.kotcrab.vis.editor.module.EventBusSubscriber;
import com.kotcrab.vis.editor.module.editor.TabsModule;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.dialog.LoadingAssetsFailedDialog;
import com.kotcrab.vis.editor.ui.scene.SceneTab;
import com.kotcrab.vis.editor.util.scene2d.VisTabbedPaneListener;
import com.kotcrab.vis.editor.util.vis.AssetLoadingException;
import com.kotcrab.vis.editor.util.vis.EditorException;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

/**
 * Module for managing {@link SceneTab}. Ensures that only one tab is created for each tab.
 * @author Kotcrab
 */
@EventBusSubscriber
public class SceneTabsModule extends ProjectModule implements VisTabbedPaneListener {
	private TabsModule tabsModule;
	private Stage stage;

	private SceneCacheModule sceneCache;

	private Array<SceneTab> loadedTabs;

	@Override
	public void init () {
		tabsModule.addListener(this);
		loadedTabs = new Array<>();
	}

	@Override
	public void dispose () {
		tabsModule.removeListener(this);
	}

	@Subscribe
	public void handleOpenSceneRequest (OpenSceneRequest request) {
		try {
			open(request.sceneFile);
		} catch (KryoException e) {
			Dialogs.showErrorDialog(stage, "Failed to load scene due to corrupted file.", e);
			Log.exception(e);
		}
	}
	
	public Array<SceneTab> getSceneTabs() {
		return loadedTabs;
	}

	public void open (FileHandle file) {
		try {
			open(sceneCache.getSafely(file));
		} catch (EditorException e) {
			Dialogs.showErrorDialog(stage, e.getMessage(), e);
			Log.exception(e);
		}
	}

	public void open (EditorScene scene) {
		if (scene == null) throw new IllegalArgumentException("Scene cannot be null");
		SceneTab oldTab = getTabByScene(scene);

		if (oldTab == null) {
			try {
				SceneTab tab = new SceneTab(scene, projectContainer);
				loadedTabs.add(tab);
				tabsModule.addTab(tab);
			} catch (AssetLoadingException e) {
				Log.exception(e);
				stage.addActor(new LoadingAssetsFailedDialog(e.failedResourceDescriptors).fadeIn());
			} catch (Exception e) {
				Log.exception(e);
				Dialogs.showErrorDialog(stage, "Error occurred during scene loading", e);
			}
		} else {
			tabsModule.switchTab(oldTab);
		}
	}

	public SceneTab getTabByScene (EditorScene scene) {
		if (scene == null) return null;

		for (SceneTab tab : loadedTabs)
			if (tab.getScene().path.equals(scene.path)) return tab;

		return null;
	}

	public EditorScene getSceneByPath (String path) {
		for (SceneTab tab : loadedTabs)
			if (tab.getScene().path.equals(path)) return tab.getScene();

		return null;
	}

	public void switchTab (SceneTab tab) {
		tabsModule.switchTab(tab);
	}

	@Override
	public void removedTab (Tab tab) {
		if (tab instanceof SceneTab)
			loadedTabs.removeValue((SceneTab) tab, true);
	}
}
