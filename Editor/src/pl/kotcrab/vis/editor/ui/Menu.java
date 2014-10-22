
package pl.kotcrab.vis.editor.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class Menu extends Table {
	private String title;

	public Menu (String title) {
		this.title = title;
	}

	public String getTitle () {
		return title;
	}

	public void addItem (MenuItem item) {
		add(item.button).fillX().row();
		pack();
	}
}
