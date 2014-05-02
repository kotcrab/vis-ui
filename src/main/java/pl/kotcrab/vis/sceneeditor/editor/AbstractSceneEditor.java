
package pl.kotcrab.vis.sceneeditor.editor;

import pl.kotcrab.vis.sceneeditor.EditorAction;
import pl.kotcrab.vis.sceneeditor.EditorState;
import pl.kotcrab.vis.sceneeditor.SceneEditorConfig;
import pl.kotcrab.vis.sceneeditor.SceneEditorInputAdapter;
import pl.kotcrab.vis.sceneeditor.accessor.SceneEditorAccessor;
import pl.kotcrab.vis.sceneeditor.component.AccessorHandler;
import pl.kotcrab.vis.sceneeditor.component.CameraController;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;

public abstract class AbstractSceneEditor<T extends SceneEditorAccessor<?>> extends SceneEditorInputAdapter {
	public static final String TAG = "VisSceneEditor";

	protected CameraController camController;
	
	private AccessorHandler<T> accessorHandler = new AccessorHandler<T>();

	private Array<Array<EditorAction>> undoList;
	private Array<Array<EditorAction>> redoList;

	protected EditorState state = new EditorState();

	public AbstractSceneEditor (OrthographicCamera camera, boolean enableEditMode) {
		state.devMode = enableEditMode;

		// DevMode can be only activated on desktop
		if (Gdx.app.getType() != ApplicationType.Desktop) state.devMode = false;

		if (state.devMode) {
			SceneEditorConfig.load();
			
			undoList = new Array<Array<EditorAction>>();
			redoList = new Array<Array<EditorAction>>();
			
			camController = new CameraController(camera);
		}
	}

	public void undo () {
		if (undoList.size > 0) {
			Array<EditorAction> actions = undoList.pop();

			for (EditorAction action : actions)
				action.switchValues();

			redoList.add(actions);
		} else
			Gdx.app.log(TAG, "Can't undo any more!");
	}

	public void redo () {
		if (redoList.size > 0) {
			Array<EditorAction> actions = redoList.pop();

			for (EditorAction action : actions)
				action.switchValues();

			undoList.add(actions);
		} else
			Gdx.app.log(TAG, "Can't redo any more!");
	}

	protected void addUndoList (Array<EditorAction> undos) {
		undoList.add(undos);
	}

	/** Register accessor and allow object of provied class be added to scene */
	public void registerAccessor (T accessor) {
		accessorHandler.registerAccessor(accessor);
	}

	/** Check if accessor for provied class is available
	 * 
	 * @param clazz class that will be checked
	 * @return true if accessor is avaiable. false otherwise */
	public boolean isAccessorForClassAvaiable (Class<?> clazz) {
		return accessorHandler.isAccessorForClassAvaiable(clazz);
	}

	/** Returns accessor for provided class
	 * 
	 * @param clazz class that accessor will be return if available
	 * @return accessor if available, null otherwise */
	public T getAccessorForClass (Class<?> clazz) {
		return accessorHandler.getAccessorForClass(clazz);
	}

	/** Returns accessor for provided object
	 * 
	 * @param obj object that accessor will be return if available
	 * @return accessor if available, null otherwise */
	public T getAccessorForObject (Object obj) {
		return getAccessorForClass(obj.getClass());
	}

	public T getAccessorForIdentifier (String accessorIdentifier) {
		return accessorHandler.getAccessorForIdentifier(accessorIdentifier);
	}

}
