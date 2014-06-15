
package pl.kotcrab.vis.sceneeditor.plugin;

import pl.kotcrab.vis.sceneeditor.EditorState;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;

/** Plugin API for VisSceneEditor
 * 
 * Warning! ALL InputProcessor and GestureListener methods recives x, y values allready UNPROCJETD by camera
 * 
 * @author Pawel Pastuszak */
public interface Plugin {
	public void init (EditorState state);

	public void enable ();

	public void disable ();

	public void render ();

	public void dispose ();
	
	public void resize ();

	/** Called when a key was pressed
	 * 
	 * @param keycode one of the constants in {@link Input.Keys}
	 * @return whether the input was processed */
	public boolean keyDown (int keycode);

	/** Called when a key was released
	 * 
	 * @param keycode one of the constants in {@link Input.Keys}
	 * @return whether the input was processed */
	public boolean keyUp (int keycode);

	/** Called when a key was typed
	 * 
	 * @param character The character
	 * @return whether the input was processed */
	public boolean keyTyped (char character);

	/** Called when the screen was touched or a mouse button was pressed. The button parameter will be {@link Buttons#LEFT} on
	 * Android and iOS.
	 * @param sceneX The x coordinate, unprojected by camera
	 * @param sceneY The y coordinate, unprojected by camera
	 * @param pointer the pointer for the event.
	 * @param button the button
	 * @return whether the input was processed */
	public boolean touchDown (int sceneX, int sceneY, int pointer, int button);

	/** Called when a finger was lifted or a mouse button was released. The button parameter will be {@link Buttons#LEFT} on Android
	 * and iOS.
	 * @param pointer the pointer for the event.
	 * @param button the button
	 * @return whether the input was processed */
	public boolean touchUp (int sceneX, int sceneY, int pointer, int button);

	/** Called when a finger or the mouse was dragged.
	 * @param pointer the pointer for the event.
	 * @return whether the input was processed */
	public boolean touchDragged (int sceneX, int sceneY, int pointer);

	/** Called when the mouse was moved without any buttons being pressed. Will not be called on either Android or iOS.
	 * @return whether the input was processed */
	public boolean mouseMoved (int sceneX, int sceneY);

	/** Called when the mouse wheel was scrolled. Will not be called on either Android or iOS.
	 * @param amount the scroll amount, -1 or 1 depending on the direction the wheel was scrolled.
	 * @return whether the input was processed. */
	public boolean scrolled (int amount);

	/** @see InputProcessor#touchDown(int, int, int, int) */
	public boolean touchDown (float x, float y, int pointer, int button);


}
