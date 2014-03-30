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
import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
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

//TODO better selecting system

// yeah, you know there are just warnings...
@SuppressWarnings({"rawtypes", "unchecked"})
public class SceneEditor extends SceneEditorInputAdapater {
	private SpriteBatch guiBatch;
	private ShapeRenderer shapeRenderer;
	private BitmapFont font;

	private CameraController camController;

	private File file;

	private boolean devMode;
	private boolean editing;

	private HashMap<Class<?>, SceneEditorSupport<?>> supportMap;
	private HashMap<String, Object> objectMap;

	private Object selectedObj;
	private boolean pointerInsideScaleBox;
	private boolean pointerInsideRotateCircle;

	private float attachPointX; // for moving object
	private float attachPointY;

	private float attachScreenX; // for scaling/rotating object
	private float attachScreenY;

	private float startingWidth; // for scalling object
	private float startingHeight;

	private float startingRotation; // for rotating object;

	public SceneEditor (FileHandle arialFontFile, FileHandle sceneFile, OrthographicCamera camera, boolean devMode) {
		this.devMode = devMode;

		file = new File(sceneFile.path());

		supportMap = new HashMap<>();
		registerSupport(Sprite.class, new SpriteSupport());

		if (devMode) {
			guiBatch = new SpriteBatch();
			shapeRenderer = new ShapeRenderer();

			camController = new CameraController(camera);
			font = new BitmapFont(arialFontFile);

			objectMap = new HashMap<>();

			attachInputProcessor();
		}
	}

	public void load () {
		if (file.exists() == false) return;

		// try
		// {
		// SAXBuilder builder = new SAXBuilder();
		// Document document = builder.build(file);
		//
		// Element rootNode = document.getRootElement();
		// List<Element> elementList = rootNode.getChildren();
		//
		// // Element startNode = rootNode.getChildren("dStart").get(0);
		// // target = Integer.valueOf(startNode.getChildText("target0"));
		// }
		// catch (JDOMException | IOException e)
		// {
		// e.printStackTrace();
		// }
	}

	private void save () {
	}

	public SceneEditor add (Object obj, String identifier) {
		if (isSupportForObjectAvaiable(obj)) objectMap.put(identifier, obj);

		return this;
	}

	public void registerSupport (Class<?> klass, SceneEditorSupport<?> support) {
		supportMap.put(klass, support);
	}

	public boolean isSupportForObjectAvaiable (Object obj) {
		return supportMap.containsKey(obj.getClass());
	}

	private void setValuesForSelectedObject (float x, float y) {
		if (selectedObj != null) {
			SceneEditorSupport sup = supportMap.get(selectedObj.getClass());

			attachPointX = (x - sup.getX(selectedObj));
			attachPointY = (y - sup.getY(selectedObj));
			attachScreenX = x;
			attachScreenY = y;
			startingWidth = sup.getWidth(selectedObj);
			startingHeight = sup.getHeight(selectedObj);
			startingRotation = sup.getRotation(selectedObj);
		}
	}

	public String getIdentifierForObject (Object obj) {
		for (Entry<String, Object> entry : objectMap.entrySet()) {
			if (entry.getValue().equals(obj)) return entry.getKey();
		}

		return null;
	}

	public void render () {
		shapeRenderer.setProjectionMatrix(camController.getCamera().combined);

		if (editing) {
			shapeRenderer.begin(ShapeType.Line);

			for (Entry<String, Object> entry : objectMap.entrySet()) {
				Object obj = entry.getValue();

				SceneEditorSupport sup = supportMap.get(obj.getClass());

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

				// if(sup.isRotatingSupported())
				// {
				// if(obj == selectedObj && pointerInsideRotateCircle)
				// shapeRenderer.setColor(Color.RED);
				// else
				// shapeRenderer.setColor(Color.WHITE);
				//
				// renderObjectRotateCricle(sup, obj);
				// }
			}

			if (selectedObj != null) {
				SceneEditorSupport sup = supportMap.get(selectedObj.getClass());
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
				shapeRenderer.setColor(Color.GREEN);
				renderRectangle(camController.getOrginalCameraRectangle());
			}

			shapeRenderer.end();

			if (SceneEditorConfig.DRAW_GUI) {
				guiBatch.begin();
				drawTextAtLine("VisSceneEditor - Edit Mode - Entities: " + objectMap.size(), 0);
				drawTextAtLine("Camera is not locked. Press R to reset camera properties", 1);

				if (selectedObj != null) drawTextAtLine("Selected object: " + getIdentifierForObject(selectedObj), 3);
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

			if (pointerInsideRotateCircle == false && pointerInsideScaleBox == false) // without this it would deselect active object
			{
				Object matchingObject = null;
				int lastSurfaceArea = Integer.MAX_VALUE;

				for (Entry<String, Object> entry : objectMap.entrySet()) {
					Object obj = entry.getValue();

					SceneEditorSupport sup = supportMap.get(obj.getClass());

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
				SceneEditorSupport sup = supportMap.get(selectedObj.getClass());

				if (sup.isScallingSupported() && pointerInsideScaleBox) {
					float deltaX = x - attachScreenX;
					float deltaY = y - attachScreenY;

					if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_SCALE_LOCK_RATIO)) {
						float ratio = startingWidth / startingHeight;
						deltaY = deltaX / ratio;
					}

					sup.setSize(selectedObj, startingWidth + deltaX, startingHeight + deltaY);
				} else if (sup.isRotatingSupported() && pointerInsideRotateCircle) {
					float deltaX = x - attachPointX;
					float deltaY = y - attachPointY;

					float deg = MathUtils.atan2(-deltaX, deltaY) / MathUtils.degreesToRadians;

					if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_ROTATE_SNAP_VALUES)) {
						int roundDeg = Math.round(deg / 30);
						sup.setRotation(selectedObj, roundDeg * 30);
					} else
						sup.setRotation(selectedObj, deg);
				} else {
					if (sup.isMovingSupported()) {
						sup.setX(selectedObj, x - attachPointX);
						sup.setY(selectedObj, y - attachPointY);
					}
				}

				return true;
			}

		}
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		if (editing) {
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
		if (selectedObj == null) {
			if (Gdx.input.isButtonPressed(Buttons.LEFT) && Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) == false) {
				OrthographicCamera camera = camController.getCamera();
				camera.position.x = camera.position.x - deltaX * camera.zoom;
				camera.position.y = camera.position.y + deltaY * camera.zoom;
				return true;
			}
		}

		return false;
	}

	private void checkIfPointerInsideScaleBox (float x, float y) {
		if (selectedObj != null) {
			if (buildRectangeForScaleBox(supportMap.get(selectedObj.getClass()), selectedObj).contains(x, y))
				pointerInsideScaleBox = true;
			else
				pointerInsideScaleBox = false;

		}
	}

	private void checkIfPointerInsideRotateCircle (float x, float y) {
		if (selectedObj != null) {
			if (buildCirlcleForRotateBox(supportMap.get(selectedObj.getClass()), selectedObj).contains(x, y))
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

		for (Entry<Class<?>, SceneEditorSupport<?>> entry : supportMap.entrySet())
			entry.getValue().dispose();
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
				mul.addProcessor(this);
				mul.addProcessor(new GestureDetector(this));
				Gdx.input.setInputProcessor(mul);
			} else {
				InputMultiplexer mul = new InputMultiplexer();
				mul.addProcessor(Gdx.input.getInputProcessor());
				mul.addProcessor(this);
				mul.addProcessor(new GestureDetector(this));
				Gdx.input.setInputProcessor(mul);
			}
		}
	}
}
