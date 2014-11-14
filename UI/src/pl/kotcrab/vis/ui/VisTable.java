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

package pl.kotcrab.vis.ui;

import pl.kotcrab.vis.ui.widget.Separator;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

/** VisTable is normal scene2d.ui {@link Table} that allows to set default vis spacing during creation
 * @author Pawel Pastuszak */
public class VisTable extends Table {

	public VisTable () {
		super(VisUI.skin);
	}

	public VisTable (boolean setVisDefautls) {
		super(VisUI.skin);
		if (setVisDefautls) TableUtils.setSpaceDefaults(this);
	}

	public void addSeparator () {
		add(new Separator()).padTop(2).padBottom(2).fill().expand().row();
	}

}
