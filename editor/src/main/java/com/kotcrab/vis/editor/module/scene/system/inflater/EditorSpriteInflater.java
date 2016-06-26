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

package com.kotcrab.vis.editor.module.scene.system.inflater;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.editor.module.scene.AssetsLoadingMonitorModule;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.VisSprite;
import com.kotcrab.vis.runtime.component.proto.ProtoVisSprite;
import com.kotcrab.vis.runtime.system.inflater.InflaterSystem;

/** @author Kotcrab */
public class EditorSpriteInflater extends InflaterSystem {
	private TextureCacheModule textureCache;
	private AssetsLoadingMonitorModule loadingMonitor;

	private ComponentMapper<VisSprite> spriteCm;
	private ComponentMapper<ProtoVisSprite> protoCm;
	private ComponentMapper<AssetReference> assetCm;

	public EditorSpriteInflater () {
		super(Aspect.all(ProtoVisSprite.class, AssetReference.class));
	}

	@Override
	public void inserted (int entityId) {
		VisAssetDescriptor asset = assetCm.get(entityId).asset;
		TextureRegion region;

		try {
			region = textureCache.getRegion(asset);
		} catch (GdxRuntimeException e) {
			Log.exception(e);
			region = Icons.QUESTION_BIG.textureRegion();
			loadingMonitor.addFailedResource(asset, e);
		}

		VisSprite sprite = spriteCm.create(entityId);
		sprite.setRegion(region);
		protoCm.get(entityId).fill(sprite);
		protoCm.remove(entityId);
	}
}
