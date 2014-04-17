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

package pl.kotcrab.vis.sceneeditor;

import pl.kotcrab.vis.sceneeditor.serializer.SceneSerializer;
import pl.kotcrab.vis.sceneeditor.support.ButtonSupport;
import pl.kotcrab.vis.sceneeditor.support.CheckBoxSupport;
import pl.kotcrab.vis.sceneeditor.support.LabelSupport;
import pl.kotcrab.vis.sceneeditor.support.ListSupport;
import pl.kotcrab.vis.sceneeditor.support.ProgressBarSupport;
import pl.kotcrab.vis.sceneeditor.support.SelectBoxSupport;
import pl.kotcrab.vis.sceneeditor.support.TouchpadSupport;

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

	/** Constructs SceneEditor, this contrustor does not create Serializer for you. You must do it manualy using
	 * {@link SceneEditor#setSerializer(SceneSerializer)}
	 * 
	 * @param stage actors will be added to it
	 * @param devMode devMode allow to enter editing mode, if not on desktop it will automaticly be set to false */
	public StageSceneEditor (Stage stage, boolean devMode) {
		super((OrthographicCamera)stage.getCamera(), devMode);

		this.stage = stage;
	}

	/** Constructs SceneEditor with FileSerializer for provied internal file.
	 * 
	 * @param sceneFile path to scene file, typicaly with .json extension
	 * @param stage actors will be added to it
	 * @param devMode devMode allow to enter editing mode, if not on desktop it will automaticly be set to false */
	public StageSceneEditor (FileHandle sceneFile, Stage stage, boolean devMode) {
		super(sceneFile, (OrthographicCamera)stage.getCamera(), devMode);

		this.stage = stage;
	}

	/** Add actor to object list, if support for this object class was not registed it won't be added. Also adds actor to stage,
	 * even is support is not registed!
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
	 * Note: This will only work for new added objects, after adding objects calling {@link StageSceneEditor#load()} will overide
	 * previously set vales. */
	public void autoSizeLists () {
		autoSizeLists = true;
	}

	/** Disabled auto sizing lists and selectboxed
	 * @see StageSceneEditor#autoSizeLists() */
	public void disableAutoSizeLists () {
		autoSizeLists = false;
	}

	/** Automaticly registers all built-in scene2d.ui supports.
	 * 
	 * Support are registed for {@link Label}, {@link Button}, {@link CheckBox}, {@link ProgressBar}, {@link List},
	 * {@link SelectBox}, {@link Touchpad}
	 * 
	 * @param activeAutoSizeLists if true {@link StageSceneEditor#autoSizeLists()} will be called. */
	public void registerScene2dUISupports (boolean activeAutoSizeLists) {
		if (activeAutoSizeLists) autoSizeLists();

		registerSupport(new LabelSupport());
		registerSupport(new ButtonSupport());
		registerSupport(new CheckBoxSupport());
		registerSupport(new ProgressBarSupport());
		registerSupport(new ListSupport());
		registerSupport(new SelectBoxSupport());
		registerSupport(new TouchpadSupport());
	}
}
