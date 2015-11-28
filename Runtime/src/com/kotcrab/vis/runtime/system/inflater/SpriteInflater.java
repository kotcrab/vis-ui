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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
public class SpriteInflater extends InflaterSystem {
	private ComponentMapper<SpriteProtoComponent> protoCm;
	private ComponentMapper<SpriteComponent> spriteCm;
	private ComponentMapper<AssetComponent> assetCm;

	private RuntimeConfiguration configuration;
	private AssetManager manager;

	public SpriteInflater (RuntimeConfiguration configuration, AssetManager manager) {
		super(Aspect.all(SpriteProtoComponent.class, AssetComponent.class));
		this.configuration = configuration;
		this.manager = manager;
	}

	@Override
	public void inserted (int entityId) {
		SpriteProtoComponent protoComponent = protoCm.get(entityId);
		AssetComponent assetComponent = assetCm.get(entityId);

		VisAssetDescriptor asset = assetComponent.asset;

		String atlasPath;
		String atlasRegion;

		if (asset instanceof TextureRegionAsset) {
			TextureRegionAsset regionAsset = (TextureRegionAsset) asset;
			atlasPath = "textures.atlas";
			atlasRegion = PathUtils.removeExtension(regionAsset.getPath()); //remove gfx/ and file extension

		} else if (asset instanceof AtlasRegionAsset) {
			AtlasRegionAsset regionAsset = (AtlasRegionAsset) asset;
			atlasPath = regionAsset.getPath();
			atlasRegion = regionAsset.getRegionName();

		} else {
			throw new UnsupportedAssetDescriptorException(asset);
		}

		TextureAtlas atlas = manager.get(atlasPath, TextureAtlas.class);
		TextureRegion region = atlas.findRegion(atlasRegion);
		if (region == null) throw new IllegalStateException("Can't load scene, gfx asset is missing: " + atlasRegion);
		Sprite sprite = new Sprite(region);

		SpriteComponent spriteComponent = spriteCm.create(entityId);
		spriteComponent.sprite = sprite;
		protoComponent.fill(spriteComponent);

		if (configuration.removeAssetsComponentAfterInflating) assetCm.remove(entityId);
		protoCm.remove(entityId);
	}
}
