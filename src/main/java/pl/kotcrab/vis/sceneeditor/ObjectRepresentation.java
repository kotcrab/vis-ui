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

package pl.kotcrab.vis.sceneeditor;

import pl.kotcrab.vis.sceneeditor.accessor.SceneEditorAccessor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

@SuppressWarnings({"rawtypes", "unchecked"})
class ObjectRepresentation {
	private SceneEditorAccessor sup;
	private Object obj;
	private String identifier;

	private float startingValue; // keyboard input mode uses this when inputing object properties

	private final float scaleRatio;
	private final float defaultWidth;
	private final float defaultHeight;

	private boolean pointerInsideScaleArea;
	private boolean pointerInsideRotateArea;

	private float attachScreenX; // for scaling/rotating object
	private float attachScreenY;
	private float lastTouchX;
	private float lastTouchY;

	private float startingWidth; // object properies before moving/scalling/rotating/etc
	private float startingHeight;
	private float startingRotation;
	// private float startingX;
	// private float startingY;

	private EditorAction lastEditorAction;

	public ObjectRepresentation (SceneEditorAccessor sup, Object obj, String identifier) {
		this.sup = sup;
		this.obj = obj;
		this.identifier = identifier;

		scaleRatio = getWidth() / getHeight();
		defaultWidth = getWidth();
		defaultHeight = getHeight();
	}

	/** @param x unprocjeted by camera screen x
	 * @param y unprocjeted by camera screen y */
	public void setValues (float x, float y) {
		attachScreenX = x;
		attachScreenY = y;
		lastTouchX = x;
		lastTouchY = y;

		// startingX = sup.getX(obj);
		// startingY = sup.getY(obj);
		startingWidth = getWidth();
		startingHeight = getHeight();
		startingRotation = getRotation();

		lastEditorAction = new EditorAction(this);
	}

	public void mouseMoved (float x, float y) {
		pointerInsideScaleArea = Utils.buildRectangeForScaleArea(this).contains(x, y);
		pointerInsideRotateArea = Utils.buildCirlcleForRotateArea(this).contains(x, y);
	}

	public boolean draggedScale (float x, float y) {
		if (isScallingSupported()) {
			float deltaX = x - attachScreenX;
			float deltaY = y - attachScreenY;

			if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_SCALE_LOCK_RATIO)) {
				deltaY = deltaX / scaleRatio;
			}

			setSize(startingWidth + deltaX, startingHeight + deltaY);
			return true;
		} else
			return false;
	}

	public boolean draggedMove (float x, float y) {
		if (isMovingSupported()) {
			float deltaX = (x - lastTouchX);
			float deltaY = (y - lastTouchY);

			if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_PRECISION_MODE)) {
				deltaX /= SceneEditorConfig.PRECISION_DIVIDE_BY;
				deltaY /= SceneEditorConfig.PRECISION_DIVIDE_BY;
			}
			setX(getX() + deltaX);
			setY(getY() + deltaY);

			lastTouchX = x;
			lastTouchY = y;
			return true;
		} else
			return false;
	}

	public boolean draggedRotate (float x, float y) {
		if (isRotatingSupported()) {
			Rectangle rect = getBoundingRectangle();
			float deltaX = x - (rect.x + rect.width / 2);
			float deltaY = y - (rect.y + rect.height / 2);

			float deg = MathUtils.atan2(-deltaX, deltaY) / MathUtils.degreesToRadians;

			if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_ROTATE_SNAP_VALUES)) {
				int roundDeg = Math.round(deg / 30);
				setRotation(startingRotation + roundDeg * 30);
			} else
				setRotation(startingRotation + deg);

			return true;
		} else
			return false;
	}

	public void resetSize () {
		setSize(defaultWidth, defaultHeight);
	}

	public float getStartingValue () {
		return startingValue;
	}

	public void setStartingValue (float startingValue) {
		this.startingValue = startingValue;
	}

	public float getStartingRotation () {
		return startingRotation;
	}

	public EditorAction getLastEditorAction () {
		return lastEditorAction;
	}

	public boolean isPointerInsideScaleArea () {
		return pointerInsideScaleArea;
	}

	public boolean isPointerInsideRotateArea () {
		return pointerInsideRotateArea;
	}

	public Object getObject () {
		return obj;
	}

	public String getIdentifier () {
		return identifier;
	}

	// SceneEditorSupport impl

	public boolean isScallingSupported () {
		return sup.isScallingSupported();
	}

	public boolean isRotatingSupported () {
		return sup.isRotatingSupported();
	}

	public boolean isMovingSupported () {
		return sup.isMovingSupported();
	}

	public boolean isOriginSupported () {
		return sup.isOriginSupported();
	}

	public void setX (float x) {
		sup.setX(obj, x);
	}

	public void setY (float y) {
		sup.setY(obj, y);
	}

	public float getX () {
		return sup.getX(obj);
	}

	public float getY () {
		return sup.getY(obj);
	}

	public void setOrigin (float x, float y) {
		sup.setOrigin(obj, x, y);
	}

	public float getOriginX () {
		return sup.getOriginX(obj);
	}

	public float getOriginY () {
		return sup.getOriginY(obj);
	}

	public void setScale (float scaleX, float scaleY) {
		sup.setScale(obj, scaleX, scaleY);
	}

	public void setSize (float width, float height) {
		sup.setSize(obj, width, height);
	}

	public float getScaleX () {
		return sup.getScaleX(obj);
	}

	public float getScaleY () {
		return sup.getScaleY(obj);
	}

	public float getScaleRatio () {
		return scaleRatio;
	}

	public float getWidth () {
		return sup.getWidth(obj);
	}

	public float getHeight () {
		return sup.getHeight(obj);
	}

	public float getRotation () {
		return sup.getRotation(obj);
	}

	public void setRotation (float rotation) {
		sup.setRotation(obj, rotation);
	}

	public boolean contains (float x, float y) {
		return sup.contains(obj, x, y);
	}

	public Rectangle getBoundingRectangle () {
		return sup.getBoundingRectangle(obj);
	}

}
