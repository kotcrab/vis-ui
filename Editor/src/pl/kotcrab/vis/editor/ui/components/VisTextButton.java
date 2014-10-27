
package pl.kotcrab.vis.editor.ui.components;

import pl.kotcrab.vis.editor.ui.Focusable;
import pl.kotcrab.vis.editor.ui.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class VisTextButton extends TextButton implements Focusable {
	private VisTextButtonStyle style;

	private boolean drawBorder;

	public VisTextButton (String text, Skin skin) {
		super(text, skin.get(VisTextButtonStyle.class));
		style = (VisTextButtonStyle)getStyle();

		addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				UI.focusManager.requestFocus(VisTextButton.this);
				return false;
			}
		});
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (drawBorder) style.focusBorder.draw(batch, getX(), getY(), getWidth(), getHeight());
	}

	static public class VisTextButtonStyle extends TextButtonStyle {
		public Drawable focusBorder;

		public VisTextButtonStyle () {
			super();
		}

		public VisTextButtonStyle (Drawable up, Drawable down, Drawable checked, BitmapFont font) {
			super(up, down, checked, font);
		}

		public VisTextButtonStyle (VisTextButtonStyle style) {
			super(style);
			this.focusBorder = style.focusBorder;
		}
	}

	@Override
	public void focusLost () {
		drawBorder = false;
	}

	@Override
	public void focusGained () {
		drawBorder = true;
	}
}
