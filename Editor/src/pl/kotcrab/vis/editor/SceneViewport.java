
package pl.kotcrab.vis.editor;

public enum SceneViewport {
	STRETCH, FIT, FILL, SCREEN, EXTEND;

	@Override
	public String toString () {
		switch (this) {
		case STRETCH:
			return "Stretch Viewport";
		case FIT:
			return "Fit Viewport";
		case FILL:
			return "Fill Viewport";
		case EXTEND:
			return "Extend Viewport";
		case SCREEN:
			return "Screen Viewport";
		default:
			return super.toString();
		}
	}
}
