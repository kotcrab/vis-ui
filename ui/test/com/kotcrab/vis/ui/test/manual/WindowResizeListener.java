package com.kotcrab.vis.ui.test.manual;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public abstract class WindowResizeListener implements EventListener {
	@Override
	public boolean handle (Event event) {
		if (event instanceof WindowResizeEvent == false) return false;
		resize();
		return false;
	}

	public abstract void resize ();
}

class WindowResizeEvent extends Event {
}
