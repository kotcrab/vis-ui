/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package pl.kotcrab.vis.sceneeditor.editor;

import pl.kotcrab.vis.sceneeditor.AccessorHandler;
import pl.kotcrab.vis.sceneeditor.EditorState;
import pl.kotcrab.vis.sceneeditor.SceneEditorConfig;
import pl.kotcrab.vis.sceneeditor.SceneEditorInputAdapter;
import pl.kotcrab.vis.sceneeditor.accessor.SceneEditorAccessor;
import pl.kotcrab.vis.sceneeditor.plugin.PluginManager;
import pl.kotcrab.vis.sceneeditor.plugin.impl.CameraControllerPlugin;
import pl.kotcrab.vis.sceneeditor.plugin.impl.FileSerializerPlugin;
import pl.kotcrab.vis.sceneeditor.plugin.impl.GUIPlugin;
import pl.kotcrab.vis.sceneeditor.plugin.impl.KeyboardInputModePlugin;
import pl.kotcrab.vis.sceneeditor.plugin.impl.ObjectManagerPlugin;
import pl.kotcrab.vis.sceneeditor.plugin.impl.ObjectManipulatorPlugin;
import pl.kotcrab.vis.sceneeditor.plugin.impl.RectangularSelectionPlugin;
import pl.kotcrab.vis.sceneeditor.plugin.impl.RendererPlugin;
import pl.kotcrab.vis.sceneeditor.plugin.impl.UndoPlugin;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Disposable;

public class SceneEditor extends SceneEditorInputAdapter implements Disposable {
	private AccessorHandler<SceneEditorAccessor<?>> accessorHandler = new AccessorHandler<SceneEditorAccessor<?>>();

	private PluginManager pluginManger;
	private EditorState state;

	// plugins
	private CameraControllerPlugin cameraControllerPlugin;
	private ObjectManagerPlugin objectManagerPlugin;
	private FileSerializerPlugin fileSerializerPlugin;

	private KeyboardInputModePlugin keyboardInputModePlugin;

	/** Constructs SceneEditor with FileSerializer for provied internal file.
	 * 
	 * @param sceneFile path to scene file, typicaly with .json extension
	 * @param stage actors will be added to it
	 * @param devMode devMode allow to enter editing mode, if not on desktop it will automaticly be set to false */
	public SceneEditor (FileHandle sceneFile, OrthographicCamera camera, boolean enableEditMode) {
		state = new EditorState();

		// DevMode can be only activated on desktop
		if (Gdx.app.getType() != ApplicationType.Desktop) enableEditMode = false;
		if (enableEditMode) state.devMode = true;

		pluginManger = new PluginManager(state);

		objectManagerPlugin = new ObjectManagerPlugin(accessorHandler);
		fileSerializerPlugin = new FileSerializerPlugin(sceneFile, accessorHandler, objectManagerPlugin);

		pluginManger.registerPlugin(objectManagerPlugin);
		pluginManger.registerPlugin(fileSerializerPlugin);

		if (state.devMode) {
			SceneEditorConfig.load();

			attachInputProcessor();

			cameraControllerPlugin = new CameraControllerPlugin(objectManagerPlugin, camera);

			UndoPlugin undoPlugin = new UndoPlugin();
			RendererPlugin rendererPlugin = new RendererPlugin(cameraControllerPlugin, objectManagerPlugin);
			ObjectManipulatorPlugin objectManipulatorPlugin = new ObjectManipulatorPlugin(objectManagerPlugin, undoPlugin);
			keyboardInputModePlugin = new KeyboardInputModePlugin(objectManagerPlugin, undoPlugin);
			RectangularSelectionPlugin rectangularSelectionPlugin = new RectangularSelectionPlugin(objectManagerPlugin);
			GUIPlugin guiPlugin = new GUIPlugin(cameraControllerPlugin, objectManagerPlugin, keyboardInputModePlugin);

			rendererPlugin.addRenderable(rectangularSelectionPlugin);

			pluginManger.registerPlugin(cameraControllerPlugin);
			pluginManger.registerPlugin(objectManipulatorPlugin);
			pluginManger.registerPlugin(rendererPlugin);
			pluginManger.registerPlugin(guiPlugin);
			pluginManger.registerPlugin(rectangularSelectionPlugin);
			pluginManger.registerPlugin(keyboardInputModePlugin);
			pluginManger.registerPlugin(undoPlugin);
		}

	}

	/** Add obj to object list, if accessor for this object class was not registed it won't be added
	 * 
	 * @param obj object that will be added to list
	 * @param identifier unique identifer, used when saving and loading
	 * 
	 * @return This SceneEditor for the purpose of chaining methods together. */
	public SceneEditor add (Object obj, String identifier) {
		objectManagerPlugin.add(obj, identifier);
		return this;
	}

	/** Register accessor and allow object of provied class be added to scene */
	public void registerAccessor (SceneEditorAccessor<?> accessor) {
		accessorHandler.registerAccessor(accessor);
	}

	public void load () {
		fileSerializerPlugin.load();
	}

	public void save () {
		fileSerializerPlugin.save();
	}

	/** Enables editing mode */
	public void enable () {
		state.editing = true;
		pluginManger.enable();
	}

	/** Disabled editing mode */
	public void disable () {
		if (state.editing) {
			if (state.dirty)
				state.exitingEditMode = true;
			else
				forceDisableEditMode();
		}
	}

	/** Disabled edit mode, without checking if any chagnes was made */
	private void forceDisableEditMode () {
		pluginManger.disable();
		state.editing = false;
		state.exitingEditMode = false;
	}

	public void render () {
		pluginManger.render();
	}

	public void dispose () {
		pluginManger.dispose();

		if (SceneEditorConfig.LAST_CHANCE_SAVE_ENABLED && state.dirty) lastChanceSave();

	}

	private void lastChanceSave () {
		if (SceneEditorConfig.desktopInterface.lastChanceSave()) save();
	}

	public void resize () {
		pluginManger.resize();
	}

	public boolean keyDown (int keycode) {
		if (keycode == SceneEditorConfig.KEY_TOGGLE_EDIT_MODE) {
			if (state.editing)
				disable();
			else
				enable();
		}

		if (state.exitingEditMode) {
			// gui dialog "Unsaved changes, save before exit? (Y/N)"
			if (keycode == Keys.N) forceDisableEditMode();
			if (keycode == Keys.Y) {
				save();
				disable();
			}
		} else
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
