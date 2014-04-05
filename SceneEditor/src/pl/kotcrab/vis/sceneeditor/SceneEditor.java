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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.SerializationException;

// yeah, you know there are just warnings...
@SuppressWarnings({"rawtypes", "unchecked"})
public class SceneEditor extends SceneEditorInputAdapater {
	private static final String TAG = "VisSceneEditor";

	private Json json;

	private SpriteBatch guiBatch;
	private ShapeRenderer shapeRenderer;
	private BitmapFont font;

	private CameraController camController;

	private FileHandle file;

	private boolean devMode;
	private boolean editing;
	private boolean dirty;
	private boolean cameraLocked;

	private ObjectMap<Class<?>, SceneEditorSupport<?>> supportMap;
	private ObjectMap<String, Object> objectMap;

	private Object selectedObj;
	private boolean pointerInsideScaleBox;
	private boolean pointerInsideRotateCircle;

	private float attachScreenX; // for scaling/rotating object
	private float attachScreenY;

	private float startingWidth; // for scalling object
	private float startingHeight;

	private float startingRotation;

	private float lastX;
	private float lastY;

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
			guiBatch = new SpriteBatch();
			shapeRenderer = new ShapeRenderer();

			camController = new CameraController(camera);
			font = new BitmapFont(Gdx.files.internal("data/arial.fnt"));

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

			json.toJson(infos, Gdx.files.absolute(new File("").getAbsolutePath() + File.separator + file.path()));
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

//	public boolean isSupportForObjectAvaiable (Object obj) {
//		return supportMap.containsKey(obj.getClass());
//	}
	
	public boolean isSupportForClassAvaiable(Class klass)
	{
		if(supportMap.containsKey(klass))
			return true;
		else
		{
			if(klass.getSuperclass() != null)
				return isSupportForClassAvaiable(klass.getSuperclass());
			else
				return false;
		}
	}
	
	public SceneEditorSupport<?> getSupportForClass(Class klass)
	{
		if(supportMap.containsKey(klass))
			return supportMap.get(klass);
		else
		{
			if(klass.getSuperclass() != null)
				return getSupportForClass(klass.getSuperclass());
			else
				return null;
		}
	}

	private void setValuesForSelectedObject (float x, float y) {
		if (selectedObj != null) {
			SceneEditorSupport sup = getSupportForClass(selectedObj.getClass());

			attachScreenX = x;
			attachScreenY = y;
			startingWidth = sup.getWidth(selectedObj);
			startingHeight = sup.getHeight(selectedObj);
		}
	}

	public String getIdentifierForObject (Object obj) {
		for (Entry<String, Object> entry : objectMap.entries()) {
			if (entry.value.equals(obj)) return entry.key;
		}

		return null;
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

			int line = 0;

			if (SceneEditorConfig.GUI_DRAW) {
				guiBatch.begin();

				if (SceneEditorConfig.GUI_DRAW_TITLE)
					drawTextAtLine("VisSceneEditor - Edit Mode - Entities: " + objectMap.size, line++);

				if (cameraLocked)
					drawTextAtLine("Camera is locked.", line++);
				else
					drawTextAtLine("Camera is not locked.", line++);

				guiBatch.flush();
				
				if (dirty)
					drawTextAtLine("Unsaved changes. Exit edit mode to save them.", line++);
				else
					drawTextAtLine("All changes saved.", line++);
				
				line++;

				if (selectedObj != null) {
					SceneEditorSupport sup = getSupportForClass(selectedObj.getClass());

					drawTextAtLine("Selected object: " + getIdentifierForObject(selectedObj), line++);

					if (SceneEditorConfig.GUI_DRAW_OBJECT_INFO)
						drawTextAtLine(
							"X: " + (int)sup.getX(selectedObj) + " Y:" + (int)sup.getY(selectedObj) + " Width: "
								+ (int)sup.getWidth(selectedObj) + " Heihgt: " + (int)sup.getHeight(selectedObj), line++);
				}
				guiBatch.end();
			}
		}
	}

	private void renderObjectOutline (SceneEditorSupport sup, Object obj) {
		renderRectangle(sup.getBoundingRectangle(obj));
	}

	private void renderObjectScaleBox (SceneEditorSupport sup, Object obj) {
		renderRectangle(buildRectangeForScaleBox(sup, obj));
	}

	private void renderObjectRotateCricle (SceneEditorSupport sup, Object obj) {
		renderCircle(buildCirlcleForRotateBox(sup, obj));
	}

	private void renderRectangle (Rectangle rect) {
		shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
	}

	private void renderCircle (Circle cir) {
		shapeRenderer.circle(cir.x, cir.y, cir.radius);
	}

	private void drawTextAtLine (String text, int line) {
		font.draw(guiBatch, text, 2, Gdx.graphics.getHeight() - 2 - (line * 17));
	}

	private Rectangle buildRectangeForScaleBox (SceneEditorSupport sup, Object obj) {
		Rectangle rect = sup.getBoundingRectangle(obj);
		return new Rectangle(rect.x + rect.width - 15, rect.y + rect.height - 15, 15, 15);
	}

	private Circle buildCirlcleForRotateBox (SceneEditorSupport sup, Object obj) {
		Rectangle rect = sup.getBoundingRectangle(obj);

		int cWidth = 5;

		return new Circle(rect.x + rect.width / 2 + cWidth, rect.y + rect.height + cWidth, cWidth);
	}

	@Override
	public boolean keyDown (int keycode) {

		if (editing) {
			if (keycode == SceneEditorConfig.KEY_RESET_CAMERA) camController.restoreOrginalCameraProperties();
			if (keycode == SceneEditorConfig.KEY_LOCK_CAMERA) cameraLocked = !cameraLocked;
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
			lastX = x;
			lastY = y;

			checkIfPointerInsideScaleBox(x, y);
			if (pointerInsideScaleBox && selectedObj != null) {
				SceneEditorSupport sup = getSupportForClass(selectedObj.getClass());
				startingRotation = sup.getRotation(selectedObj);
				sup.setRotation(selectedObj, 0);
			}

			if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_NO_SELECT_MODE) == false && pointerInsideRotateCircle == false
				&& pointerInsideScaleBox == false) // without this it would deselect active object
			{
				Object matchingObject = null;
				int lastSurfaceArea = Integer.MAX_VALUE;

				for (Entry<String, Object> entry : objectMap.entries()) {
					Object obj = entry.value;

					SceneEditorSupport sup = getSupportForClass(obj.getClass());

					if (sup.contains(obj, camController.calcX(screenX), camController.calcY(screenY))) {
						int currentSurfaceArea = (int)(sup.getWidth(obj) * sup.getHeight(obj));

						if (currentSurfaceArea < lastSurfaceArea) {
							matchingObject = obj;
							lastSurfaceArea = currentSurfaceArea;
						}
					}
				}

				if (matchingObject != null) {
					selectedObj = matchingObject;

					setValuesForSelectedObject(x, y);
					checkIfPointerInsideScaleBox(x, y);

					return true;
				}

				selectedObj = null;
			}

			setValuesForSelectedObject(x, y);

		}
		return false;
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		if (editing) {
			if (pointerInsideScaleBox) {
				SceneEditorSupport sup = getSupportForClass(selectedObj.getClass());
				sup.setRotation(selectedObj, startingRotation);
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

				} else if (sup.isRotatingSupported() && pointerInsideRotateCircle) {
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
				} else {
					if (sup.isMovingSupported()) {
						float deltaX = (x - lastX);
						float deltaY = (y - lastY);

						if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_PRECISION_MODE)) {
							deltaX /= SceneEditorConfig.PRECISION_DIVIDE_BY;
							deltaY /= SceneEditorConfig.PRECISION_DIVIDE_BY;
						}

						sup.setX(selectedObj, sup.getX(selectedObj) + deltaX);
						sup.setY(selectedObj, sup.getY(selectedObj) + deltaY);

						lastX = x;
						lastY = y;

						dirty = true;
					}
				}

				return true;
			}

		}
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		if (editing && cameraLocked == false) {
			OrthographicCamera camera = camController.getCamera();

			float newZoom = 0;
			float x = camController.getX();
			float y = camController.getY();

			if (amount == 1) // out
			{
				if (camera.zoom >= SceneEditorConfig.CAMERA_MAX_ZOOM_OUT) return false;

				newZoom = camera.zoom + 0.1f * camera.zoom * 2;

				camera.position.x = x + (camera.zoom / newZoom) * (camera.position.x - x);
				camera.position.y = y + (camera.zoom / newZoom) * (camera.position.y - y);

				camera.zoom += 0.1f * camera.zoom * 2;
			}

			if (amount == -1) // in
			{
				if (camera.zoom <= SceneEditorConfig.CAMERA_MAX_ZOOM_IN) return false;

				newZoom = camera.zoom - 0.1f * camera.zoom * 2;

				camera.position.x = x + (newZoom / camera.zoom) * (camera.position.x - x);
				camera.position.y = y + (newZoom / camera.zoom) * (camera.position.y - y);

				camera.zoom -= 0.1f * camera.zoom * 2;
			}
			return true;
		}

		return false;
	}

	// pan is worse because you must drag mouse a little bit to fire this event
	@Override
	public boolean pan (float x, float y, float deltaX, float deltaY) {
		if (editing && selectedObj == null && cameraLocked == false) {
			if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
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
			if (buildCirlcleForRotateBox(getSupportForClass(selectedObj.getClass()), selectedObj).contains(x, y))
				pointerInsideRotateCircle = true;
			else
				pointerInsideRotateCircle = false;
		}
	}

	public void dispose () {
		if (devMode) {
			guiBatch.dispose();
			font.dispose();
		}
	}

	public void resize () {
		if (devMode)
			guiBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
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
