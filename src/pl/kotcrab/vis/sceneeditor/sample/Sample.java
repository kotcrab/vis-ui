package pl.kotcrab.vis.sceneeditor.sample;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Sample implements ApplicationListener
{
	private SpriteBatch batch;
	private OrthographicCamera camera;
	
	private SampleScene scene;
	
	@Override
	public void create()
	{
		batch = new SpriteBatch();
		camera = new OrthographicCamera(800, 480);
		camera.position.x = 800 / 2;
		camera.position.y = 480 / 2;
		
		scene = new SampleScene(camera);
	}
	
	@Override
	public void resize(int width, int height)
	{
		scene.reisze();
	}
	
	@Override
	public void render()
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		scene.render(batch);
	}
	
	@Override
	public void pause()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void resume()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void dispose()
	{
		batch.dispose();
		scene.dispose();
	}
	
}