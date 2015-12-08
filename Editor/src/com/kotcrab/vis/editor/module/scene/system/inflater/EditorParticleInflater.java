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
import com.kotcrab.vis.editor.entity.PixelsPerUnit;
import com.kotcrab.vis.editor.module.project.ParticleCacheModule;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.VisParticle;
import com.kotcrab.vis.runtime.component.VisParticleChanged;
import com.kotcrab.vis.runtime.component.proto.ProtoVisParticle;
import com.kotcrab.vis.runtime.system.inflater.InflaterSystem;

/** @author Kotcrab */
public class EditorParticleInflater extends InflaterSystem {
	private ParticleCacheModule particleCache;
	private float pixelsPerUnit;

	private ComponentMapper<AssetReference> assetCm;
	private ComponentMapper<VisParticle> partcielCm;
	private ComponentMapper<VisParticleChanged> changedCm;
	private ComponentMapper<ProtoVisParticle> protoCm;

	public EditorParticleInflater (float pixelsPerUnit) {
		super(Aspect.all(ProtoVisParticle.class, PixelsPerUnit.class, AssetReference.class));
		this.pixelsPerUnit = pixelsPerUnit;
	}

	@Override
	protected void inserted (int entityId) {
		AssetReference assetRef = assetCm.get(entityId);
		ProtoVisParticle protoComponent = protoCm.get(entityId);

		VisParticle particle = partcielCm.create(entityId);

		particle.setEffect(particleCache.get(assetRef.asset, 1f / pixelsPerUnit));
		protoComponent.fill(particle);

		changedCm.create(entityId);
		protoCm.remove(entityId);
	}
}
