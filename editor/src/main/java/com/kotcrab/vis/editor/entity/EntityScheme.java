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

package com.kotcrab.vis.editor.entity;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.annotations.Transient;
import com.artemis.utils.Bag;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.component.*;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.properties.UsesProtoComponent;
import com.kotcrab.vis.runtime.util.EntityEngine;
import com.rits.cloning.Cloner;

import java.util.UUID;

/** @author Kotcrab */
public class EntityScheme {
	private static transient final Bag<Component> fillBag = new Bag<>();

	private transient UUID schemeUUID;
	private Array<Component> components;

	public static EntityScheme of (Entity entity) {
		return new EntityScheme(entity, null, CloningPolicy.DEFAULT);
	}

	public static EntityScheme clonedOf (Entity entity, Cloner cloner, CloningPolicy cloningPolicy) {
		return new EntityScheme(entity, cloner, cloningPolicy);
	}

	public static EntityScheme clonedOf (Entity entity, Cloner cloner) {
		return new EntityScheme(entity, cloner, CloningPolicy.DEFAULT);
	}

	private EntityScheme (Entity entity, Cloner cloner, CloningPolicy cloningPolicy) {
		fillBag.clear();
		entity.getComponents(fillBag);
		components = new Array<>(fillBag.size());

		for (Component component : fillBag) {
			if (component == null) continue;
			if (component.getClass().isAnnotationPresent(Transient.class)) continue;
			if (cloningPolicy == CloningPolicy.SKIP_INVISIBLE && component instanceof Invisible) continue;

			if (component instanceof VisUUID) schemeUUID = ((VisUUID) component).getUUID();

			if (component instanceof UsesProtoComponent) {
				components.add(((UsesProtoComponent) component).toProtoComponent());
			} else {
				components.add(component);
			}
		}

		if (schemeUUID == null) throw new IllegalStateException("Missing VisUUID component in Entity");

		if (cloner != null) {
			components = cloner.deepClone(components);
		}
	}

	public Entity build (EntityEngine engine, Cloner cloner, UUIDPolicy uuidPolicy) {
		EntityBuilder builder = new EntityBuilder(engine);

		Array<Component> clonedComps = cloner.deepClone(components);

		switch (uuidPolicy) {
			case PRESERVE:
				clonedComps.forEach(builder::with);
				break;
			case ASSIGN_NEW:
				for (Component component : clonedComps) {
					if (component instanceof VisUUID) {
						builder.with(new VisUUID()); //will assign new random UUID
					} else {
						builder.with(component);
					}
				}
				break;
		}

		for (Component component : clonedComps) {
			if (component instanceof Transform) ((Transform) component).setDirty(true);
			if (component instanceof Origin) ((Origin) component).setDirty(true);
			if (component instanceof Tint) ((Tint) component).setDirty(true);
		}

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
			if (component.getClass().isAnnotationPresent(ExcludeFromEntityData.class)) continue;

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

	public Array<Component> getComponents () {
		return components;
	}

	/**
	 * Returns this scheme UUID. Note that if scheme was deserlzied a UUID lookup on component will be performed first, if
	 * there is no {@link VisUUID} component an exception will be thrown.
	 * @throws IllegalStateException when VisUUID component is missing on deserialized instance
	 */
	public UUID getSchemeUUID () {
		if (schemeUUID == null) {
			for (Component component : components) {
				if (component instanceof VisUUID) {
					schemeUUID = ((VisUUID) component).getUUID();
					break;
				}
			}

			//uuid was not found
			if (schemeUUID == null) {
				throw new IllegalStateException("Missing VisUUID component in EntityScheme");
			}
		}

		return schemeUUID;
	}

	public enum CloningPolicy {
		DEFAULT, SKIP_INVISIBLE
	}

	public enum UUIDPolicy {
		PRESERVE, ASSIGN_NEW
	}
}
