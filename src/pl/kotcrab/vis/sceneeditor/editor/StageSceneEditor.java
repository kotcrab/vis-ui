/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/

package pl.kotcrab.vis.sceneeditor.editor;

import pl.kotcrab.vis.sceneeditor.accessor.scene2d.ButtonAccessor;
import pl.kotcrab.vis.sceneeditor.accessor.scene2d.CheckBoxAccessor;
import pl.kotcrab.vis.sceneeditor.accessor.scene2d.LabelAccessor;
import pl.kotcrab.vis.sceneeditor.accessor.scene2d.ListAccessor;
import pl.kotcrab.vis.sceneeditor.accessor.scene2d.ProgressBarAccessor;
import pl.kotcrab.vis.sceneeditor.accessor.scene2d.SelectBoxAccessor;
import pl.kotcrab.vis.sceneeditor.accessor.scene2d.TouchpadAccessor;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

/** Simplified SceneEditor for scene2d (and scene2d.ui)
 * 
 * @author Pawel Pastuszak */
public class StageSceneEditor extends SceneEditor {
	private Stage stage;

	private boolean autoSizeLists;


	public StageSceneEditor (FileHandle sceneFile, Stage stage, boolean devMode) {
		super(sceneFile, (OrthographicCamera)stage.getCamera(), devMode);

		this.stage = stage;
	}

	/** Add actor to object list, if accessor for this object class was not registed it won't be added. Also adds actor to stage,
	 * even is accessor is not registed!
	 * 
	 * @param a actor that will be added to list
	 * @param identifier unique identifer, used when saving and loading
	 * 
	 * @return This StageSceneEditor for the purpose of chaining methods together. */
	public StageSceneEditor add (Actor a, String identifier) {
		super.add(a, identifier);
		stage.addActor(a);

		if (autoSizeLists) {
			if (a instanceof List) {
				Widget widget = (Widget)a;
				widget.setSize(200, 200);
			}

			if (a instanceof SelectBox<?>) {
				Widget widget = (Widget)a;
				widget.setWidth(200);
			}
		}

		return this;
	}

	/** Turning this on will automaticly sets all added Lists size to 200, 200. All added SelectBoxes will have width set to 200 <br>
	 * Note: This will only happen for new added objects, after adding objects calling {@link StageSceneEditor#load()} will overide
	 * previously set vales. */
	public void autoSizeLists () {
		autoSizeLists = true;
	}

	/** Disabled auto sizing lists and selectboxed
	 * @see StageSceneEditor#autoSizeLists() */
	public void disableAutoSizeLists () {
		autoSizeLists = false;
	}

	/** Automaticly registers all built-in scene2d.ui accessors.
	 * 
	 * Accessors are registed for {@link Label}, {@link Button}, {@link CheckBox}, {@link ProgressBar}, {@link List},
	 * {@link SelectBox}, {@link Touchpad}
	 * 
	 * @param activeAutoSizeLists if true {@link StageSceneEditor#autoSizeLists()} will be called. */
	public void registerScene2dUIAccessors (boolean activeAutoSizeLists) {
		if (activeAutoSizeLists) autoSizeLists();

		registerAccessor(new LabelAccessor());
		registerAccessor(new ButtonAccessor());
		registerAccessor(new CheckBoxAccessor());
		registerAccessor(new ProgressBarAccessor());
		registerAccessor(new ListAccessor());
		registerAccessor(new SelectBoxAccessor());
		registerAccessor(new TouchpadAccessor());
	}
 }
