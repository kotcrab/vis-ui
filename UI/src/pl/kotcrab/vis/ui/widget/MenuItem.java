/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package pl.kotcrab.vis.ui.widget;

import pl.kotcrab.vis.ui.VisUI;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;

public class MenuItem extends Button {
	private final Image image;
	private final Label label;
	private TextButtonStyle style;

	private boolean generateDisabledImage = true;

	private Cell<VisLabel> shortcutLabelCell;

	public MenuItem (String text) {
		this(text, null, VisUI.skin.get(TextButtonStyle.class));
	}

	public MenuItem (String text, ChangeListener changeListener) {
		this(text, null, VisUI.skin.get(TextButtonStyle.class));
		addListener(changeListener);
	}

	public MenuItem (String text, Drawable image) {
		this(text, image, VisUI.skin.get(TextButtonStyle.class));
	}

	public MenuItem (String text, Drawable image, ChangeListener changeListener) {
		this(text, image, VisUI.skin.get(TextButtonStyle.class));
		addListener(changeListener);
	}

	public MenuItem (String text, Drawable image, String styleName) {
		this(text, image, VisUI.skin.get(styleName, TextButtonStyle.class));
	}

	public MenuItem (String text, Drawable icon, TextButtonStyle style) {
		super(style);
		setSkin(VisUI.skin);

		this.style = style;
		defaults().space(3);

		image = new Image(icon);
		image.setScaling(Scaling.fit);
		add(image).padLeft(icon != null ? 0 : 24);

		label = new Label(text, new LabelStyle(style.font, style.fontColor));
		label.setAlignment(Align.left);
		add(label).expand().fill();

		setStyle(style);
	}

	@Override
	public void setStyle (ButtonStyle style) {
		if (!(style instanceof TextButtonStyle)) throw new IllegalArgumentException("style must be a TextButtonStyle.");
		super.setStyle(style);
		this.style = (TextButtonStyle)style;
		if (label != null) {
			TextButtonStyle textButtonStyle = (TextButtonStyle)style;
			LabelStyle labelStyle = label.getStyle();
			labelStyle.font = textButtonStyle.font;
			labelStyle.fontColor = textButtonStyle.fontColor;
			label.setStyle(labelStyle);
		}
	}

	@Override
	public TextButtonStyle getStyle () {
		return style;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		Color fontColor;
		if (isDisabled() && style.disabledFontColor != null)
			fontColor = style.disabledFontColor;
		else if (isPressed() && style.downFontColor != null)
			fontColor = style.downFontColor;
		else if (isChecked() && style.checkedFontColor != null)
			fontColor = (isOver() && style.checkedOverFontColor != null) ? style.checkedOverFontColor : style.checkedFontColor;
		else if (isOver() && style.overFontColor != null)
			fontColor = style.overFontColor;
		else
			fontColor = style.fontColor;
		if (fontColor != null) label.getStyle().fontColor = fontColor;

		if (generateDisabledImage) {
			if (isDisabled())
				image.setColor(Color.GRAY);
			else
				image.setColor(Color.WHITE);
		}

		super.draw(batch, parentAlpha);
	}

	public boolean isGenerateDisabledImage () {
		return generateDisabledImage;
	}

	public void setGenerateDisabledImage (boolean generateDisabledImage) {
		this.generateDisabledImage = generateDisabledImage;
	}

	/** @param keycode from {@link Keys} */
	public MenuItem setShortcut (int keycode) {
		return setShortcut(Keys.toString(keycode));
	}

	/** Displayed as modifier+keycode (eg. Ctrl+F5)
	 * @param modifier form {@link Keys}
	 * @param keycode form {@link Keys} */
	public MenuItem setShortcut (int modifier, int keycode) {
		return setShortcut(Keys.toString(modifier) + "+" + Keys.toString(keycode));
	}

	public MenuItem setShortcut (String text) {
		if (shortcutLabelCell == null)
			shortcutLabelCell = add(new VisLabel(text, "menuitem-shortcut")).padLeft(10).right();
		else
			shortcutLabelCell.getActor().setText(text);

		return this;
	}

	public Image getImage () {
		return image;
	}

	public Cell<?> getImageCell () {
		return getCell(image);
	}

	public Label getLabel () {
		return label;
	}

	public Cell<?> getLabelCell () {
		return getCell(label);
	}

	public void setText (CharSequence text) {
		label.setText(text);
	}

	public CharSequence getText () {
		return label.getText();
	}
}
