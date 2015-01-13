/*
 * Copyright 2014-2015 Pawel Pastuszak
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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;

public class PopupMenu extends Table {
	private PopupMenuStyle style;

	private Rectangle boundingRectangle;
	private boolean autoRemove;

	private InputListener autoRemoveListener = new InputListener() {
		@Override
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			if (contains(x, y) == false && autoRemove) {
				remove();
				return true;
			}

			return false;
		}
	};

	public PopupMenu () {
		this(false, "default");
	}

	public PopupMenu (boolean autoRemove) {
		this(autoRemove, "default");
	}

	public PopupMenu (String styleName) {
		this(false, styleName);
	}

	public PopupMenu (boolean autoRemove, String styleName) {
		this.autoRemove = autoRemove;
		style = VisUI.skin.get(styleName, PopupMenuStyle.class);
	}

	public void addItem (MenuItem item) {
		add(item).fillX().row();
		pack();
	}

	public void addSeparator () {
		add(new Separator(true)).padTop(2).padBottom(2).fill().expand().row();
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		if (style.background != null) style.background.draw(batch, getX(), getY(), getWidth(), getHeight());
		super.draw(batch, parentAlpha);
		if (style.border != null) style.border.draw(batch, getX(), getY(), getWidth(), getHeight());
	}

	public void displayMenu (Stage stage, float x, float y) {
		setPosition(x, y - getHeight());
		if (stage.getHeight() - getY() > stage.getHeight()) setY(getY() + getHeight());
		stage.addActor(this);
	}

	public boolean isAutoRemove () {
		return autoRemove;
	}

	public void setAutoRemove (boolean autoRemove) {
		this.autoRemove = autoRemove;
	}

	private boolean contains (float x, float y) {
		return boundingRectangle.contains(x, y);
	}

	@Override
	protected void setStage (Stage stage) {
		super.setStage(stage);
		if (stage != null) stage.addListener(autoRemoveListener);
	}

	@Override
	public boolean remove () {
		if (getStage() != null) getStage().removeListener(autoRemoveListener);
		return super.remove();
	}

	@Override
	public void validate () {
		super.validate();
		boundingRectangle = new Rectangle(getX(), getY(), getWidth(), getHeight());
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
