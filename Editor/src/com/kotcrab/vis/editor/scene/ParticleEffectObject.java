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

package com.kotcrab.vis.editor.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Rectangle;
import com.kotcrab.vis.editor.util.gdx.ParticleUtils;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.entity.ParticleEffectEntity;

public class ParticleEffectObject extends ParticleEffectEntity implements EditorObject {
	private VisAssetDescriptor assetDescriptor;
	private Rectangle bounds;

	public ParticleEffectObject (String relativePath, ParticleEffect effect) {
		super(null, effect);
		setAssetDescriptor(new PathAsset(relativePath));
		bounds = new Rectangle();
	}

	public ParticleEffectObject (ParticleEffectObject other, ParticleEffect effect) {
		super(other.getId(), effect);
		this.assetDescriptor = other.assetDescriptor;
		bounds = new Rectangle();
		setPosition(other.getX(), other.getY());
	}

	public void onDeserialize (ParticleEffect effect, float x, float y) {
		this.effect = effect;
		effect.setPosition(x, y);
	}

	@Override
	public void render (Batch batch) {
		//effect is always active in editor
		effect.update(Gdx.graphics.getDeltaTime());
		effect.draw(batch);

		if (isComplete())
			reset();

		ParticleUtils.calculateBoundingRectangle(effect, bounds, false);
	}

	@Override
	public void setPosition (float x, float y) {
		super.setPosition(x, y);
		bounds.set(0, 0, 0, 0);
	}

	@Override
	public void setY (float y) {
		super.setY(y);
		bounds.set(0, 0, 0, 0);
	}

	@Override
	public void setX (float x) {
		super.setX(x);
		bounds.set(0, 0, 0, 0);
	}

	@Override
	public float getWidth () {
		return bounds.width;
	}

	@Override
	public float getHeight () {
		return bounds.height;
	}

	@Override
	public Rectangle getBoundingRectangle () {
		return bounds;
	}

	@Override
	protected void finalize () throws Throwable {
		try {
			dispose();
		} catch (Throwable t) {
			throw t;
		} finally {
			super.finalize();
		}
	}

	@Override
	public boolean isAssetsDescriptorSupported (VisAssetDescriptor assetDescriptor) {
		return assetDescriptor instanceof PathAsset;
	}

	@Override
	public VisAssetDescriptor getAssetDescriptor () {
		return assetDescriptor;
	}

	@Override
	public void setAssetDescriptor (VisAssetDescriptor assetDescriptor) {
		checkAssetDescriptor(assetDescriptor);
		this.assetDescriptor = assetDescriptor;
	}
}
