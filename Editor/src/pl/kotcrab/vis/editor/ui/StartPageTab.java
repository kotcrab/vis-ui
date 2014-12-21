
package pl.kotcrab.vis.editor.ui;

import pl.kotcrab.vis.editor.Tab;
import pl.kotcrab.vis.ui.widget.VisLabel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class StartPageTab implements Tab {
	@Override
	public String getButtonText () {
		return "Start Page";
	}

	@Override
	public Table getContentTable () {
		Table tab = new Table();
		tab.add(new VisLabel("Ohayou!"));
		return tab;
	}

	@Override
	public void render (Batch batch) {

	}
}
