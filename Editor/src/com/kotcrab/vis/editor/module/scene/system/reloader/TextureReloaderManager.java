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
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.VisSprite;

/** @author Kotcrab */
public class TextureReloaderManager extends Manager { //TODO: migrate all reloaders to entity processing systems from managers
	private TextureCacheModule textureCache;

	private ComponentMapper<VisSprite> spriteCm;
	private ComponentMapper<AssetReference> assetCm;
	private AspectSubscriptionManager subscriptionManager;
	private EntitySubscription subscription;

	@Override
	protected void initialize () {
		subscription = subscriptionManager.get(Aspect.all(VisSprite.class, AssetReference.class));
	}

	public void reloadTextures () {
		IntBag bag = subscription.getEntities();
		int[] data = bag.getData();

		for (int i = 0; i < bag.size(); i++) {
			int id = data[i];
			Entity entity = world.getEntity(id);

			VisSprite sprite = spriteCm.get(entity);
			VisAssetDescriptor asset = assetCm.get(entity).asset;

			boolean flipX = sprite.isFlipX();
			boolean flipY = sprite.isFlipY();
			sprite.setRegion(textureCache.getRegion(asset));
			sprite.setFlip(flipX, flipY);
		}
	}
}
