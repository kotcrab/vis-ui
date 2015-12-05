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

package com.kotcrab.vis.editor.ui.scene.entityproperties.specifictable;

import com.artemis.Entity;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.runtime.assets.BmpFontAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.VisText;
import com.kotcrab.vis.ui.widget.Tooltip;

import static com.kotcrab.vis.editor.util.vis.EntityUtils.setCommonCheckBoxState;

/**
 * @author Kotcrab
 */
public class BMPTextUITable extends TextUITable {
	private IndeterminateCheckbox distanceFieldCheck;

	@Override
	protected void init () {
		super.init();
		distanceFieldCheck = new IndeterminateCheckbox("Use DF");
		distanceFieldCheck.addListener(properties.getSharedCheckBoxChangeListener());

		fontPropertiesTable.add(distanceFieldCheck);

		new Tooltip(distanceFieldCheck, "Use distance field shader for this text");
	}

	@Override
	protected String getFontExtension () {
		return "fnt";
	}

	@Override
	public boolean isSupported (EntityProxy proxy) {
		if (proxy.hasComponent(VisText.class) == false) return false;

		VisAssetDescriptor asset = proxy.getEntity().getComponent(AssetReference.class).asset;
		if (asset instanceof BmpFontAsset == false)
			return false;

		return true;
	}

	@Override
	public void updateUIValues () {
		super.updateUIValues();
		setCommonCheckBoxState(properties.getSelectedEntities(), distanceFieldCheck, (Entity entity) -> entity.getComponent(VisText.class).isDistanceFieldShaderEnabled());
	}

	@Override
	protected void updateEntitiesValues () {
		for (EntityProxy proxy : properties.getSelectedEntities()) {
			Entity entity = proxy.getEntity();
			VisText text = entity.getComponent(VisText.class);
			AssetReference assetRef = entity.getComponent(AssetReference.class);

			if (distanceFieldCheck.isIndeterminate() == false) {
				text.setDistanceFieldShaderEnabled(distanceFieldCheck.isChecked());
				assetRef.asset = getNewAsset((BmpFontAsset) assetRef.asset, distanceFieldCheck.isChecked());
			}
		}
	}

	private VisAssetDescriptor getNewAsset (BmpFontAsset original, boolean useDistanceFieldFilters) {
		BitmapFontParameter data = new BitmapFontParameter();

		if (useDistanceFieldFilters) {
			data.genMipMaps = true;
			data.minFilter = TextureFilter.MipMapLinearLinear;
			data.magFilter = TextureFilter.Linear;
		}

		return new BmpFontAsset(original.getPath(), data);
	}
}
