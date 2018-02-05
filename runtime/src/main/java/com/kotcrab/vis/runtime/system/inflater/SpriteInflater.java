/*
 * Copyright 2014-2017 See AUTHORS file.
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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.assets.AtlasRegionAsset;
import com.kotcrab.vis.runtime.assets.TextureAsset;
import com.kotcrab.vis.runtime.assets.TextureRegionAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.VisSprite;
import com.kotcrab.vis.runtime.component.proto.ProtoVisSprite;
import com.kotcrab.vis.runtime.util.PathUtils;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;

/**
 * Inflates {@link ProtoVisSprite} into {@link VisSprite}.
 * @author Kotcrab
 */
public class SpriteInflater extends InflaterSystem {
	private ComponentMapper<VisSprite> spriteCm;
	private ComponentMapper<ProtoVisSprite> protoCm;
	private ComponentMapper<AssetReference> assetCm;

	private RuntimeConfiguration configuration;
	private AssetManager manager;
	private String sceneTextureAtlasPath;

	public SpriteInflater (RuntimeConfiguration configuration, AssetManager manager, String sceneTextureAtlasPath) {
		super(Aspect.all(ProtoVisSprite.class, AssetReference.class));
		this.configuration = configuration;
		this.manager = manager;
		this.sceneTextureAtlasPath = sceneTextureAtlasPath;
	}

	@Override
	public void inserted (int entityId) {
		VisAssetDescriptor asset = assetCm.get(entityId).asset;

		String atlasPath;
		String atlasRegion;
		TextureAtlas atlas;
		TextureRegion region;

		if (asset instanceof TextureRegionAsset) {
			TextureRegionAsset regionAsset = (TextureRegionAsset) asset;
			atlasPath = sceneTextureAtlasPath;
			atlasRegion = PathUtils.removeExtension(regionAsset.getPath());
			atlas = manager.get(atlasPath, TextureAtlas.class);
			region = atlas.findRegion(atlasRegion);

		} else if (asset instanceof AtlasRegionAsset) {
			AtlasRegionAsset regionAsset = (AtlasRegionAsset) asset;
			atlasPath = regionAsset.getPath();
			atlasRegion = regionAsset.getRegionName();
			atlas = manager.get(atlasPath, TextureAtlas.class);
			region = atlas.findRegion(atlasRegion);

		} else if (asset instanceof TextureAsset) {
			TextureAsset textureAsset = (TextureAsset) asset;
			manager.load(textureAsset.getPath(), Texture.class);
			manager.finishLoading();
            Texture texture = manager.get(textureAsset.getPath(), Texture.class);
            region = new TextureRegion(texture);
            atlasRegion = textureAsset.getPath();
		} else {
			throw new UnsupportedAssetDescriptorException(asset);
		}


		if (region == null) throw new IllegalStateException("Can't load scene, gfx asset is missing: " + atlasRegion);
		VisSprite sprite = spriteCm.create(entityId);
		sprite.setRegion(region);
		protoCm.get(entityId).fill(sprite);
		protoCm.remove(entityId);
	}
}
