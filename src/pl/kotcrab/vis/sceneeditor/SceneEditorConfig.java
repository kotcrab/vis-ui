package pl.kotcrab.vis.sceneeditor;

import com.badlogic.gdx.Input.Keys;

public class SceneEditorConfig
{
	public static int KEY_ROTATE_SNAP_VALUES = Keys.SHIFT_LEFT;
	public static int KEY_SCALE_LOCK_RATIO = Keys.SHIFT_LEFT;
	
	public static int KEY_RESET_CAMERA = Keys.R;
	public static int KEY_TOGGLE_EDIT_MODE = Keys.F11;
	
	/**
	 * Maximum zooming in camera value.<br>
	 * Note: Final zoom may be smaller than this value.
	 */
	public static float CAMERA_MAX_ZOOM_IN = 0.5f;
	/**
	 * Maximum zooming out camera value.<br>
	 * Note: Final zoom may be bigger than this value.
	 */
	public static float CAMERA_MAX_ZOOM_OUT = 4f;
	
	public static boolean DRAW_GUI = true;
}
