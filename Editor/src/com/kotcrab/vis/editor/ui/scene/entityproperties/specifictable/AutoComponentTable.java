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

package com.kotcrab.vis.editor.ui.scene.entityproperties.specifictable;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.editor.ui.scene.entityproperties.NumberInputField;
import com.kotcrab.vis.editor.util.gdx.FieldUtils;
import com.kotcrab.vis.editor.util.gdx.IntDigitsOnlyFilter;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.util.EntityPropertyUI;
import com.kotcrab.vis.ui.widget.VisLabel;

import java.lang.reflect.Field;

/**
 * Uses magic of annotations and reflection to automatically build and update specific table for component.
 * Only some primitives fields are supported. Component fields must be marked with {@link EntityPropertyUI} annotation.
 * @author Kotcrab
 */
public abstract class AutoComponentTable<T extends Component> extends SpecificComponentTable<T> {
	private Class<T> componentClass;
	private ObjectMap<Field, NumberInputField> numberFields = new ObjectMap<>();

	public AutoComponentTable (Class<T> componentClass) {
		super(true);
		this.componentClass = componentClass;
	}

	@Override
	protected void init () {
		defaults().left();
		left();

		for (Field field : componentClass.getDeclaredFields()) {
			Class type = field.getType();
			EntityPropertyUI propertyUI = field.getAnnotation(EntityPropertyUI.class);

			if (propertyUI == null) continue;

			if (type.equals(Integer.TYPE) == false && type.equals(Float.TYPE) == false) {
				throw new UnsupportedOperationException("Field of this type is not supported by AutoComponentTable: " + type);
			}

			String fieldName = propertyUI.fieldName().equals("") ? field.getName() : propertyUI.fieldName();
			NumberInputField numberInputField = new NumberInputField(properties.getSharedFocusListener(), properties.getSharedChangeListener());

			if (type.equals(Integer.TYPE)) numberInputField.setTextFieldFilter(new IntDigitsOnlyFilter());

			add(new VisLabel(fieldName)).width(EntityProperties.LABEL_WIDTH);
			add(numberInputField).width(EntityProperties.FIELD_WIDTH);
			add().expandX().fillX().row();
			numberFields.put(field, numberInputField);
		}

		componentClass.getAnnotation(EntityPropertyUI.class);
	}

	@Override
	public Class<T> getComponentClass () {
		return componentClass;
	}

	@Override
	public void updateUIValues () {
		Array<EntityProxy> proxies = properties.getProxies();

		for (Field field : componentClass.getDeclaredFields()) {
			Class type = field.getType();
			EntityPropertyUI propertyUI = field.getAnnotation(EntityPropertyUI.class);

			if (propertyUI == null) continue;

			if (type.equals(Integer.TYPE) || type.equals(Float.TYPE)) {
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
	}

	@Override
	public void setValuesToEntities () {
		for (EntityProxy proxy : properties.getProxies()) {
			for (Entity entity : proxy.getEntities()) {

				T component = entity.getComponent(componentClass);

				for (Field field : componentClass.getDeclaredFields()) {
					Class type = field.getType();
					EntityPropertyUI propertyUI = field.getAnnotation(EntityPropertyUI.class);

					if (propertyUI == null) continue;

					if (type.equals(Integer.TYPE)) {
						try {
							int value = FieldUtils.getInt(numberFields.get(field), (int) field.get(component));
							field.set(component, value);
						} catch (IllegalAccessException e) {
							throw new IllegalStateException(e);
						}
					}

					if (type.equals(Float.TYPE)) {
						try {
							float value = FieldUtils.getFloat(numberFields.get(field), (float) field.get(component));
							field.set(component, value);
						} catch (IllegalAccessException e) {
							throw new IllegalStateException(e);
						}
					}
				}
			}
		}
	}
}
