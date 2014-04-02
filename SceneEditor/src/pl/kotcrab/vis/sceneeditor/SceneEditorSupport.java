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

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public interface SceneEditorSupport<O> extends Disposable {
	public boolean isScallingSupported ();

	public boolean isRotatingSupported ();

	public boolean isMovingSupported ();

	public void setX (O obj, float x);

	public float getX (O obj);

	public float getY (O obj);

	public void setY (O obj, float y);

	public void setOrigin (O obj, float x, float y);

	public float getOriginX (O obj);

	public float getOriginY (O obj);

	public void setScale (O obj, float scaleX, float scaleY);

	public void setSize (O obj, float width, float height);

	public float getScaleX (O obj);

	public float getScaleY (O obj);

	public float getWidth (O obj);

	public float getHeight (O obj);

	public float getRotation (O obj);

	public void setRotation (O obj, float rotation);

	public boolean contains (O obj, float x, float y);

	public Rectangle getBoundingRectangle (O obj);

}
