
package pl.kotcrab.vis.editor.ui;

import pl.kotcrab.vis.editor.ui.tab.Tab;
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
		Table content = new Table();
		content.add(new VisLabel("Ohayou!"));
		content.row();
		content.add(new VisLabel("(here will be recent project list etc.)"));
		return content;
	}

	@Override
	public void render (Batch batch) {

	}
}
