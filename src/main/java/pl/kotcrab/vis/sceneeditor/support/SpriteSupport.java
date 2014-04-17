/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/

package pl.kotcrab.vis.sceneeditor.support;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

/** Support for Sprite class
 * @author Pawel Pastuszak */
public class SpriteSupport implements SceneEditorSupport<Sprite> {

	@Override
	public boolean isScallingSupported () {
		return true;
	}

	@Override
	public boolean isRotatingSupported () {
		return true;
	}

	@Override
	public boolean isMovingSupported () {
		return true;
	}

	@Override
	public boolean isOriginSupported () {
		return true;
	}

	@Override
	public Class<?> getSupportedClass () {
		return Sprite.class;
	}

	@Override
	public void setX (Sprite s, float x) {
		s.setX(x);
	}

	@Override
	public void setY (Sprite s, float y) {
		s.setY(y);
	}

	@Override
	public float getX (Sprite s) {
		return s.getX();
	}

	@Override
	public float getY (Sprite s) {
		return s.getY();
	}

	@Override
	public float getWidth (Sprite s) {
		return s.getWidth();
	}

	@Override
	public float getHeight (Sprite s) {
		return s.getHeight();
	}

	@Override
	public void setScale (Sprite s, float x, float y) {
		s.setScale(x, y);
	}

	@Override
	public float getScaleX (Sprite s) {
		return s.getScaleX();
	}

	@Override
	public float getScaleY (Sprite s) {
		return s.getScaleY();
	}

	@Override
	public boolean contains (Sprite s, float x, float y) {
		return s.getBoundingRectangle().contains(x, y);
	}

	@Override
	public float getRotation (Sprite s) {
		return s.getRotation();
	}

	@Override
	public void setRotation (Sprite s, float rotation) {
		s.setRotation(rotation);
	}

	@Override
	public void setOrigin (Sprite s, float x, float y) {
		s.setOrigin(x, y);
	}

	@Override
	public float getOriginX (Sprite s) {
		return s.getOriginX();
	}

	@Override
	public float getOriginY (Sprite s) {
		return s.getOriginY();
	}

	@Override
	public Rectangle getBoundingRectangle (Sprite s) {
		return s.getBoundingRectangle();
	}

	@Override
	public void setSize (Sprite s, float width, float height) {
		s.setSize(width, height);
	}

}
