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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.SerializationException;

// yeah, you know there are just warnings...
@SuppressWarnings({"rawtypes", "unchecked"})
public class SceneEditor extends SceneEditorInputAdapater {
	private static final String TAG = "VisSceneEditor";

	private Json json;

	private ShapeRenderer shapeRenderer;

	private GUI gui;

	private CameraController camController;

	private FileHandle file;

	private boolean devMode;
	private boolean editing;
	private boolean dirty;
	private boolean cameraLocked;

	private ObjectMap<Class<?>, SceneEditorSupport<?>> supportMap;
	private ObjectMap<String, Object> objectMap;

	private Array<EditorAction> undoActions;
	private Array<EditorAction> redoActions;

	private Object selectedObj;
	private boolean pointerInsideScaleBox;
	private boolean pointerInsideRotateCircle;

	private float attachScreenX; // for scaling/rotating object
	private float attachScreenY;

	private float startingWidth; // object properies before moving/scalling/rotating/etc
	private float startingHeight;
	private float startingRotation;
	private float startingX;
	private float startingY;

	private float lastTouchX;
	private float lastTouchY;

	public SceneEditor (FileHandle sceneFile, OrthographicCamera camera, boolean devMode, boolean registerBasicsSupports) {
		this.devMode = devMode;
		this.file = sceneFile;

		if (Gdx.app.getType() != ApplicationType.Desktop) this.devMode = false;

		json = new Json();
		json.addClassTag("objectInfo", ObjectInfo.class);

		supportMap = new ObjectMap<>();

		if (registerBasicsSupports) {
			registerSupport(Sprite.class, new SpriteSupport());
			registerSupport(Actor.class, new ActorSupport());
		}

		if (devMode) {
			gui = new GUI(this);
			shapeRenderer = new ShapeRenderer();

			undoActions = new Array<>();
			redoActions = new Array<>();

			camController = new CameraController(camera);

			objectMap = new ObjectMap<>();

			attachInputProcessor();
		}
	}

	public SceneEditor (FileHandle sceneFile, OrthographicCamera camera, boolean devMode) {
		this(sceneFile, camera, devMode, true);
	}

	public void load () {
		if (file.exists() == false) return;

		ArrayList<ObjectInfo> infos = new ArrayList<>();
		infos = json.fromJson(infos.getClass(), file);

		for (ObjectInfo info : infos) {
			try {
				Class<?> klass = Class.forName(info.className);
				SceneEditorSupport sup = getSupportForClass(klass);

				Object obj = objectMap.get(info.identifier);

				sup.setX(obj, info.x);
				sup.setY(obj, info.y);
				sup.setOrigin(obj, info.originX, info.originY);
				sup.setSize(obj, info.width, info.height);
				sup.setScale(obj, info.scaleX, info.scaleY);
				sup.setRotation(obj, info.rotation);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}
	}

	private void save () {
		if (file.exists() && SceneEditorConfig.backupFolderPath != null) {
			createBackup();
		}

		ArrayList<ObjectInfo> infos = new ArrayList<>();

		for (Entry<String, Object> entry : objectMap.entries()) {

			Object obj = entry.value;

			SceneEditorSupport sup = getSupportForClass(obj.getClass());

			ObjectInfo info = new ObjectInfo();
			info.className = obj.getClass().getName();
			info.identifier = entry.key;
			info.x = sup.getX(obj);
			info.y = sup.getY(obj);
			info.scaleX = sup.getScaleX(obj);
			info.scaleY = sup.getScaleY(obj);
			info.originX = sup.getOriginX(obj);
			info.originY = sup.getOriginY(obj);
			info.width = sup.getWidth(obj);
			info.height = sup.getHeight(obj);
			info.rotation = sup.getRotation(obj);

			infos.add(info);
		}

		try {

			if (SceneEditorConfig.assetsFolderPath == null)
				json.toJson(infos, Gdx.files.absolute(new File("").getAbsolutePath() + File.separator + file.path()));
			else
				json.toJson(infos, Gdx.files.absolute(SceneEditorConfig.assetsFolderPath + file.path()));

			Gdx.app.log(TAG, "Saved changes to file.");
			dirty = false;
		} catch (SerializationException e) {
			Gdx.app.log(TAG, "Error while saving file.");
			e.printStackTrace();
		}

	}

	private void createBackup () {
		try {
			String fileName = file.name();
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			Date date = new Date();
			fileName += " - " + dateFormat.format(date) + ".json";

			Files.copy(new File(new File("").getAbsolutePath() + File.separator + file.path()).toPath(), new File(
				SceneEditorConfig.backupFolderPath + fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
			Gdx.app.log(TAG, "Backup file created.");
		} catch (IOException e) {
			Gdx.app.log(TAG, "Error while creating backup.");
			e.printStackTrace();
		}
	}

	/** TODO
	 * @param obj
	 * @param identifier
	 * 
	 * @return This SceneEditor for the purpose of chaining methods together. */
	public SceneEditor add (Object obj, String identifier) {
		if (isSupportForClassAvaiable(obj.getClass())) objectMap.put(identifier, obj);

		return this;
	}

	public void registerSupport (Class<?> klass, SceneEditorSupport<?> support) {
		supportMap.put(klass, support);
	}

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

	public SceneEditorSupport<?> getSupportForClass (Class klass) {
		if (supportMap.containsKey(klass))
			return supportMap.get(klass);
		else {
			if (klass.getSuperclass() == null)
				return null;
			else
				return getSupportForClass(klass.getSuperclass());
		}
	}

	/**
	 * 
	 * @param x pointer cordinate unprocjeted by camera
	 * @param y pointer cordinate unprocjeted by camera
	 */
	private void setValuesForSelectedObject (float x, float y) {
		if (selectedObj != null) {
			SceneEditorSupport sup = getSupportForClass(selectedObj.getClass());

			attachScreenX = x;
			attachScreenY = y;
			startingX = sup.getX(selectedObj);
			startingY = sup.getY(selectedObj);
			startingWidth = sup.getWidth(selectedObj);
			startingHeight = sup.getHeight(selectedObj);
			startingRotation = sup.getRotation(selectedObj);
		}
	}

	public String getIdentifierForObject (Object obj) {
		for (Entry<String, Object> entry : objectMap.entries()) {
			if (entry.value.equals(obj)) return entry.key;
		}

		return null;
	}

	public Object findObjectWithSamllestSurfaceArea (float x, float y) {
		Object matchingObject = null;
		int lastSurfaceArea = Integer.MAX_VALUE;

		for (Entry<String, Object> entry : objectMap.entries()) {
			Object obj = entry.value;

			SceneEditorSupport sup = getSupportForClass(obj.getClass());

			if (sup.contains(obj, x, y)) {
				int currentSurfaceArea = (int)(sup.getWidth(obj) * sup.getHeight(obj));

				if (currentSurfaceArea < lastSurfaceArea) {
					matchingObject = obj;
					lastSurfaceArea = currentSurfaceArea;
				}
			}
		}

		return matchingObject;
	}

	public void render () {
		shapeRenderer.setProjectionMatrix(camController.getCamera().combined);

		if (editing) {
			shapeRenderer.begin(ShapeType.Line);

			for (Entry<String, Object> entry : objectMap.entries()) {
				Object obj = entry.value;

				SceneEditorSupport sup = getSupportForClass(obj.getClass());

				if (sup.isMovingSupported())
					shapeRenderer.setColor(Color.WHITE);
				else
					shapeRenderer.setColor(Color.GRAY);

				renderObjectOutline(sup, obj);

				if (sup.isScallingSupported()) {
					if (obj == selectedObj && pointerInsideScaleBox)
						shapeRenderer.setColor(Color.RED);
					else
						shapeRenderer.setColor(Color.WHITE);

					renderObjectScaleBox(sup, obj);
				}
			}

			if (selectedObj != null) {
				SceneEditorSupport sup = getSupportForClass(selectedObj.getClass());
				shapeRenderer.setColor(Color.RED);

				renderObjectOutline(sup, selectedObj);

				if (sup.isScallingSupported()) {
					if (pointerInsideScaleBox)
						shapeRenderer.setColor(Color.RED);
					else
						shapeRenderer.setColor(Color.WHITE);

					renderObjectScaleBox(sup, selectedObj);
				}

				if (sup.isRotatingSupported()) {
					if (pointerInsideRotateCircle)
						shapeRenderer.setColor(Color.RED);
					else
						shapeRenderer.setColor(Color.WHITE);

					renderObjectRotateCricle(sup, selectedObj);
				}

			}

			if (camController.isCameraDirty()) {
				if (cameraLocked)
					shapeRenderer.setColor(Color.RED);
				else
					shapeRenderer.setColor(Color.GREEN);
				renderRectangle(camController.getOrginalCameraRectangle());
			}

			shapeRenderer.end();

			gui.render(objectMap.size, cameraLocked, dirty, selectedObj);
		}
	}

	private void renderObjectOutline (SceneEditorSupport sup, Object obj) {
		renderRectangle(sup.getBoundingRectangle(obj));
	}

	private void renderObjectScaleBox (SceneEditorSupport sup, Object obj) {
		renderRectangle(buildRectangeForScaleBox(sup, obj));
	}

	private void renderObjectRotateCricle (SceneEditorSupport sup, Object obj) {
		renderCircle(buildCirlcleForRotateCircle(sup, obj));
	}

	private void renderRectangle (Rectangle rect) {
		shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
	}

	private void renderCircle (Circle cir) {
		shapeRenderer.circle(cir.x, cir.y, cir.radius);
	}

	private Rectangle buildRectangeForScaleBox (SceneEditorSupport sup, Object obj) {
		Rectangle rect = sup.getBoundingRectangle(obj);
		return new Rectangle(rect.x + rect.width - 15, rect.y + rect.height - 15, 15, 15);
	}

	private Circle buildCirlcleForRotateCircle (SceneEditorSupport sup, Object obj) {
		Rectangle rect = sup.getBoundingRectangle(obj);

		int cWidth = 5;

		return new Circle(rect.x + rect.width / 2 + cWidth, rect.y + rect.height + cWidth, cWidth);
	}

	private void undo () {
		if (undoActions.size > 0) {
			EditorAction action = undoActions.pop();

			SceneEditorSupport sup = getSupportForClass(action.obj.getClass());

			switch (action.type) {
			case ORIGIN: // TODO (origin not implemented yet)
				break;
			case POS:
				redoActions.add(new EditorAction(action.obj, ActionType.POS, sup.getX(action.obj), sup.getY(action.obj)));
				sup.setX(action.obj, action.xVal);
				sup.setY(action.obj, action.yVal);
				break;
			case SCALE: // scale is not used for now
				break;
			case SIZE:
				redoActions.add(new EditorAction(action.obj, ActionType.SIZE, sup.getWidth(action.obj), sup.getHeight(action.obj)));
				sup.setSize(action.obj, action.xVal, action.yVal);
				break;
			case ROTATION:
				redoActions.add(new EditorAction(action.obj, ActionType.ROTATION, sup.getRotation(action.obj), 0));
				sup.setRotation(action.obj, action.xVal);
				break;
			default:
				break;

			}
		} else
			Gdx.app.log(TAG, "Can't undo any more!");
	}

	private void redo () {
		if (redoActions.size > 0) {
			EditorAction action = redoActions.pop();

			SceneEditorSupport sup = getSupportForClass(action.obj.getClass());

			switch (action.type) {
			case ORIGIN: // origin not implemented yet
				break;
			case POS:
				undoActions.add(new EditorAction(action.obj, ActionType.POS, sup.getX(action.obj), sup.getY(action.obj)));
				sup.setX(action.obj, action.xVal);
				sup.setY(action.obj, action.yVal);
				break;
			case SCALE: // scale is not used for now
				break;
			case SIZE:
				undoActions.add(new EditorAction(action.obj, ActionType.SIZE, sup.getWidth(action.obj), sup.getHeight(action.obj)));
				sup.setSize(action.obj, action.xVal, action.yVal);
				break;
			case ROTATION:
				undoActions.add(new EditorAction(action.obj, ActionType.ROTATION, sup.getRotation(action.obj), 0));
				sup.setRotation(action.obj, action.xVal);
				break;
			default:
				break;

			}
		} else
			Gdx.app.log(TAG, "Can't redo any more!");
	}

	@Override
	public boolean keyDown (int keycode) {

		if (editing) {
			if (keycode == SceneEditorConfig.KEY_RESET_CAMERA) camController.restoreOrginalCameraProperties();
			if (keycode == SceneEditorConfig.KEY_LOCK_CAMERA) cameraLocked = !cameraLocked;

			if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_SPECIAL_ACTIONS)) {
				if (keycode == SceneEditorConfig.KEY_SPECIAL_SAVE_CHANGES) save();
				if (keycode == SceneEditorConfig.KEY_SPECIAL_UNDO) undo();
				if (keycode == SceneEditorConfig.KEY_SPECIAL_REDO) redo();
			}
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

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		if (editing) {
			final float x = camController.calcX(screenX);
			final float y = camController.calcY(screenY);
			lastTouchX = x;
			lastTouchY = y;

			if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_NO_SELECT_MODE) == false) {
				if (pointerInsideRotateCircle == false && pointerInsideScaleBox == false) {
					Object matchingObject = findObjectWithSamllestSurfaceArea(x, y);

					if (matchingObject != null) {
						selectedObj = matchingObject;

						setValuesForSelectedObject(x, y);
						checkIfPointerInsideScaleBox(x, y);
						setValuesForSelectedObject(x, y);

						return true;
					}

					selectedObj = null;
				} else {
					checkIfPointerInsideScaleBox(x, y);
					checkIfPointerInsideRotateCircle(x, y);
					setValuesForSelectedObject(x, y);

					if (pointerInsideScaleBox && selectedObj != null) {
						SceneEditorSupport sup = getSupportForClass(selectedObj.getClass());
						sup.setRotation(selectedObj, 0);
					}

					return true;
				}

				setValuesForSelectedObject(x, y);
			}
		}

		return false;
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		if (editing) {
			if (pointerInsideScaleBox) {
				SceneEditorSupport sup = getSupportForClass(selectedObj.getClass());
				sup.setRotation(selectedObj, startingRotation);

				undoActions.add(new EditorAction(selectedObj, ActionType.SIZE, startingWidth, startingHeight));
			}

			if (pointerInsideRotateCircle) {
				undoActions.add(new EditorAction(selectedObj, ActionType.ROTATION, startingRotation, 0));
			}

			if (pointerInsideScaleBox == false && pointerInsideRotateCircle == false && selectedObj != null) {
				undoActions.add(new EditorAction(selectedObj, ActionType.POS, startingX, startingY));
			}

		}
		return false;
	}

	@Override
	public boolean mouseMoved (int screenX, int screenY) {
		if (editing) {
			float x = camController.calcX(screenX);
			float y = camController.calcY(screenY);

			checkIfPointerInsideScaleBox(x, y);
			checkIfPointerInsideRotateCircle(x, y);
		}

		return false;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		final float x = camController.calcX(screenX);
		final float y = camController.calcY(screenY);

		if (editing) {
			if (selectedObj != null && Gdx.input.isButtonPressed(Buttons.LEFT)) {
				SceneEditorSupport sup = getSupportForClass(selectedObj.getClass());

				if (sup.isScallingSupported() && pointerInsideScaleBox) {
					float deltaX = x - attachScreenX;
					float deltaY = y - attachScreenY;

					if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_SCALE_LOCK_RATIO)) {
						float ratio = startingWidth / startingHeight;
						deltaY = deltaX / ratio;
					}

					sup.setSize(selectedObj, startingWidth + deltaX, startingHeight + deltaY);
					dirty = true;
					return true;

				}

				if (sup.isRotatingSupported() && pointerInsideRotateCircle) {
					Rectangle rect = sup.getBoundingRectangle(selectedObj);
					float deltaX = x - (rect.x + rect.width / 2);
					float deltaY = y - (rect.y + rect.height / 2);

					float deg = MathUtils.atan2(-deltaX, deltaY) / MathUtils.degreesToRadians;

					if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_ROTATE_SNAP_VALUES)) {
						int roundDeg = Math.round(deg / 30);
						sup.setRotation(selectedObj, roundDeg * 30);
					} else
						sup.setRotation(selectedObj, deg);

					dirty = true;
					return true;
				}

				if (sup.isMovingSupported()) {
					float deltaX = (x - lastTouchX);
					float deltaY = (y - lastTouchY);

					if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_PRECISION_MODE)) {
						deltaX /= SceneEditorConfig.PRECISION_DIVIDE_BY;
						deltaY /= SceneEditorConfig.PRECISION_DIVIDE_BY;
					}

					sup.setX(selectedObj, sup.getX(selectedObj) + deltaX);
					sup.setY(selectedObj, sup.getY(selectedObj) + deltaY);

					lastTouchX = x;
					lastTouchY = y;

					dirty = true;
					return true;
				}

			}

		}
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		if (editing && cameraLocked == false) {
			OrthographicCamera camera = camController.getCamera();

			float newZoom = 0;
			float camX = camController.getX();
			float camY = camController.getY();

			if (amount == 1) // out
			{
				if (camera.zoom >= SceneEditorConfig.CAMERA_MAX_ZOOM_OUT) return false;

				newZoom = camera.zoom + 0.1f * camera.zoom * 2;

				// some complicated callucations, basicly we want to zoom in/out where mouse pointer is
				camera.position.x = camX + (camera.zoom / newZoom) * (camera.position.x - camX);
				camera.position.y = camY + (camera.zoom / newZoom) * (camera.position.y - camY);

				camera.zoom = newZoom;
			}

			if (amount == -1) // in
			{
				if (camera.zoom <= SceneEditorConfig.CAMERA_MAX_ZOOM_IN) return false;

				newZoom = camera.zoom - 0.1f * camera.zoom * 2;

				camera.position.x = camX + (newZoom / camera.zoom) * (camera.position.x - camX);
				camera.position.y = camY + (newZoom / camera.zoom) * (camera.position.y - camY);

				camera.zoom = newZoom;
			}
			return true;
		}

		return false;
	}

	// pan is worse because you must drag mouse a little bit to fire this event, but it's simpler
	@Override
	public boolean pan (float x, float y, float deltaX, float deltaY) {
		if (editing) {
			if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
				if (selectedObj == null && cameraLocked == false) {
					OrthographicCamera camera = camController.getCamera();

					if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_PRECISION_MODE)) {
						deltaX /= SceneEditorConfig.PRECISION_DIVIDE_BY;
						deltaY /= SceneEditorConfig.PRECISION_DIVIDE_BY;
					}

					camera.position.x = camera.position.x - deltaX * camera.zoom;
					camera.position.y = camera.position.y + deltaY * camera.zoom;
					return true;
				}
			}
		}

		return false;
	}

	private void checkIfPointerInsideScaleBox (float x, float y) {
		if (selectedObj != null) {
			if (buildRectangeForScaleBox(getSupportForClass(selectedObj.getClass()), selectedObj).contains(x, y))
				pointerInsideScaleBox = true;
			else
				pointerInsideScaleBox = false;

		}
	}

	private void checkIfPointerInsideRotateCircle (float x, float y) {
		if (selectedObj != null) {
			if (buildCirlcleForRotateCircle(getSupportForClass(selectedObj.getClass()), selectedObj).contains(x, y))
				pointerInsideRotateCircle = true;
			else
				pointerInsideRotateCircle = false;
		}
	}

	public void dispose () {
		if (devMode) {
			gui.dispose();
		}
	}

	public void resize () {
		if (devMode) gui.resize();
	}

	public void enable () {
		if (devMode) {
			editing = true;
			camController.switchCameraProperties();
		}
	}

	public void disable () {
		if (devMode) {
			editing = false;
			camController.switchCameraProperties();
			save();
		}
	}

	public void attachInputProcessor () {
		if (devMode) {
			if (Gdx.input.getInputProcessor() == null) {
				InputMultiplexer mul = new InputMultiplexer();
				mul.addProcessor(this);
				mul.addProcessor(new GestureDetector(this));
				Gdx.input.setInputProcessor(mul);
				return;
			}

			if (Gdx.input.getInputProcessor() instanceof InputMultiplexer) {
				InputMultiplexer mul = (InputMultiplexer)Gdx.input.getInputProcessor();
				mul.addProcessor(0, this);
				mul.addProcessor(1, new GestureDetector(this));
				Gdx.input.setInputProcessor(mul);
			} else {
				InputMultiplexer mul = new InputMultiplexer();
				mul.addProcessor(this);
				mul.addProcessor(new GestureDetector(this));
				mul.addProcessor(Gdx.input.getInputProcessor());
				Gdx.input.setInputProcessor(mul);
			}
		}
	}
}
