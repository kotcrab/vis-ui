/*******************************************************************************
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
 ******************************************************************************/

package pl.kotcrab.vis.ui;

import pl.kotcrab.vis.ui.widget.Separator;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class VisTable extends Table {

	public VisTable () {
		super(VisUI.skin);
	}

	/** @param setVisDefautls if true default vis spacing defaults will be set */
	public VisTable (boolean setVisDefautls) {
		super(VisUI.skin);
		if (setVisDefautls) TableUtils.setSpaceDefaults(this);
	}

	/** Adds {@link Separator} widget to table with padding top, bottom 2px with fill and expand properties and inserts new row
	 * after separator (not before!) */
	public Cell<Separator> addSeparator () {
		Cell<Separator> cell = add(new Separator()).padTop(2).padBottom(2).fill().expand();
		row();
		return cell; 
	}

}
