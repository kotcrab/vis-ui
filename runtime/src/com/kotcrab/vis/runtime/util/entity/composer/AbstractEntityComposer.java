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

public abstract class AbstractEntityComposer {
	private EntityEngine engine;

	private ComponentMapper<AssetReference> assetReferenceCm;

	private boolean inProgress;
	protected Entity entity;

	public AbstractEntityComposer (EntityEngine engine) {
		this.engine = engine;
		engine.inject(this);
	}

	protected void createComponents () {
	}

	public void begin () {
		if (inProgress) {
			throw new IllegalStateException("Only one type of entity can be created at a time. " +
					"Call #create to finalize creating entity and allow to create next entities");
		}
		inProgress = true;
		entity = engine.createEntity();
		createComponents();
	}

	public void setAssetDescriptor (VisAssetDescriptor asset) {
		assetReferenceCm.create(entity).asset = asset;
	}

	public Entity finish () {
		inProgress = false;
		Entity entityTmp = entity;
		entity = null;
		return entityTmp;
	}

	public Entity getEntity () {
		return entity;
	}
}
