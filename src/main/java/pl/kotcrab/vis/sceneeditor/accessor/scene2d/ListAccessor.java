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
import com.badlogic.gdx.scenes.scene2d.ui.List;

/** Scene2d.ui List support.
 * 
 * @author Pawel Pastuszak */
@SuppressWarnings("rawtypes")
public class ListAccessor extends DefaultSceneEditorAccessor<List> {

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
		return List.class;
	}

	@Override
	public void setX (List l, float x) {
		l.setX(x);
	}

	@Override
	public void setY (List l, float y) {
		l.setY(y);
	}

	@Override
	public float getX (List l) {
		return l.getX();
	}

	@Override
	public float getY (List l) {
		return l.getY();
	}

	@Override
	public float getWidth (List l) {
		return l.getWidth();
	}

	@Override
	public float getHeight (List l) {
		return l.getHeight();
	}

	@Override
	public void setScale (List l, float x, float y) {
		l.setScale(x, y);
	}

	@Override
	public float getScaleX (List l) {
		return l.getScaleX();
	}

	@Override
	public float getScaleY (List l) {
		return l.getScaleY();
	}

	@Override
	public boolean contains (List l, float x, float y) {
		return l.getX() <= x && l.getX() + l.getWidth() >= x && l.getY() <= y && l.getY() + l.getHeight() >= y;
	}

	@Override
	public float getRotation (List l) {
		return l.getRotation();
	}

	@Override
	public void setRotation (List l, float rotation) {
		l.setRotation(rotation);
	}

	@Override
	public void setOrigin (List l, float x, float y) {
		l.setOrigin(x, y);
	}

	@Override
	public float getOriginX (List l) {
		return l.getOriginX();
	}

	@Override
	public float getOriginY (List l) {
		return l.getOriginY();
	}

	@Override
	public Rectangle getBoundingRectangle (List l) {
		return new Rectangle(l.getX(), l.getY(), l.getWidth(), l.getHeight());
	}

	@Override
	public void setSize (List l, float width, float height) {
		l.setSize(width, height);
	}

}
