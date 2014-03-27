package pl.kotcrab.vis.sceneeditor;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public interface SceneEditorSupport<O> extends Disposable
{
	public Object load();
	
	public void save(O obj);
	
	public void setX(O obj, float x);
	public float getX(O obj);
	
	public float getY(O obj);
	public void setY(O obj, float y);
	
	public void setOrigin(O obj, float x, float y);
	
	public float getOriginX(O obj);
	public float getOriginY(O obj);
	
	public void setScale(O obj, float x, float y);
	
	public float getScaleX(O obj);
	public float getScaleY(O obj);
	
	public float getWidth(O obj);
	public float getHeight(O obj);
	
	public float getRotation(O obj);
	public void setRotation(O obj, float rotation);
	
	public boolean isScallingSupported();
	public boolean isRotatingSupported();
	public boolean isMovingSupported();
	
	public boolean contains(O obj, float x, float y);
	public Rectangle getBoundingRectangle(O obj);
	
}