/**
 * Copyright 2014 Pawel Pastuszak
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

package pl.kotcrab.vis.editor.ui;

import pl.kotcrab.vis.editor.ui.components.EmptyWidget;
import pl.kotcrab.vis.editor.ui.components.VisCheckBox;
import pl.kotcrab.vis.editor.ui.components.VisTextButton;
import pl.kotcrab.vis.editor.ui.components.VisTextField;
import pl.kotcrab.vis.editor.ui.components.VisWindow;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class NewProjectDialog extends VisWindow {

	public NewProjectDialog (Stage parent, Skin skin) {
		super(parent, "New Project", skin);
		setModal(true);
		
		VisTextField projectRoot = new VisTextField("", skin);
		TextButton chooseButton = new VisTextButton("Choose...", skin);
		VisTextField sourceLoc = new VisTextField("/core/src", skin);
		VisTextField assetsLoc = new VisTextField("/android/assets", skin);

		VisCheckBox signFiles = new VisCheckBox(" Sign files using private key", skin);

		TableUtils.setSpaceDefaults(this);

		columnDefaults(0).left();
		columnDefaults(1).width(300);

		add(new EmptyWidget(10, 3)).space(0).row();
		add(new Label("Project root:", skin));
		add(projectRoot);
		add(chooseButton);
		row();

		add(new Label("Source folder:", skin));
		add(sourceLoc).fill();
		row();

		add(new Label("Assets folder:", skin));
		add(assetsLoc).fill();
		row();
		
		add(signFiles).colspan(2);
		row();


		Table buttonTable = new Table();
		TableUtils.setSpaceDefaults(buttonTable);
		buttonTable.defaults().minWidth(70);
		
		TextButton cancelButton = new VisTextButton("Cancel", skin);
		TextButton createButton = new VisTextButton("Create", skin);

		buttonTable.add(cancelButton);
		buttonTable.add(createButton);

		add(buttonTable).colspan(3).right();
		
		//TableUtils.setColumnsDefaults(this);
		pack();
		setPositionToCenter();
	}
}
