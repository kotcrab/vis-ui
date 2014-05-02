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

package pl.kotcrab.vis.sceneeditor.accessor.scene2d;

import pl.kotcrab.vis.sceneeditor.accessor.DefaultSceneEditorAccessor;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/** Scene2d.ui Label support.
 * 
 * @author Pawel Pastuszak */
public class LabelAccessor extends DefaultSceneEditorAccessor<Label> {

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
		return Label.class;
	}

	@Override
	public void setX (Label l, float x) {
		l.setX(x);
	}

	@Override
	public void setY (Label l, float y) {
		l.setY(y);
	}

	@Override
	public float getX (Label l) {
		return l.getX();
	}

	@Override
	public float getY (Label l) {
		return l.getY();
	}

	@Override
	public float getWidth (Label l) {
		return l.getWidth();
	}

	@Override
	public float getHeight (Label l) {
		return l.getHeight();
	}

	@Override
	public void setScale (Label l, float x, float y) {
		l.setScale(x, y);
	}

	@Override
	public float getScaleX (Label l) {
		return l.getScaleX();
	}

	@Override
	public float getScaleY (Label l) {
		return l.getScaleY();
	}

	@Override
	public boolean contains (Label l, float x, float y) {
		return l.getX() <= x && l.getX() + l.getWidth() >= x && l.getY() <= y && l.getY() + l.getHeight() >= y;
	}

	@Override
	public float getRotation (Label l) {
		return l.getRotation();
	}

	@Override
	public void setRotation (Label l, float rotation) {
		l.setRotation(rotation);
	}

	@Override
	public void setOrigin (Label l, float x, float y) {
		l.setOrigin(x, y);
	}

	@Override
	public float getOriginX (Label l) {
		return l.getOriginX();
	}

	@Override
	public float getOriginY (Label l) {
		return l.getOriginY();
	}

	@Override
	public Rectangle getBoundingRectangle (Label l) {
		return new Rectangle(l.getX(), l.getY(), l.getWidth(), l.getHeight());
	}

	@Override
	public void setSize (Label l, float width, float height) {
		l.setSize(width, height);
	}

}
