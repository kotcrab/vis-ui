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

package com.kotcrab.vis.editor.util.vis;

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.editor.util.NumberUtils;
import com.kotcrab.vis.editor.util.value.BooleanValue;
import com.kotcrab.vis.editor.util.value.FloatValue;
import com.kotcrab.vis.editor.util.value.StringValue;
import com.kotcrab.vis.runtime.entity.Entity;

/**
 * {@link Entity} and {@link EditorObject} related utils.
 * @author Kotcrab
 */
public class EntityUtils {
	public static String getEntitiesCommonFloatValue (Array<EditorObject> entities, FloatValue objValue) {
		float value = objValue.getFloat(entities.first());

		for (EditorObject entity : entities)
			if (value != objValue.getFloat(entity)) return "?";

		return NumberUtils.floatToString(value);
	}

	public static void setCommonCheckBoxState (Array<EditorObject> entities, IndeterminateCheckbox target, BooleanValue value) {
		boolean enabled = value.getBoolean(entities.first());

		for (EditorObject entity : entities) {
			if (enabled != value.getBoolean(entity)) {
				target.setIndeterminate(true);
				return;
			}
		}

		target.setChecked(enabled);
	}

	public static String getCommonString (Array<EditorObject> entities, String ifNotCommon, StringValue value) {
		String firstText = value.getString(entities.first());

		for (EditorObject entity : entities) {
			if (value.getString(entity).equals(firstText) == false) return ifNotCommon;
		}

		return firstText;
	}

	public static String getCommonId (Array<EditorObject> entities) {
		String firstId = entities.first().getId();
		if (firstId == null) firstId = "";

		for (EditorObject entity : entities) {
			String entityId = entity.getId();
			if (entityId == null) entityId = "";

			if (firstId.equals(entityId) == false) {
				return "<?>";
			}
		}

		return firstId;
	}

	public static boolean isScaleSupportedForEntities (Array<EditorObject> entities) {
		for (EditorObject entity : entities) {
			if (entity.isScaleSupported() == false) return false;
		}

		return true;
	}

	public static boolean isOriginSupportedForEntities (Array<EditorObject> entities) {
		for (EditorObject entity : entities) {
			if (entity.isOriginSupported() == false) return false;
		}

		return true;
	}

	public static boolean isRotationSupportedForEntities (Array<EditorObject> entities) {
		for (EditorObject entity : entities) {
			if (entity.isRotationSupported() == false) return false;
		}

		return true;
	}

	public static boolean isTintSupportedForEntities (Array<EditorObject> entities) {
		for (EditorObject entity : entities) {
			if (entity.isTintSupported() == false) return false;
		}

		return true;
	}

	public static boolean isFlipSupportedForEntities (Array<EditorObject> entities) {
		for (EditorObject entity : entities) {
			if (entity.isFlipSupported() == false) return false;
		}

		return true;
	}
}
