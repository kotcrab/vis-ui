
package pl.kotcrab.vis.editor.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class VisTextField extends TextField {
	private VisTextFieldStyle style;

	public VisTextField (String text, Skin skin) {
		super(text, skin.get(VisTextFieldStyle.class));
		style = (VisTextFieldStyle)getStyle();
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		boolean focused = getStage() != null && getStage().getKeyboardFocus() == this;
		if (focused && style.focusBorder != null) style.focusBorder.draw(batch, getX(), getY(), getWidth(), getHeight());
	}

	static public class VisTextFieldStyle extends TextFieldStyle {
		public Drawable focusBorder;

		public VisTextFieldStyle () {
		}

		public VisTextFieldStyle (BitmapFont font, Color fontColor, Drawable cursor, Drawable selection, Drawable background) {
			super(font, fontColor, cursor, selection, background);
		}

		public VisTextFieldStyle (VisTextFieldStyle style) {
			super(style);
			this.focusBorder = style.focusBorder;
		}
	}
}
