package com.kotcrab.vis.launcher;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.common.utils.VisChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisImageTextButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.file.FileChooser;

public class Launcher extends ApplicationAdapter {
	private Stage stage;
	private VisTable root;

	@Override
	public void create () {
		Assets.load();
		VisUI.load();
		VisUI.setDefaultTitleAlign(Align.center);
		FileChooser.setFavoritesPrefsName("com.kotcrab.vis.editor");

		root = new VisTable();
		root.setFillParent(true);

		stage = new Stage(new ScreenViewport());
		stage.addActor(root);
		Gdx.input.setInputProcessor(stage);
//		stage.setDebugAll(true);

		VisTable leftTable = new VisTable(false);
		VisTable rightTable = new VisTable(false);

		leftTable.setBackground(VisUI.getSkin().getDrawable("window-bg"));
		rightTable.setBackground(VisUI.getSkin().getDrawable("window-bg"));
		leftTable.top();
		rightTable.top();

		root.top();
		root.add(leftTable).width(128).fill();
		root.addSeparator(true).pad(0);
		root.add(rightTable).expand().fill();

		VisTable sectionsTable = createSectionsTable();

		VisTextButton launchButton = new VisTextButton("Launch");
		launchButton.setFocusBorderEnabled(false);

		leftTable.defaults().expandX().fillX();
		leftTable.add(new Image(Assets.getIcon(Icons.EDITOR_ICON))).size(100).row();
		leftTable.addSeparator().pad(0).spaceBottom(0).row();
		leftTable.add(sectionsTable).row();
		leftTable.add().expand().fill().row();
		leftTable.addSeparator().pad(0);
		leftTable.add(launchButton).height(60);

		VisTable sectionContentTable = new VisTable();
		sectionContentTable.top().left();
		sectionContentTable.defaults().left();

		sectionContentTable.add("News").padBottom(6).row();
		sectionContentTable.add("VisUI 0.7.4 released!\nIt does many amazing things. Now with free lorem ipsum").row();

		rightTable.pad(5);
		rightTable.add("VisLauncher - Start");
		rightTable.add(createSocialTable()).expandX().right().row();
		rightTable.addSeparator().colspan(2).padTop(6);
		rightTable.add(sectionContentTable).colspan(2).expand().fill();
	}

	private VisTable createSocialTable () {
		VisTable table = new VisTable(true);

		VisImageButton www = new VisImageButton(Assets.getIcon(Icons.GLOBE));
		VisImageButton twitter = new VisImageButton(Assets.getIcon(Icons.TWITTER));
		VisImageButton github = new VisImageButton(Assets.getIcon(Icons.GITHUB));

		www.addListener(new VisChangeListener((event, actor) -> Gdx.net.openURI("http://vis.kotcrab.com")));
		twitter.addListener(new VisChangeListener((event, actor) -> Gdx.net.openURI("https://twitter.com/kotcrab")));
		github.addListener(new VisChangeListener((event, actor) -> Gdx.net.openURI("https://github.com/kotcrab/VisEditor")));

		table.add(www);
		table.add(twitter);
		table.add(github);
		return table;
	}

	private VisTable createSectionsTable () {
		VisTable table = new VisTable(false);

		VisImageTextButton startButton = createSectionButton("Start", "default-noborder", Assets.getIcon(Icons.HOME));
		VisImageTextButton editorButton = createSectionButton("Editor", "default-noborder", Assets.getIcon(Icons.VIS_ICON));
		VisImageTextButton toolsButton = createSectionButton("Tools", "default-noborder", Assets.getIcon(Icons.TOOLS));
		VisImageTextButton contentButton = createSectionButton("Content", "default-noborder", Assets.getIcon(Icons.CONTENT));

		table.defaults().expandX().fillX();
		table.add(startButton).row();
		table.addSeparator().pad(0);
		table.add(editorButton).row();
		table.addSeparator().pad(0);
		table.add(toolsButton).row();
		table.addSeparator().pad(0);
		table.add(contentButton).row();
		table.addSeparator().pad(0);
		return table;
	}

	private VisImageTextButton createSectionButton (String text, String style, Drawable icon) {
		VisImageTextButton button = new VisImageTextButton(text, style, icon);
		button.getLabel().setAlignment(Align.left);
		button.getLabelCell().expandX().fillX();
		return button;
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void dispose () {
		stage.dispose();
		VisUI.dispose();
		Assets.dispose();
	}
}
