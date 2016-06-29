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

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.scene.UndoModule;
import com.kotcrab.vis.editor.module.scene.action.ChangeLayerProperties;
import com.kotcrab.vis.editor.scene.EditorLayer;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.EnumSelectBox;
import com.kotcrab.vis.editor.util.scene2d.TableBuilder;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.editor.util.vis.PrettyEnumNameProvider;
import com.kotcrab.vis.runtime.scene.LayerCordsSystem;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.form.FormInputValidator;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.*;

/** @author Kotcrab */
public class LayerSettingsDialog extends VisWindow {
	private UndoModule undoModule;

	public LayerSettingsDialog (ModuleInjector injector, EditorScene scene) {
		super("Layer Settings");
		injector.injectModules(this);

		EditorLayer layer = scene.getActiveLayer();

		TableUtils.setSpacingDefaults(this);
		setModal(true);
		addCloseButton();
		closeOnEscape();

		defaults().left();

		VisLabel idLabel = new VisLabel();
		idLabel.setColor(Color.GRAY);
		VisValidatableTextField nameField = new VisValidatableTextField();
		EnumSelectBox<LayerCordsSystem> cordsSelectBox = new EnumSelectBox<>(LayerCordsSystem.class, new PrettyEnumNameProvider<>());
		add(new VisLabel("Layer ID"), idLabel);
		row();
		add(new VisLabel("Name"), nameField);
		row();
		add(new VisLabel("Coordinates system"));
		add(cordsSelectBox).width(150);
		row();

		idLabel.setText(String.valueOf(layer.id));
		nameField.setText(layer.name);
		cordsSelectBox.setSelectedEnum(layer.cordsSystem);

		VisLabel errorLabel = new VisLabel();
		errorLabel.setColor(Color.RED);

		VisTextButton cancelButton = new VisTextButton("Cancel");
		VisTextButton applyButton = new VisTextButton("Apply");

		cancelButton.addListener(new VisChangeListener((event, actor) -> fadeOut()));
		applyButton.addListener(new VisChangeListener((event, actor) -> {
			undoModule.execute(new ChangeLayerProperties(scene, layer, nameField.getText(), cordsSelectBox.getSelectedEnum()));
			fadeOut();
		}));

		VisTable bottomTable = new VisTable();

		bottomTable.add(errorLabel).expandX().fillX();
		bottomTable.add(TableBuilder.build(cancelButton, applyButton)).padTop(2).padLeft(3).padBottom(3).right();

		add(bottomTable).colspan(2).expandX().fillX();

		FormValidator validator = new FormValidator(applyButton, errorLabel);
		validator.notEmpty(nameField, "Name cannot be empty");
		validator.custom(nameField, new FormInputValidator("This name is already used") {
			@Override
			protected boolean validate (String input) {
				if (input.equals(layer.name)) return true;

				return scene.getLayerByName(input) == null;
			}
		});

		pack();
		centerWindow();
	}

}
