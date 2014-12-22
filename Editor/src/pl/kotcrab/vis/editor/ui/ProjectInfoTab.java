
package pl.kotcrab.vis.editor.ui;

import pl.kotcrab.vis.editor.Project;
import pl.kotcrab.vis.editor.Tab;
import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.widget.VisLabel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ProjectInfoTab implements Tab {

	private Project project;

	private Table content;

	public ProjectInfoTab (Project project) {
		this.project = project;

		content = new VisTable(true);

		content.add(new VisLabel("Some project info:"));
		content.row();
		content.add(new VisLabel("Root: " + project.root));
	}

	@Override
	public String getButtonText () {
		return "Project";
	}

	@Override
	public Table getContentTable () {
		return content;
	}

	@Override
	public void render (Batch batch) {

	}

}
