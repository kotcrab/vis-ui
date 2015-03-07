/*
 * Copyright 2014-2015 Pawel Pastuszak
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

package com.kotcrab.vis.runtime.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.utils.Disposable;

public class ParticleEffectEntity extends Entity implements Disposable {
	protected transient ParticleEffect effect;
	protected transient ParticleEmitter emitter;

	protected String effectRelativePath;
	private boolean flipX, flipY;

	public ParticleEffectEntity (String id, String effectRelativePath, ParticleEffect effect) {
		super(id);

		this.effectRelativePath = effectRelativePath;

		this.effect = effect;
		this.emitter = effect.getEmitters().first();

		effect.start();
	}

	@Override
	public void render (Batch batch) {
		emitter.update(Gdx.graphics.getDeltaTime());

		emitter.draw(batch);

		if (emitter.isComplete())
			emitter.reset();
	}


	public float getX () {
		return emitter.getX();
	}

	public void setX (float x) {
		emitter.setPosition(x, getY());
		emitter.reset();
	}

	public float getY () {
		return emitter.getY();
	}

	public void setY (float y) {
		emitter.setPosition(getX(), y);
		emitter.reset();
	}

	public void setPosition (float x, float y) {
		emitter.setPosition(x, y);
		emitter.reset();
	}

	public boolean isFlipX () {
		return flipX;
	}

	public boolean isFlipY () {
		return flipY;
	}

	public void setFlip (boolean x, boolean y) {
		flipX = x;
		flipY = y;
		emitter.setFlip(x, y);
	}

	public String getRelativeEffectPath () {
		return effectRelativePath;
	}

	public void setEffectRelativePath (String effectRelativePath) {
		this.effectRelativePath = effectRelativePath;
	}

	@Override
	public void dispose () {
		effect.dispose();
	}
}
