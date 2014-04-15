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

package pl.kotcrab.vis.sceneeditor;

import pl.kotcrab.vis.sceneeditor.serializer.FileSerializer;
import pl.kotcrab.vis.sceneeditor.serializer.SceneSerializer;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

/** Main class of VisSceneEditor
 * 
 * @author Pawel Pastuszak */
@SuppressWarnings({"rawtypes"})
// yeah, you know there are just warnings...
public class SceneEditor extends SceneEditorInputAdapater {
	private static final String TAG = "VisSceneEditor";

	private CameraController camController;

	private ObjectMap<Class<?>, SceneEditorSupport<?>> supportMap;
	private ObjectMap<String, Object> objectMap;
	private Array<ObjectRepresentation> objectRepresenationList;
	private Array<ObjectRepresentation> selectedObjs;
	private ObjectRepresentation masterOrep;

	// modules
	private Renderer renderer;
	private SceneSerializer serializer;
	private KeyboardInputMode keyboardInputMode;
	private RectangularSelection rectangularSelection;

	private Array<Array<EditorAction>> undoList;
	private Array<Array<EditorAction>> redoList;

	private boolean devMode;
	private boolean editing;
	private boolean dirty;
	private boolean cameraLocked;
	private boolean hideOutlines;

	/** @see SceneEditor#SceneEditor(FileHandle, OrthographicCamera, boolean)
	 * @param registerBasicsSupports if true Sprite and Actor support will be registered */
	public SceneEditor (OrthographicCamera camera, boolean devMode) {
		this.devMode = devMode;

		// DevMode can be only activated on desktop
		if (Gdx.app.getType() != ApplicationType.Desktop) this.devMode = false;

		supportMap = new ObjectMap<Class<?>, SceneEditorSupport<?>>();
		objectMap = new ObjectMap<String, Object>();

		if (devMode) {
			undoList = new Array<Array<EditorAction>>();
			redoList = new Array<Array<EditorAction>>();
			objectRepresenationList = new Array<ObjectRepresentation>();
			selectedObjs = new Array<ObjectRepresentation>();

			camController = new CameraController(camera);

			keyboardInputMode = new KeyboardInputMode(new KeyboardInputActionFinished() {
				@Override
				public void editingFinished (Array<EditorAction> actions) {
					undoList.add(actions);
				}
			}, selectedObjs);

			rectangularSelection = new RectangularSelection(new RectangularSelectionListener() {
				@Override
				public void finishedDrawing (Array<ObjectRepresentation> matchingObjects) {
					selectedObjs.clear();
					selectedObjs.addAll(matchingObjects); // we can't just swap tables

				}
			}, camController, objectRepresenationList);

			renderer = new Renderer(this, camController, keyboardInputMode, rectangularSelection, objectMap, selectedObjs);

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
		serializer = new FileSerializer(this, sceneFile);
		serializer.setObjectMap(objectMap);
	}

	public void load () {
		if (serializer == null) {
			Gdx.app.error(TAG, "Serializer not set, loading is not available!! See SceneEditor.setSerializer()");
			return;
		}

		serializer.load();
	}

	private void save () {
		if (serializer == null) {
			Gdx.app.error(TAG, "Serializer not set, saving is not available!! See SceneEditor.setSerializer()");
			return;
		}

		if (serializer.save()) dirty = false;
	}

	public void setSerializer (SceneSerializer serializer) {
		this.serializer = serializer;
	}

	/** Add obj to object list, if support for this object class was not registed it won't be added
	 * @param obj object that will be added to list
	 * @param identifier unique identifer, used when saving and loading
	 * 
	 * @return This SceneEditor for the purpose of chaining methods together. */
	public SceneEditor add (Object obj, String identifier) {
		if (isSupportForClassAvaiable(obj.getClass())) {
			objectMap.put(identifier, obj);
			objectRepresenationList.add(new ObjectRepresentation(getSupportForObject(obj), obj));
		} else {
			Gdx.app.error(TAG,
				"Could not add object with identifier: '" + identifier + "'. Support not found for class " + obj.getClass()
					+ ". See SceneEditor.registerSupport()");
		}

		return this;
	}

	/** Register support and allow object of provied class be added to scene */
	public void registerSupport (Class<?> klass, SceneEditorSupport<?> support) {
		supportMap.put(klass, support);
	}

	/** Check if support for provied class is available
	 * @param klass class that will be checked
	 * @return true if support is avaiable. false otherwise */
	public boolean isSupportForClassAvaiable (Class klass) {
		if (supportMap.containsKey(klass))
			return true;
		else {
			if (klass.getSuperclass() == null)
				return false;
			else
				return isSupportForClassAvaiable(klass.getSuperclass());
		}
	}

	/** Returns support for provided class
	 * 
	 * @param klass class that support will be return if available
	 * @return support if available, null otherwise */
	public SceneEditorSupport getSupportForClass (Class klass) {
		if (supportMap.containsKey(klass))
			return supportMap.get(klass);
		else {
			if (klass.getSuperclass() == null)
				return null;
			else
				return getSupportForClass(klass.getSuperclass());
		}
	}

	public SceneEditorSupport getSupportForObject (Object obj) {
		return getSupportForClass(obj.getClass());
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
	 * @param x pointer cordinate unprocjeted by camera
	 * @param y pointer cordinate unprocjeted by camera
	 * @return */
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

	/** Renders everything */
	public void render () {
		if (editing) {
			if (hideOutlines == false) renderer.render(cameraLocked);
			renderer.renderGUI(cameraLocked, dirty);
		}
	}

	private void undo () {
		if (undoList.size > 0) {
			Array<EditorAction> actions = undoList.pop();

			for (EditorAction action : actions)
				action.switchValues();

			redoList.add(actions);
		} else
			Gdx.app.log(TAG, "Can't undo any more!");
	}

	private void redo () {
		if (redoList.size > 0) {
			Array<EditorAction> actions = redoList.pop();

			for (EditorAction action : actions)
				action.switchValues();

			undoList.add(actions);
		} else
			Gdx.app.log(TAG, "Can't redo any more!");
	}

	private void addUndoActions () {
		Array<EditorAction> localUndoList = new Array<EditorAction>();

		for (ObjectRepresentation orep : selectedObjs)
			if (orep.getLastEditorAction() != null) localUndoList.add(orep.getLastEditorAction());

		if (localUndoList.size > 0) undoList.add(localUndoList);
	}

	private boolean areAllSelectedObjectSupportsMoving () {
		for (ObjectRepresentation orep : selectedObjs) {
			if (orep.isMovingSupported() == false) {
				Gdx.app.log(TAG, "Some of the selected object does not support moving.");
				return false;
			}
		}
		return true;
	}

	private boolean areAllSelectedObjectSupportsScalling () {
		for (ObjectRepresentation orep : selectedObjs) {
			if (orep.isScallingSupported() == false) {
				Gdx.app.log(TAG, "Some of the selected object does not support scalling.");
				return false;
			}
		}
		return true;
	}

	private boolean areAllSelectedObjectSupportsRotating () {
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
		if (devMode) {
			if (editing == false) {
				editing = true;
				camController.switchCameraProperties();
			}
		}
	}

	/** Disabled editing mode */
	public void disable () {
		if (devMode) {
			if (editing) {
				editing = false;
				keyboardInputMode.cancel();
				camController.switchCameraProperties();
				save();
			}
		}
	}

	/** Releases used assets */
	public void dispose () {
		if (devMode) {
			renderer.dispose();
		}
	}

	/** This must be called when screen size changed */
	public void resize () {
		if (devMode) renderer.resize();
	}

	public boolean isDevMode () {
		return devMode;
	}

	/** {@inheritDoc} */
	@Override
	public void attachInputProcessor () {
		if (devMode) super.attachInputProcessor();
	}

	@Override
	public boolean keyDown (int keycode) {
		if (editing) {
			if (keyboardInputMode.isActive() == false) {
				if (keycode == SceneEditorConfig.KEY_LOCK_CAMERA) cameraLocked = !cameraLocked;
				if (keycode == SceneEditorConfig.KEY_RESET_CAMERA) camController.restoreOrginalCameraProperties();
				if (keycode == SceneEditorConfig.KEY_RESET_OBJECT_SIZE) resetSelectedObjectsSize();
				if (keycode == SceneEditorConfig.KEY_HIDE_OUTLINES) hideOutlines = !hideOutlines;

				if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_SPECIAL_ACTIONS)) {
					if (keycode == SceneEditorConfig.KEY_SPECIAL_SAVE_CHANGES) save();
					if (keycode == SceneEditorConfig.KEY_SPECIAL_UNDO) undo();
					if (keycode == SceneEditorConfig.KEY_SPECIAL_REDO) redo();
					return true; // we don't want to trigger diffrent events
				}

				if (selectedObjs.size > 0) {

					if (areAllSelectedObjectSupportsMoving()) {
						if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_POSX) keyboardInputMode.setObject(EditType.X);
						if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_POSY) keyboardInputMode.setObject(EditType.Y);
					}

					if (areAllSelectedObjectSupportsScalling()) {
						if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_WIDTH) keyboardInputMode.setObject(EditType.WIDTH);
						if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_HEIGHT) keyboardInputMode.setObject(EditType.HEIGHT);
					}

					if (areAllSelectedObjectSupportsRotating()) {
						if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_ROTATION) keyboardInputMode.setObject(EditType.ROTATION);
					}
				}
			}

			keyboardInputMode.keyDown(keycode);
		}

		if (keycode == SceneEditorConfig.KEY_TOGGLE_EDIT_MODE) {
			if (editing)
				disable();
			else
				enable();

			return true;
		}

		return false;
	}

	private void resetSelectedObjectsSize () {
		for (ObjectRepresentation orep : selectedObjs)
			orep.resetSize();
	}

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {

		if (editing) {
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
					ObjectRepresentation matchingObject = findObjectWithSamllestSurfaceArea(x, y);

					if (matchingObject != null) {
						if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_MULTISELECT) == false) selectedObjs.clear();

						if (selectedObjs.contains(matchingObject, false))
							selectedObjs.removeValue(matchingObject, false);
						else
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

		return false;
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {

		if (editing) {
			keyboardInputMode.finish();

			rectangularSelection.touchUp(screenX, screenY, pointer, button);

			if (selectedObjs.size > 0) addUndoActions();

			masterOrep = null;
		}

		return false;
	}

	@Override
	public boolean mouseMoved (int screenX, int screenY) {
		if (editing) {
			float x = camController.calcX(screenX);
			float y = camController.calcY(screenY);

			for (ObjectRepresentation orep : selectedObjs)
				orep.mouseMoved(x, y);
		}

		return false;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		final float x = camController.calcX(screenX);
		final float y = camController.calcY(screenY);

		if (editing) {
			keyboardInputMode.finish();

			rectangularSelection.touchDragged(screenX, screenY, pointer);

			// sorry...
			if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
				boolean isMouseInsideAnyScaleArea = isMouseInsideAnySelectedObjectsScaleArea();
				boolean isMouseInsideAnyRotateArea = isMouseInsideAnySelectedObjectsRotateArea();

				if (masterOrep != null && isMouseInsideAnyRotateArea) masterOrep.draggedRotate(x, y);

				for (ObjectRepresentation orep : selectedObjs) {
					if (isMouseInsideAnyRotateArea) {
						if (selectedObjs.size > 1) {
							if (masterOrep == orep) continue;

							orep.setRotation(masterOrep.getRotation());
							dirty = true;
						} else if (orep.draggedRotate(x, y)) dirty = true;

					} else if (isMouseInsideAnyScaleArea) {
						if (orep.draggedScale(x, y)) dirty = true;

					} else if (orep.draggedMove(x, y)) dirty = true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		if (editing && cameraLocked == false) return camController.scrolled(amount);

		return false;
	}

	// pan is worse because you must drag mouse a little bit to fire this event, but it's simpler
	@Override
	public boolean pan (float x, float y, float deltaX, float deltaY) {
		if (editing) {
			keyboardInputMode.finish();

			if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
				if (selectedObjs.size == 0 && cameraLocked == false) {
					return camController.pan(deltaX, deltaY);
				}
			}
		}

		return false;
	}

}
