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
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

//TODO better selecting system

// yeah, you know there are just warnings...
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SceneEditor extends InputAdapter
{
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
	
	public SceneEditor(FileHandle arialFontFile, FileHandle sceneFile, Camera camera, boolean devMode)
	{
		this.devMode = devMode;
		
		file = new File(sceneFile.path());
		
		supportMap = new HashMap<>();
		registerSupport(Sprite.class, new SpriteSupport());
		
		if(devMode)
		{
			guiBatch = new SpriteBatch();
			shapeRenderer = new ShapeRenderer();
			
			camController = new CameraController(camera);
			font = new BitmapFont(arialFontFile);
			
			objectMap = new HashMap<>();
			
			attachInputProcessor();
		}
	}
	
	public void load()
	{
		if(file.exists() == false) return;
		
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
	
	private void save()
	{
		
	}
	
	public SceneEditor add(Object obj, String identifier)
	{
		if(isSupportForObjectAvaiable(obj)) objectMap.put(identifier, obj);
		
		return this;
	}
	
	public void registerSupport(Class<?> klass, SceneEditorSupport<?> support)
	{
		supportMap.put(klass, support);
	}
	
	public boolean isSupportForObjectAvaiable(Object obj)
	{
		return supportMap.containsKey(obj.getClass());
	}
	
	public void render()
	{
		shapeRenderer.setProjectionMatrix(camController.getCamera().combined);
		
		if(editing)
		{
			shapeRenderer.begin(ShapeType.Line);
			
			for(Entry<String, Object> entry : objectMap.entrySet())
			{
				Object obj = entry.getValue();
				
				SceneEditorSupport sup = supportMap.get(obj.getClass());
				
				if(sup.isMovingSupported())
					shapeRenderer.setColor(Color.WHITE);
				else
					shapeRenderer.setColor(Color.GRAY);
				
				renderObjectOutline(sup, obj);
				
				if(sup.isScallingSupported())
				{
					if(obj == selectedObj && pointerInsideScaleBox)
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
			
			if(selectedObj != null)
			{
				SceneEditorSupport sup = supportMap.get(selectedObj.getClass());
				shapeRenderer.setColor(Color.RED);
				
				renderObjectOutline(sup, selectedObj);
				
				if(sup.isScallingSupported())
				{
					if(pointerInsideScaleBox)
						shapeRenderer.setColor(Color.RED);
					else
						shapeRenderer.setColor(Color.WHITE);
					
					renderObjectScaleBox(sup, selectedObj);
				}
				
				if(sup.isRotatingSupported())
				{
					if(pointerInsideRotateCircle)
						shapeRenderer.setColor(Color.RED);
					else
						shapeRenderer.setColor(Color.WHITE);
					
					renderObjectRotateCricle(sup, selectedObj);
				}
			}
			
			shapeRenderer.end();
			
			if(SceneEditorConfig.drawGui)
			{
				guiBatch.begin();
				drawTextAtLine("VisSceneEditor - Edit Mode", 0);
				
				if(selectedObj != null) drawTextAtLine("Selected object: " + getIdentifierForObject(selectedObj), 2);
				guiBatch.end();
			}
		}
	}
	
	private void renderObjectOutline(SceneEditorSupport sup, Object obj)
	{
		renderRectangle(sup.getBoundingRectangle(obj));
	}
	
	private void renderObjectScaleBox(SceneEditorSupport sup, Object obj)
	{
		renderRectangle(buildRectangeForScaleBox(sup, obj));
	}
	
	private void renderObjectRotateCricle(SceneEditorSupport sup, Object obj)
	{
		renderCircle(buildCirlcleForRotateBox(sup, obj));
	}
	
	private Rectangle buildRectangeForScaleBox(SceneEditorSupport sup, Object obj)
	{
		Rectangle rect = sup.getBoundingRectangle(obj);
		return new Rectangle(rect.x + rect.width - 15, rect.y + rect.height - 15, 15, 15);
	}
	
	private Circle buildCirlcleForRotateBox(SceneEditorSupport sup, Object obj)
	{
		Rectangle rect = sup.getBoundingRectangle(obj);
		
		int cWidth = 5;
		
		return new Circle(rect.x + rect.width / 2 + cWidth, rect.y + rect.height + cWidth, cWidth);
	}
	
	private void renderRectangle(Rectangle rect)
	{
		shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
	}
	
	private void renderCircle(Circle cir)
	{
		shapeRenderer.circle(cir.x, cir.y, cir.radius);
	}
	
	private void drawTextAtLine(String text, int line)
	{
		font.draw(guiBatch, text, 2, Gdx.graphics.getHeight() - 2 - (line * 17));
	}
	
	@Override
	public boolean keyDown(int keycode)
	{
		if(keycode == Keys.F11)
		{
			editing = !editing;
			
			if(editing == false) save();
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		if(editing)
		{
			final float x = camController.calcX(screenX);
			final float y = camController.calcY(screenY);
			
			if(pointerInsideRotateCircle == false) // without this it would deselect active object if pointer clicked in rotate cricle
			{
				for(Entry<String, Object> entry : objectMap.entrySet())
				{
					Object obj = entry.getValue();
					
					SceneEditorSupport sup = supportMap.get(obj.getClass());
					
					if(sup.contains(obj, camController.calcX(screenX), camController.calcY(screenY)))
					{
						selectedObj = obj;
						attachPointX = (x - sup.getX(selectedObj));
						attachPointY = (y - sup.getY(selectedObj));
						attachScreenX = x;
						attachScreenY = y;
						startingWidth = sup.getWidth(selectedObj);
						startingHeight = sup.getHeight(selectedObj);
						startingRotation = sup.getRotation(selectedObj);
						
						checkIfPointerInsideScaleBox(x, y);
						
						return true;
					}
				}
				
				selectedObj = null;
			}
		}
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		float x = camController.calcX(screenX);
		float y = camController.calcY(screenY);
		
		checkIfPointerInsideScaleBox(x, y);
		checkIfPointerInsideRotateCircle(x, y);
		
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		final float x = camController.calcX(screenX);
		final float y = camController.calcY(screenY);
		
		if(editing)
		{
			if(selectedObj != null && Gdx.input.isButtonPressed(Buttons.LEFT))
			{
				SceneEditorSupport sup = supportMap.get(selectedObj.getClass());
				
				if(sup.isScallingSupported() && pointerInsideScaleBox)
				{
					float deltaX = x - attachScreenX;
					float deltaY = y - attachScreenY;
					
					if(Gdx.input.isKeyPressed(SceneEditorConfig.SCALE_LOCK_RATIO))
					{
						float ratio = startingWidth / startingHeight;
						deltaY = deltaX / ratio;
					}
					
					sup.setSize(selectedObj, startingWidth + deltaX, startingHeight + deltaY);
				}
				else if(sup.isRotatingSupported() && pointerInsideRotateCircle)
				{
					float deltaX = x - attachScreenX;
					float deltaY = y - attachScreenY;
					
					float deg = MathUtils.atan2(-deltaX, deltaY) / MathUtils.degreesToRadians;
					
					if(Gdx.input.isKeyPressed(SceneEditorConfig.ROTATE_SNAP_VALUES))
					{
						int roundDeg =  Math.round(deg / 30);
						sup.setRotation(selectedObj, roundDeg * 30);
					}
					else
						sup.setRotation(selectedObj, deg);
				}
				else
				{
					if(sup.isMovingSupported())
					{
						sup.setX(selectedObj, x - attachPointX);
						sup.setY(selectedObj, y - attachPointY);
					}
				}
				
				return true;
			}
		}
		return false;
	}
	
	private void checkIfPointerInsideScaleBox(float x, float y)
	{
		if(selectedObj != null)
		{
			if(buildRectangeForScaleBox(supportMap.get(selectedObj.getClass()), selectedObj).contains(x, y))
				pointerInsideScaleBox = true;
			else
				pointerInsideScaleBox = false;
			
		}
	}
	
	private void checkIfPointerInsideRotateCircle(float x, float y)
	{
		if(selectedObj != null)
		{
			if(buildCirlcleForRotateBox(supportMap.get(selectedObj.getClass()), selectedObj).contains(x, y))
				pointerInsideRotateCircle = true;
			else
				pointerInsideRotateCircle = false;
		}
	}
	
	public void dispose()
	{
		if(devMode)
		{
			guiBatch.dispose();
			font.dispose();
		}
		
		for(Entry<Class<?>, SceneEditorSupport<?>> entry : supportMap.entrySet())
			entry.getValue().dispose();
	}
	
	public void resize()
	{
		if(devMode) guiBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	}
	
	public String getIdentifierForObject(Object obj)
	{
		for(Entry<String, Object> entry : objectMap.entrySet())
		{
			if(entry.getValue().equals(obj)) return entry.getKey();
		}
		
		return null;
	}
	
	public void enable()
	{
		if(devMode) editing = true;
	}
	
	public void disable()
	{
		if(devMode) editing = false;
	}
	
	public void attachInputProcessor()
	{
		if(devMode)
		{
			if(Gdx.input.getInputProcessor() == null)
			{
				Gdx.input.setInputProcessor(this);
				return;
			}
			
			if(Gdx.input.getInputProcessor() instanceof InputMultiplexer)
			{
				InputMultiplexer mul = (InputMultiplexer) Gdx.input.getInputProcessor();
				mul.addProcessor(this);
				Gdx.input.setInputProcessor(mul);
			}
			else
			{
				InputMultiplexer mul = new InputMultiplexer();
				mul.addProcessor(Gdx.input.getInputProcessor());
				mul.addProcessor(this);
				Gdx.input.setInputProcessor(mul);
			}
		}
	}
}
