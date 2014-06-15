
package pl.kotcrab.vis.sceneeditor.editor;

import pl.kotcrab.vis.sceneeditor.EditorState;
import pl.kotcrab.vis.sceneeditor.SceneEditorConfig;
import pl.kotcrab.vis.sceneeditor.SceneEditorInputAdapter;
import pl.kotcrab.vis.sceneeditor.accessor.SceneEditorAccessor;
import pl.kotcrab.vis.sceneeditor.component.AccessorHandler;
import pl.kotcrab.vis.sceneeditor.plugin.PluginManager;
import pl.kotcrab.vis.sceneeditor.plugin.impl.CameraControllerPlugin;
import pl.kotcrab.vis.sceneeditor.plugin.impl.GUIPlugin;
import pl.kotcrab.vis.sceneeditor.plugin.impl.ObjectManagerPlugin;
import pl.kotcrab.vis.sceneeditor.plugin.impl.ObjectManipulatorPlugin;
import pl.kotcrab.vis.sceneeditor.plugin.impl.RendererPlugin;
import pl.kotcrab.vis.sceneeditor.plugin.impl.UndoPlugin;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public abstract class PluginableSceneEditor extends SceneEditorInputAdapter {
	private AccessorHandler<SceneEditorAccessor<?>> accessorHandler = new AccessorHandler<SceneEditorAccessor<?>>();

	private PluginManager pluginManger;
	private EditorState state;

	// plugins
	private CameraControllerPlugin cameraControllerPlugin;
	private ObjectManagerPlugin objectManagerPlugin;
	private UndoPlugin undoPlugin;

	public PluginableSceneEditor (OrthographicCamera camera, boolean enableEditMode) {
		state = new EditorState();

		if (Gdx.app.getType() != ApplicationType.Desktop) enableEditMode = false;
		if (enableEditMode) state.devMode = true;

		pluginManger = new PluginManager(state);

		objectManagerPlugin = new ObjectManagerPlugin(accessorHandler);
		pluginManger.registerPlugin(objectManagerPlugin);

		// DevMode can be only activated on desktop

		if (state.devMode) {
			SceneEditorConfig.load();
			
			attachInputProcessor();

			state.devMode = true;

			cameraControllerPlugin = new CameraControllerPlugin(objectManagerPlugin, camera);
			undoPlugin = new UndoPlugin();
			
			pluginManger.registerPlugin(cameraControllerPlugin);
			pluginManger.registerPlugin(undoPlugin);
			pluginManger.registerPlugin(new ObjectManipulatorPlugin(objectManagerPlugin, undoPlugin));
			pluginManger.registerPlugin(new RendererPlugin(cameraControllerPlugin, objectManagerPlugin));
			pluginManger.registerPlugin(new GUIPlugin(cameraControllerPlugin, objectManagerPlugin));
		}

	}

	/** Add obj to object list, if accessor for this object class was not registed it won't be added
	 * 
	 * @param obj object that will be added to list
	 * @param identifier unique identifer, used when saving and loading
	 * 
	 * @return This SceneEditor for the purpose of chaining methods together. */
	public PluginableSceneEditor add (Object obj, String identifier) {
		objectManagerPlugin.add(obj, identifier);
		return this;
	}

	/** Register accessor and allow object of provied class be added to scene */
	public void registerAccessor (SceneEditorAccessor<?> accessor) {
		accessorHandler.registerAccessor(accessor);
	}

	public void enable () {
		state.editing = true;
		pluginManger.enable();
	}

	public void disable () {
		state.editing = false;
		pluginManger.disable();
	}

	public void render () {
		pluginManger.render();
	}

	public void dispose () {
		pluginManger.dispose();
	}

	public void resize () {
		pluginManger.resize();
	}

	public boolean keyDown (int keycode) {
		if(keycode == SceneEditorConfig.KEY_TOGGLE_EDIT_MODE)
		{
			if(state.editing)
				disable();
			else
				enable();
		}
		
		pluginManger.keyDown(keycode);
		return false;
	}

	public boolean keyUp (int keycode) {
		pluginManger.keyUp(keycode);
		return false;
	}

	public boolean keyTyped (char character) {
		pluginManger.keyTyped(character);
		return false;
	}

	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		int sceneX = cameraControllerPlugin.calcX(screenX);
		int sceneY = cameraControllerPlugin.calcY(screenY);
		pluginManger.touchDown(sceneX, sceneY, pointer, button);
		return false;
	}

	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		int sceneX = cameraControllerPlugin.calcX(screenX);
		int sceneY = cameraControllerPlugin.calcY(screenY);
		pluginManger.touchUp(sceneX, sceneY, pointer, button);
		return false;
	}

	public boolean touchDragged (int screenX, int screenY, int pointer) {
		int sceneX = cameraControllerPlugin.calcX(screenX);
		int sceneY = cameraControllerPlugin.calcY(screenY);
		pluginManger.touchDragged(sceneX, sceneY, pointer);
		return false;
	}

	@Override
	public boolean mouseMoved (int screenX, int screenY) {
		int sceneX = cameraControllerPlugin.calcX(screenX);
		int sceneY = cameraControllerPlugin.calcY(screenY);
		pluginManger.mouseMoved(sceneX, sceneY);
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		pluginManger.scrolled(amount);
		return false;
	}
}
