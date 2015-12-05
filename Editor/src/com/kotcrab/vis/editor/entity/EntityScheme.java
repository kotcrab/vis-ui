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

package com.kotcrab.vis.editor.entity;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.annotations.Transient;
import com.artemis.utils.Bag;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.component.VisGroup;
import com.kotcrab.vis.runtime.component.VisID;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.properties.UsesProtoComponent;
import com.kotcrab.vis.runtime.util.EntityEngine;

/** @author Kotcrab */
public class EntityScheme {
	private static transient final Bag<Component> fillBag = new Bag<>();

	public Array<Component> components;

	public EntityScheme (Entity entity) {
		fillBag.clear();
		entity.getComponents(fillBag);
		components = new Array<>(fillBag.size());

		for (Component component : fillBag) {
			if (component.getClass().isAnnotationPresent(Transient.class)) continue;

			if (component instanceof UsesProtoComponent) {
				components.add(((UsesProtoComponent) component).toProtoComponent());
			} else {
				components.add(component);
			}
		}

	}

	public Entity build (EntityEngine engine) {
		EntityBuilder builder = new EntityBuilder(engine);

		components.forEach(builder::with);

		return builder.build();
	}

	public EntityData toData () {
		Array<Component> dataComponents = new Array<>();

		ExporterDropsComponent dropsComponent = null;

		for (Component component : components) {
			if (component instanceof ExporterDropsComponent) {
				dropsComponent = (ExporterDropsComponent) component;
				break;
			}
		}

		for (Component component : components) {
			if (component instanceof ExporterDropsComponent || component instanceof UUIDComponent)
				continue;

			if (dropsComponent != null && dropsComponent.componentsToDrop.contains(component.getClass(), false))
				continue;

			if (component instanceof UsesProtoComponent) {
				dataComponents.add(((UsesProtoComponent) component).toProtoComponent());
			} else if (component instanceof VisGroup) { //strip empty GroupComponents
				VisGroup gdc = (VisGroup) component;
				if (gdc.groupIds.size > 0) dataComponents.add(component);
			} else if (component instanceof VisID) { //strip empty IDComponents
				VisID idc = (VisID) component;
				if (idc.id != null && idc.id.equals("") == false) {
					dataComponents.add(component);
				}
			} else {
				dataComponents.add(component);
			}
		}

		return new EntityData(dataComponents);
	}
}
