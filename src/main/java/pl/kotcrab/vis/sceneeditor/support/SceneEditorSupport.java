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

/** Interface that all supports must implement. <br>
 * 
 * Method that must be implemneted: getX, getY, getBoundingRectangle, contains. For other please refer to isMovingSupported(),
 * isRotatingSupported() and isScallingSupported()
 * @see SceneEditorSupport#isMovingSupported()
 * @see SceneEditorSupport#isRotatingSupported()
 * @see SceneEditorSupport#isScallingSupported()
 * 
 * @author Pawel Pastuszak
 * 
 * @param <O> Object that this class will support */
public interface SceneEditorSupport<O> {
	/** When this method return true, setScale, setSize, getScaleX, getScaleY, getWidth, getHeight must be implemented
	 * 
	 * @return true if object support scalling, false otherwise */
	public boolean isScallingSupported ();

	/** When this method return true, setRotation, getRotation must be implemented
	 * 
	 * @return true if object support rotating, false otherwise */
	public boolean isRotatingSupported ();

	/** When this method return true, setX, setY must be implemented
	 * 
	 * @return true if object support moving, false otherwise */
	public boolean isMovingSupported ();

	/** When this method return true, setOrigin, getOriginX, getOriginY must be implemented
	 * 
	 * @return true if object support origin, false otherwise */
	public boolean isOriginSupported ();

	public Class<?> getSupportedClass();
	
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
