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
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;

/** Scene2d.ui CheckBox support.
 * 
 * @author Pawel Pastuszak */
public class CheckBoxSupport implements SceneEditorSupport<CheckBox> {

	@Override
	public boolean isScallingSupported () {
		return false;
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
		return CheckBox.class;
	}

	@Override
	public void setX (CheckBox cb, float x) {
		cb.setX(x);
	}

	@Override
	public void setY (CheckBox cb, float y) {
		cb.setY(y);
	}

	@Override
	public float getX (CheckBox cb) {
		return cb.getX();
	}

	@Override
	public float getY (CheckBox cb) {
		return cb.getY();
	}

	@Override
	public float getWidth (CheckBox cb) {
		return cb.getWidth();
	}

	@Override
	public float getHeight (CheckBox cb) {
		return cb.getHeight();
	}

	@Override
	public void setScale (CheckBox cb, float x, float y) {
		cb.setScale(x, y);
	}

	@Override
	public float getScaleX (CheckBox cb) {
		return cb.getScaleX();
	}

	@Override
	public float getScaleY (CheckBox cb) {
		return cb.getScaleY();
	}

	@Override
	public boolean contains (CheckBox cb, float x, float y) {
		return cb.getX() <= x && cb.getX() + cb.getWidth() >= x && cb.getY() <= y && cb.getY() + cb.getHeight() >= y;
	}

	@Override
	public float getRotation (CheckBox cb) {
		return cb.getRotation();
	}

	@Override
	public void setRotation (CheckBox cb, float rotation) {
		cb.setRotation(rotation);
	}

	@Override
	public void setOrigin (CheckBox cb, float x, float y) {
		cb.setOrigin(x, y);
	}

	@Override
	public float getOriginX (CheckBox cb) {
		return cb.getOriginX();
	}

	@Override
	public float getOriginY (CheckBox cb) {
		return cb.getOriginY();
	}

	@Override
	public Rectangle getBoundingRectangle (CheckBox cb) {
		return new Rectangle(cb.getX(), cb.getY(), cb.getWidth(), cb.getHeight());
	}

	@Override
	public void setSize (CheckBox cb, float width, float height) {
		cb.setSize(width, height);
	}

}
