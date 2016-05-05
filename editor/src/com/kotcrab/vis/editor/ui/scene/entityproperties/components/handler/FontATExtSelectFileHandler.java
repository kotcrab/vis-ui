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

package com.kotcrab.vis.editor.ui.scene.entityproperties.components.handler;

import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.util.Holder;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.assets.BmpFontAsset;
import com.kotcrab.vis.runtime.assets.TtfFontAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.util.ImmutableArray;

/** @author Kotcrab */
public class FontATExtSelectFileHandler implements ATExtSelectFileHandler {
	@Override
	public String resolveExtension (ImmutableArray<EntityProxy> selectedEntities) {
		Holder<Boolean> ttfPresent = Holder.of(false);
		Holder<Boolean> bmpPresent = Holder.of(false);

		EntityUtils.stream(selectedEntities, AssetReference.class, (proxy, assetReference) -> {
			VisAssetDescriptor asset = assetReference.getAsset();

			if (asset instanceof TtfFontAsset) {
				ttfPresent.value = true;
			}

			if (asset instanceof BmpFontAsset) {
				bmpPresent.value = true;
			}
		});

		if (ttfPresent.value && bmpPresent.value) return null;
		if (ttfPresent.value) return "ttf";
		if (bmpPresent.value) return "fnt";
		return null;
	}
}
