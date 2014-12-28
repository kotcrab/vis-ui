
package pl.kotcrab.vis.editor.module;

public class EditorModuleContainer extends BaseModuleContainer<EditorModule> {
	@Override
	public void add (EditorModule module) {
		module.setContainer(this);
		super.add(module);
	}
}
