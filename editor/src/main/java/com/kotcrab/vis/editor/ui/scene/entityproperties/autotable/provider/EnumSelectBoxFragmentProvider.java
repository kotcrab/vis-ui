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
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.AutoTableEnumSelectBox;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.accessor.DirectFieldAccessor;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.accessor.GetterSetterFieldAccessor;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.accessor.FieldAccessor;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.runtime.util.autotable.ATEnumProperty;
import com.kotcrab.vis.runtime.util.autotable.ATUseGetterSetter;
import com.kotcrab.vis.runtime.util.autotable.EnumNameProvider;
import com.kotcrab.vis.ui.widget.VisTable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/** @author Kotcrab */
public class EnumSelectBoxFragmentProvider extends AutoTableFragmentProvider<ATEnumProperty> {
	private ObjectMap<Field, FieldAccessor> propertyAccessors = new ObjectMap<>();
	private ObjectMap<Field, EnumSelectBoxSet> enumSelectBoxes = new ObjectMap<>();

	@Override
	public void createUI (ATEnumProperty annotation, Field field, Class<?> fieldType) throws ReflectiveOperationException {
		String fieldName = annotation.fieldName().equals("") ? field.getName() : annotation.fieldName();

		Constructor<? extends EnumNameProvider> constructor = annotation.uiNameProvider().getConstructor();
		EnumNameProvider nameProvider = constructor.newInstance();

		AutoTableEnumSelectBox selectBox = new AutoTableEnumSelectBox(fieldType, nameProvider);
		selectBox.getSelection().setProgrammaticChangeEvents(false);
		selectBox.addListener(properties.getSharedSelectBoxChangeListener());
		enumSelectBoxes.put(field, new EnumSelectBoxSet(selectBox, nameProvider));

		if (field.isAnnotationPresent(ATUseGetterSetter.class)) {
			propertyAccessors.put(field, new GetterSetterFieldAccessor(field));
		} else {
			propertyAccessors.put(field, new DirectFieldAccessor(field));
		}

		VisTable table = new VisTable(true);
		table.add(fieldName);
		table.add(selectBox).expandX().fillX().left();
		uiTable.add(table).left().expandX().fillX().row();
	}

	@Override
	public void updateUIFromEntities (ImmutableArray<EntityProxy> proxies, Field field, Class<?> fieldType) throws ReflectiveOperationException {
		EnumSelectBoxSet set = enumSelectBoxes.get(field);

		String commonValue = EntityUtils.getCommonString(proxies, AutoTableEnumSelectBox.INDETERMINATE,
				(Entity entity) -> {
					try {
						return set.enumNameProvider.getPrettyName((Enum) propertyAccessors.get(field).get(entity.getComponent(componentClass)));
					} catch (ReflectiveOperationException e) {
						throw new IllegalStateException(e);
					}
				});

		if (commonValue.equals(AutoTableEnumSelectBox.INDETERMINATE)) {
			set.selectBox.setIndeterminate(true);
		} else {
			set.selectBox.setIndeterminate(false);
			set.selectBox.setSelectedEnum((Enum) propertyAccessors.get(field).get(proxies.first().getComponent(componentClass)));
		}
	}

	@Override
	public void setToEntities (Component component, Field field, Class<?> fieldType) throws ReflectiveOperationException {
		EnumSelectBoxSet set = enumSelectBoxes.get(field);

		if (set.selectBox.isIndeterminate() == false) {
			propertyAccessors.get(field).set(component, set.selectBox.getSelectedEnum());
		}
	}

	@Override
	public Actor getUIByField (Class type, Field field) {
		EnumSelectBoxSet set = enumSelectBoxes.get(field);
		if (set == null) return null;
		return set.selectBox;
	}

	private static class EnumSelectBoxSet {
		public AutoTableEnumSelectBox selectBox;
		public EnumNameProvider enumNameProvider;

		public EnumSelectBoxSet (AutoTableEnumSelectBox selectBox, EnumNameProvider enumNameProvider) {
			this.selectBox = selectBox;
			this.enumNameProvider = enumNameProvider;
		}
	}
}
