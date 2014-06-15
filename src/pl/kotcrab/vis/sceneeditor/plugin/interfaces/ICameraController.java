
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
