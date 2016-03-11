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
import com.kotcrab.vis.editor.ui.Vector2ArrayView;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.component.VisPolygon;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.runtime.util.autotable.ATVector2Array;

import java.lang.reflect.Field;

/** @author Kotcrab */
public class Vector2ViewFragmentProvider extends AutoTableFragmentProvider<ATVector2Array> {
	private ObjectMap<Field, Vector2ArrayView> views = new ObjectMap<>();

	@Override
	public void createUI (ATVector2Array annotation, Field field, Class<?> fieldType) {
		Vector2ArrayView view = new Vector2ArrayView();
		views.put(field, view);

		uiTable.add(annotation.fieldName()).spaceBottom(3).row();
		uiTable.add(view).expandX().fillX().row();
	}

	@Override
	public void updateUIFromEntities (ImmutableArray<EntityProxy> proxies, Field field, Class<?> fieldType) throws ReflectiveOperationException {
		Vector2ArrayView view = views.get(field);

		if (proxies.size() > 1)
			view.setMultipleSelected(true);
		else
			view.setVectors(EntityUtils.getFirstEntityComponent(proxies, VisPolygon.class).vertices);
	}

	@Override
	public void setToEntities (Component component, Field field, Class<?> fieldType) throws ReflectiveOperationException {

	}

	@Override
	public Actor getUIByField (Class type, Field field) {
		return views.get(field);
	}
}
