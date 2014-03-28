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
	private boolean pointerInsideScaleArea;
	private float attachPointX;
	private float attachPointY;
	private float attachScreenX;
	private float attachScreenY;
	private float startingWidth;
	private float startingHeight;
	
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
					if(obj == selectedObj && pointerInsideScaleArea)
						shapeRenderer.setColor(Color.RED);
					else
						shapeRenderer.setColor(Color.WHITE);
					
					renderObjectScaleBox(sup, obj);
				}
				
				if(sup.isRotatingSupported())
				{
					renderObjectRotateBox(sup, obj);
				}
			}
			
			if(selectedObj != null)
			{
				SceneEditorSupport sup = supportMap.get(selectedObj.getClass());
				shapeRenderer.setColor(Color.RED);
				
				renderObjectOutline(sup, selectedObj);
				
				if(sup.isScallingSupported())
				{
					if(pointerInsideScaleArea)
						shapeRenderer.setColor(Color.RED);
					else
						shapeRenderer.setColor(Color.WHITE);
					
					renderObjectScaleBox(sup, selectedObj);
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
		renderRectangle(buildRectangeForScaleArea(sup, obj));
	}
	
	private Rectangle buildRectangeForScaleArea(SceneEditorSupport sup, Object obj)
	{
		Rectangle rect = sup.getBoundingRectangle(obj);
		return new Rectangle(rect.x + rect.width - 15, rect.y + rect.height - 15, 15, 15);
	}
	
	private void renderRectangle(Rectangle rect)
	{
		shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
	}
	
	private void drawTextAtLine(String text, int line)
	{
		font.draw(guiBatch, text, 2, Gdx.graphics.getHeight() - 2 - (line * 17));
	}
	
	private void renderObjectRotateBox(SceneEditorSupport sup, Object obj)
	{
		// TODO Auto-generated method stub
		
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
			float x = camController.calcX(screenX);
			float y = camController.calcY(screenY);
			
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
					
					checkIfPointerInsideScaleArea(x, y);
					
					return true;
				}
			}
			
			selectedObj = null;
		}
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		float x = camController.calcX(screenX);
		float y = camController.calcY(screenY);
		
		checkIfPointerInsideScaleArea(x, y);
		
		return false;
	}
	
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		float x = camController.calcX(screenX);
		float y = camController.calcY(screenY);
		
		if(editing)
		{
			if(selectedObj != null && Gdx.input.isButtonPressed(Buttons.LEFT))
			{
				SceneEditorSupport sup = supportMap.get(selectedObj.getClass());
				
				if(sup.isScallingSupported() && pointerInsideScaleArea)
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
				else
				{
					if(sup.isMovingSupported())
					{
						sup.setX(selectedObj, camController.calcX(screenX) - attachPointX);
						sup.setY(selectedObj, camController.calcY(screenY) - attachPointY);
					}
				}
				
				return true;
			}
		}
		return false;
	}
	
	private void checkIfPointerInsideScaleArea(float x, float y)
	{
		if(selectedObj != null)
		{
			if(buildRectangeForScaleArea(supportMap.get(selectedObj.getClass()), selectedObj).contains(x, y))
				pointerInsideScaleArea = true;
			else
				pointerInsideScaleArea = false;
			
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
