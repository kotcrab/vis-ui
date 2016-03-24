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
