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

package com.kotcrab.vis.editor.ui;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.module.editor.StatusBarModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetsUIModule;
import com.kotcrab.vis.editor.ui.scene.NewSceneDialog;
import com.kotcrab.vis.editor.util.gdx.TableBuilder;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/** @author Kotcrab */
public class NoProjectFilesOpenView extends VisTable {
	private char bullet = 8226;

	public NoProjectFilesOpenView (ProjectModuleContainer projectContainer) {
		super(true);

		defaults().left();
		left();

		//TODO: recent scene files list

		LinkLabel newSceneLabel = new LinkLabel("New Scene");
		newSceneLabel.setListener(url -> getStage().addActor(new NewSceneDialog(projectContainer).fadeIn()));
		LinkLabel openSceneLabel = new LinkLabel("Existing Scene");
		openSceneLabel.setListener(url -> {
			AssetsUIModule assetsUi = projectContainer.get(AssetsUIModule.class);
			FileAccessModule fileAccess = projectContainer.get(FileAccessModule.class);
			StatusBarModule statusBar = projectContainer.findInHierarchy(StatusBarModule.class);
			FileHandle sceneFolder = fileAccess.getSceneFolder();

			if (assetsUi.getCurrentDirectory().equals(sceneFolder))
				statusBar.setText("Double click scene file in panel above to open it");
			else
				assetsUi.changeCurrentDirectory(sceneFolder);
		});

		add(new VisLabel("No files are open")).center().row();
		addSeparator().width(200).spaceBottom(15);
		add(TableBuilder.build(3, new VisLabel(bullet + " Create"), newSceneLabel)).spaceBottom(15).row();
		add(TableBuilder.build(3, new VisLabel(bullet + " Open"), openSceneLabel)).row();
	}
}
