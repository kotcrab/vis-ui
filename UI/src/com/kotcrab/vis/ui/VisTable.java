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

package com.kotcrab.vis.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.Separator;

public class VisTable extends Table {
	public VisTable () {
		super(VisUI.getSkin());
	}

	/** @param setVisDefaults if true default vis spacing defaults will be set */
	public VisTable (boolean setVisDefaults) {
		super(VisUI.getSkin());
		if (setVisDefaults) TableUtils.setSpaceDefaults(this);
	}

	/** Adds {@link Separator} widget to table with padding top, bottom 2px with fill and expand properties and inserts new row
	 * after separator (not before!) */
	public Cell<Separator> addSeparator () {
		Cell<Separator> cell = add(new Separator()).padTop(2).padBottom(2).fillX().expandX();
		row();
		return cell;
	}
}
