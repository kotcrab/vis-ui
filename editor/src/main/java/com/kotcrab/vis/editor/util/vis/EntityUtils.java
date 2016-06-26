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

package com.kotcrab.vis.editor.util.vis;

import com.artemis.Component;
import com.artemis.Entity;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.editor.util.NumberUtils;
import com.kotcrab.vis.editor.util.value.*;
import com.kotcrab.vis.runtime.util.ImmutableArray;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Kotcrab
 */
public class EntityUtils {
	public static String getCommonFloatValue (ImmutableArray<EntityProxy> entities, FloatProxyValue objValue) {
		float value = objValue.getFloat(entities.first());

		for (EntityProxy entity : entities)
			if (value != objValue.getFloat(entity)) return "?";

		return NumberUtils.floatToString(value);
	}

	public static String getCommonFloatValue (ImmutableArray<EntityProxy> proxies, FloatEntityValue objValue) {
		float value = objValue.getFloat(proxies.first().getEntity());

		for (EntityProxy proxy : proxies) {
			if (value != objValue.getFloat(proxy.getEntity())) return "?";
		}

		return NumberUtils.floatToString(value);
	}

	public static String getCommonIntegerValue (ImmutableArray<EntityProxy> entities, IntegerProxyValue objValue) {
		int value = objValue.getInteger(entities.first());

		for (EntityProxy entity : entities)
			if (value != objValue.getInteger(entity)) return "?";

		return String.valueOf(value);
	}

	public static String getCommonIntegerValue (ImmutableArray<EntityProxy> proxies, IntegerEntityValue objValue) {
		int value = objValue.getInteger(proxies.first().getEntity());

		for (EntityProxy proxy : proxies) {
			if (value != objValue.getInteger(proxy.getEntity())) return "?";
		}

		return String.valueOf(value);
	}

	public static String getCommonString (ImmutableArray<EntityProxy> entities, String ifNotCommon, StringProxyValue value) {
		String firstText = value.getString(entities.first());

		for (EntityProxy entity : entities) {
			if (value.getString(entity).equals(firstText) == false) return ifNotCommon;
		}

		return firstText;
	}

	public static String getCommonString (ImmutableArray<EntityProxy> proxies, String ifNotCommon, StringEntityValue value) {
		String firstText = value.getString(proxies.first().getEntity());

		for (EntityProxy proxy : proxies) {
			if (value.getString(proxy.getEntity()).equals(firstText) == false) return ifNotCommon;
		}

		return firstText;
	}

	/** @return common id or null if none exists */
	public static String getCommonId (ImmutableArray<EntityProxy> entities) {
		String firstId = entities.first().getId();
		if (firstId == null) firstId = "";

		for (EntityProxy entity : entities) {
			String entityId = entity.getId();
			if (entityId == null) entityId = "";

			if (firstId.equals(entityId) == false) {
				return null;
			}
		}

		return firstId;
	}

	public static void setCommonCheckBoxState (ImmutableArray<EntityProxy> entities, IndeterminateCheckbox target, BooleanProxyValue value) {
		boolean enabled = value.getBoolean(entities.first());

		for (EntityProxy entity : entities) {
			if (enabled != value.getBoolean(entity)) {
				target.setIndeterminate(true);
				return;
			}
		}

		target.setChecked(enabled);
	}

	public static void setCommonCheckBoxState (ImmutableArray<EntityProxy> proxies, IndeterminateCheckbox target, BooleanEntityValue value) {
		boolean enabled = value.getBoolean(proxies.first().getEntity());

		for (EntityProxy proxy : proxies) {
			if (enabled != value.getBoolean(proxy.getEntity())) {
				target.setIndeterminate(true);
				return;
			}
		}

		target.setChecked(enabled);
	}

	public static boolean isScaleSupportedForEntities (ImmutableArray<EntityProxy> entities) {
		for (EntityProxy entity : entities) {
			if (entity.isScaleSupported() == false) return false;
		}

		return true;
	}

	public static boolean isOriginSupportedForEntities (ImmutableArray<EntityProxy> entities) {
		for (EntityProxy entity : entities) {
			if (entity.isOriginSupported() == false) return false;
		}

		return true;
	}

	public static boolean isRotationSupportedForEntities (ImmutableArray<EntityProxy> entities) {
		for (EntityProxy entity : entities) {
			if (entity.isRotationSupported() == false) return false;
		}

		return true;
	}

	public static boolean isTintSupportedForEntities (ImmutableArray<EntityProxy> entities) {
		for (EntityProxy entity : entities) {
			if (entity.isColorSupported() == false) return false;
		}

		return true;
	}

	public static boolean isFlipSupportedForEntities (ImmutableArray<EntityProxy> entities) {
		for (EntityProxy entity : entities) {
			if (entity.isFlipSupported() == false) return false;
		}

		return true;
	}

	public static boolean isComponentCommon (Component component, ImmutableArray<EntityProxy> entities) {
		return isComponentCommon(component.getClass(), entities);
	}

	public static boolean isComponentCommon (Class<? extends Component> componentClazz, ImmutableArray<EntityProxy> entities) {
		for (EntityProxy entity : entities) {
			if (entity.hasComponent(componentClazz) == false) return false;
		}

		return true;
	}

	public static <T extends Component> void stream (ImmutableArray<EntityProxy> proxies, Class<T> componentClass, BiConsumer<EntityProxy, T> consumer) {
		stream(proxies, proxy -> consumer.accept(proxy, proxy.getComponent(componentClass)));
	}

	public static void stream (ImmutableArray<EntityProxy> proxies, Consumer<EntityProxy> consumer) {
		for (int i = 0; i < proxies.size(); i++) {
			consumer.accept(proxies.get(i));
		}
	}

	public static Entity getFirstEntity (ImmutableArray<EntityProxy> proxies) {
		return proxies.first().getEntity();
	}

	public static <T extends Component> T getFirstEntityComponent (ImmutableArray<EntityProxy> proxies, Class<T> componentClass) {
		return getFirstEntity(proxies).getComponent(componentClass);
	}
}
