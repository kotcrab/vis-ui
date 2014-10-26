
package pl.kotcrab.vis.editor.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class VisTextButton extends TextButton {
	private VisTextButtonStyle style;

	private boolean drawBorder;

	public VisTextButton (String text, Skin skin) {
		super(text, skin.get(VisTextButtonStyle.class));
		style = (VisTextButtonStyle)getStyle();
	}

	@Override
	public void act (float delta) {
		super.act(delta);

		if (isPressed()) drawBorder = true;
		if (isOver() == false) drawBorder = false;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (style.focusBorder != null && drawBorder) style.focusBorder.draw(batch, getX(), getY(), getWidth(), getHeight());
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
}
