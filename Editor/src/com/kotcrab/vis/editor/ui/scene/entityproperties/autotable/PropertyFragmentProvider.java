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

package com.kotcrab.vis.editor.ui.scene.entityproperties.autotable;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.editor.ui.scene.entityproperties.NumberInputField;
import com.kotcrab.vis.editor.util.gdx.FieldUtils;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.util.autotable.ATProperty;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;
import com.kotcrab.vis.ui.util.Validators.LesserThanValidator;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import java.lang.reflect.Field;

/** @author Kotcrab */
public class PropertyFragmentProvider extends AutoTableFragmentProvider<ATProperty> {
	private ObjectMap<Field, NumberInputField> numberFields = new ObjectMap<>();
	private ObjectMap<Field, IndeterminateCheckbox> checkboxFields = new ObjectMap<>();

	@Override
	public void createUI (ATProperty annotation, Class type, Field field) {
		if (type.equals(Integer.TYPE) == false && type.equals(Float.TYPE) == false && type.equals(Boolean.TYPE) == false) {
			throw new UnsupportedOperationException("Field of this type is not supported by EntityPropertyUI: " + type);
		}

		String fieldName = annotation.fieldName().equals("") ? field.getName() : annotation.fieldName();
		if (type.equals(Boolean.TYPE)) {
			IndeterminateCheckbox checkbox = new IndeterminateCheckbox(fieldName);
			checkbox.addListener(properties.getSharedCheckBoxChangeListener());

			VisTable table = new VisTable(true);
			table.add(checkbox).left();
			uiTable.add(table).left().expandX().row();
			checkboxFields.put(field, checkbox);
		} else {
			NumberInputField numberInputField = new NumberInputField(properties.getSharedFocusListener(), properties.getSharedChangeListener(), type.equals(Float.TYPE));

			if (annotation.max() != Float.MAX_VALUE)
				numberInputField.addValidator(new LesserThanValidator(annotation.max(), true));

			if (annotation.min() != Float.MIN_VALUE)
				numberInputField.addValidator(new GreaterThanValidator(annotation.min(), true));

			VisTable table = new VisTable(true);

			table.add(new VisLabel(fieldName)).width(LABEL_WIDTH);
			table.add(numberInputField).width(EntityProperties.FIELD_WIDTH);
			uiTable.add(table).expandX().fillX().row();
			numberFields.put(field, numberInputField);
		}
	}

	@Override
	public void updateUIFromEntities (Array<EntityProxy> proxies, Class type, Field field) {
		if (type.equals(Boolean.TYPE)) {
			IndeterminateCheckbox checkbox = checkboxFields.get(field);

			EntityUtils.setCommonCheckBoxState(proxies, checkbox, (Entity entity) -> {
				try {
					return (boolean) field.get(entity.getComponent(componentClass));
				} catch (IllegalAccessException e) {
					throw new IllegalStateException(e);
				}
			});

		} else {
			NumberInputField inputField = numberFields.get(field);

			if (type.equals(Integer.TYPE)) {
				inputField.setText(EntityUtils.getEntitiesCommonIntegerValue(proxies,
						(Entity entity) -> {
							try {
								return (int) field.get(entity.getComponent(componentClass));
							} catch (IllegalAccessException e) {
								throw new IllegalStateException(e);
							}
						}));
			} else {
				inputField.setText(EntityUtils.getEntitiesCommonFloatValue(proxies,
						(Entity entity) -> {
							try {
								return (float) field.get(entity.getComponent(componentClass));
							} catch (IllegalAccessException e) {
								throw new IllegalStateException(e);
							}
						}));
			}
		}
	}

	@Override
	public void setToEntities (Class type, Field field, Component component) throws ReflectiveOperationException {
		if (type.equals(Boolean.TYPE)) {
			IndeterminateCheckbox checkbox = checkboxFields.get(field);
			if (checkbox.isIndeterminate() == false) field.set(component, checkbox.isChecked());
			return;
		}

		NumberInputField inputField = numberFields.get(field);
		inputField.validateInput();

		if (type.equals(Integer.TYPE)) {
			int value = FieldUtils.getInt(inputField, (Integer) field.get(component));
			field.set(component, value);
		}

		if (type.equals(Float.TYPE)) {
			float value = FieldUtils.getFloat(inputField, (Float) field.get(component));
			field.set(component, value);
		}
	}

	@Override
	public Object getUiByField (Class type, Field field) {
		if (type.equals(Boolean.TYPE))
			return checkboxFields.get(field);
		else
			return numberFields.get(field);
	}
}
