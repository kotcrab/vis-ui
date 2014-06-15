
package pl.kotcrab.vis.sceneeditor.plugin.impl;

import pl.kotcrab.vis.sceneeditor.EditorAction;
import pl.kotcrab.vis.sceneeditor.ObjectRepresentation;
import pl.kotcrab.vis.sceneeditor.SceneEditorConfig;
import pl.kotcrab.vis.sceneeditor.plugin.PluginState;
import pl.kotcrab.vis.sceneeditor.plugin.interfaces.IObjectManager;
import pl.kotcrab.vis.sceneeditor.plugin.interfaces.IUndo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.utils.Array;

public class ObjectManipulatorPlugin extends PluginState {
	private IObjectManager managerInterface;
	private IUndo undoInterface;

	// linked to ObjectManager list
	private Array<ObjectRepresentation> selectedObjs;

	// when rotating multiple objcets, only masterOrep will be rotated, other objects rotation will be set to masterOrep rotation
	private ObjectRepresentation masterOrep;

	public ObjectManipulatorPlugin (IObjectManager managerInterface, IUndo undoInterface) {
		this.managerInterface = managerInterface;
		this.undoInterface = undoInterface;
		selectedObjs = managerInterface.getSelectedObjs();
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

	/** Finds object with smallest surface area that contains x,y point
	 * 
	 * @param x pointer cordinate unprocjeted by camera
	 * @param y pointer cordinate unprocjeted by camera */
	private ObjectRepresentation findObjectWithSamllestSurfaceArea (float x, float y) {
		ObjectRepresentation matchingObject = null;
		int lastSurfaceArea = Integer.MAX_VALUE;

		for (ObjectRepresentation orep : managerInterface.getObjectRepresenationList()) {
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

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		if (state.editing) {
			if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_NO_SELECT_MODE))
				selectedObjs.clear();
			else {
				// rectangularSelection.touchDown(sceneX, sceneY, pointer, button);

				// is no multislecy key is pressed, it will check that isMouseInsideSelectedObjects if true this won't execture
				// because it would deslect clicked object
				if ((Gdx.input.isKeyPressed(SceneEditorConfig.KEY_MULTISELECT) || isMouseInsideSelectedObjects(x, y) == false)
					&& isMouseInsideAnySelectedObjectsRotateArea() == false) {
					ObjectRepresentation matchingObject = findObjectWithSamllestSurfaceArea(x, y);

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
	public boolean touchUp (int x, int y, int pointer, int button) {
		if (state.editing) {
			// keyboardInputMode.finish();

			// rectangularSelection.touchUp(screenX, screenY, pointer, button);

			if (selectedObjs.size > 0) addUndoActionsAfterEdit();

			masterOrep = null;
		}

		return true;
	}

	private void addUndoActionsAfterEdit() {
		Array<EditorAction> localUndoList = new Array<EditorAction>();

		for (ObjectRepresentation orep : selectedObjs)
			if (orep.getLastEditorAction() != null) localUndoList.add(orep.getLastEditorAction());

		if (localUndoList.size > 0) undoInterface.addToUndoList(localUndoList);		
	}

	@Override
	public boolean mouseMoved (int x, int y) {
		if (state.editing) {

			for (ObjectRepresentation orep : managerInterface.getObjectRepresenationList())
				orep.mouseMoved(x, y);
		}

		return true;
	}

	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		// final float x = camController.calcX(screenX);
		// final float y = camController.calcY(screenY);

		if (state.editing) {
			// keyboardInputMode.finish();

			// rectangularSelection.touchDragged(screenX, screenY, pointer);

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
}
