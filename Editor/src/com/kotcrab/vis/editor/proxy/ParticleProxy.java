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

package com.kotcrab.vis.editor.proxy;

import com.artemis.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.kotcrab.vis.editor.util.gdx.ParticleUtils;
import com.kotcrab.vis.runtime.accessor.BasicPropertiesAccessor;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.ParticleComponent;

/** @author Kotcrab */
public class ParticleProxy extends EntityProxy {
	private transient ParticleComponent particle;

	public ParticleProxy (Entity entity) {
		super(entity);
		particle = entity.getComponent(ParticleComponent.class);
	}

	@Override
	protected BasicPropertiesAccessor initAccessors () {
		return new Accessor();
	}

	@Override
	protected String getEntityName () {
		return "ParticleEntity";
	}

	@Override
	boolean isAssetsDescriptorSupported (VisAssetDescriptor assetDescriptor) {
		return assetDescriptor instanceof PathAsset;
	}

	private class Accessor implements BasicPropertiesAccessor {
		private Rectangle bounds = new Rectangle();

		public Accessor () {
			bounds = new Rectangle();
		}

		@Override
		public float getX () {
			return particle.getX();
		}

		@Override
		public void setX (float x) {
			particle.setX(x);
		}

		@Override
		public float getY () {
			return particle.getY();
		}

		@Override
		public void setY (float y) {
			particle.setY(y);
		}

		@Override
		public void setPosition (float x, float y) {
			particle.setPosition(x, y);
		}

		@Override
		public float getWidth () {
			return bounds.getWidth();
		}

		@Override
		public float getHeight () {
			return bounds.getHeight();
		}

		@Override
		public Rectangle getBoundingRectangle () {
			ParticleUtils.calculateBoundingRectangle(particle.effect, bounds, false);
			return bounds;
		}
	}
}
