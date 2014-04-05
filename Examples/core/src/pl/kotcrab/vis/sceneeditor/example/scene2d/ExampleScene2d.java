package pl.kotcrab.vis.sceneeditor.example.scene2d;

import pl.kotcrab.vis.sceneeditor.SceneEditor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ExampleScene2d implements ApplicationListener
{
	private Texture texture;
	
	private SceneEditor sceneEditor;
	
	private Stage stage;
	private MyActor actor1;
	private MyActor actor2;
	
	public void create()
	{
		texture = new Texture("bush.png");
		
		actor1 = new MyActor(texture);
		actor2 = new MyActor(texture);
		
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		
		stage.addActor(actor1);
		stage.addActor(actor2);
	
		sceneEditor = new SceneEditor(Gdx.files.internal("sampleScene2d.json"), (OrthographicCamera) stage.getCamera(), true);
		sceneEditor.add(actor1, "actor1");
		sceneEditor.add(actor2, "actor2");
		sceneEditor.load();
	}
	
	public void resize(int width, int height)
	{
		stage.getViewport().update(width, height, true);
	}
	
	public void render()
	{
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		sceneEditor.render();
	}
	
	public void dispose()
	{
		texture.dispose();
		sceneEditor.dispose();
		stage.dispose();
	}

	@Override
	public void pause()
	{
		
	}

	@Override
	public void resume()
	{
		
	}
}
