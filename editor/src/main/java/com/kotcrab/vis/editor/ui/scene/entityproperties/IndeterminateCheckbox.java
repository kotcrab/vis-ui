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

package com.kotcrab.vis.editor.ui.scene.entityproperties;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.ui.widget.VisCheckBox;

/**
 * Intermediate checkbox with undefined state. TODO: to be merged into VisUI when next LibGDX (1.6.3?) version is released.
 * @author Kotcrab
 */
public class IndeterminateCheckbox extends VisCheckBox {
	private static final Drawable INDETERMINATE = Assets.getMisc("check-indeterminate");
	private static final Drawable INDETERMINATE_OVER = Assets.getMisc("check-over-indeterminate");

	private boolean indeterminate;

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
		setProgrammaticChangeEvents(false);
	}

	public boolean isIndeterminate () {
		return indeterminate;
	}

	public void setIndeterminate (boolean indeterminate) {
		this.indeterminate = indeterminate;
	}

	@Override
	protected Drawable getCheckboxTickImage () {
		if (isIndeterminate()) {
			if (isOver())
				return INDETERMINATE_OVER;
			else
				return INDETERMINATE;
		}

		return super.getCheckboxTickImage();
	}

	@Override
	public void setChecked (boolean isChecked) {
		indeterminate = false;
		super.setChecked(isChecked);
	}
}
