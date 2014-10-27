
package pl.kotcrab.vis.editor.ui;

public class FocusManager {
	private Focusable focusedCompoennt;

	public void requestFocus (Focusable component) {
		if (focusedCompoennt != null) focusedCompoennt.focusLost();
		focusedCompoennt = component;
		focusedCompoennt.focusGained();
	}
}
