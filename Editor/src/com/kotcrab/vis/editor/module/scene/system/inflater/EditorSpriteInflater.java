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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.SpriteComponent;
import com.kotcrab.vis.runtime.component.SpriteProtoComponent;
import com.kotcrab.vis.runtime.system.inflater.InflaterSystem;

/** @author Kotcrab */
public class EditorSpriteInflater extends InflaterSystem {
	private ComponentMapper<SpriteProtoComponent> protoCm;
	private ComponentMapper<SpriteComponent> spriteCm;
	private ComponentMapper<AssetComponent> assetCm;
	private TextureCacheModule textureCache;

	public EditorSpriteInflater () {
		super(Aspect.all(SpriteProtoComponent.class, AssetComponent.class));
	}

	@Override
	public void inserted (int entityId) {
		SpriteProtoComponent protoComponent = protoCm.get(entityId);
		AssetComponent assetComponent = assetCm.get(entityId);

		Sprite sprite = new Sprite(textureCache.getSprite(assetComponent.asset, 1));

		SpriteComponent spriteComponent = spriteCm.create(entityId);
		spriteComponent.sprite = sprite;
		protoComponent.fill(spriteComponent);

		protoCm.remove(entityId);
	}
}
