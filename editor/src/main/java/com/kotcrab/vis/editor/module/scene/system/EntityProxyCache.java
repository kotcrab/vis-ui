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

package com.kotcrab.vis.editor.module.scene.system;

import com.artemis.*;
import com.artemis.EntitySubscription.SubscriptionListener;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.entity.EntityScheme;
import com.kotcrab.vis.editor.module.project.ProjectExtensionStorageModule;
import com.kotcrab.vis.editor.plugin.api.EditorEntitySupport;
import com.kotcrab.vis.editor.proxy.*;
import com.kotcrab.vis.runtime.component.*;

public class EntityProxyCache extends Manager {
	private static final String TAG = "EntityProxyCache";

	private ProjectExtensionStorageModule projectExtensionStorage;

	private AspectSubscriptionManager subscriptionManager;

	private Array<EntityProxyCacheListener> listeners = new Array<>();
	private ObjectMap<Entity, EntityProxy> cache = new ObjectMap<>();
	private float pixelsPerUnit;

	public EntityProxyCache (float pixelsPerUnit) {
		this.pixelsPerUnit = pixelsPerUnit;
	}

	@Override
	protected void initialize () {
		EntitySubscription subscription = subscriptionManager.get(Aspect.all(Layer.class, Renderable.class));

		subscription.addSubscriptionListener(new SubscriptionListener() {
			@Override
			public void inserted (IntBag entities) {
				ObjectMap<Entity, EntityProxy> tmpCache = new ObjectMap<>();

				int[] data = entities.getData();
				for (int i = 0; i < entities.size(); i++) {
					int entityId = data[i];
					Entity entity = world.getEntity(entityId);
					tmpCache.put(entity, getProxy(entity));
				}

				cache.putAll(tmpCache);
				listeners.forEach(EntityProxyCacheListener::cacheChanged);
			}

			@Override
			public void removed (IntBag entities) {
				int[] data = entities.getData();
				for (int i = 0; i < entities.size(); i++) {
					cache.remove(world.getEntity(data[i]));
				}
				listeners.forEach(EntityProxyCacheListener::cacheChanged);
			}
		});
	}

	private EntityProxy getProxy (Entity entity) {
		EntityProxy proxy = getInternalProxyFor(entity);

		if (proxy == null) {
			for (EditorEntitySupport support : projectExtensionStorage.getEntitySupports()) {
				proxy = support.resolveProxy(entity);
				if (proxy != null)
					return proxy;
			}
		}

		if (proxy == null) {
			proxy = new MissingProxy(entity);
			Log.error(TAG, String.format("Missing proxy for: %s. Entity structure:", entity));
			Log.error(TAG, String.format("=-%s", entity));

			Bag<Component> components = entity.getComponents(new Bag<>());
			for (int i = 0; i < components.size(); i++) {
				String formatString;
				if (i == components.size() - 1)
					formatString = " \\-- %s";
				else
					formatString = " |-- %s";

				Log.error(TAG, String.format(formatString, components.get(i)));
			}
		}

		return proxy;
	}

	private EntityProxy getInternalProxyFor (Entity entity) {
		if (entity.getComponent(VisParticle.class) != null) return new ParticleProxy(entity, pixelsPerUnit);
		if (entity.getComponent(VisSound.class) != null) return new AudioProxy(entity, false, pixelsPerUnit);
		if (entity.getComponent(VisMusic.class) != null) return new AudioProxy(entity, true, pixelsPerUnit);
		if (entity.getComponent(VisText.class) != null) return new TextProxy(entity);
		if (entity.getComponent(Point.class) != null) return new PointProxy(entity, pixelsPerUnit);
		if (entity.getComponent(VisSprite.class) != null) return new SpriteProxy(entity);

		return null;
	}

	public EntityProxy get (int entityId) {
		return get(world.getEntity(entityId));
	}

	public EntityProxy get (Entity entity) {
		EntityProxy proxy = cache.get(entity);

		if (proxy == null) {
			proxy = getProxy(entity);
			cache.put(entity, proxy);
		}

		return proxy;
	}

	public Array<EntityScheme> getEntitySchemes () {
		Array<EntityScheme> schemes = new Array<>(cache.size);
		cache.values().forEach(proxy -> schemes.add(proxy.getScheme()));
		return schemes;
	}

	public ObjectMap<Entity, EntityProxy> getCache () {
		return cache;
	}

	public void addListener (EntityProxyCacheListener listener) {
		listeners.add(listener);
	}

	public boolean removeListener (EntityProxyCacheListener listener) {
		return listeners.removeValue(listener, true);
	}

	public interface EntityProxyCacheListener {
		void cacheChanged ();
	}
}
