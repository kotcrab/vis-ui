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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/** Scene2d.ui TextButton support.
 * 
 * @author Pawel Pastuszak */
public class TextButtonSupport implements SceneEditorSupport<TextButton> {
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
		return TextButton.class;
	}
	
	@Override
	public void setX (TextButton tb, float x) {
		tb.setX(x);
	}

	@Override
	public void setY (TextButton tb, float y) {
		tb.setY(y);
	}

	@Override
	public float getX (TextButton tb) {
		return tb.getX();
	}

	@Override
	public float getY (TextButton tb) {
		return tb.getY();
	}

	@Override
	public float getWidth (TextButton tb) {
		return tb.getWidth();
	}

	@Override
	public float getHeight (TextButton tb) {
		return tb.getHeight();
	}

	@Override
	public void setScale (TextButton tb, float x, float y) {
		tb.setScale(x, y);
	}

	@Override
	public float getScaleX (TextButton tb) {
		return tb.getScaleX();
	}

	@Override
	public float getScaleY (TextButton tb) {
		return tb.getScaleY();
	}

	@Override
	public boolean contains (TextButton tb, float x, float y) {
		return tb.getX() <= x && tb.getX() + tb.getWidth() >= x && tb.getY() <= y && tb.getY() + tb.getHeight() >= y;
	}

	@Override
	public float getRotation (TextButton tb) {
		return tb.getRotation();
	}

	@Override
	public void setRotation (TextButton tb, float rotation) {
		tb.setRotation(rotation);
	}

	@Override
	public void setOrigin (TextButton tb, float x, float y) {
		tb.setOrigin(x, y);
	}

	@Override
	public float getOriginX (TextButton tb) {
		return tb.getOriginX();
	}

	@Override
	public float getOriginY (TextButton tb) {
		return tb.getOriginY();
	}

	@Override
	public Rectangle getBoundingRectangle (TextButton tb) {
		return new Rectangle(tb.getX(), tb.getY(), tb.getWidth(), tb.getHeight());
	}

	@Override
	public void setSize (TextButton tb, float width, float height) {
		tb.setSize(width, height);
	}

}
