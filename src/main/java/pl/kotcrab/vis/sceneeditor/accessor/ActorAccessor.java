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

package pl.kotcrab.vis.sceneeditor.accessor;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

/** Scene2d Actor support.
 * 
 * @author Pawel Pastuszak */
public class ActorAccessor extends DefaultSceneEditorAccessor<Actor> {
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
		return Actor.class;
	}

	@Override
	public void setX (Actor a, float x) {
		a.setX(x);
	}

	@Override
	public void setY (Actor a, float y) {
		a.setY(y);
	}

	@Override
	public float getX (Actor a) {
		return a.getX();
	}

	@Override
	public float getY (Actor a) {
		return a.getY();
	}

	@Override
	public float getWidth (Actor a) {
		return a.getWidth();
	}

	@Override
	public float getHeight (Actor a) {
		return a.getHeight();
	}

	@Override
	public void setScale (Actor a, float x, float y) {
		a.setScale(x, y);
	}

	@Override
	public float getScaleX (Actor a) {
		return a.getScaleX();
	}

	@Override
	public float getScaleY (Actor a) {
		return a.getScaleY();
	}

	@Override
	public boolean contains (Actor a, float x, float y) {
		return a.getX() <= x && a.getX() + a.getWidth() >= x && a.getY() <= y && a.getY() + a.getHeight() >= y;
	}

	@Override
	public float getRotation (Actor a) {
		return a.getRotation();
	}

	@Override
	public void setRotation (Actor a, float rotation) {
		a.setRotation(rotation);
	}

	@Override
	public void setOrigin (Actor a, float x, float y) {
		a.setOrigin(x, y);
	}

	@Override
	public float getOriginX (Actor a) {
		return a.getOriginX();
	}

	@Override
	public float getOriginY (Actor a) {
		return a.getOriginY();
	}

	@Override
	public Rectangle getBoundingRectangle (Actor a) {
		return new Rectangle(a.getX(), a.getY(), a.getWidth(), a.getHeight());
	}

	@Override
	public void setSize (Actor a, float width, float height) {
		a.setSize(width, height);
	}

}
