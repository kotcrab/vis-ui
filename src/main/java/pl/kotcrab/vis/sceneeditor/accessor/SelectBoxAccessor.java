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
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;

/** Scene2d.ui SelectBox support.
 * 
 * @author Pawel Pastuszak */
@SuppressWarnings("rawtypes")
public class SelectBoxAccessor extends DefaultSceneEditorAccessor<SelectBox> {

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
		return SelectBox.class;
	}

	@Override
	public void setX (SelectBox sb, float x) {
		sb.setX(x);
	}

	@Override
	public void setY (SelectBox sb, float y) {
		sb.setY(y);
	}

	@Override
	public float getX (SelectBox sb) {
		return sb.getX();
	}

	@Override
	public float getY (SelectBox sb) {
		return sb.getY();
	}

	@Override
	public float getWidth (SelectBox sb) {
		return sb.getWidth();
	}

	@Override
	public float getHeight (SelectBox sb) {
		return sb.getHeight();
	}

	@Override
	public void setScale (SelectBox sb, float x, float y) {
		sb.setScale(x, y);
	}

	@Override
	public float getScaleX (SelectBox sb) {
		return sb.getScaleX();
	}

	@Override
	public float getScaleY (SelectBox sb) {
		return sb.getScaleY();
	}

	@Override
	public boolean contains (SelectBox sb, float x, float y) {
		return sb.getX() <= x && sb.getX() + sb.getWidth() >= x && sb.getY() <= y && sb.getY() + sb.getHeight() >= y;
	}

	@Override
	public float getRotation (SelectBox sb) {
		return sb.getRotation();
	}

	@Override
	public void setRotation (SelectBox sb, float rotation) {
		sb.setRotation(rotation);
	}

	@Override
	public void setOrigin (SelectBox sb, float x, float y) {
		sb.setOrigin(x, y);
	}

	@Override
	public float getOriginX (SelectBox sb) {
		return sb.getOriginX();
	}

	@Override
	public float getOriginY (SelectBox sb) {
		return sb.getOriginY();
	}

	@Override
	public Rectangle getBoundingRectangle (SelectBox sb) {
		return new Rectangle(sb.getX(), sb.getY(), sb.getWidth(), sb.getHeight());
	}

	@Override
	public void setSize (SelectBox sb, float width, float height) {
		sb.setSize(width, height);
	}

}
