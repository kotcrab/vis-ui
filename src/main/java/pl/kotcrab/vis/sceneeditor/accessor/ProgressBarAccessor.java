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
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;

/** Scene2d.ui ProgressBar pbupport.
 * 
 * @author Pawel Pastuszak */
public class ProgressBarAccessor extends DefaultSceneEditorAccessor<ProgressBar> {

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
		return ProgressBar.class;
	}

	@Override
	public void setX (ProgressBar pb, float x) {
		pb.setX(x);
	}

	@Override
	public void setY (ProgressBar pb, float y) {
		pb.setY(y);
	}

	@Override
	public float getX (ProgressBar pb) {
		return pb.getX();
	}

	@Override
	public float getY (ProgressBar pb) {
		return pb.getY();
	}

	@Override
	public float getWidth (ProgressBar pb) {
		return pb.getWidth();
	}

	@Override
	public float getHeight (ProgressBar pb) {
		return pb.getHeight();
	}

	@Override
	public void setScale (ProgressBar pb, float x, float y) {
		pb.setScale(x, y);
	}

	@Override
	public float getScaleX (ProgressBar pb) {
		return pb.getScaleX();
	}

	@Override
	public float getScaleY (ProgressBar pb) {
		return pb.getScaleY();
	}

	@Override
	public boolean contains (ProgressBar pb, float x, float y) {
		return pb.getX() <= x && pb.getX() + pb.getWidth() >= x && pb.getY() <= y && pb.getY() + pb.getHeight() >= y;
	}

	@Override
	public float getRotation (ProgressBar pb) {
		return pb.getRotation();
	}

	@Override
	public void setRotation (ProgressBar pb, float rotation) {
		pb.setRotation(rotation);
	}

	@Override
	public void setOrigin (ProgressBar pb, float x, float y) {
		pb.setOrigin(x, y);
	}

	@Override
	public float getOriginX (ProgressBar pb) {
		return pb.getOriginX();
	}

	@Override
	public float getOriginY (ProgressBar pb) {
		return pb.getOriginY();
	}

	@Override
	public Rectangle getBoundingRectangle (ProgressBar pb) {
		return new Rectangle(pb.getX(), pb.getY(), pb.getWidth(), pb.getHeight());
	}

	@Override
	public void setSize (ProgressBar pb, float width, float height) {
		pb.setSize(width, height);
	}

}
