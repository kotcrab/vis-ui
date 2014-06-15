
package pl.kotcrab.vis.sceneeditor.plugin;

import pl.kotcrab.vis.sceneeditor.EditorState;

public class PluginState extends PluginAdapter {
	protected EditorState state;

	@Override
	public void init (EditorState state) {
		this.state = state;
	}
}
