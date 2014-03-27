package pl.kotcrab.vis.sceneeditor;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class SpriteSupport implements SceneEditorSupport<Sprite>
{
	
	
	@Override
	public Object load()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void save(Sprite s)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void dispose()
	{
	}
	
	@Override
	public void setX(Sprite s, float x)
	{
		s.setX(x);
	}
	
	@Override
	public void setY(Sprite s, float y)
	{
		s.setY(y);
	}
	
	@Override
	public float getX(Sprite s)
	{
		return s.getX();
	}
	
	@Override
	public float getY(Sprite s)
	{
		return s.getY();
	}
	
	@Override
	public float getWidth(Sprite s)
	{
		return s.getWidth();
	}
	
	@Override
	public float getHeight(Sprite s)
	{
		return s.getHeight();
	}

	@Override
	public void setScale(Sprite s, float x, float y)
	{
		s.setScale(x, y);
	}

	@Override
	public float getScaleX(Sprite s)
	{
		return s.getScaleX();
	}

	@Override
	public float getScaleY(Sprite s)
	{
		return s.getScaleY();
	}

	@Override
	public boolean contains(Sprite s, float x, float y)
	{
		return s.getBoundingRectangle().contains(x, y);
	}

	@Override
	public boolean isScallingSupported()
	{
		return true;
	}

	@Override
	public boolean isRotatingSupported()
	{
		return true;
	}

	@Override
	public boolean isMovingSupported()
	{
		return true;
	}

	@Override
	public float getRotation(Sprite s)
	{
		return s.getRotation();
	}

	@Override
	public void setRotation(Sprite s, float rotation)
	{
		s.setRotation(rotation);
	}

	@Override
	public void setOrigin(Sprite s, float x, float y)
	{
		s.setOrigin(x, y);
	}

	@Override
	public float getOriginX(Sprite s)
	{
		return s.getOriginX();
	}

	@Override
	public float getOriginY(Sprite s)
	{
		return s.getOriginY();
	}

	@Override
	public Rectangle getBoundingRectangle(Sprite s)
	{
		return s.getBoundingRectangle();
	}

}