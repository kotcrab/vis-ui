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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.StringStringMapView;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.component.Variables;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.runtime.util.autotable.ATStringStringMap;

import java.lang.reflect.Field;

/** @author Kotcrab */
public class StringStringMapFragmentProvider extends AutoTableFragmentProvider<ATStringStringMap> {
	private ObjectMap<Field, StringStringMapView> views = new ObjectMap<>();

	@Override
	public void createUI (ATStringStringMap annotation, Field field, Class<?> fieldType) throws ReflectiveOperationException {
		StringStringMapView view = new StringStringMapView("No variables", properties);
		views.put(field, view);

		if (annotation.fieldName().equals("") == false) uiTable.add(annotation.fieldName()).spaceBottom(3);
		uiTable.row();
		uiTable.add(view).expandX().fillX().row();
	}

	@Override
	public void updateUIFromEntities (ImmutableArray<EntityProxy> proxies, Field field, Class<?> fieldType) throws ReflectiveOperationException {
		StringStringMapView view = views.get(field);

		if (proxies.size() > 1)
			view.multipleSelected();
		else
			view.setMap(EntityUtils.getFirstEntityComponent(proxies, Variables.class));
	}

	@Override
	public void setToEntities (Component component, Field field, Class<?> fieldType) throws ReflectiveOperationException {
		StringStringMapView view = views.get(field);
		view.updateMapFromUI();
	}

	@Override
	public Actor getUIByField (Class type, Field field) {
		return views.get(field);
	}
}
