package com.kotcrab.vis.launcher.ui;

import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.common.utils.VisChangeListener;
import com.kotcrab.vis.launcher.Assets;
import com.kotcrab.vis.launcher.Icons;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;

public class SocialTable extends VisTable {
	public SocialTable () {
		super(true);

		VisImageButton www = new VisImageButton(Assets.getIcon(Icons.GLOBE));
		VisImageButton twitter = new VisImageButton(Assets.getIcon(Icons.TWITTER));
		VisImageButton github = new VisImageButton(Assets.getIcon(Icons.GITHUB));

		www.addListener(new VisChangeListener((event, actor) -> Gdx.net.openURI("http://vis.kotcrab.com")));
		twitter.addListener(new VisChangeListener((event, actor) -> Gdx.net.openURI("https://twitter.com/kotcrab")));
		github.addListener(new VisChangeListener((event, actor) -> Gdx.net.openURI("https://github.com/kotcrab/VisEditor")));

		add(www);
		add(twitter);
		add(github);
	}
}
