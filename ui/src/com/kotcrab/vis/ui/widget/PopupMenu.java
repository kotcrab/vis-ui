/*
 * Copyright 2014-2016 See AUTHORS file.
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

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.ActorUtils;

/**
 * Standard popup menu that can be displayed anywhere on stage. Menu is automatically removed when user clicked outside menu,
 * or clicked menu item. For proper behaviour menu should be displayed in touchUp event. If you want to display
 * menu from touchDown you have to call event.stop() otherwise menu will by immediately closed.
 * <p>
 * If you want to add right click menu to actor you can use getDefaultInputListener() to get premade default listener.
 * @author Kotcrab
 */
public class PopupMenu extends Table {
	private PopupMenuStyle style;

	private InputListener stageListener;
	private ChangeListener sharedMenuItemListener;

	private InputListener defaultInputListener;

	/** The current subMenu, set by MenuItem */
	private PopupMenu subMenu;

	public PopupMenu () {
		this("default");
	}

	public PopupMenu (String styleName) {
		this(VisUI.getSkin().get(styleName, PopupMenuStyle.class));
	}

	public PopupMenu (PopupMenuStyle style) {
		this.style = style;
		setTouchable(Touchable.enabled);
		pad(0);
		setBackground(style.background);
		createListeners();
	}

	private void createListeners () {
		stageListener = new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (menuStructureContains(x, y) == false) {
					remove();
					return true;
				}

				return true;
			}

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				//handles situation where menuitem was clicked in subMenu
				if (subMenu != null) removeIfNeeded(x, y);
			}

			private boolean removeIfNeeded (float x, float y) {
				if (contains(x, y) == false) {
					remove();
					return true;
				}

				return false;
			}
		};

		sharedMenuItemListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (event.isStopped() == false)
					remove();
			}
		};
	}

	@Override
	public <T extends Actor> Cell<T> add (T actor) {
		if (actor instanceof MenuItem) {
			throw new IllegalArgumentException("MenuItems can be only added to PopupMenu by using addItem(MenuItem) method");
		}

		return super.add(actor);
	}

	public void addItem (MenuItem item) {
		super.add(item).fillX().expandX().row();
		pack();

		item.addListener(sharedMenuItemListener);
	}

	public void addSeparator () {
		add(new Separator("menu")).padTop(2).padBottom(2).fill().expand().row();
	}

	/**
	 * Returns input listener that can be added to scene2d actor. When right mouse button is pressed on that actor,
	 * menu will be displayed
	 */
	public InputListener getDefaultInputListener () {
		if (defaultInputListener == null) {
			defaultInputListener = new InputListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					return true;
				}

				@Override
				public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
					if (event.getButton() == Buttons.RIGHT)
						showMenu(event.getStage(), event.getStageX(), event.getStageY());
				}
			};
		}

		return defaultInputListener;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (style.border != null) style.border.draw(batch, getX(), getY(), getWidth(), getHeight());
	}

	public void showMenu (Stage stage, float x, float y) {
		setPosition(x, y - getHeight());
		if (stage.getHeight() - getY() > stage.getHeight()) setY(getY() + getHeight());
		ActorUtils.keepWithinStage(stage, this);
		stage.addActor(this);
	}

	public boolean contains (float x, float y) {
		return getX() <= x && getX() + getWidth() >= x && getY() <= y && getY() + getHeight() >= y;
	}

	public boolean menuStructureContains (float x, float y) {
		if (contains(x, y)) return true;
		if (subMenu != null) return subMenu.menuStructureContains(x, y);
		return false;
	}

	/** Called by framework, when PopupMenu is added to MenuItem as submenu */
	void setSubMenu (PopupMenu subMenu) {
		if (this.subMenu == subMenu) return;
		if (this.subMenu != null) this.subMenu.remove();
		this.subMenu = subMenu;
	}

	@Override
	protected void setStage (Stage stage) {
		super.setStage(stage);
		if (stage != null) stage.addListener(stageListener);
	}

	@Override
	public boolean remove () {
		if (getStage() != null) getStage().removeListener(stageListener);
		if (subMenu != null) subMenu.remove();
		return super.remove();
	}

	static public class PopupMenuStyle {
		public Drawable background;
		public Drawable border;

		public PopupMenuStyle () {
		}

		public PopupMenuStyle (Drawable background, Drawable border) {
			this.background = background;
			this.border = border;
		}

		public PopupMenuStyle (PopupMenuStyle style) {
			this.background = style.background;
			this.border = style.border;
		}
	}
}
