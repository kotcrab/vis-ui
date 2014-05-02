
package pl.kotcrab.vis.sceneeditor.editor;

import pl.kotcrab.vis.sceneeditor.Layer;
import pl.kotcrab.vis.sceneeditor.ObjectRepresentation;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;

public class LayeredSceneEditor extends SceneEditor {
	private Array<Layer> layers = new Array<Layer>();

	public LayeredSceneEditor (FileHandle sceneFile, OrthographicCamera camera, boolean devMode) {
		super(sceneFile, camera, devMode);
	}

	public LayeredSceneEditor (OrthographicCamera camera, boolean enableEditMode) {
		super(camera, enableEditMode);
	}

	public SceneEditor add (Layer layer) {
		layers.add(layer);

		return this;
	}

//	@Override
///	private ObjectRepresentation getSelectedObjectForMousePosition (float x, float y) {
	//	return null;
		//return findObjectWithSamllestSurfaceArea(x, y);
	//}
}
