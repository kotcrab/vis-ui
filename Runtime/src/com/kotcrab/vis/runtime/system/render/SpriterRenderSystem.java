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

package com.kotcrab.vis.runtime.system.render;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kotcrab.vis.runtime.assets.SpriterAsset;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.InvisibleComponent;
import com.kotcrab.vis.runtime.component.SpriterComponent;
import com.kotcrab.vis.runtime.spriter.Drawer;
import com.kotcrab.vis.runtime.spriter.Timeline.Key;
import com.kotcrab.vis.runtime.system.delegate.DeferredEntityProcessingSystem;
import com.kotcrab.vis.runtime.system.delegate.EntityProcessPrincipal;

/** @author Kotcrab */
public class SpriterRenderSystem extends DeferredEntityProcessingSystem {
	private ComponentMapper<SpriterComponent> spriterCm;
	private ComponentMapper<AssetComponent> assetCm;

	private RenderBatchingSystem renderBatchingSystem;
	private Batch batch;

	private SpriterDrawer drawer;

	public SpriterRenderSystem (EntityProcessPrincipal principal) {
		super(Aspect.all(SpriterComponent.class).exclude(InvisibleComponent.class), principal);
	}

	@Override
	protected void initialize () {
		batch = renderBatchingSystem.getBatch();
		drawer = new SpriterDrawer((SpriteBatch) batch);
	}

	@Override
	protected void process (int entityId) {
		SpriterComponent spriter = spriterCm.get(entityId);
		SpriterAsset asset = (SpriterAsset) assetCm.get(entityId).asset;
		if (spriter.animationPlaying == false) spriter.player.setTime(0);
		spriter.player.update();

		drawer.setLoader(spriter.loader);
		drawer.setImageScale(asset.getImageScale());
		drawer.draw(spriter.player);
	}

	public class SpriterDrawer extends Drawer<Sprite> {
		SpriteBatch batch;
		float spriteScale;

		public SpriterDrawer (SpriteBatch batch) {
			super(null);
			this.batch = batch;
		}

		@Override
		public void setColor (float r, float g, float b, float a) {
		}

		@Override
		public void rectangle (float x, float y, float width, float height) {
		}

		@Override
		public void line (float x1, float y1, float x2, float y2) {
		}

		@Override
		public void circle (float x, float y, float radius) {
		}

		@Override
		public void draw (Key.Object object) {
			Sprite sprite = loader.get(object.ref);
			float newPivotX = (sprite.getWidth() * object.pivot.x);
			float newX = object.position.x - newPivotX;
			float newPivotY = (sprite.getHeight() * object.pivot.y);
			float newY = object.position.y - newPivotY;

			sprite.setX(newX);
			sprite.setY(newY);

			sprite.setOrigin(newPivotX, newPivotY);
			sprite.setRotation(object.angle);

			sprite.setColor(1f, 1f, 1f, object.alpha);
			sprite.setScale(object.scale.x * spriteScale, object.scale.y * spriteScale);
			sprite.draw(batch);
		}

		public void setImageScale (float imageScale) {
			this.spriteScale = 1f / imageScale;
		}
	}

}
