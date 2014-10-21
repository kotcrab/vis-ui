
package pl.kotcrab.vis.editor;

import pl.kotcrab.vis.editor.ui.Menu;
import pl.kotcrab.vis.editor.ui.MenuBar;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Editor extends ApplicationAdapter {

	private Stage stage;
	private Table root;
	private ShapeRenderer shapeRenderer;
	private MenuBar mb;

	@Override
	public void create () {
		stage = new Stage(new ScreenViewport());

		Gdx.input.setInputProcessor(stage);

		root = new Table();
		root.setFillParent(true);
		root.debug();
		stage.addActor(root);
		
		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
		shapeRenderer = new ShapeRenderer();

		mb = new MenuBar(stage, skin);

		root.left().top();
		root.add(mb);

		Menu m1 = new Menu("File");
		Menu m2 = new Menu("Scene");
		Menu m3 = new Menu("Help");

		mb.addMenu(m1);
		mb.addMenu(m2);
		mb.addMenu(m3);

		m1.addItem(new TextButton("Test option 1", skin, "menu"));
		m1.addItem(new TextButton("Test option 2", skin, "menu"));
		m1.addItem(new TextButton("Test option 3", skin, "menu"));

		m2.addItem(new TextButton("Test option 4", skin, "menu"));
		m2.addItem(new TextButton("Test option 5", skin, "menu"));
		m2.addItem(new TextButton("Test option 6", skin, "menu"));
		
		m3.addItem(new TextButton("Test option 7", skin, "menu"));
		m3.addItem(new TextButton("Test option 8", skin, "menu"));
		m3.addItem(new TextButton("Test option 9", skin, "menu"));
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

		shapeRenderer.begin(ShapeType.Line);
		root.drawDebug(shapeRenderer); // This is optional, but enables debug lines for tables.
		shapeRenderer.end();
	}

	@Override
	public void dispose () {
		stage.dispose();
		shapeRenderer.dispose();
	}

}
