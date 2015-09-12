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

package com.kotcrab.vis.runtime.system.inflater;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.ParticleComponent;
import com.kotcrab.vis.runtime.component.ParticleProtoComponent;

/**
 * Inflates {@link ParticleProtoComponent} into {@link ParticleComponent}
 * @author Kotcrab
 */
@Wire
public class ParticleInflater extends Manager {
	private ComponentMapper<AssetComponent> assetCm;
	private ComponentMapper<ParticleProtoComponent> protoCm;

	private Entity flyweight;

	private EntityTransmuter transmuter;

	private RuntimeConfiguration configuration;
	private AssetManager manager;

	private float pixelsPerUnit;

	public ParticleInflater (RuntimeConfiguration configuration, AssetManager manager, float pixelsPerUnit) {
		this.configuration = configuration;
		this.manager = manager;
		this.pixelsPerUnit = pixelsPerUnit;
	}

	@Override
	protected void setWorld(World world) {
		super.setWorld(world);
		flyweight = Entity.createFlyweight(world);
	}

	@Override
	protected void initialize () {
		EntityTransmuterFactory factory = new EntityTransmuterFactory(world).remove(ParticleProtoComponent.class);
		if (configuration.removeAssetsComponentAfterInflating) factory.remove(AssetComponent.class);
		transmuter = factory.build();
	}

	@Override
	public void added (int entityId) {
		flyweight.id = entityId;
		if (protoCm.has(entityId) == false) return;

		AssetComponent assetComponent = assetCm.get(entityId);
		ParticleProtoComponent protoComponent = protoCm.get(entityId);

		PathAsset path = (PathAsset) assetComponent.asset;

		ParticleEffect effect = manager.get(path.getPath(), ParticleEffect.class);
		if (effect == null)
			throw new IllegalStateException("Can't load scene particle effect is missing: " + path.getPath());

		ParticleComponent particleComponent = new ParticleComponent(new ParticleEffect(effect));
		particleComponent.setPosition(protoComponent.x, protoComponent.y);
		particleComponent.active = protoComponent.active;
		particleComponent.effect.scaleEffect(1f / pixelsPerUnit);

		transmuter.transmute(flyweight);
		flyweight.edit().add(particleComponent);
	}
}
