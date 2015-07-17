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

package com.kotcrab.vis.editor.module.scene;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.entity.PositionComponent;
import com.kotcrab.vis.runtime.component.InvisibleComponent;
import com.kotcrab.vis.runtime.component.MusicComponent;
import com.kotcrab.vis.runtime.component.SoundComponent;
import com.kotcrab.vis.runtime.system.RenderBatchingSystem;
import net.mostlyoriginal.api.system.delegate.DeferredEntityProcessingSystem;
import net.mostlyoriginal.api.system.delegate.EntityProcessPrincipal;

/** @author Kotcrab */
@Wire
public class SoundAndMusicRenderSystem extends DeferredEntityProcessingSystem {
	public static final int ICON_SIZE = 76;

	private ComponentMapper<PositionComponent> posCm;
	private ComponentMapper<MusicComponent> musicCm;

	private TextureRegion soundIcon;
	private TextureRegion musicIcon;

	private RenderBatchingSystem renderBatchingSystem;
	private Batch batch;

	public SoundAndMusicRenderSystem (EntityProcessPrincipal principal) {
		super(Aspect.all(PositionComponent.class).one(SoundComponent.class, MusicComponent.class).exclude(InvisibleComponent.class), principal);
		soundIcon = Assets.getIconRegion(Icons.SOUND);
		musicIcon = Assets.getIconRegion(Icons.MUSIC);
	}

	@Override
	protected void initialize () {
		batch = renderBatchingSystem.getBatch();
	}

	@Override
	protected void process (final Entity entity) {
		PositionComponent pos = posCm.get(entity);

		if (musicCm.has(entity))
			batch.draw(musicIcon, pos.x, pos.y);
		else
			batch.draw(soundIcon, pos.x, pos.y);

	}
}
