
package pl.kotcrab.vis.sceneeditor;

import com.badlogic.gdx.Input.Keys;

public class SceneEditorConfig {
	/**
	 * Path to backup folder, must be ended with File.separator
	 */
	public static String backupFolderPath = null;
	
	/**
	 * Path to Android project assets folder, must be ended with File.separator
	 */
	public static String assetsFolderPath = null;
	
	public static int KEY_ROTATE_SNAP_VALUES = Keys.SHIFT_LEFT;
	public static int KEY_SCALE_LOCK_RATIO = Keys.SHIFT_LEFT;
	
	public static int KEY_NO_SELECT_MODE = Keys.SHIFT_LEFT;
	public static int KEY_PRECISION_MODE = Keys.CONTROL_LEFT;
	public static int PRECISION_DIVIDE_BY = 10;

	public static int KEY_RESET_CAMERA = Keys.R;
	public static int KEY_LOCK_CAMERA = Keys.L;
	public static int KEY_TOGGLE_EDIT_MODE = Keys.F11;

	/** Maximum zooming in camera value.<br>
	 * Note: Final zoom may be smaller than this value. */
	public static float CAMERA_MAX_ZOOM_IN = 0.5f;
	/** Maximum zooming out camera value.<br>
	 * Note: Final zoom may be bigger than this value. */
	public static float CAMERA_MAX_ZOOM_OUT = 4f;

	public static boolean DRAW_GUI = true;
	public static boolean DRAW_OBJECT_INFO = true;
	
}
