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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.assets.AtlasRegionAsset;
import com.kotcrab.vis.runtime.assets.TextureRegionAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.SpriteComponent;
import com.kotcrab.vis.runtime.component.SpriteProtoComponent;
import com.kotcrab.vis.runtime.util.PathUtils;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;

/**
 * Inflates {@link SpriteProtoComponent} into {@link SpriteComponent}
 * @author Kotcrab
 */
@Wire
public class SpriteInflater extends Manager {
	private ComponentMapper<SpriteProtoComponent> protoCm;
	private ComponentMapper<AssetComponent> assetCm;

	private EntityTransmuter transmuter;

	private RuntimeConfiguration configuration;
	private AssetManager manager;

	public SpriteInflater (RuntimeConfiguration configuration, AssetManager manager) {
		this.configuration = configuration;
		this.manager = manager;
	}

	@Override
	protected void initialize () {
		EntityTransmuterFactory factory = new EntityTransmuterFactory(world).remove(SpriteProtoComponent.class);
		if (configuration.removeAssetsComponentAfterInflating) factory.remove(AssetComponent.class);
		transmuter = factory.build();
	}

	@Override
	public void added (Entity e) {
		if (protoCm.has(e) == false) return;

		SpriteProtoComponent proto = protoCm.get(e);
		AssetComponent assetComponent = assetCm.get(e);

		VisAssetDescriptor asset = assetComponent.asset;

		String atlasPath;
		String atlasRegion;

		if (asset instanceof TextureRegionAsset) {
			TextureRegionAsset regionAsset = (TextureRegionAsset) asset;
			atlasPath = "gfx/textures.atlas";
			atlasRegion = PathUtils.removeFirstSeparator(PathUtils.removeExtension(regionAsset.getPath())); //remove gfx/ and file extension

		} else if (asset instanceof AtlasRegionAsset) {
			AtlasRegionAsset regionAsset = (AtlasRegionAsset) asset;
			atlasPath = regionAsset.getPath();
			atlasRegion = regionAsset.getRegionName();

		} else {
			throw new UnsupportedAssetDescriptorException(asset);
		}

		TextureAtlas atlas = manager.get(atlasPath, TextureAtlas.class);

		Sprite sprite = new Sprite(atlas.findRegion(atlasRegion));

		SpriteComponent spriteComponent = new SpriteComponent(sprite);

		sprite.setPosition(proto.x, proto.y);
		sprite.setSize(proto.width, proto.height);
		sprite.setOrigin(proto.originX, proto.originY);
		sprite.setRotation(proto.rotation);
		sprite.setScale(proto.scaleX, proto.scaleY);
		sprite.setColor(proto.tint);
		sprite.setFlip(proto.flipX, proto.flipY);

		transmuter.transmute(e);
		e.edit().add(spriteComponent);
	}
}
