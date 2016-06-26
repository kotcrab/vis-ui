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

package com.kotcrab.vis.editor.ui.scene.entityproperties.autotable;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.runtime.component.Variables;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.*;

import java.util.Optional;

/** @author Kotcrab */
public class StringStringMapView extends VisTable {
	private static final int FIELD_WIDTH = 100;

	private String emptyMsg;
	private Optional<EntityProperties> properties;
	private boolean multipleSelected;

	private ObjectMap<String, String> map;

	private KeyExistsValidator keyExistsValidator = new KeyExistsValidator();

	private Array<VisValidatableTextField> keyFields = new Array<>();
	private Array<VisTextField> valueFields = new Array<>();
	private VisValidatableTextField newVariableField;

	/** @param properties may be null if not using with {@link EntityProperties} */
	public StringStringMapView (String emptyMsg, EntityProperties properties) {
		this.emptyMsg = emptyMsg;
		this.properties = Optional.ofNullable(properties);
		updateUIFromMap();

		left();
		defaults().left().spaceBottom(8);

		newVariableField = new VisValidatableTextField(input -> {
			if (map == null || map.size == 0) return true;
			return !map.containsKey(input);
		});

		newVariableField.setMessageText("Add...");
		newVariableField.addListener(new InputListener() {
			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					String newKey = newVariableField.getText();
					if (map.containsKey(newKey)) {
						Dialogs.showErrorDialog(getStage(), "Variable with that name already exists!");
					}

					newVariableField.setText("");
					map.put(newKey, "");
					updateUIFromMap();
					return true;
				} else
					return false;
			}
		});
	}

	public void setMap (Variables vars) {
		setMap(vars.variables);
	}

	public void setMap (ObjectMap<String, String> newMap) {
		map = newMap;
		multipleSelected = false;
		updateUIFromMap();
	}

	public void multipleSelected () {
		multipleSelected = true;
		map = null;
		updateUIFromMap();
	}

	private void updateUIFromMap () {
		clearChildren();

		keyFields.clear();
		valueFields.clear();

		if (multipleSelected == false) {
			if (map == null || map.size == 0) {
				add(emptyMsg).row();
			} else {
				add(new VisLabel("Key")).spaceBottom(3);
				add(new VisLabel("Value")).spaceBottom(3).row();

				for (Entry<String, String> e : map.entries()) {
					//TODO: [misc] we can reuse fields to reduce overhead
					VisValidatableTextField keyField = new VisValidatableTextField(keyExistsValidator);
					VisTextField valueField = new VisTextField();
					VisImageButton deleteButton = new VisImageButton(Icons.CLOSE.drawable());

					keyFields.add(keyField);
					valueFields.add(valueField);

					keyField.setText(e.key);
					valueField.setText(e.value);

					keyField.setRestoreLastValid(true);

					properties.ifPresent(props -> {
						props.setupStdPropertiesTextField(keyField);
						props.setupStdPropertiesTextField(valueField);
					});

					add(keyField).width(FIELD_WIDTH).padRight(4);
					add(valueField).width(FIELD_WIDTH).padRight(4);
					add(deleteButton).height(valueField.getHeight());
					row();

					deleteButton.addListener(new VisChangeListener((event, actor) -> {
						properties.ifPresent(EntityProperties::beginSnapshot);
						map.remove(keyField.getText());
						updateUIFromMap();
						properties.ifPresent(EntityProperties::endSnapshot);
					}
					));
				}
			}

			add(newVariableField).width(FIELD_WIDTH);
		} else {
			add("<select only one entity>");
		}
	}

	public void updateMapFromUI () {
		if (map == null) return;

		map.clear();

		for (int i = 0; i < keyFields.size; i++) {
			VisValidatableTextField keyField = keyFields.get(i);
			VisTextField valueField = valueFields.get(i);

			if (keyField.isInputValid() == false) keyField.restoreLastValidText();

			map.put(keyField.getText(), valueField.getText());
		}
	}

	private class KeyExistsValidator implements InputValidator {
		@Override
		public boolean validateInput (String input) {
			int count = 0;

			for (VisValidatableTextField field : keyFields) {
				if (field.getText().equals(input))
					count++;
			}

			return !(count > 1);
		}
	}
}
