/*******************************************************************************
 * Copyright 2014-2015 Pawel Pastuszak
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

package pl.kotcrab.vis.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class TableUtils {
	/** Sets default table spacing for vis skin */
	public static void setSpaceDefaults (Table table) {
		table.defaults().spaceBottom(8);
		table.defaults().spaceRight(6);
	}

	@Deprecated
	public static void setColumnsDefaults (Table table) {
		table.columnDefaults(0).spaceLeft(3);
		table.columnDefaults(table.getColumns()).spaceRight(3);
	}
}
