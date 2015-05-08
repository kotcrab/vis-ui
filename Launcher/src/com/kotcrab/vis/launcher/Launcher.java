package com.kotcrab.vis.launcher;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.launcher.api.APIClient.SetCallback;
import com.kotcrab.vis.launcher.api.ContentSet;
import com.kotcrab.vis.launcher.api.DataCache;
import com.kotcrab.vis.launcher.api.GdxReleaseSet;
import com.kotcrab.vis.launcher.api.VersionSet;
import com.kotcrab.vis.launcher.ui.NewsSection;
import com.kotcrab.vis.launcher.ui.SocialTable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageTextButton;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.file.FileChooser;

public class Launcher extends ApplicationAdapter {
	private FileAccess fileAccess;
	private DataCache dataCache;

	private Stage stage;
	private VisTable root;

	@Override
	public void create () {
		Assets.load();
		VisUI.load();
		VisUI.setDefaultTitleAlign(Align.center);
		FileChooser.setFavoritesPrefsName("com.kotcrab.vis.editor");

		fileAccess = new FileAccess();
		dataCache = new DataCache(fileAccess);

		NewsSection newsSection = new NewsSection();
		dataCache.setNewsCallback(newsSection);

		//dummy callbacks for now
		dataCache.setContentCallback(new SetCallback<ContentSet>() {
		});
		dataCache.setGdxCallback(new SetCallback<GdxReleaseSet>() {
		});
		dataCache.setVersionCallback(new SetCallback<VersionSet>() {
		});

		dataCache.init();

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
		sectionContentTable.add(newsSection).expand().fill();

		VisScrollPane scrollPane = new VisScrollPane(sectionContentTable);
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setFadeScrollBars(false);

		rightTable.pad(5);
		rightTable.add("VisLauncher - Start");
		rightTable.add(new SocialTable()).expandX().right().row();
		rightTable.addSeparator().colspan(2).padTop(6);
		rightTable.add(scrollPane).colspan(2).expand().fill();
	}

	private VisTable createSectionsTable () {
		VisTable table = new VisTable(false);

		VisImageTextButton startButton = createSectionButton("Start", "toggle-noborder", Assets.getIcon(Icons.HOME));
		VisImageTextButton editorButton = createSectionButton("Editor", "toggle-noborder", Assets.getIcon(Icons.VIS_ICON));
		VisImageTextButton toolsButton = createSectionButton("Tools", "toggle-noborder", Assets.getIcon(Icons.TOOLS));
		VisImageTextButton contentButton = createSectionButton("Content", "toggle-noborder", Assets.getIcon(Icons.CONTENT));

		ButtonGroup<VisImageTextButton> group = new ButtonGroup<>();
		group.add(startButton);
		group.add(editorButton);
		group.add(toolsButton);
		group.add(contentButton);

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
