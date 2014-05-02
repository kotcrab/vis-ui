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

import pl.kotcrab.vis.sceneeditor.EditorAction;
import pl.kotcrab.vis.sceneeditor.ObjectRepresentation;
import pl.kotcrab.vis.sceneeditor.SceneEditorConfig;
import pl.kotcrab.vis.sceneeditor.accessor.SceneEditorAccessor;
import pl.kotcrab.vis.sceneeditor.component.DesktopInterface;
import pl.kotcrab.vis.sceneeditor.component.EditType;
import pl.kotcrab.vis.sceneeditor.component.KeyboardInputActionListener;
import pl.kotcrab.vis.sceneeditor.component.KeyboardInputMode;
import pl.kotcrab.vis.sceneeditor.component.RectangularSelection;
import pl.kotcrab.vis.sceneeditor.component.RectangularSelectionActionListener;
import pl.kotcrab.vis.sceneeditor.component.Renderer;
import pl.kotcrab.vis.sceneeditor.serializer.FileSerializer;
import pl.kotcrab.vis.sceneeditor.serializer.SceneSerializer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

/** Main class of VisSceneEditor
 * 
 * @author Pawel Pastuszak */
public class SceneEditor extends AbstractSceneEditor<SceneEditorAccessor<?>> {

	private ObjectMap<String, Object> objectMap;

	private Array<ObjectRepresentation> objectRepresenationList;
	private Array<ObjectRepresentation> selectedObjs;

	// when rotating multiple objcets, only masterOrep will be rotated, other objects rotation will be set to masterOrep rotation
	private ObjectRepresentation masterOrep;

	// modules
	private SceneSerializer serializer;
	private DesktopInterface desktopInterface;
	private Renderer renderer;
	private KeyboardInputMode keyboardInputMode;
	private RectangularSelection rectangularSelection;

	public boolean cameraDragging;

	/** Constructs SceneEditor, this contrustor does not create Serializer for you. You must do it manualy using
	 * {@link SceneEditor#setSerializer(SceneSerializer)}
	 * 
	 * @param camera camera used for rendering
	 * @param enableEditMode devMode allow to enter editing mode, if not on desktop it will automaticly be set to false */
	public SceneEditor (OrthographicCamera camera, boolean enableEditMode) {
		super(camera, enableEditMode);

		objectMap = new ObjectMap<String, Object>();

		if (state.devMode) {
			desktopInterface = SceneEditorConfig.desktopInterface;

			objectRepresenationList = new Array<ObjectRepresentation>();
			selectedObjs = new Array<ObjectRepresentation>();

			keyboardInputMode = new KeyboardInputMode(new KeyboardInputActionListener() {
				@Override
				public void editingFinished (Array<EditorAction> actions) {
					addUndoList(actions);
					state.dirty = true;
				}
			}, selectedObjs);

			rectangularSelection = new RectangularSelection(new RectangularSelectionActionListener() {
				@Override
				public void drawingFinished (Array<ObjectRepresentation> matchingObjects) {
					selectedObjs.clear();
					selectedObjs.addAll(matchingObjects); // we can't just swap tables

				}
			}, camController, objectRepresenationList);

			createRenderer();

			attachInputProcessor();
		}
	}

	/** Constructs SceneEditor with FileSerializer for provied internal file.
	 * 
	 * @param sceneFile path to scene file, typicaly with .json extension
	 * @param camera camera used for rendering
	 * @param devMode devMode allow to enter editing mode, if not on desktop it will automaticly be set to false */
	public SceneEditor (FileHandle sceneFile, OrthographicCamera camera, boolean devMode) {
		this(camera, devMode);
		setSerializer(new FileSerializer(sceneFile));
	}

	/** Loads all objects saved data, called first time will do nothing */
	public void load () {
		if (serializer == null) {
			Gdx.app.error(TAG, "Serializer not set, loading is not available! See SceneEditor.setSerializer()");
			return;
		}

		serializer.load();
	}

	public void save () {
		if (serializer == null) {
			Gdx.app.error(TAG, "Serializer not set, saving is not available! See SceneEditor.setSerializer()");
			return;
		}

		if (serializer.save()) state.dirty = false;
	}

	/** Sets SceneSerializer for SceneEditor
	 * 
	 * @param serializer used for saving and loading objects data */
	public void setSerializer (SceneSerializer serializer) {
		this.serializer = serializer;
		serializer.init(this, objectMap);
	}

	public SceneSerializer getSerializer () {
		return serializer;
	}

	/** Add obj to object list, if accessor for this object class was not registed it won't be added
	 * 
	 * @param obj object that will be added to list
	 * @param identifier unique identifer, used when saving and loading
	 * 
	 * @return This SceneEditor for the purpose of chaining methods together. */
	public SceneEditor add (Object obj, String identifier) {
		if (isAccessorForClassAvaiable(obj.getClass())) {
			objectMap.put(identifier, obj);

			if (state.devMode) objectRepresenationList.add(new ObjectRepresentation(getAccessorForObject(obj), obj, identifier));
		} else {
			Gdx.app.error(TAG,
				"Could not add object with identifier: '" + identifier + "'. Accessor not found for class " + obj.getClass()
					+ ". See SceneEditor.registerAccessor()");
		}

		return this;
	}

	/** @param x pointer cordinate unprocjeted by camera
	 * @param y pointer cordinate unprocjeted by camera */
	private void setValuesForSelectedObject (float x, float y) {
		for (ObjectRepresentation orep : selectedObjs)
			orep.setValues(x, y);

		if (selectedObjs.size > 1 && isMouseInsideAnySelectedObjectsRotateArea()) {
			for (ObjectRepresentation orep : selectedObjs) {
				if (orep.isPointerInsideRotateArea()) {
					masterOrep = orep;
					return;
				}
			}
		}
	}

	/** Finds and return identifer for provied object
	 * 
	 * @param obj that identifier will be returned
	 * @return identifier if found, null otherwise */
	public String getIdentifierForObject (Object obj) {
		for (Entry<String, Object> entry : objectMap.entries()) {
			if (entry.value.equals(obj)) return entry.key;
		}

		return null;
	}

	/** Finds object with smallest surface area that contains x,y point
	 * 
	 * @param x pointer cordinate unprocjeted by camera
	 * @param y pointer cordinate unprocjeted by camera */
	private ObjectRepresentation findObjectWithSamllestSurfaceArea (float x, float y) {
		ObjectRepresentation matchingObject = null;
		int lastSurfaceArea = Integer.MAX_VALUE;

		for (ObjectRepresentation orep : objectRepresenationList) {
			if (orep.contains(x, y)) {
				int currentSurfaceArea = (int)(orep.getWidth() * orep.getHeight());

				if (currentSurfaceArea < lastSurfaceArea) {
					matchingObject = orep;
					lastSurfaceArea = currentSurfaceArea;
				}
			}
		}

		return matchingObject;
	}
	
	private ObjectRepresentation getSelectedObjectForMousePosition (float x, float y) {
		return findObjectWithSamllestSurfaceArea(x, y);
	}

	/** Renders everything */
	public void render () {
		if (state.editing) {
			if (state.hideOutlines == false) renderer.render(state.cameraLocked);
			renderer.renderGUI(state.cameraLocked, state.dirty, state.exitingEditMode);
		}
	}

	private void addUndoActionsAfterEdit () {
		Array<EditorAction> localUndoList = new Array<EditorAction>();

		for (ObjectRepresentation orep : selectedObjs)
			if (orep.getLastEditorAction() != null) localUndoList.add(orep.getLastEditorAction());

		if (localUndoList.size > 0) addUndoList(localUndoList);
	}

	private boolean doesAllSelectedObjectSupportsMoving () {
		for (ObjectRepresentation orep : selectedObjs) {
			if (orep.isMovingSupported() == false) {
				Gdx.app.log(TAG, "Some of the selected object does not support moving.");
				return false;
			}
		}
		return true;
	}

	private boolean doesAllSelectedObjectSupportsScalling () {
		for (ObjectRepresentation orep : selectedObjs) {
			if (orep.isScallingSupported() == false) {
				Gdx.app.log(TAG, "Some of the selected object does not support scalling.");
				return false;
			}
		}
		return true;
	}

	private boolean doesAllSelectedObjectSupportsRotating () {
		for (ObjectRepresentation orep : selectedObjs) {
			if (orep.isRotatingSupported() == false) {
				Gdx.app.log(TAG, "Some of the selected object does not support rotating.");
				return false;
			}
		}
		return true;
	}

	private boolean isMouseInsideAnySelectedObjectsScaleArea () {
		for (ObjectRepresentation orep : selectedObjs) {
			if (orep.isPointerInsideScaleArea()) return true;
		}
		return false;
	}

	private boolean isMouseInsideAnySelectedObjectsRotateArea () {
		for (ObjectRepresentation orep : selectedObjs) {
			if (orep.isPointerInsideRotateArea()) return true;
		}
		return false;
	}

	private boolean isMouseInsideSelectedObjects (float x, float y) {
		for (ObjectRepresentation orep : selectedObjs) {
			if (orep.contains(x, y)) return true;
		}
		return false;
	}

	/** Enabled editing mode */
	public void enable () {
		if (state.devMode) {
			if (state.editing == false) {
				state.editing = true;
				camController.switchCameraProperties();
			}
		}
	}

	/** Disabled editing mode */
	public void disable () {
		if (state.devMode) {
			if (state.editing) {
				keyboardInputMode.cancel();

				if (state.dirty)
					state.exitingEditMode = true;
				else {
					forceDisableEditMode();
				}
			}
		}
	}

	/** Disabled edit mode, without checking if any chagnes was made */
	private void forceDisableEditMode () {
		keyboardInputMode.cancel();
		camController.switchCameraProperties();
		state.editing = false;
		state.exitingEditMode = false;
	}

	/** Releases used assets */
	public void dispose () {
		if (state.devMode) {
			renderer.dispose();
		}

		if (SceneEditorConfig.LAST_CHANCE_SAVE_ENABLED && state.dirty) lastChanceSave();
	}

	private void lastChanceSave () {
		if (desktopInterface.lastChanceSave()) save();
	}

	private void createRenderer () {
		renderer = new Renderer(camController, keyboardInputMode, rectangularSelection, objectRepresenationList, selectedObjs);
	}

	/** Must be called when screen size changed */
	public void resize () {
		if (state.devMode) renderer.resize();
	}

	public boolean isDevMode () {
		return state.devMode;
	}

	@Override
	public void attachInputProcessor () {
		if (state.devMode) super.attachInputProcessor();
	}

	private void resetSelectedObjectsSize () {
		for (ObjectRepresentation orep : selectedObjs)
			orep.resetSize();
	}

	// ===========Input methods=================

	@Override
	public boolean keyDown (int keycode) {
		if (state.editing) {
			if (state.exitingEditMode) {
				// gui dialog "Unsaved changes, save before exit? (Y/N)"
				if (keycode == Keys.N) forceDisableEditMode();
				if (keycode == Keys.Y) {
					save();
					disable();
				}

			} else {
				if (keyboardInputMode.isActive() == false) {
					if (keycode == SceneEditorConfig.KEY_LOCK_CAMERA) state.cameraLocked = !state.cameraLocked;
					if (keycode == SceneEditorConfig.KEY_RESET_CAMERA) camController.restoreOrginalCameraProperties();
					if (keycode == SceneEditorConfig.KEY_RESET_OBJECT_SIZE) resetSelectedObjectsSize();
					if (keycode == SceneEditorConfig.KEY_HIDE_OUTLINES) state.hideOutlines = !state.hideOutlines;

					if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_SPECIAL_ACTIONS)) {
						if (keycode == SceneEditorConfig.KEY_SPECIAL_SAVE_CHANGES) save();
						if (keycode == SceneEditorConfig.KEY_SPECIAL_UNDO) undo();
						if (keycode == SceneEditorConfig.KEY_SPECIAL_REDO) redo();
						return true; // we don't want to trigger diffrent events
					}

					if (selectedObjs.size > 0) {
						if ((keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_POSX || keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_POSY)
							&& doesAllSelectedObjectSupportsMoving()) {
							if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_POSX) keyboardInputMode.setObject(EditType.X);
							if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_POSY) keyboardInputMode.setObject(EditType.Y);
						}

						if ((keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_WIDTH || keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_HEIGHT)
							&& doesAllSelectedObjectSupportsScalling()) {
							if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_WIDTH) keyboardInputMode.setObject(EditType.WIDTH);
							if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_HEIGHT) keyboardInputMode.setObject(EditType.HEIGHT);
						}

						if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_ROTATION && doesAllSelectedObjectSupportsRotating()) {
							if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_ROTATION)
								keyboardInputMode.setObject(EditType.ROTATION);
						}
						// }
					}
				}

				keyboardInputMode.keyDown(keycode);
			}
		}

		if (keycode == SceneEditorConfig.KEY_TOGGLE_EDIT_MODE) {
			if (state.editing)
				disable();
			else
				enable();

			return true;
		}

		return true;
	}

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		if (state.editing) {
			keyboardInputMode.finish();

			final float x = camController.calcX(screenX);
			final float y = camController.calcY(screenY);

			if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_NO_SELECT_MODE))
				selectedObjs.clear();
			else {
				rectangularSelection.touchDown(screenX, screenY, pointer, button);

				// is no multislecy key is pressed, it will check that isMouseInsideSelectedObjects if true this won't execture
				// because it would deslect clicked object
				if ((Gdx.input.isKeyPressed(SceneEditorConfig.KEY_MULTISELECT) || isMouseInsideSelectedObjects(x, y) == false)
					&& isMouseInsideAnySelectedObjectsRotateArea() == false) {
					ObjectRepresentation matchingObject = getSelectedObjectForMousePosition(x, y);

					if (matchingObject != null) {
						if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_MULTISELECT) == false) selectedObjs.clear();

						if (selectedObjs.contains(matchingObject, false)) {
							if (matchingObject.isPointerInsideScaleArea() == false
								&& matchingObject.isPointerInsideRotateArea() == false) selectedObjs.removeValue(matchingObject, false);
						} else
							selectedObjs.add(matchingObject);

						setValuesForSelectedObject(x, y);
						return true;
					}

					selectedObjs.clear();
				} else {
					setValuesForSelectedObject(x, y);
					return true;
				}
			}

		}

		return true;
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		if (state.editing) {
			keyboardInputMode.finish();

			cameraDragging = false;

			rectangularSelection.touchUp(screenX, screenY, pointer, button);

			if (selectedObjs.size > 0) addUndoActionsAfterEdit();

			masterOrep = null;
		}

		return true;
	}

	@Override
	public boolean mouseMoved (int screenX, int screenY) {
		if (state.editing) {
			float x = camController.calcX(screenX);
			float y = camController.calcY(screenY);

			for (ObjectRepresentation orep : objectRepresenationList)
				orep.mouseMoved(x, y);
		}

		return true;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		final float x = camController.calcX(screenX);
		final float y = camController.calcY(screenY);

		if (state.editing) {
			keyboardInputMode.finish();

			rectangularSelection.touchDragged(screenX, screenY, pointer);

			if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
				boolean isMouseInsideAnyScaleArea = isMouseInsideAnySelectedObjectsScaleArea();
				boolean isMouseInsideAnyRotateArea = isMouseInsideAnySelectedObjectsRotateArea();

				if (masterOrep != null && isMouseInsideAnyRotateArea) masterOrep.draggedRotate(x, y);

				for (ObjectRepresentation orep : selectedObjs) {
					if (isMouseInsideAnyRotateArea) {
						if (selectedObjs.size > 1) {
							if (masterOrep == orep) continue;

							orep.setRotation(masterOrep.getRotation());
							state.dirty = true;
						} else if (orep.draggedRotate(x, y)) state.dirty = true;

					} else if (isMouseInsideAnyScaleArea) {
						if (orep.draggedScale(x, y)) state.dirty = true;

					} else if (orep.draggedMove(x, y)) state.dirty = true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		if (state.editing && state.cameraLocked == false) return camController.scrolled(amount);

		return true;
	}

	// pan is worse because you must drag mouse a little bit to fire this event, but it's simpler
	@Override
	public boolean pan (float x, float y, float deltaX, float deltaY) {
		if (state.editing) {
			keyboardInputMode.finish();

			if (cameraDragging == false) // we wan't to ignore first deltaX
			{
				cameraDragging = true;
				return true;
			}

			if (Gdx.input.isButtonPressed(Buttons.LEFT) && cameraDragging) {
				if (selectedObjs.size == 0 && state.cameraLocked == false) {
					return camController.pan(deltaX, deltaY);
				}
			}
		}

		return true;
	}
}
