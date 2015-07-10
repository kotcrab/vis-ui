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

package com.kotcrab.vis.runtime.system;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.ParticleComponent;
import com.kotcrab.vis.runtime.component.ParticleProtoComponent;

/** @author Kotcrab */
@Wire
public class ParticleInflaterSystem extends EntityProcessingSystem {
	private ComponentMapper<AssetComponent> assetCm;
	private ComponentMapper<ParticleProtoComponent> protoCm;

	private EntityTransmuter transmuter;

	private RuntimeConfiguration configuration;
	private AssetManager manager;

	public ParticleInflaterSystem (RuntimeConfiguration configuration, AssetManager manager) {
		super(Aspect.all(ParticleProtoComponent.class, AssetComponent.class));
		this.configuration = configuration;
		this.manager = manager;
	}

	@Override
	protected void initialize () {
		EntityTransmuterFactory factory = new EntityTransmuterFactory(world).remove(ParticleProtoComponent.class);
		if (configuration.removeAssetsComponentAfterInflating) factory.remove(AssetComponent.class);
		transmuter = factory.build();
	}

	@Override
	protected void process (Entity e) {
		AssetComponent assetComponent = assetCm.get(e);
		ParticleProtoComponent protoComponent = protoCm.get(e);

		PathAsset path = (PathAsset) assetComponent.asset;

		ParticleEffect effect = manager.get(path.getPath(), ParticleEffect.class);

		ParticleComponent particleComponent = new ParticleComponent(effect);
		particleComponent.setPosition(protoComponent.x, protoComponent.y);
		particleComponent.active = protoComponent.active;

		transmuter.transmute(e);
		e.edit().add(particleComponent);
	}
}
