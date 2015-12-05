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

package com.kotcrab.vis.editor.module.scene.system.inflater;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.VisSprite;
import com.kotcrab.vis.runtime.component.proto.ProtoVisSprite;
import com.kotcrab.vis.runtime.system.inflater.InflaterSystem;

/** @author Kotcrab */
public class EditorVisSpriteInflater extends InflaterSystem {
	private ComponentMapper<VisSprite> spriteCm;
	private ComponentMapper<ProtoVisSprite> protoCm;
	private ComponentMapper<AssetComponent> assetCm;
	private TextureCacheModule textureCache;

	public EditorVisSpriteInflater () {
		super(Aspect.all(ProtoVisSprite.class, AssetComponent.class));
	}

	@Override
	public void inserted (int entityId) {
		VisSprite sprite = spriteCm.create(entityId);
		sprite.setRegion(textureCache.getRegion(assetCm.get(entityId).asset));
		protoCm.get(entityId).fill(sprite);
		protoCm.remove(entityId);
	}
}
