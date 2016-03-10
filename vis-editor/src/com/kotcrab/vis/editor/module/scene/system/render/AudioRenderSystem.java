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

package com.kotcrab.vis.editor.module.scene.system.render;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.runtime.component.Invisible;
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.component.VisMusic;
import com.kotcrab.vis.runtime.component.VisSound;
import com.kotcrab.vis.runtime.system.delegate.EntityProcessPrincipal;

/** @author Kotcrab */
public class AudioRenderSystem extends AbstractIconRenderSystem {
	private ComponentMapper<VisMusic> musicCm;

	private TextureRegion soundIcon;
	private TextureRegion musicIcon;

	public AudioRenderSystem (EntityProcessPrincipal principal, float pixelsPerUnit) {
		super(Aspect.all(Transform.class).one(VisSound.class, VisMusic.class).exclude(Invisible.class), principal, pixelsPerUnit, Assets.BIG_ICON_SIZE);
		soundIcon = Icons.SOUND_BIG.textureRegion();
		musicIcon = Icons.MUSIC_BIG.textureRegion();
	}

	@Override
	protected TextureRegion getIconForEntity (int entityId) {
		if (musicCm.has(entityId))
			return musicIcon;
		else
			return soundIcon;
	}
}
