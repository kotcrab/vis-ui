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

package com.kotcrab.vis.plugin.spriter.system;

import com.artemis.*;
import com.artemis.utils.IntBag;
import com.kotcrab.vis.editor.module.scene.system.EntityProxyCache;
import com.kotcrab.vis.plugin.spriter.module.SpriterCacheModule;
import com.kotcrab.vis.plugin.spriter.runtime.assets.SpriterAsset;
import com.kotcrab.vis.plugin.spriter.runtime.component.VisSpriter;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.Transform;

/** @author Kotcrab */
public class SpriterReloaderManager extends Manager {
	private EntityProxyCache entityProxyCache;
	private SpriterCacheModule spriterCache;

	private ComponentMapper<VisSpriter> spriterCm;
	private ComponentMapper<Transform> transformCm;
	private ComponentMapper<AssetReference> assetCm;

	private AspectSubscriptionManager subscriptionManager;
	private EntitySubscription subscription;

	@Override
	protected void initialize () {
		subscription = subscriptionManager.get(Aspect.all(VisSpriter.class, AssetReference.class));
	}

	public void reloadSpriterData () {
		IntBag bag = subscription.getEntities();
		int[] data = bag.getData();

		for (int i = 0; i < bag.size(); i++) {
			int id = data[i];
			Entity entity = world.getEntity(id);

			VisSpriter spriter = spriterCm.get(entity);
			SpriterAsset asset = (SpriterAsset) assetCm.get(entity).asset;

			VisSpriter newSpriter = spriterCache.cloneComponent(asset, spriter);
			entity.edit().remove(spriter).add(newSpriter);
			transformCm.get(entity).setDirty(true);

			//we've replaced SpriterComponent in entity so proxy needs manual reloading
			entityProxyCache.get(entity).reload();
		}

		spriterCache.disposeOldLoaders();
	}
}
