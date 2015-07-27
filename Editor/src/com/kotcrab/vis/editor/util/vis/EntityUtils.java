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

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.editor.util.NumberUtils;
import com.kotcrab.vis.editor.util.value.*;

/**
 * @author Kotcrab
 */
public class EntityUtils {
	public static String getEntitiesCommonFloatValue (Array<EntityProxy> entities, FloatProxyValue objValue) {
		float value = objValue.getFloat(entities.first());

		for (EntityProxy entity : entities)
			if (value != objValue.getFloat(entity)) return "?";

		return NumberUtils.floatToString(value);
	}

	public static String getEntitiesCommonIntegerValue (Array<EntityProxy> entities, IntegerProxyValue objValue) {
		int value = objValue.getInteger(entities.first());

		for (EntityProxy entity : entities)
			if (value != objValue.getInteger(entity)) return "?";

		return String.valueOf(value);
	}

	public static void setCommonCheckBoxState (Array<EntityProxy> entities, IndeterminateCheckbox target, BooleanProxyValue value) {
		boolean enabled = value.getBoolean(entities.first());

		for (EntityProxy entity : entities) {
			if (enabled != value.getBoolean(entity)) {
				target.setIndeterminate(true);
				return;
			}
		}

		target.setChecked(enabled);
	}

	public static String getCommonString (Array<EntityProxy> entities, String ifNotCommon, StringProxyValue value) {
		String firstText = value.getString(entities.first());

		for (EntityProxy entity : entities) {
			if (value.getString(entity).equals(firstText) == false) return ifNotCommon;
		}

		return firstText;
	}

	public static String getEntitiesCommonFloatValue (Array<EntityProxy> proxies, FloatEntityValue objValue) {
		float value = objValue.getFloat(proxies.first().getEntities().first());

		for (EntityProxy proxy : proxies) {
			for (Entity entity : proxy.getEntities()) {
				if (value != objValue.getFloat(entity)) return "?";
			}
		}

		return NumberUtils.floatToString(value);
	}

	public static String getEntitiesCommonIntegerValue (Array<EntityProxy> proxies, IntegerEntityValue objValue) {
		int value = objValue.getInteger(proxies.first().getEntities().first());

		for (EntityProxy proxy : proxies) {
			for (Entity entity : proxy.getEntities()) {
				if (value != objValue.getInteger(entity)) return "?";
			}
		}

		return String.valueOf(value);
	}

	public static void setCommonCheckBoxState (Array<EntityProxy> proxies, IndeterminateCheckbox target, BooleanEntityValue value) {
		boolean enabled = value.getBoolean(proxies.first().getEntities().first());

		for (EntityProxy proxy : proxies) {
			for (Entity entity : proxy.getEntities()) {
				if (enabled != value.getBoolean(entity)) {
					target.setIndeterminate(true);
					return;
				}
			}
		}

		target.setChecked(enabled);
	}

	public static String getCommonString (Array<EntityProxy> proxies, String ifNotCommon, StringEntityValue value) {
		String firstText = value.getString(proxies.first().getEntities().first());

		for (EntityProxy proxy : proxies) {
			for (Entity entity : proxy.getEntities()) {
				if (value.getString(entity).equals(firstText) == false) return ifNotCommon;
			}
		}

		return firstText;
	}

	public static String getCommonId (Array<EntityProxy> entities) {
		String firstId = entities.first().getId();
		if (firstId == null) firstId = "";

		for (EntityProxy entity : entities) {
			String entityId = entity.getId();
			if (entityId == null) entityId = "";

			if (firstId.equals(entityId) == false) {
				return "<?>";
			}
		}

		return firstId;
	}

	public static boolean isScaleSupportedForEntities (Array<EntityProxy> entities) {
		for (EntityProxy entity : entities) {
			if (entity.isScaleSupported() == false) return false;
		}

		return true;
	}

	public static boolean isOriginSupportedForEntities (Array<EntityProxy> entities) {
		for (EntityProxy entity : entities) {
			if (entity.isOriginSupported() == false) return false;
		}

		return true;
	}

	public static boolean isRotationSupportedForEntities (Array<EntityProxy> entities) {
		for (EntityProxy entity : entities) {
			if (entity.isRotationSupported() == false) return false;
		}

		return true;
	}

	public static boolean isTintSupportedForEntities (Array<EntityProxy> entities) {
		for (EntityProxy entity : entities) {
			if (entity.isColorSupported() == false) return false;
		}

		return true;
	}

	public static boolean isFlipSupportedForEntities (Array<EntityProxy> entities) {
		for (EntityProxy entity : entities) {
			if (entity.isFlipSupported() == false) return false;
		}

		return true;
	}

	public static boolean isComponentCommon (Component component, Array<EntityProxy> entities) {
		for (EntityProxy entity : entities) {
			if (entity.hasComponent(component.getClass()) == false) return false;
		}

		return true;
	}
}
