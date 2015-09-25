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
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.kotcrab.vis.editor.module.project.SpriterCacheModule;
import com.kotcrab.vis.runtime.assets.SpriterAsset;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.SpriterComponent;

/** @author Kotcrab */
@Wire
public class SpriterReloaderManager extends Manager {
	private SpriterCacheModule spriterCacheModule;

	private SpriterCacheModule spriterCache;

	private ComponentMapper<SpriterComponent> spriterCm;
	private ComponentMapper<AssetComponent> assetCm;

	private AspectSubscriptionManager subscriptionManager;
	private EntitySubscription subscription;

	public SpriterReloaderManager (SpriterCacheModule spriterCache) {
		this.spriterCache = spriterCache;
	}

	@Override
	protected void initialize () {
		subscription = subscriptionManager.get(Aspect.all(SpriterComponent.class, AssetComponent.class));
	}

	public void reloadSpriterData () {
		IntBag bag = subscription.getEntities();
		int[] data = bag.getData();

		for (int i = 0; i < bag.size(); i++) {
			int id = data[i];
			Entity entity = world.getEntity(id);

			SpriterComponent spriter = spriterCm.get(entity);
			SpriterAsset asset = (SpriterAsset) assetCm.get(entity).asset;

			SpriterComponent newSpriter = spriterCache.cloneComponent(asset, spriter);
			entity.edit().remove(spriter).add(newSpriter);
		}

		spriterCacheModule.disposeOldLoaders();
	}
}
