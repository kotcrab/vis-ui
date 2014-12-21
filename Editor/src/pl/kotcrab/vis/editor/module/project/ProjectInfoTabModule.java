
package pl.kotcrab.vis.editor.module.project;

import pl.kotcrab.vis.editor.App;
import pl.kotcrab.vis.editor.Editor;
import pl.kotcrab.vis.editor.event.Event;
import pl.kotcrab.vis.editor.event.EventListener;
import pl.kotcrab.vis.editor.module.TabsModule;
import pl.kotcrab.vis.editor.ui.ProjectInfoTab;

public class ProjectInfoTabModule extends ProjectModule implements EventListener {

	private Editor editor;
	private TabsModule tabsModule;

	private ProjectInfoTab tab;

	public ProjectInfoTabModule () {
		editor = Editor.instance;
		tabsModule = editor.getModule(TabsModule.class);
	}

	@Override
	public void init () {
		tab = new ProjectInfoTab(project);
		tabsModule.addTab(tab);
	}
	
	@Override
	public void added () {
		App.eventBus.register(this);
	}

	@Override
	public void dispose () {
		App.eventBus.unregister(this);
		tabsModule.removeTab(tab);
	}

	@Override
	public boolean onEvent (Event e) {

		return false;
	}
}
