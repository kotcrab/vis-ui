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

import com.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import com.kotcrab.vis.editor.module.scene.UndoModule;
import com.kotcrab.vis.editor.module.scene.action.ChangePhysicsSettingsAction;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.util.scene2d.TableBuilder;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.runtime.data.PhysicsSettings;
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;

/**
 * Dialog used to change physics settings
 * @author Kotcrab
 */
public class PhysicsSettingsDialog extends VisWindow {
	private UndoModule undoModule;

	private SceneTab sceneTab;
	private EditorScene scene;

	private VisTextButton cancelButton;
	private VisTextButton saveButton;

	private VisCheckBox enabledCheckbox;
	private VisCheckBox sleepCheckbox;

	private VisTextField gravityXField;
	private VisTextField gravityYField;

	private PhysicsSettings newPhysicsSettings;

	public PhysicsSettingsDialog (SceneModuleContainer sceneMC) {
		super("Physics Settings");
		sceneMC.injectModules(this);
		sceneTab = sceneMC.getSceneTab();
		scene = sceneMC.getScene();

		newPhysicsSettings = new PhysicsSettings(scene.physicsSettings);

		TableUtils.setSpacingDefaults(this);

		addCloseButton();
		closeOnEscape();
		setModal(true);

		left();
		defaults().left();

		createUI();
		createBottomTable();
		createListeners();

		pack();
		centerWindow();

	}

	private void createUI () {
		enabledCheckbox = new VisCheckBox("Physics Enabled");
		sleepCheckbox = new VisCheckBox("Allow Sleep");
		gravityXField = new VisTextField();
		gravityYField = new VisTextField();

		gravityXField.setTextFieldFilter(new FloatDigitsOnlyFilter(true));
		gravityYField.setTextFieldFilter(new FloatDigitsOnlyFilter(true));

		add(enabledCheckbox).row();
		add(sleepCheckbox).row();

		add(TableBuilder.build("Gravity X", 70, gravityXField)).row();
		add(TableBuilder.build("Gravity Y", 70, gravityYField)).row();

		enabledCheckbox.setChecked(newPhysicsSettings.physicsEnabled);
		sleepCheckbox.setChecked(newPhysicsSettings.allowSleep);
		gravityXField.setText(String.valueOf(newPhysicsSettings.gravityX));
		gravityYField.setText(String.valueOf(newPhysicsSettings.gravityY));
	}

	private void createBottomTable () {
		VisTable buttonTable = new VisTable(true);
		buttonTable.defaults().minWidth(70);

		cancelButton = new VisTextButton("Cancel");
		saveButton = new VisTextButton("OK");

		buttonTable.add().fill().expand();
		buttonTable.add(cancelButton);
		buttonTable.add(saveButton);

		add(buttonTable).colspan(2).fill().expand();
		padBottom(5);
	}

	private void createListeners () {
		cancelButton.addListener(new VisChangeListener((event, actor) -> fadeOut()));
		saveButton.addListener(new VisChangeListener((event, actor) -> {
			newPhysicsSettings.physicsEnabled = enabledCheckbox.isChecked();
			newPhysicsSettings.allowSleep = sleepCheckbox.isChecked();
			newPhysicsSettings.gravityX = Float.valueOf(gravityXField.getText());
			newPhysicsSettings.gravityY = Float.valueOf(gravityYField.getText());

			undoModule.execute(new ChangePhysicsSettingsAction(scene, newPhysicsSettings));
			sceneTab.dirty();
			fadeOut();
		}));
	}
}
