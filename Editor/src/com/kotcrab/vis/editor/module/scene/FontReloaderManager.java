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
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.runtime.assets.BmpFontAsset;
import com.kotcrab.vis.runtime.assets.TtfFontAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.TextComponent;

/** @author Kotcrab */
@Wire
public class FontReloaderManager extends Manager {
	private FontCacheModule fontCache;
	private float pixelInUntis;

	private ComponentMapper<TextComponent> textCm;
	private ComponentMapper<AssetComponent> assetCm;
	private AspectSubscriptionManager subscriptionManager;
	private EntitySubscription subscription;

	public FontReloaderManager (FontCacheModule fontCache, float pixelInUntis) {
		this.fontCache = fontCache;
		this.pixelInUntis = pixelInUntis;
	}

	@Override
	protected void initialize () {
		subscription = subscriptionManager.get(Aspect.all(TextComponent.class, AssetComponent.class));
	}

	public void reloadFonts (boolean reloadBmpFonts, boolean reloadTtfFonts) {
		IntBag bag = subscription.getEntities();
		int[] data = bag.getData();

		for (int i = 0; i < bag.size(); i++) {
			int id = data[i];
			Entity entity = world.getEntity(id);

			TextComponent text = textCm.get(entity);
			VisAssetDescriptor asset = assetCm.get(entity).asset;

			if (asset instanceof BmpFontAsset && reloadBmpFonts)
				text.setFont(fontCache.get((BmpFontAsset) asset, pixelInUntis));

			if (asset instanceof TtfFontAsset && reloadTtfFonts)
				text.setFont(fontCache.get((TtfFontAsset) asset, pixelInUntis));
		}
	}
}
