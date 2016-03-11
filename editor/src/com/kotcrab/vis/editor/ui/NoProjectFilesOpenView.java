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

package com.kotcrab.vis.editor.ui;

import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.project.SceneCacheModule;
import com.kotcrab.vis.editor.module.project.SceneTabsModule;
import com.kotcrab.vis.editor.ui.dialog.SelectFileDialog;
import com.kotcrab.vis.editor.ui.scene.NewSceneDialog;
import com.kotcrab.vis.editor.util.scene2d.TableBuilder;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/** @author Kotcrab */
public class NoProjectFilesOpenView extends VisTable {
	private char bullet = 8226;

	public NoProjectFilesOpenView (ProjectModuleContainer projectMC) {
		super(true);

		defaults().left();
		left();

		//TODO: recent scene files list

		LinkLabel newSceneLabel = new LinkLabel("New Scene");
		newSceneLabel.setListener(url -> getStage().addActor(new NewSceneDialog(projectMC).fadeIn()));
		LinkLabel openSceneLabel = new LinkLabel("Existing Scene");
		openSceneLabel.setListener(url -> {
			FileAccessModule fileAccess = projectMC.get(FileAccessModule.class);
			SceneTabsModule sceneTabs = projectMC.findInHierarchy(SceneTabsModule.class);
			SceneCacheModule sceneCache = projectMC.findInHierarchy(SceneCacheModule.class);

			getStage().addActor(new SelectFileDialog("scene", fileAccess.getAssetsFolder(), sceneTabs::open).fadeIn());
		});

		add(new VisLabel("No files are open")).center().row();
		addSeparator().width(200).spaceBottom(15);
		add(TableBuilder.build(3, new VisLabel(bullet + " Create"), newSceneLabel)).spaceBottom(15).row();
		add(TableBuilder.build(3, new VisLabel(bullet + " Open"), openSceneLabel)).row();
	}
}
