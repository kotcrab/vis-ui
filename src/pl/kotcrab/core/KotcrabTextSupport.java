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

package pl.kotcrab.core;


import pl.kotcrab.vis.sceneeditor.SceneEditorSupport;

import com.badlogic.gdx.math.Rectangle;

public class KotcrabTextSupport implements SceneEditorSupport<KotcrabText> {

	@Override
	public Object load () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save (KotcrabText k) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose () {
	}

	@Override
	public void setX (KotcrabText k, float x) {
		k.setX(x);
	}

	@Override
	public void setY (KotcrabText k, float y) {
		k.setY(y);
	}

	@Override
	public float getX (KotcrabText k) {
		return k.getPosition().x;
	}

	@Override
	public float getY (KotcrabText k) {
		return k.getPosition().y;
	}

	@Override
	public float getWidth (KotcrabText k) {
		return k.getTextBounds().width;
	}

	@Override
	public float getHeight (KotcrabText k) {
		return k.getTextBounds().height;
	}

	@Override
	public void setScale (KotcrabText k, float x, float y) {
		k.setScale(x, y);
	}

	@Override
	public float getScaleX (KotcrabText k) {
		return k.getScale().x;
	}

	@Override
	public float getScaleY (KotcrabText k) {
		return k.getScale().y;
	}

	@Override
	public boolean contains (KotcrabText k, float x, float y) {
		return new Rectangle(k.getPosition().x, k.getPosition().y, k.getTextBounds().width, k.getTextBounds().height)
			.contains(x, y);
	}

	@Override
	public boolean isScallingSupported () {
		return false;
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
	public float getRotation (KotcrabText k) {
		return k.getRotation();
	}

	@Override
	public void setRotation (KotcrabText k, float rotation) {
		k.setRotation(rotation);
	}

	@Override
	public void setOrigin (KotcrabText k, float x, float y) {
		k.setOrigin(x, y);
	}

	@Override
	public float getOriginX (KotcrabText k) {
		return k.getOrigin().x;
	}

	@Override
	public float getOriginY (KotcrabText k) {
		return k.getOrigin().y;
	}

	@Override
	public Rectangle getBoundingRectangle (KotcrabText k) {
		return new Rectangle(k.getPosition().x, k.getPosition().y, k.getTextBounds().width, k.getTextBounds().height);
	}

	@Override
	public void setSize (KotcrabText k, float width, float height) {
// k.setSize(width, height);
	}

}
