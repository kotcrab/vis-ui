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

@SuppressWarnings({ "rawtypes", "unchecked" })
// yeah, you know there are just warnings...
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
		if(devMode && isSupportForObjectAvaiable(obj)) objectMap.put(identifier, obj);
		
		return this;
	}
	
	public void registerSupport(Class<?> klass, SceneEditorSupport<?> support)
	{
		if(devMode) supportMap.put(klass, support);
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
			
			guiBatch.begin();
			drawTextAtLine("VisSceneEditor - Edit Mode", 0);
			
			if(selectedObj != null) drawTextAtLine("Selected object: " + getIdentifierForObject(selectedObj), 2);
			guiBatch.end();
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
		
		if(selectedObj != null)
		{
			if(buildRectangeForScaleArea(supportMap.get(selectedObj.getClass()), selectedObj).contains(x, y))
				pointerInsideScaleArea = true;
			else
				pointerInsideScaleArea = false;
			
		}
		
		return false;
	}
	
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		if(editing)
		{
			if(selectedObj != null && Gdx.input.isButtonPressed(Buttons.LEFT))
			{
				SceneEditorSupport sup = supportMap.get(selectedObj.getClass());
				
				if(sup.isMovingSupported())
				{
					sup.setX(selectedObj, camController.calcX(screenX) - attachPointX);
					sup.setY(selectedObj, camController.calcY(screenY) - attachPointY);
				}
				
				return true;
			}
		}
		return false;
	}
	
	public void dispose()
	{
		if(devMode)
		{
			guiBatch.dispose();
			font.dispose();
			for(Entry<Class<?>, SceneEditorSupport<?>> entry : supportMap.entrySet())
				entry.getValue().dispose();
		}
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
