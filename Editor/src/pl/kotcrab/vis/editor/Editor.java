
package pl.kotcrab.vis.editor;

import pl.kotcrab.vis.editor.ui.Menu;
import pl.kotcrab.vis.editor.ui.MenuBar;
import pl.kotcrab.vis.editor.ui.MenuItem;
import pl.kotcrab.vis.editor.ui.NewProjectDialog;
import pl.kotcrab.vis.editor.ui.UI;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Editor extends ApplicationAdapter {

	private Stage stage;
	private Skin skin;
	private Table root;
	private ShapeRenderer shapeRenderer;
	private MenuBar menuBar;

	@Override
	public void create () {
		UI.load();

		stage = new Stage(new ScreenViewport());

		Gdx.input.setInputProcessor(stage);

		root = new Table();
		root.setFillParent(true);
		if (UI.DEBUG) root.debug();

		stage.addActor(root);

		skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		shapeRenderer = new ShapeRenderer();
		menuBar = new MenuBar(stage, skin);

		root.left().top();
		root.add(menuBar.getTable()).fillX().expandX();

		Menu fileMenu = new Menu("File");

		menuBar.addMenu(fileMenu);
		stage.addActor(new NewProjectDialog(stage, skin));

		fileMenu.addItem(new MenuItem("New project...", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				stage.addActor(new NewProjectDialog(stage, skin));
			}
		}));

		fileMenu.addItem(new MenuItem("Load project..."));
		fileMenu.addItem(new MenuItem("Close project"));
		fileMenu.addItem(new MenuItem("Exit"));
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
		shapeRenderer.setTransformMatrix(new Matrix4().setToOrtho2D(0, 0, width, height));

		menuBar.resize();
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
		UI.dispose();

		stage.dispose();
		skin.dispose();
		shapeRenderer.dispose();
	}

}
