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

import com.artemis.Component;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.scene.entityproperties.ComponentTable;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.provider.*;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.runtime.util.autotable.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Uses magic of annotations and reflection to automatically build and update specific table for component.
 * Component fields must be marked with one of auto table annotations.
 * (see util.autotable package in runtime).
 * @author Kotcrab
 */
public class AutoComponentTable<T extends Component> extends ComponentTable<T> {
	private Class<T> componentClass;
	private ObjectMap<String, Field> fieldIdsMap = new ObjectMap<>();

	private ModuleInjector injector;
	private boolean removable;

	private ObjectMap<Class<? extends Annotation>, AutoTableFragmentProvider<?>> fragmentProviders = new ObjectMap<>();

	public AutoComponentTable (ModuleInjector sceneMC, Class<T> componentClass, boolean removable) {
		super(true);
		this.injector = sceneMC;
		this.removable = removable;
		this.componentClass = componentClass;
		sceneMC.injectModules(this);

		//TODO: [plugin] plugin entry point
		fragmentProviders.put(ATProperty.class, new PropertyFragmentProvider());
		fragmentProviders.put(ATTextProperty.class, new TextPropertyFragmentProvider());
		fragmentProviders.put(ATSelectFile.class, new SelectFileFragmentProvider());
		fragmentProviders.put(ATVector2Array.class, new Vector2ViewFragmentProvider());
		fragmentProviders.put(ATEnumProperty.class, new EnumSelectBoxFragmentProvider());
		fragmentProviders.put(ATStringStringMap.class, new StringStringMapFragmentProvider());
	}

	@Override
	public Class<T> getComponentClass () {
		return componentClass;
	}

	@Override
	public boolean isRemovable () {
		return removable;
	}

	@Override
	protected void init () {
		//init fragment providers
		for (AutoTableFragmentProvider<?> provider : fragmentProviders.values()) {
			provider.setObjects(componentClass, this, injector, properties);
			injector.injectModules(provider);
			provider.init();
		}

		defaults().left();
		left();

		try {

			for (Field field : componentClass.getDeclaredFields()) {
				Class<?> fieldType = field.getType();
				for (Annotation annotation : field.getAnnotations()) {
					if (annotation instanceof ATFieldId) {
						ATFieldId atFieldId = (ATFieldId) annotation;
						fieldIdsMap.put(atFieldId.id(), field);
						continue;
					}

					AutoTableFragmentProvider fragmentProvider = fragmentProviders.get(annotation.annotationType());
					if (fragmentProvider != null) {
						fragmentProvider.createUI(annotation, field, fieldType);
					}
				}
			}

		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void updateUIValues () {
		try {

			ImmutableArray<EntityProxy> proxies = properties.getSelectedEntities();

			for (Field field : componentClass.getDeclaredFields()) {
				Class<?> type = field.getType();
				for (Annotation annotation : field.getAnnotations()) {
					AutoTableFragmentProvider<?> fragmentProvider = fragmentProviders.get(annotation.annotationType());
					if (fragmentProvider != null) {
						fragmentProvider.updateUIFromEntities(proxies, field, type);
					}
				}
			}

		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void setValuesToEntities () {
		try {

			for (EntityProxy proxy : properties.getSelectedEntities()) {
				T component = proxy.getComponent(componentClass);

				for (Field field : componentClass.getDeclaredFields()) {
					Class<?> type = field.getType();
					for (Annotation annotation : field.getAnnotations()) {
						AutoTableFragmentProvider<?> fragmentProvider = fragmentProviders.get(annotation.annotationType());
						if (fragmentProvider != null) {
							fragmentProvider.setToEntities(component, field, type);
						}
					}
				}
			}

		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}

	protected <T1> T1 getUIByFieldId (String fieldId, Class<T1> widgetType) {
		Field field = fieldIdsMap.get(fieldId);
		if (field == null)
			throw new IllegalStateException("Could not find field with fieldId: " + fieldId + " in type " + componentClass.getSimpleName());

		Class<?> type = field.getType();
		for (AutoTableFragmentProvider<?> provider : fragmentProviders.values()) {
			Object actor = provider.getUIByField(type, field);
			if (actor != null) return widgetType.cast(actor);
		}

		throw new IllegalStateException("Registered fragment providers could not return field with fieldId: " + fieldId + " in type " + componentClass.getSimpleName());
	}
}
