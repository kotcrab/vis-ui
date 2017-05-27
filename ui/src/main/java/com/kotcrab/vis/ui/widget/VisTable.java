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

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;

/**
 * Extends functionality of standard {@link Table}, supports setting default VisUI spacing and has utilities method for adding
 * separators. Compatible with {@link Table}.
 * @author Kotcrab
 * @see Table
 */
public class VisTable extends Table {
	public VisTable () {
		super(VisUI.getSkin());
	}

	/** @param setVisDefaults if true default vis spacing defaults will be set */
	public VisTable (boolean setVisDefaults) {
		super(VisUI.getSkin());
		if (setVisDefaults) TableUtils.setSpacingDefaults(this);
	}

	/**
	 * Adds vertical or horizontal {@link Separator} widget to table with padding top, bottom 2px with fill and expand properties.
	 * If vertical == false then inserts new row after separator (not before!)
	 */
	public Cell<Separator> addSeparator (boolean vertical) {
		Cell<Separator> cell = add(new Separator()).padTop(2).padBottom(2);

		if (vertical)
			cell.fillY().expandY();
		else {
			cell.fillX().expandX();
			row();
		}

		return cell;
	}

	/**
	 * Adds horizontal {@link Separator} widget to table with padding top, bottom 2px with fillX and expandX properties and inserts new row
	 * after separator (not before!)
	 */
	public Cell<Separator> addSeparator () {
		return addSeparator(false);
	}
}
