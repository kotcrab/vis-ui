package com.kotcrab.vis.ui.lml.test.manual;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class LmlApp extends ApplicationAdapter {
	@Override
	public void create () {
		super.create();
	}

	@Override
	public void dispose () {
		super.dispose();
	}

	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new LmlApp(), config);
	}
}
