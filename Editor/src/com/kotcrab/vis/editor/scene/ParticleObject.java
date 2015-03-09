/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.scene;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Rectangle;
import com.kotcrab.vis.editor.util.ParticleUtils;
import com.kotcrab.vis.runtime.entity.ParticleEffectEntity;

public class ParticleObject extends ParticleEffectEntity implements EditorEntity {
	private Rectangle bounds;

	/** Effect is not serialized so we have to save and store position here when serialized using Kryo */
	private float serializeX, serializeY;

	public ParticleObject (String relativePath, ParticleEffect effect) {
		super(null, relativePath, effect);
		bounds = new Rectangle();
	}

	@Override
	public void beforeSerialize () {
		serializeX = getX();
		serializeY = getY();
	}

	public void afterDeserialize (ParticleEffect effect) {
		this.effect = effect;
		effect.setPosition(serializeX, serializeY);
	}

	@Override
	public void render (Batch batch) {
		super.render(batch);
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
}
