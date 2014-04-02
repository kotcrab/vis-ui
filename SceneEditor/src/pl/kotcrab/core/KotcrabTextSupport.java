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
import com.sun.org.apache.regexp.internal.recompile;

public class KotcrabTextSupport implements SceneEditorSupport<KotcrabText> {

	@Override
	public KotcrabText load () {
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
	public void setX (KotcrabText k, float x) {
		k.setX(x);
	}

	@Override
	public void setY (KotcrabText k, float y) {
		k.setY(y);
	}

	@Override
	public float getX (KotcrabText k) {
		return k.getX();
	}

	@Override
	public float getY (KotcrabText k) {
		return k.getY();
	}

	@Override
	public float getWidth (KotcrabText k) {
		return k.getWidth();
	}

	@Override
	public float getHeight (KotcrabText k) {
		return k.getHeight();
	}

	@Override
	public void setScale (KotcrabText k, float x, float y) {
		k.setScale(x, y);
	}

	@Override
	public float getScaleX (KotcrabText k) {
		return k.getScaleX();
	}

	@Override
	public float getScaleY (KotcrabText k) {
		return k.getScaleY();
	}

	@Override
	public void setSize (KotcrabText k, float width, float height) {
		k.setSize(width, height);
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
		return k.getOriginX();
	}

	@Override
	public float getOriginY (KotcrabText k) {
		return k.getOriginY();
	}

	@Override
	public Rectangle getBoundingRectangle (KotcrabText k) {
		return k.getBoundingRectangle();
	}

	@Override
	public boolean contains (KotcrabText k, float x, float y) {
		return k.getBoundingRectangle().contains(x, y);
	}

}
