/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.ui.widget.VisCheckBox;

public class IndeterminateCheckbox extends VisCheckBox {
	private static final Drawable INDETERMINATE = Assets.getMisc("check-indeterminate");
	private static final Drawable INDETERMINATE_OVER = Assets.getMisc("check-over-indeterminate");

	private boolean indeterminate;
	private boolean allowEvent;

	public IndeterminateCheckbox (String text) {
		super(text);
		init();
	}

	public IndeterminateCheckbox (String text, boolean checked) {
		super(text, checked);
		init();
	}

	public IndeterminateCheckbox (String text, String styleName) {
		super(text, styleName);
		init();
	}

	public IndeterminateCheckbox (String text, VisCheckBoxStyle style) {
		super(text, style);
		init();
	}

	private void init () {
		//smuggling our listener as first so we can detect and disable programmatic change listeners
		getListeners().insert(0, new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				allowEvent = true;
			}
		});

		addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (allowEvent == false)
					event.stop();

				allowEvent = false;
			}
		});
	}

	public boolean isIndeterminate () {
		return indeterminate;
	}

	public void setIndeterminate (boolean indeterminate) {
		this.indeterminate = indeterminate;
	}

	@Override
	protected Drawable getCheckboxImage () {
		if (isIndeterminate()) {
			if (isOver())
				return INDETERMINATE_OVER;
			else
				return INDETERMINATE;
		}

		return super.getCheckboxImage();
	}

	@Override
	public void setChecked (boolean isChecked) {
		indeterminate = false;
		super.setChecked(isChecked);
	}
}
