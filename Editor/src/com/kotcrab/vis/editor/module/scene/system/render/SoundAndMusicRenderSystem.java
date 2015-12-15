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

package com.kotcrab.vis.editor.module.scene.system.render;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.runtime.component.Invisible;
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.component.VisMusic;
import com.kotcrab.vis.runtime.component.VisSound;
import com.kotcrab.vis.runtime.system.delegate.DeferredEntityProcessingSystem;
import com.kotcrab.vis.runtime.system.delegate.EntityProcessPrincipal;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;

/** @author Kotcrab */
public class SoundAndMusicRenderSystem extends DeferredEntityProcessingSystem {
	public static final int ICON_SIZE = 76;

	private ComponentMapper<Transform> posCm;
	private ComponentMapper<VisMusic> musicCm;

	private TextureRegion soundIcon;
	private TextureRegion musicIcon;

	private RenderBatchingSystem renderBatchingSystem;
	private Batch batch;

	private float renderSize;

	public SoundAndMusicRenderSystem (EntityProcessPrincipal principal, float pixelsPerUnit) {
		super(Aspect.all(Transform.class).one(VisSound.class, VisMusic.class).exclude(Invisible.class), principal);
		soundIcon = Icons.SOUND_BIG.textureRegion();
		musicIcon = Icons.MUSIC_BIG.textureRegion();

		renderSize = ICON_SIZE / pixelsPerUnit;
	}

	@Override
	protected void initialize () {
		batch = renderBatchingSystem.getBatch();
	}

	@Override
	protected void process (int entityId) {
		Transform ptransforms = posCm.get(entityId);

		if (musicCm.has(entityId))
			batch.draw(musicIcon, ptransforms.getX(), ptransforms.getY(), renderSize, renderSize);
		else
			batch.draw(soundIcon, ptransforms.getX(), ptransforms.getY(), renderSize, renderSize);
	}
}
