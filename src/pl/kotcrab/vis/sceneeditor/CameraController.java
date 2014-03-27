/*******************************************************************************
 * Copyright 2013 Pawel Pastuszak
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

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

/**
 * Class for caluclating propper touch cordinates
 * 
 * @author Pawel Pastuszak
 */
public class CameraController
{
	private Camera camera;
	private Camera rawCamera;
	private Vector3 calcVector;
	
	/** Prepares class for use */
	public CameraController(Camera camera)
	{
		this.camera = camera;
		rawCamera = new OrthographicCamera(camera.viewportWidth, camera.viewportHeight);
		calcVector = new Vector3(0, 0, 0);
	}
	
	/** Return camera */
	public Camera getCamera()
	{
		return camera;
	}
	
	/**
	 * Return proper touch posistion using provided camera<br>
	 * Call the {@link #setCamera(OrthographicCamera camera) setCamera} method before using.
	 * 
	 * @param x
	 *            form Gdx.input.getX() or event method
	 */
	public float calcX(float x)
	{
		calcVector.x = x;
		camera.unproject(calcVector);
		return calcVector.x;
	}
	
	/**
	 * Return proper touch posistion using provided camera<br> Call the {@link #setCamera(OrthographicCamera camera) setCamera} method before using.
	 * 
	 * @param y
	 *            form Gdx.input.getY() or event method
	 */
	public float calcY(float y)
	{
		calcVector.y = y;
		camera.unproject(calcVector);
		return calcVector.y;
	}
}