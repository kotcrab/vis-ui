
package pl.kotcrab.vis.editor.ui.scene;

import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.VisUI;
import pl.kotcrab.vis.ui.widget.VisLabel;

public class ActorProperites extends VisTable {
	public ActorProperites () {
		super(true);
		setBackground(VisUI.skin.getDrawable("window-bg"));
		top();
		add(new VisLabel("Actor Properties"));
	}
}
