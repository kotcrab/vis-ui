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

package com.kotcrab.vis.editor.module.project.converter;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.module.VisContainers;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.module.editor.ProjectIOModule;
import com.kotcrab.vis.editor.module.project.*;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetsUIModule;
import com.kotcrab.vis.editor.module.project.support.vc8.SceneIOModuleVC8;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.util.AsyncTask;
import com.kotcrab.vis.editor.util.SteppedAsyncTask;
import com.kotcrab.vis.editor.util.ThreadUtils;

/** @author Kotcrab */
public class ProjectConverter8to9 extends ProjectConverter {
	private ExtensionStorageModule extensionStorage;
	private ProjectIOModule projectIO;

	public ProjectConverter8to9 () {
		super(8, 9);
	}

	@Override
	public AsyncTask getConversionTask (FileHandle dataFile) {
		return new SteppedAsyncTask("ProjectConverter8to9") {
			@Override
			public void execute () {
				Project project = projectIO.readProjectDataFile(dataFile);

				setMessage("Loading project for conversion...");
				setProgressPercent(10);
				ThreadUtils.sleep(10);

				Editor editor = Editor.instance;

				final ProjectModuleContainer oldProjectMC = new ProjectModuleContainer(editor.getEditorModuleContainer());
				final ProjectModuleContainer newProjectMC = new ProjectModuleContainer(editor.getEditorModuleContainer());

				executeOnOpenGL(() -> {
					//manually bootstrapping project container sure is fun
					oldProjectMC.setProject(project);
					newProjectMC.setProject(project);

					VisContainers.createProjectModules(oldProjectMC, extensionStorage);
					VisContainers.createProjectModules(newProjectMC, extensionStorage);

					oldProjectMC.get(TextureCacheModule.class).setPackagingEnabled(false);
					newProjectMC.get(TextureCacheModule.class).setPackagingEnabled(false);
					oldProjectMC.remove(AssetsUIModule.class);
					newProjectMC.remove(AssetsUIModule.class);

					oldProjectMC.remove(SceneIOModule.class);
					oldProjectMC.add(new SceneIOModuleVC8());

					oldProjectMC.init();
					newProjectMC.init();
				});

				setMessage("Converting scene data...");
				setProgressPercent(50);

				FileAccessModule fileAccess = newProjectMC.get(FileAccessModule.class);

				SceneIOModuleVC8 oldSceneIO = oldProjectMC.get(SceneIOModuleVC8.class);
				SceneIOModule newSceneIO = newProjectMC.get(SceneIOModule.class);

				for (FileHandle sceneFile : fileAccess.getSceneFiles()) {
					setMessage("Converting " + sceneFile.name() + "...");
					ThreadUtils.sleep(10);

					executeOnOpenGL(() -> {
						EditorScene scene = oldSceneIO.load(sceneFile);
						newSceneIO.save(scene);
					});
				}

				setMessage("Cleanup...");
				setProgressPercent(90);

				executeOnOpenGL(() -> {
					oldProjectMC.dispose();
					newProjectMC.dispose();
				});
			}
		};
	}
}
