/*
 * Copyright 2014-2017 See AUTHORS file.
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
 */

package com.kotcrab.vis.ui.util;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.VisUI;

/**
 * Utilities for VisTable/Table.
 * @author Kotcrab
 */
public class TableUtils {
	/** Sets default table spacing for VisUI skin. Uses values from current skin {@link Sizes} class obtained from {@link VisUI#getSizes()}. */
	public static void setSpacingDefaults (Table table) {
		Sizes sizes = VisUI.getSizes();
		if (sizes.spacingTop != 0) table.defaults().spaceTop(sizes.spacingTop);
		if (sizes.spacingBottom != 0) table.defaults().spaceBottom(sizes.spacingBottom);
		if (sizes.spacingRight != 0) table.defaults().spaceRight(sizes.spacingRight);
		if (sizes.spacingLeft != 0) table.defaults().spaceLeft(sizes.spacingLeft);
	}
}
