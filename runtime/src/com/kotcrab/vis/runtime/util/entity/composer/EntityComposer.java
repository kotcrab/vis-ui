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
import com.kotcrab.vis.runtime.scene.Scene;
import com.kotcrab.vis.runtime.util.EntityEngine;

/**
 * Utility class simplifying creation of VisRuntime entities.
 * @author Kotcrab
 * @since 0.3.3
 */
public class EntityComposer {
	private float pixelsPerUnit;
	private SpriteEntityComposer spriteComposer;
	private TextEntityComposer textComposer;
	private ParticleEntityComposer particleComposer;

	private int defaultLayerId = 0;
	private int defaultZIndex = 0;

	/**
	 * Creates new instance of EntityComposer bound to specified {@link Scene} instance.
	 * @param scene scene that this composer will be bound to
	 */
	public EntityComposer (Scene scene) {
		this(scene.getEntityEngine(), scene.getPixelsPerUnit());
	}

	/**
	 * Creates new instance of EntityComposer bound to specified {@link EntityEngine} instance.
	 * @param engine entity engine of current scene
	 * @param pixelsPerUnit pixels per unit of current scene
	 */
	public EntityComposer (EntityEngine engine, float pixelsPerUnit) {
		this.pixelsPerUnit = pixelsPerUnit;
		spriteComposer = new SpriteEntityComposer(engine);
		textComposer = new TextEntityComposer(engine);
		particleComposer = new ParticleEntityComposer(engine);
	}

	/**
	 * Begins creation of sprite type entity.
	 * @param region texture region for sprite
	 * @param x x entity position
	 * @param y y entity position
	 * @return shared instance of {@link SpriteEntityComposer} which allows further customization. Note you must call
	 * {@link SpriteEntityComposer#finish()} after creating entity to allow creating next entities.
	 */
	public SpriteEntityComposer sprite (TextureRegion region, float x, float y) {
		spriteComposer.begin();
		setupRenderableEntity(spriteComposer, x, y);
		VisSprite sprite = spriteComposer.getSprite();
		spriteComposer.getSprite().setRegion(region);
		spriteComposer.getSprite().setSize(sprite.getRegion().getRegionWidth() / pixelsPerUnit, sprite.getRegion().getRegionHeight() / pixelsPerUnit);
		return spriteComposer;
	}

	/**
	 * Begins creation of sprite type entity.
	 * @param sprite {@link VisSprite} template for new entity
	 * @param x x entity position
	 * @param y y entity position
	 * @return shared instance of {@link SpriteEntityComposer} which allows further customization. Note you must call
	 * {@link SpriteEntityComposer#finish()} after creating entity to allow creating next entities.
	 */
	public SpriteEntityComposer sprite (VisSprite sprite, float x, float y) {
		spriteComposer.begin();
		setupRenderableEntity(spriteComposer, x, y);
		spriteComposer.getSprite().setRegion(sprite.getRegion());
		spriteComposer.getSprite().setSize(sprite.getWidth(), sprite.getHeight());
		return spriteComposer;
	}

	/**
	 * Begins creation of text type entity.
	 * @param font {@link BitmapFont} used for text, can be either loaded directly from bmp font data or generated using TrueType
	 * @param text initial text of entity
	 * @param x x entity position
	 * @param y y entity position
	 * @return shared instance of {@link TextEntityComposer} which allows further customization. Note you must call
	 * {@link TextEntityComposer#finish()} after creating entity to allow creating next entities.
	 */
	public TextEntityComposer text (BitmapFont font, String text, float x, float y) {
		textComposer.begin();
		setupRenderableEntity(textComposer, x, y);
		textComposer.getText().init(font, text);
		return textComposer;
	}

	/**
	 * Begins creation of particle type entity.
	 * @param effect {@link ParticleEffect} that this entity will use
	 * @param x x entity position
	 * @param y y entity position
	 * @return shared instance of {@link ParticleEntityComposer} which allows further customization. Note you must call
	 * {@link ParticleEntityComposer#finish()} after creating entity to allow creating next entities.
	 */
	public ParticleEntityComposer particle (ParticleEffect effect, float x, float y) {
		particleComposer.begin();
		setupRenderableEntity(particleComposer, x, y);
		particleComposer.getParticle().setEffect(effect);
		return particleComposer;
	}

	private void setupRenderableEntity (RenderableEntityComposer composer, float x, float y) {
		composer.getTransform().setPosition(x, y);
		composer.getLayer().layerId = defaultLayerId;
		composer.getRenderable().zIndex = defaultZIndex;

	}

	public int getDefaultLayerId () {
		return defaultLayerId;
	}

	/** Sets default layer id for newly created entities. */
	public void setDefaultLayerId (int defaultLayerId) {
		this.defaultLayerId = defaultLayerId;
	}

	public int getDefaultZIndex () {
		return defaultZIndex;
	}

	/** Sets default z index for newly created entities. */
	public void setDefaultZIndex (int defaultZIndex) {
		this.defaultZIndex = defaultZIndex;
	}
}
