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

package com.kotcrab.vis.editor.module.scene.system.reloader;

import com.artemis.*;
import com.artemis.utils.IntBag;
import com.kotcrab.vis.editor.module.project.ParticleCacheModule;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.component.VisParticle;

/** @author Kotcrab */
public class ParticleReloaderManager extends Manager {
	private ParticleCacheModule particleCache;
	private float pixelsPerUnit;

	private ComponentMapper<VisParticle> particleCm;
	private ComponentMapper<Transform> transformCm;
	private ComponentMapper<AssetReference> assetCm;
	private AspectSubscriptionManager subscriptionManager;
	private EntitySubscription subscription;

	public ParticleReloaderManager (float pixelsPerUnit) {
		this.pixelsPerUnit = pixelsPerUnit;
	}

	@Override
	protected void initialize () {
		subscription = subscriptionManager.get(Aspect.all(VisParticle.class, AssetReference.class));
	}

	public void reloadParticles () {
		IntBag bag = subscription.getEntities();
		int[] data = bag.getData();

		for (int i = 0; i < bag.size(); i++) {
			int id = data[i];
			Entity entity = world.getEntity(id);

			VisParticle particle = particleCm.get(entity);
			VisAssetDescriptor asset = assetCm.get(entity).asset;

			particle.getEffect().dispose();

			particle.setEffect(particleCache.get(asset, 1f / pixelsPerUnit));
			transformCm.get(entity).setDirty(true);
		}
	}
}
