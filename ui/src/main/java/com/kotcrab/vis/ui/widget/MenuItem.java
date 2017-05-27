/*
 * Copyright 2014-2017 See AUTHORS file.
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
 */

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.OsUtils;

/**
 * MenuItem displayed in {@link Menu} and {@link PopupMenu}. MenuItem contains text or text with icon.
 * Best icon size is 22px. MenuItem can also have a hotkey text.
 * <p>
 * When listening for menu item press {@link ChangeListener} should be always preferred (instead of {@link ClickListener}).
 * {@link ClickListener} does not support disabling menu item and will still report item clicks.
 * @author Kotcrab
 */
public class MenuItem extends Button {
	private static final Vector2 tmpVector = new Vector2();

	//MenuItem is modified version of TextButton

	private MenuItemStyle style;

	private Image image;
	private boolean generateDisabledImage = true;
	private Label label;
	private Color shortcutLabelColor;
	private VisLabel shortcutLabel;
	private Image subMenuImage;
	private Cell<Image> subMenuIconCell;

	private PopupMenu subMenu;

	/** Menu that this item belongs to */
	PopupMenu containerMenu;

	public MenuItem (String text) {
		this(text, (Image) null, VisUI.getSkin().get(MenuItemStyle.class));
	}

	public MenuItem (String text, String styleName) {
		this(text, (Image) null, VisUI.getSkin().get(styleName, MenuItemStyle.class));
	}

	public MenuItem (String text, ChangeListener changeListener) {
		this(text, (Image) null, VisUI.getSkin().get(MenuItemStyle.class));
		addListener(changeListener);
	}

	public MenuItem (String text, Drawable drawable) {
		this(text, drawable, VisUI.getSkin().get(MenuItemStyle.class));
	}

	public MenuItem (String text, Drawable drawable, ChangeListener changeListener) {
		this(text, drawable, VisUI.getSkin().get(MenuItemStyle.class));
		addListener(changeListener);
	}

	public MenuItem (String text, Drawable drawable, String styleName) {
		this(text, drawable, VisUI.getSkin().get(styleName, MenuItemStyle.class));
	}

	public MenuItem (String text, Image image) {
		this(text, image, VisUI.getSkin().get(MenuItemStyle.class));
	}

	public MenuItem (String text, Image image, ChangeListener changeListener) {
		this(text, image, VisUI.getSkin().get(MenuItemStyle.class));
		addListener(changeListener);
	}

	public MenuItem (String text, Image image, String styleName) {
		this(text, image, VisUI.getSkin().get(styleName, MenuItemStyle.class));
	}

	// Base constructors

	public MenuItem (String text, Image image, MenuItemStyle style) {
		super(style);
		init(text, image, style);
	}

	public MenuItem (String text, Drawable drawable, MenuItemStyle style) {
		super(style);
		init(text, new Image(drawable), style);
	}

	private void init (String text, Image image, MenuItemStyle style) {
		this.style = style;
		this.image = image;
		setSkin(VisUI.getSkin());
		Sizes sizes = VisUI.getSizes();

		defaults().space(3);

		if (image != null) image.setScaling(Scaling.fit);
		add(image).size(sizes.menuItemIconSize);

		label = new Label(text, new LabelStyle(style.font, style.fontColor));
		label.setAlignment(Align.left);
		add(label).expand().fill();

		add(shortcutLabel = new VisLabel("", "menuitem-shortcut")).padLeft(10).right();
		shortcutLabelColor = shortcutLabel.getStyle().fontColor;

		subMenuIconCell = add(subMenuImage = new Image(style.subMenu)).padLeft(3).padRight(3)
				.size(style.subMenu.getMinWidth(), style.subMenu.getMinHeight());
		subMenuIconCell.setActor(null);

		addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (subMenu != null) { //makes submenu item not clickable
					event.stop();
				}
			}
		});

		addListener(new InputListener() {
			@Override
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if (subMenu != null) { //removes selection of child submenu if mouse moved to parent submenu
					subMenu.setActiveItem(null, false);
					subMenu.setActiveSubMenu(null);
				}

				if (subMenu == null || isDisabled()) { //hides last visible submenu (if any)
					hideSubMenu();
				} else {
					showSubMenu();
				}
			}
		});
	}

	public void setSubMenu (final PopupMenu subMenu) {
		this.subMenu = subMenu;

		if (subMenu == null) {
			subMenuIconCell.setActor(null);
		} else {
			subMenuIconCell.setActor(subMenuImage);
		}
	}

	public PopupMenu getSubMenu () {
		return subMenu;
	}

	void packContainerMenu () {
		if (containerMenu != null) containerMenu.pack();
	}

	@Override
	protected void setParent (Group parent) {
		super.setParent(parent);
		if (parent instanceof PopupMenu)
			containerMenu = (PopupMenu) parent;
		else
			containerMenu = null;
	}

	void hideSubMenu () {
		if (containerMenu != null) {
			containerMenu.setActiveSubMenu(null);
		}
	}

	void showSubMenu () {
		Stage stage = getStage();
		Vector2 pos = localToStageCoordinates(tmpVector.setZero());

		float availableSpaceLeft = pos.x;
		float availableSpaceRight = stage.getWidth() - (pos.x + getWidth());
		boolean canFitOnTheRight = pos.x + getWidth() + subMenu.getWidth() <= stage.getWidth();
		float subMenuX;
		if (canFitOnTheRight || availableSpaceRight > availableSpaceLeft) {
			subMenuX = pos.x + getWidth() - 1;
		} else {
			subMenuX = pos.x - subMenu.getWidth() + 1;
		}

		subMenu.setPosition(subMenuX, pos.y - subMenu.getHeight() + getHeight());

		if (subMenu.getY() < 0) {
			subMenu.setY(subMenu.getY() + subMenu.getHeight() - getHeight());
		}

		stage.addActor(subMenu);
		containerMenu.setActiveSubMenu(subMenu);
	}

	void fireChangeEvent () {
		ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
		fire(changeEvent);
		Pools.free(changeEvent);
	}

	@Override
	public MenuItemStyle getStyle () {
		return style;
	}

	@Override
	public void setStyle (ButtonStyle style) {
		if (!(style instanceof MenuItemStyle)) throw new IllegalArgumentException("style must be a MenuItemStyle.");
		super.setStyle(style);
		this.style = (MenuItemStyle) style;
		if (label != null) {
			TextButtonStyle textButtonStyle = (TextButtonStyle) style;
			LabelStyle labelStyle = label.getStyle();
			labelStyle.font = textButtonStyle.font;
			labelStyle.fontColor = textButtonStyle.fontColor;
			label.setStyle(labelStyle);
		}
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

		if (isDisabled())
			shortcutLabel.getStyle().fontColor = style.disabledFontColor;
		else
			shortcutLabel.getStyle().fontColor = shortcutLabelColor;

		if (image != null && generateDisabledImage) {
			if (isDisabled())
				image.setColor(Color.GRAY);
			else
				image.setColor(Color.WHITE);
		}

		super.draw(batch, parentAlpha);
	}

	@Override
	public boolean isOver () {
		if (containerMenu == null || containerMenu.getActiveItem() == null) {
			return super.isOver();
		} else {
			return containerMenu.getActiveItem() == this;
		}
	}

	public boolean isGenerateDisabledImage () {
		return generateDisabledImage;
	}

	/**
	 * Changes generateDisabledImage property, when true that function is enabled. When it is enabled and this MenuItem
	 * is disabled then image color will be changed to gray meaning that it is disabled, by default it is enabled.
	 */
	public void setGenerateDisabledImage (boolean generateDisabledImage) {
		this.generateDisabledImage = generateDisabledImage;
	}

	/**
	 * Set shortcuts text displayed in this menu item.
	 * This DOES NOT set actual hot key for this menu item, it only makes shortcut text visible in item.
	 * @param keycode from {@link Keys}.
	 */
	public MenuItem setShortcut (int keycode) {
		return setShortcut(Keys.toString(keycode));
	}

	public CharSequence getShortcut () {
		return shortcutLabel.getText();
	}

	/**
	 * Set shortcuts text displayed in this menu item. This DOES NOT set actual hot key for this menu item,
	 * it only makes shortcut text visible in item.
	 * @param text text that will be displayed
	 * @return this object for the purpose of chaining methods
	 */
	public MenuItem setShortcut (String text) {
		shortcutLabel.setText(text);
		packContainerMenu();
		return this;
	}

	/**
	 * Creates platform dependant shortcut text. Converts int keycodes to String text. Eg. Keys.CONTROL_LEFT,
	 * Keys.SHIFT_LEFT, Keys.F5 will be converted to Ctrl+Shift+F5 on Windows and Linux, and to ⌘⇧F5 on Mac.
	 * <p>
	 * CONTROL_LEFT and CONTROL_RIGHT are mapped to Ctrl. The same goes for Alt (ALT_LEFT, ALT_RIGHT) and Shift (SHIFT_LEFT, SHIFT_RIGHT).
	 * <p>
	 * This DOES NOT set actual hot key for this menu item, it only makes shortcut text visible in item.
	 * @param keycodes keycodes from {@link Keys} that are used to create shortcut text
	 * @return this object for the purpose of chaining methods
	 */
	public MenuItem setShortcut (int... keycodes) {
		shortcutLabel.setText(OsUtils.getShortcutFor(keycodes));
		packContainerMenu();
		return this;
	}

	@Override
	protected void setStage (Stage stage) {
		super.setStage(stage);
		label.invalidate(); //fixes issue with disappearing menu item after holding right mouse button and dragging down while opening menu
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

	public CharSequence getText () {
		return label.getText();
	}

	public void setText (CharSequence text) {
		label.setText(text);
	}

	public Cell<Image> getSubMenuIconCell () {
		return subMenuIconCell;
	}

	public Cell<VisLabel> getShortcutCell () {
		return getCell(shortcutLabel);
	}

	static public class MenuItemStyle extends TextButtonStyle {
		public Drawable subMenu;

		public MenuItemStyle () {
		}

		public MenuItemStyle (Drawable subMenu) {
			this.subMenu = subMenu;
		}

		public MenuItemStyle (MenuItemStyle style) {
			super(style);
			this.subMenu = style.subMenu;
		}
	}
}
