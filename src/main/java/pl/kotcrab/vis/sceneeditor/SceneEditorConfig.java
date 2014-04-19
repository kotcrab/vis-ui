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

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

/** Configuration of SceneEditor
 * 
 * @author Pawel Pastuszak */
public class SceneEditorConfig {
	
	/**
	 * Must be set manualy only for desktop, on other platforms this must be null.
	 * Set to SceneEditorConfig.desktopInterface = new DesktopHandler();
	 */
	public static DesktopInterface desktopInterface;
	
	public static int KEY_ROTATE_SNAP_VALUES = Keys.SHIFT_LEFT;
	public static int KEY_SCALE_LOCK_RATIO = Keys.SHIFT_LEFT;
	public static int KEY_MULTISELECT = Keys.SHIFT_LEFT;

	public static int KEY_NO_SELECT_MODE = Keys.ALT_LEFT;
	public static int KEY_PRECISION_MODE = Keys.CONTROL_LEFT;
	public static int PRECISION_DIVIDE_BY = 10;

	public static int KEY_LOCK_CAMERA = Keys.F1;
	public static int KEY_RESET_CAMERA = Keys.F2;
	public static int KEY_RESET_OBJECT_SIZE = Keys.F3;
	public static int KEY_HIDE_OUTLINES = Keys.F4;
	public static int KEY_TOGGLE_EDIT_MODE = Keys.F11;

	public static int KEY_SPECIAL_ACTIONS = Keys.CONTROL_LEFT;
	public static int KEY_SPECIAL_SAVE_CHANGES = Keys.S;
	public static int KEY_SPECIAL_UNDO = Keys.Z;
	public static int KEY_SPECIAL_REDO = Keys.Y;

	public static int KEY_INPUT_MODE_EDIT_CANCEL = Keys.ESCAPE;
	public static int KEY_INPUT_MODE_EDIT_CONFIRM = Keys.ENTER;
	public static int KEY_INPUT_MODE_EDIT_BACKSPACE = Keys.BACKSPACE;

	public static int KEY_INPUT_MODE_EDIT_POSX = Keys.X;
	public static int KEY_INPUT_MODE_EDIT_POSY = Keys.Y;
	public static int KEY_INPUT_MODE_EDIT_ROTATION = Keys.R;
	public static int KEY_INPUT_MODE_EDIT_WIDTH = Keys.W;
	public static int KEY_INPUT_MODE_EDIT_HEIGHT = Keys.H;

	/** When Libgdx app is exiting, and {@link SceneEditor#dispose()} was called. Exit will be stoped and user will have to input in
	 * console Y or N, when he wants to save changes to file or not */
	public static boolean lastChanceSave = true;

	/** Maximum zooming in camera value.<br>
	 * Note: Final zoom may be smaller than this value. */
	public static float CAMERA_MAX_ZOOM_IN = 0.5f;
	/** Maximum zooming out camera value.<br>
	 * Note: Final zoom may be bigger than this value. */
	public static float CAMERA_MAX_ZOOM_OUT = 4f;

	public static boolean GUI_DRAW = true;
	public static boolean GUI_DRAW_TITLE = true;
	public static boolean GUI_DRAW_OBJECT_INFO = true;
	public static Color GUI_TEXT_COLOR = Color.WHITE;
}
