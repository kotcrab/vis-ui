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

package com.kotcrab.vis.runtime.system.render;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.runtime.assets.AtlasRegionAsset;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.Invisible;
import com.kotcrab.vis.runtime.component.VisSprite;
import com.kotcrab.vis.runtime.component.VisSpriteAnimation;
import com.kotcrab.vis.runtime.system.delegate.DeferredEntityProcessingSystem;
import com.kotcrab.vis.runtime.system.delegate.EntityProcessPrincipal;
import com.kotcrab.vis.runtime.util.SpriteSheetHelper;

/**
 * Updated {@link VisSprite} with proper animation frame from {@link VisSpriteAnimation}.
 * @author Kotcrab
 */
public class SpriteAnimationUpdateSystem extends DeferredEntityProcessingSystem {
	private ComponentMapper<AssetReference> assetCm;
	private ComponentMapper<VisSprite> spriteCm;
	private ComponentMapper<VisSpriteAnimation> spriteAnimCm;

	private final AssetManager assetManager;
	private final float pixelsPerUnit;

	private ObjectMap<TextureAtlas, SpriteSheetHelper> spriteSheetHelpers = new ObjectMap<TextureAtlas, SpriteSheetHelper>();

	public SpriteAnimationUpdateSystem (EntityProcessPrincipal principal, AssetManager assetManager, float pixelsPerUnit) {
		super(Aspect.all(AssetReference.class, VisSprite.class, VisSpriteAnimation.class).exclude(Invisible.class), principal);
		this.assetManager = assetManager;
		this.pixelsPerUnit = pixelsPerUnit;
	}

	@Override
	protected void process (int entity) {
		AssetReference assetRef = assetCm.get(entity);
		VisSpriteAnimation spriteAnim = spriteAnimCm.get(entity);
		VisSprite sprite = spriteCm.get(entity);

		if (spriteAnim.isDirty()) {
			spriteAnim.setAnimation(new Animation(spriteAnim.getFrameDuration(), getSpriteSheetHelper(assetRef).getAnimationRegions(spriteAnim.getAnimationName())));
		}

		if (spriteAnim.isPlaying()) {
			spriteAnim.updateTimer(world.delta);
			sprite.setRegion(spriteAnim.getKeyFrame(), pixelsPerUnit);
		}
	}

	public SpriteSheetHelper getSpriteSheetHelper (AssetReference assetRef) {
		if (assetRef.asset instanceof AtlasRegionAsset == false) {
			throw new IllegalStateException("SpriteAnimationUpdateSystem can be only used with entities that uses AtlasRegionAsset");
		}

		AtlasRegionAsset asset = (AtlasRegionAsset) assetRef.asset;
		TextureAtlas atlas = assetManager.get(asset.getPath(), TextureAtlas.class);
		SpriteSheetHelper helper = spriteSheetHelpers.get(atlas);
		if (helper == null) {
			helper = new SpriteSheetHelper(atlas);
			spriteSheetHelpers.put(atlas, helper);
		}
		return helper;
	}
}
