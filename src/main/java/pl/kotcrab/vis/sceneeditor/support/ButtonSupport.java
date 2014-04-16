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
import com.badlogic.gdx.scenes.scene2d.ui.Button;

/** Scene2d.ui Button support.
 * 
 * @author Pawel Pastuszak */
public class ButtonSupport implements SceneEditorSupport<Button> {
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
		return Button.class;
	}
	
	@Override
	public void setX (Button b, float x) {
		b.setX(x);
	}

	@Override
	public void setY (Button b, float y) {
		b.setY(y);
	}

	@Override
	public float getX (Button b) {
		return b.getX();
	}

	@Override
	public float getY (Button b) {
		return b.getY();
	}

	@Override
	public float getWidth (Button b) {
		return b.getWidth();
	}

	@Override
	public float getHeight (Button b) {
		return b.getHeight();
	}

	@Override
	public void setScale (Button b, float x, float y) {
		b.setScale(x, y);
	}

	@Override
	public float getScaleX (Button b) {
		return b.getScaleX();
	}

	@Override
	public float getScaleY (Button b) {
		return b.getScaleY();
	}

	@Override
	public boolean contains (Button b, float x, float y) {
		return b.getX() <= x && b.getX() + b.getWidth() >= x && b.getY() <= y && b.getY() + b.getHeight() >= y;
	}

	@Override
	public float getRotation (Button b) {
		return b.getRotation();
	}

	@Override
	public void setRotation (Button b, float rotation) {
		b.setRotation(rotation);
	}

	@Override
	public void setOrigin (Button b, float x, float y) {
		b.setOrigin(x, y);
	}

	@Override
	public float getOriginX (Button b) {
		return b.getOriginX();
	}

	@Override
	public float getOriginY (Button b) {
		return b.getOriginY();
	}

	@Override
	public Rectangle getBoundingRectangle (Button b) {
		return new Rectangle(b.getX(), b.getY(), b.getWidth(), b.getHeight());
	}

	@Override
	public void setSize (Button b, float width, float height) {
		b.setSize(width, height);
	}

}
