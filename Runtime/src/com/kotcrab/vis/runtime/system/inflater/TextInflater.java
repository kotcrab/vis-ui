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

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.assets.BmpFontAsset;
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
@Wire
public class TextInflater extends Manager {
	private ComponentMapper<AssetComponent> assetCm;
	private ComponentMapper<TextProtoComponent> protoCm;

	private EntityTransmuter transmuter;

	private RuntimeConfiguration configuration;
	private AssetManager manager;
	private float pixelsPerUnit;

	public TextInflater (RuntimeConfiguration configuration, AssetManager manager, float pixelsPerUnit) {
		this.configuration = configuration;
		this.manager = manager;
		this.pixelsPerUnit = pixelsPerUnit;
	}

	@Override
	protected void initialize () {
		EntityTransmuterFactory factory = new EntityTransmuterFactory(world).remove(TextProtoComponent.class);
		if (configuration.removeAssetsComponentAfterInflating) factory.remove(AssetComponent.class);
		transmuter = factory.build();
	}

	@Override
	public void added (Entity e) {
		if (protoCm.has(e) == false) return;

		VisAssetDescriptor asset = assetCm.get(e).asset;
		TextProtoComponent protoComponent = protoCm.get(e);

		BitmapFont font;

		if (asset instanceof BmpFontAsset) {
			BmpFontAsset fontAsset = (BmpFontAsset) asset;
			font = manager.get(fontAsset.getPath(), BitmapFont.class);
		} else if (asset instanceof TtfFontAsset) {
			TtfFontAsset fontAsset = (TtfFontAsset) asset;
			font = manager.get(fontAsset.getArbitraryFontName(), BitmapFont.class);
		} else
			throw new UnsupportedAssetDescriptorException(asset);

		font.setUseIntegerPositions(false);
		font.getData().setScale(1f / pixelsPerUnit);

		TextComponent textComponent = new TextComponent(font, protoComponent.text);

		textComponent.setPosition(protoComponent.x, protoComponent.y);
		textComponent.setOrigin(protoComponent.originX, protoComponent.originY);
		textComponent.setRotation(protoComponent.rotation);
		textComponent.setScale(protoComponent.scaleX, protoComponent.scaleY);
		textComponent.setColor(protoComponent.tint);

		textComponent.setText(protoComponent.text);
		//text.setFontSize(fontSize); //font size must be handled manually from SceneLoader because it is not a public property for TextEntity
		textComponent.setAutoSetOriginToCenter(protoComponent.autoSetOriginToCenter);

		textComponent.setDistanceFieldShaderEnabled(protoComponent.isUsesDistanceField);

		transmuter.transmute(e);
		e.edit().add(textComponent);
	}
}
