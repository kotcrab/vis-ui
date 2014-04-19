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

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

/** Scene2d.ui Touchpad support.
 * 
 * @author Pawel Pastuszak */
public class TouchpadSupport extends DefaultSceneEditorSupport<Touchpad> {

	@Override
	public boolean isScallingSupported () {
		return true;
	}

	@Override
	public boolean isRotatingSupported () {
		return false;
	}

	@Override
	public boolean isMovingSupported () {
		return true;
	}

	@Override
	public boolean isOriginSupported () {
		return false;
	}

	@Override
	public Class<?> getSupportedClass () {
		return Touchpad.class;
	}

	@Override
	public void setX (Touchpad t, float x) {
		t.setX(x);
	}

	@Override
	public void setY (Touchpad t, float y) {
		t.setY(y);
	}

	@Override
	public float getX (Touchpad t) {
		return t.getX();
	}

	@Override
	public float getY (Touchpad t) {
		return t.getY();
	}

	@Override
	public float getWidth (Touchpad t) {
		return t.getWidth();
	}

	@Override
	public float getHeight (Touchpad t) {
		return t.getHeight();
	}

	@Override
	public void setScale (Touchpad t, float x, float y) {
		t.setScale(x, y);
	}

	@Override
	public float getScaleX (Touchpad t) {
		return t.getScaleX();
	}

	@Override
	public float getScaleY (Touchpad t) {
		return t.getScaleY();
	}

	@Override
	public boolean contains (Touchpad t, float x, float y) {
		return t.getX() <= x && t.getX() + t.getWidth() >= x && t.getY() <= y && t.getY() + t.getHeight() >= y;
	}

	@Override
	public float getRotation (Touchpad t) {
		return t.getRotation();
	}

	@Override
	public void setRotation (Touchpad t, float rotation) {
		t.setRotation(rotation);
	}

	@Override
	public void setOrigin (Touchpad t, float x, float y) {
		t.setOrigin(x, y);
	}

	@Override
	public float getOriginX (Touchpad t) {
		return t.getOriginX();
	}

	@Override
	public float getOriginY (Touchpad t) {
		return t.getOriginY();
	}

	@Override
	public Rectangle getBoundingRectangle (Touchpad t) {
		return new Rectangle(t.getX(), t.getY(), t.getWidth(), t.getHeight());
	}

	@Override
	public void setSize (Touchpad t, float width, float height) {
		t.setSize(width, height);
	}

}
