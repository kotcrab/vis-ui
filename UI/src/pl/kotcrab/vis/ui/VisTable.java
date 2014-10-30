
package pl.kotcrab.vis.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class VisTable extends Table {

	public VisTable () {
		super(VisUI.skin);
	}

	public VisTable (boolean setVisDefautls) {
		super(VisUI.skin);
		if (setVisDefautls) TableUtils.setSpaceDefaults(this);
	}

}
