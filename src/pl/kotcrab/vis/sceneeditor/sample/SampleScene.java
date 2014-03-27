package pl.kotcrab.vis.sceneeditor.sample;

import pl.kotcrab.vis.sceneeditor.SceneEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SampleScene
{
	private SceneEditor sceneEditor;
	
	private Texture bushTexture;
	private Texture netTexture;
	
	private Sprite bush1;
	private Sprite bush2;
	private Sprite net1;
	private Sprite net2;
	
	public SampleScene(OrthographicCamera camera)
	{
		bushTexture = new Texture(Gdx.files.internal("sample_assets/bush.png"));
		netTexture = new Texture(Gdx.files.internal("sample_assets/net.png"));
		
		bush1 = new Sprite(bushTexture);
		bush2 = new Sprite(bushTexture);
		net1 = new Sprite(netTexture);
		net2 = new Sprite(netTexture);
		
		net1.setRotation(45); //DEBUG
		net1.setPosition(450, 240);
		
		sceneEditor = new SceneEditor(Gdx.files.internal("assets/data/arial.fnt"), Gdx.files.internal("sample_assets/scene.xml"), camera, true);
		sceneEditor.add(bush1, "bush1").add(bush2, "bush2").add(net1, "net1").add(net2, "net2");
		sceneEditor.load();
		sceneEditor.enable();
	}
	
	public void render(SpriteBatch batch)
	{
		batch.begin();
		bush1.draw(batch);
		bush2.draw(batch);
		net1.draw(batch);
		net2.draw(batch);
		batch.end();
		
		sceneEditor.render();
	}
	
	public void dispose()
	{
		bushTexture.dispose();
		netTexture.dispose();
	}
	
	public void reisze()
	{
		sceneEditor.resize();
	}
}