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

package com.kotcrab.vis.runtime.util.entity.composer;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.util.EntityEngine;

/**
 * Base class for all entity composers. Subclasses can use {@link ComponentMapper} which will be automatically injected from Artemis.
 * <p>
 * Even though entity composers can be used directly, it is recommenced to use {@link EntityComposer} utility which
 * reuses composers and provides useful utility methods.
 * @author Kotcrab
 * @see EntityComposer
 * @since 0.3.3
 */
public abstract class AbstractEntityComposer {
	private EntityEngine engine;

	private ComponentMapper<AssetReference> assetReferenceCm;

	private boolean inProgress;
	protected Entity entity;

	public AbstractEntityComposer (EntityEngine engine) {
		this.engine = engine;
		engine.inject(this);
	}

	/** Called when composer should create component for new instances. */
	protected void createComponents () {
	}

	/** Begins creation of new entity. You must call {@link #finish()} after creating entity to allow creating next entities. */
	public void begin () {
		if (inProgress) {
			throw new IllegalStateException("Only one type of entity can be created at a time. " +
					"Call #finish to finalize creating entity and allow creating next entities");
		}
		inProgress = true;
		entity = engine.createEntity();
		createComponents();
	}

	/**
	 * Ends creation of new entity and prepares for creating new entity. After this is called you may call {@link #begin()}
	 * to start creating new entity.
	 * @return newly created entity
	 */
	public Entity finish () {
		if (inProgress == false) {
			throw new IllegalStateException("Call #begin first to start creating new entity.");
		}
		inProgress = false;
		Entity entityTmp = entity;
		entity = null;
		return entityTmp;
	}

	/** Sets asset descriptor associated to this entity */
	public void setAssetDescriptor (VisAssetDescriptor asset) {
		assetReferenceCm.create(entity).asset = asset;
	}

	/** @return entity under construction or null if {@link #begin()} was not called. */
	public Entity getEntity () {
		return entity;
	}
}
