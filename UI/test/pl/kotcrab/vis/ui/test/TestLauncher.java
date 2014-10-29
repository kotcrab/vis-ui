
package pl.kotcrab.vis.ui.test;

import pl.kotcrab.vis.ui.VisUI;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TestLauncher {

	public static void main (String[] args) {
		
		LwjglApplicationConfiguration c = new LwjglApplicationConfiguration();
		c.width = 800;
		c.height = 480;
		new LwjglApplication(new TestApplication(), c);
	}

}

class TestApplication extends ApplicationAdapter {

	private Stage stage;
	private ShapeRenderer shapeRenderer;

	@Override
	public void create () {
		VisUI.load();

		stage = new Stage(new ScreenViewport());

		Gdx.input.setInputProcessor(stage);

		stage.addActor(new TestWindow(stage));

		shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
		shapeRenderer.setTransformMatrix(new Matrix4().setToOrtho2D(0, 0, width, height));
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

	}

	@Override
	public void dispose () {
		VisUI.dispose();

		stage.dispose();
		shapeRenderer.dispose();
	}

}
