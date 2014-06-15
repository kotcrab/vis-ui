
package pl.kotcrab.vis.sceneeditor.component;

import pl.kotcrab.vis.sceneeditor.ObjectRepresentation;

import com.badlogic.gdx.utils.Array;

public interface RectangularSelectionActionListener {
	public void drawingFinished (Array<ObjectRepresentation> matchingObjects);
}
