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

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class NewProjectDialog extends VisWindow {

	public NewProjectDialog (Stage parent, Skin skin) {
		super(parent, "New Project", skin);
		setModal(true);
		System.out.println(getParent());

		TextField projectRoot = new TextField("", skin);
		TextButton chooseButton = new TextButton("Choose..", skin);
		TextField sourceLoc = new TextField("/core/src", skin);
		TextField assetsLoc = new TextField("/android/assets", skin);

		CheckBox signFiles = new CheckBox(" Sign files using private key", skin);

		TableUtils.setSpaceDefaults(this);

		columnDefaults(0).left();
		columnDefaults(1).width(300);

		add(new Label("Project root:", skin)).spaceTop(6);
		add(projectRoot);
		add(chooseButton);
		row();

		add(new Label("Source folder:", skin));
		add(sourceLoc).fill();
		row();

		add(new Label("Assets folder:", skin));
		add(assetsLoc).fill();
		row();

		add(signFiles).colspan(2).spaceBottom(0);
		row();

		TableUtils.setColumnsDefaults(this);

		Table buttonTable = new Table();
		TableUtils.setSpaceDefaults(buttonTable);

		TextButton cancelButton = new TextButton("Cancel", skin);
		TextButton createButton = new TextButton("Create", skin);

		buttonTable.add(cancelButton).minWidth(70);
		buttonTable.add(createButton).minWidth(70);

		add(buttonTable).colspan(3).right();

		pack();
		setPositionToCenter();
	}
}
