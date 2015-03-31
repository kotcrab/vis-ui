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
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.runtime.scene.VisAssetDescriptor;

public class ParticleEffectEntity extends Entity implements Disposable {
	protected transient ParticleEffect effect;

	public ParticleEffectEntity (String id, String effectRelativePath, ParticleEffect effect) {
		super(id);

		setAssetDescriptor(new VisAssetDescriptor(effectRelativePath));

		this.effect = effect;

		effect.start();
	}

	@Override
	public void render (Batch batch) {
		effect.update(Gdx.graphics.getDeltaTime());

		effect.draw(batch);

		if (effect.isComplete())
			effect.reset();
	}

	public float getX () {
		return effect.getEmitters().get(0).getX();
	}

	public void setX (float x) {
		effect.setPosition(x, getY());
		effect.reset();
	}

	public float getY () {
		return effect.getEmitters().get(0).getY();
	}

	public void setY (float y) {
		effect.setPosition(getX(), y);
		effect.reset();
	}

	public void setPosition (float x, float y) {
		effect.setPosition(x, y);
		effect.reset();
	}

	@Override
	public void dispose () {
		effect.dispose();
	}
}
