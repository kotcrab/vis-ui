package com.kotcrab.vis.launcher;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "VisLauncher";
		config.width = 650;
		config.height = 610;
		config.backgroundFPS = 0;

		new LwjglApplication(new Launcher(), config);
	}
}
