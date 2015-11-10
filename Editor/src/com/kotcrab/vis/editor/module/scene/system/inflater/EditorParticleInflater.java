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

package com.kotcrab.vis.editor.module.scene.system.inflater;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.kotcrab.vis.editor.entity.PixelsPerUnitComponent;
import com.kotcrab.vis.editor.module.project.ParticleCacheModule;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.ParticleComponent;
import com.kotcrab.vis.runtime.component.ParticleProtoComponent;
import com.kotcrab.vis.runtime.system.inflater.InflaterSystem;

/** @author Kotcrab */
public class EditorParticleInflater extends InflaterSystem {
	private ParticleCacheModule particleCache;
	private float pixelsPerUnit;

	private ComponentMapper<AssetComponent> assetCm;
	private ComponentMapper<ParticleComponent> partcielCm;
	private ComponentMapper<ParticleProtoComponent> protoCm;

	public EditorParticleInflater (float pixelsPerUnit) {
		super(Aspect.all(ParticleProtoComponent.class, PixelsPerUnitComponent.class, AssetComponent.class));
		this.pixelsPerUnit = pixelsPerUnit;
	}

	@Override
	protected void inserted (int entityId) {
		AssetComponent assetComponent = assetCm.get(entityId);
		ParticleProtoComponent protoComponent = protoCm.get(entityId);

		ParticleComponent particleComponent = partcielCm.create(entityId);
		particleComponent.effect = particleCache.get(assetComponent.asset, 1f / pixelsPerUnit);
		particleComponent.setPosition(protoComponent.x, protoComponent.y);
		particleComponent.active = protoComponent.active;

		protoCm.remove(entityId);
	}
}
