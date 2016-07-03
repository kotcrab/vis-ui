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

package com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.provider;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.editor.ui.scene.entityproperties.NumberInputField;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.accessor.DirectFieldAccessor;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.accessor.GetterSetterFieldAccessor;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.accessor.FieldAccessor;
import com.kotcrab.vis.editor.util.scene2d.FieldUtils;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.runtime.util.autotable.ATProperty;
import com.kotcrab.vis.runtime.util.autotable.ATReflectedProperty;
import com.kotcrab.vis.runtime.util.autotable.ATUseGetterSetter;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;
import com.kotcrab.vis.ui.util.Validators.LesserThanValidator;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** @author Kotcrab */
public class PropertyFragmentProvider extends AutoTableFragmentProvider<ATProperty> {
	private ObjectMap<Field, FieldAccessor> propertyAccessors = new ObjectMap<>();
	private ObjectMap<Field, NumberInputField> numberFields = new ObjectMap<>();
	private ObjectMap<Field, IndeterminateCheckbox> checkboxFields = new ObjectMap<>();

	@Override
	public void createUI (ATProperty annotation, Field field, Class<?> fieldType) throws ReflectiveOperationException {
		ATReflectedProperty reflAnnotation = field.getDeclaredAnnotation(ATReflectedProperty.class);
		if (reflAnnotation != null)
			fieldType = reflAnnotation.targetType();

		if (fieldType.equals(Integer.TYPE) == false && fieldType.equals(Float.TYPE) == false && fieldType.equals(Boolean.TYPE) == false
				&& fieldType.equals(Integer.class) == false && fieldType.equals(Float.class) == false && fieldType.equals(Boolean.class) == false) {
			throw new UnsupportedOperationException("Field of this type is not supported by PropertyFragmentProvider: " + fieldType);
		}

		String fieldName = annotation.fieldName().equals("") ? field.getName() : annotation.fieldName();
		String tooltipText = annotation.tooltip();

		if (fieldType.equals(Boolean.TYPE)) {
			IndeterminateCheckbox checkbox = new IndeterminateCheckbox(fieldName);
			checkbox.addListener(properties.getSharedCheckBoxChangeListener());

			if (tooltipText.equals("") == false) new Tooltip.Builder(tooltipText).target(checkbox).build();

			VisTable table = new VisTable(true);
			table.add(checkbox).left();
			uiTable.add(table).left().expandX().row();
			checkboxFields.put(field, checkbox);
		} else {
			NumberInputField numberInputField = new NumberInputField(properties.getSharedFocusListener(), properties.getSharedChangeListener(), fieldType.equals(Float.TYPE));
			if (tooltipText.equals("") == false) new Tooltip.Builder(tooltipText).target(numberInputField).build();

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

		if (reflAnnotation != null) {
			propertyAccessors.put(field, new NestedGetterSetterAccessor(field, reflAnnotation.targetType(), reflAnnotation.getterName(), reflAnnotation.setterName()));
		} else {
			if (field.isAnnotationPresent(ATUseGetterSetter.class)) {
				propertyAccessors.put(field, new GetterSetterFieldAccessor(field));
			} else {
				propertyAccessors.put(field, new DirectFieldAccessor(field));
			}
		}
	}

	@Override
	public void updateUIFromEntities (ImmutableArray<EntityProxy> proxies, Field field, Class<?> fieldType) {
		ATReflectedProperty reflection = field.getDeclaredAnnotation(ATReflectedProperty.class);
		if (reflection != null)
			fieldType = reflection.targetType();

		if (fieldType.equals(Boolean.TYPE)) {
			IndeterminateCheckbox checkbox = checkboxFields.get(field);

			EntityUtils.setCommonCheckBoxState(proxies, checkbox, (Entity entity) -> {
				try {
					return (boolean) propertyAccessors.get(field).get(entity.getComponent(componentClass));
				} catch (ReflectiveOperationException e) {
					throw new IllegalStateException(e);
				}
			});

		} else {
			NumberInputField inputField = numberFields.get(field);

			if (fieldType.equals(Integer.TYPE)) {
				inputField.setText(EntityUtils.getCommonIntegerValue(proxies,
						(Entity entity) -> {
							try {
								return (int) propertyAccessors.get(field).get(entity.getComponent(componentClass));
							} catch (ReflectiveOperationException e) {
								throw new IllegalStateException(e);
							}
						}));
			} else {
				inputField.setText(EntityUtils.getCommonFloatValue(proxies,
						(Entity entity) -> {
							try {
								return (float) propertyAccessors.get(field).get(entity.getComponent(componentClass));
							} catch (ReflectiveOperationException e) {
								throw new IllegalStateException(e);
							}
						}));
			}
		}
	}

	@Override
	public void setToEntities (Component component, Field field, Class<?> fieldType) throws ReflectiveOperationException {
		ATReflectedProperty reflection = field.getDeclaredAnnotation(ATReflectedProperty.class);
		if (reflection != null)
			fieldType = reflection.targetType();

		if (fieldType.equals(Boolean.TYPE)) {
			IndeterminateCheckbox checkbox = checkboxFields.get(field);
			if (checkbox.isIndeterminate() == false) propertyAccessors.get(field).set(component, checkbox.isChecked());
			return;
		}

		NumberInputField inputField = numberFields.get(field);
		inputField.validateInput();

		if (fieldType.equals(Integer.TYPE)) {
			int value = FieldUtils.getInt(inputField, (Integer) propertyAccessors.get(field).get(component));
			propertyAccessors.get(field).set(component, value);
		}

		if (fieldType.equals(Float.TYPE)) {
			float value = FieldUtils.getFloat(inputField, (Float) propertyAccessors.get(field).get(component));
			propertyAccessors.get(field).set(component, value);
		}
	}

	@Override
	public Actor getUIByField (Class type, Field field) {
		if (type.equals(Boolean.TYPE))
			return checkboxFields.get(field);
		else
			return numberFields.get(field);
	}

	private static class NestedGetterSetterAccessor implements FieldAccessor {
		private Field targetField;
		private final Method getter;
		private final Method setter;

		public NestedGetterSetterAccessor (Field targetField, Class<?> parameter, String getterName, String setterName) throws ReflectiveOperationException {
			this.targetField = targetField;
			Class<?> type = targetField.getType();
			getter = type.getMethod(getterName);
			setter = type.getMethod(setterName, parameter);

			if (getter.getReturnType().equals(parameter) == false) {
				throw new IllegalStateException("Invalid ATProperty, getter: " + getterName + " for type: " + type
						+ " has invalid return type: " + getter.getReturnType() + ", should be: " + parameter);
			}
		}

		@Override
		public Object get (Object obj) throws ReflectiveOperationException {
			return getter.invoke(targetField.get(obj));
		}

		@Override
		public void set (Object obj, Object value) throws ReflectiveOperationException {
			setter.invoke(targetField.get(obj), value);
		}
	}

}
