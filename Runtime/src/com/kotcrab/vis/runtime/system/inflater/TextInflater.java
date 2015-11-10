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

package com.kotcrab.vis.runtime.system.inflater;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.assets.BmpFontAsset;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.assets.TtfFontAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.TextComponent;
import com.kotcrab.vis.runtime.component.TextProtoComponent;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;

/**
 * Inflates {@link TextProtoComponent} into {@link TextComponent}
 * @author Kotcrab
 */
public class TextInflater extends InflaterSystem {
	private ComponentMapper<AssetComponent> assetCm;
	private ComponentMapper<TextComponent> textCm;
	private ComponentMapper<TextProtoComponent> protoCm;

	private RuntimeConfiguration configuration;
	private AssetManager manager;
	private float pixelsPerUnit;

	public TextInflater (RuntimeConfiguration configuration, AssetManager manager, float pixelsPerUnit) {
		super(Aspect.all(TextProtoComponent.class, AssetComponent.class));
		this.configuration = configuration;
		this.manager = manager;
		this.pixelsPerUnit = pixelsPerUnit;
	}

	@Override
	public void inserted (int entityId) {
		VisAssetDescriptor asset = assetCm.get(entityId).asset;
		TextProtoComponent protoComponent = protoCm.get(entityId);

		BitmapFont font;

		if (asset instanceof BmpFontAsset) {
			BmpFontAsset fontAsset = (BmpFontAsset) asset;
			font = manager.get(fontAsset.getPath(), BitmapFont.class);
		} else if (asset instanceof TtfFontAsset) {
			TtfFontAsset fontAsset = (TtfFontAsset) asset;
			font = manager.get(fontAsset.getArbitraryFontName(), BitmapFont.class);
		} else
			throw new UnsupportedAssetDescriptorException(asset);

		if (font == null)
			throw new IllegalStateException("Can't load scene, font is missing: " + ((PathAsset) asset).getPath());

		font.setUseIntegerPositions(false);
		font.getData().setScale(1f / pixelsPerUnit);

		TextComponent textComponent = textCm.create(entityId);

		textComponent.init(font, protoComponent.text);
		protoComponent.fill(textComponent);
		//text.setFontSize(fontSize); //font size must be handled manually from SceneLoader because it is not a public property for TextEntity

		if (configuration.removeAssetsComponentAfterInflating) assetCm.remove(entityId);
		protoCm.remove(entityId);
	}
}
