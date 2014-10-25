
package pl.kotcrab.vis.editor.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class TableUtils {
	public static void setSpaceDefaults (Table table) {
		table.defaults().spaceBottom(6);
		table.defaults().spaceRight(6);
	}

	public static void setColumnsDefaults (Table table) {
		table.columnDefaults(0).spaceLeft(3);
		table.columnDefaults(table.getColumns()).spaceRight(3);
	}
}
