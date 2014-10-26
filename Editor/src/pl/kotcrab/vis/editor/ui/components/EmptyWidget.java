
package pl.kotcrab.vis.editor.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class EmptyWidget extends Widget {
	private float prefWidth, prefHeight;

	public EmptyWidget (float prefWidth, float prefHeight) {
		this.prefWidth = prefWidth;
		this.prefHeight = prefHeight;
	}

	@Override
	public float getPrefWidth () {
		return prefWidth;
	}

	@Override
	public float getPrefHeight () {
		return prefHeight;
	}

	@Override
	public float getMaxWidth () {
		return getPrefWidth();
	}

	@Override
	public float getMaxHeight () {
		return getPrefHeight();
	}
}
