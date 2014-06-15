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

package pl.kotcrab.vis.sceneeditor.plugin.interfaces;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

public interface ICameraController {
	public Matrix4 getCombinedMatrix ();

	public boolean isCameraDirty ();

	public boolean isCameraLocked ();

	public Rectangle getOriginalCameraRectangle ();

	/** Return proper touch posistion using provided camera<br>
	 * 
	 * @param x form Gdx.input.getX() or event method */
	public float calcX (float screenX);

	/** Return proper touch posistion using provided camera<br>
	 * 
	 * @param y form Gdx.input.getY() or event method */
	public float calcY (float screenY);

	/** Return proper touch posistion using provided camera<br>
	 * 
	 * @param x form Gdx.input.getX() or event method */
	public int calcX (int screenX);

	/** Return proper touch posistion using provided camera<br>
	 * 
	 * @param y form Gdx.input.getY() or event method */
	public int calcY (int screenY);
}
