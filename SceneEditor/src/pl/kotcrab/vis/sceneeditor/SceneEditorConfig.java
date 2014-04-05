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

public class SceneEditorConfig {
	/** Path to backup folder, must be ended with File.separator */
	public static String backupFolderPath = null;

	/** Path to Android project assets folder. Set this only if you are not using Gradle (your dekstop project directory is not set
	 * to assets folder). Path must be ended with File.separator. If this is not set for non Gradle project files may be saved in
	 * wrong location! */
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

	public static boolean GUI_DRAW = true;
	public static boolean GUI_DRAW_OBJECT_INFO = true;
	public static boolean GUI_DRAW_TITLE = true;

}
