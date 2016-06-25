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

package com.kotcrab.vis.runtime.util.entity.composer;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kotcrab.vis.runtime.component.VisSprite;
import com.kotcrab.vis.runtime.util.EntityEngine;

/** @author Kotcrab */
public class EntityComposer {
	private float pixelsPerUnit;
	private SpriteEntityComposer spriteComposer;
	private TextEntityComposer textComposer;
	private ParticleEntityComposer particleComposer;

	public EntityComposer (EntityEngine engine, float pixelsPerUnit) {
		this.pixelsPerUnit = pixelsPerUnit;
		spriteComposer = new SpriteEntityComposer(engine);
		textComposer = new TextEntityComposer(engine);
		particleComposer = new ParticleEntityComposer(engine);
	}

	public SpriteEntityComposer sprite (TextureRegion region, float x, float y) {
		spriteComposer.begin();
		spriteComposer.getTransform().setPosition(x, y);
		VisSprite sprite = spriteComposer.getSprite();
		spriteComposer.getSprite().setRegion(region);
		spriteComposer.getSprite().setSize(sprite.getRegion().getRegionWidth() / pixelsPerUnit, sprite.getRegion().getRegionHeight() / pixelsPerUnit);
		return spriteComposer;
	}

	public SpriteEntityComposer sprite (VisSprite sprite, float x, float y) {
		spriteComposer.begin();
		spriteComposer.getTransform().setPosition(x, y);
		spriteComposer.getSprite().setRegion(sprite.getRegion());
		spriteComposer.getSprite().setSize(sprite.getWidth(), sprite.getHeight());
		return spriteComposer;
	}

	public TextEntityComposer text (BitmapFont font, String text, float x, float y) {
		textComposer.begin();
		textComposer.getTransform().setPosition(x, y);
		textComposer.getText().init(font, text);
		return textComposer;
	}

	public ParticleEntityComposer particle (ParticleEffect effect, float x, float y) {
		particleComposer.begin();
		particleComposer.getTransform().setPosition(x, y);
		particleComposer.getParticle().setEffect(effect);
		return particleComposer;
	}
}
