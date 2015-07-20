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

package com.kotcrab.vis.editor.module.scene;

import com.artemis.*;
import com.artemis.EntitySubscription.SubscriptionListener;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.entity.EntityScheme;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.project.SupportModule;
import com.kotcrab.vis.editor.plugin.EditorEntitySupport;
import com.kotcrab.vis.editor.proxy.*;
import com.kotcrab.vis.runtime.component.*;

@Wire
public class EntityProxyCache extends Manager {
	@InjectModule private SupportModule supportModule;

	private AspectSubscriptionManager subscriptionManager;

	private EntitySubscription subscription;

	private ObjectMap<Entity, EntityProxy> cache = new ObjectMap<>();
	private float pixelsPerUnit;

	public EntityProxyCache (float pixelsPerUnit) {
		this.pixelsPerUnit = pixelsPerUnit;
	}

	@Override
	protected void initialize () {
		subscription = subscriptionManager.get(Aspect.all(LayerComponent.class, RenderableComponent.class));

		subscription.addSubscriptionListener(new SubscriptionListener() {
			@Override
			public void inserted (ImmutableBag<Entity> entities) {
				ObjectMap<Entity, EntityProxy> tmpCache = new ObjectMap<>();

				entities.forEach(entity -> tmpCache.put(entity, getProxy(entity)));

				cache.putAll(tmpCache);
			}

			@Override
			public void removed (ImmutableBag<Entity> entities) {
				entities.forEach(cache::remove);
			}
		});
	}

	private EntityProxy getProxy (Entity entity) {
		EntityProxy proxy = getInternalProxyFor(entity);

		if (proxy == null) {
			for (EditorEntitySupport support : supportModule.getSupports()) {
				proxy = support.resolveProxy(entity);
				if (proxy != null)
					return proxy;
			}
		}

		if (proxy == null)
			throw new IllegalStateException("Could not find appropriate proxy for entity");

		return proxy;
	}

	private EntityProxy getInternalProxyFor (Entity entity) {
		if (entity.getComponent(ParticleComponent.class) != null) return new ParticleProxy(entity);
		if (entity.getComponent(SoundComponent.class) != null) return new SoundAndMusicProxy(entity, false, pixelsPerUnit);
		if (entity.getComponent(MusicComponent.class) != null) return new SoundAndMusicProxy(entity, true, pixelsPerUnit);
		if (entity.getComponent(SpriteComponent.class) != null) return new SpriteProxy(entity);
		if (entity.getComponent(TextComponent.class) != null) return new TextProxy(entity);

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

	public Array<EntityScheme> getSchemes () {
		Array<EntityScheme> schemes = new Array<>(cache.size);
		cache.values().forEach(proxy -> schemes.add(proxy.getScheme()));
		return schemes;
	}

	public ObjectMap<Entity, EntityProxy> getCache () {
		return cache;
	}
}
