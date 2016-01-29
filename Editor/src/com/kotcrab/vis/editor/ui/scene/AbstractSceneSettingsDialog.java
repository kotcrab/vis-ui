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

package com.kotcrab.vis.editor.ui.scene;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

/** @author Kotcrab */
public abstract class AbstractSceneSettingsDialog extends VisWindow {
	protected SceneTab sceneTab;
	protected EditorScene scene;

	private VisTable buttonTable;
	private VisTextButton cancelButton;
	private VisTextButton saveButton;

	public AbstractSceneSettingsDialog (String title, SceneTab tab) {
		super(title);

		this.sceneTab = tab;
		scene = tab.getScene();

		addCloseButton();
		closeOnEscape();
		setModal(true);

		createButtonsTable();

		createUI();
		createListeners();
	}

	private void createButtonsTable () {
		buttonTable = new VisTable(true);
		buttonTable.defaults().minWidth(70);

		cancelButton = new VisTextButton("Cancel");
		saveButton = new VisTextButton("Save");

		buttonTable.add().fill().expand();
		buttonTable.add(cancelButton);
		buttonTable.add(saveButton);
	}

	protected abstract void createUI ();

	protected void createListeners () {
		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				fadeOut();
			}
		});

		saveButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (sceneTab.isDirty()) {
					Dialogs.showOptionDialog(getStage(), "Save settings", "This will save any previous changes in scene, continue?", OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
						@Override
						public void yes () {
							setValuesToSceneAndSave();
						}
					});
				} else
					setValuesToSceneAndSave();

				fadeOut();
			}
		});
	}

	private void setValuesToSceneAndSave () {
		setValuesToScene();
		sceneTab.save();
	}

	protected abstract void setValuesToScene ();

	protected VisTextButton getSaveButton () {
		return saveButton;
	}

	protected VisTextButton getCancelButton () {
		return cancelButton;
	}

	protected VisTable getButtonTable () {
		return buttonTable;
	}
}
