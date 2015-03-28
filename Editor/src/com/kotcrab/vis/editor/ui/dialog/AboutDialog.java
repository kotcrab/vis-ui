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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.Version;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class AboutDialog extends VisWindow {
	public AboutDialog () {
		super("About");

		setModal(true);
		addCloseButton();
		closeOnEscape();
		TableUtils.setSpacingDefaults(this);

		VisTable contentTable = new VisTable(false);
		contentTable.defaults().expand().left();

		contentTable.add(new VisLabel("VisEditor - game level editor")).row();
		contentTable.add(new VisLabel("Copyright 2014-2015 Pawel Pastuszak")).row();
		contentTable.add(new VisLabel("Licensed under GPLv3 license")).row();
		contentTable.add(new VisLabel("Thanks to all contributors and supporters,\nand thanks to you for using this software. <3", Align.center)).center().spaceTop(8).row();

		VisTextButton okButton;

		add(contentTable).pad(3).colspan(2).expand().fill().row();
		add(new VisLabel("VisEditor " + App.VERSION + ", VisUI " + VisUI.VERSION + ", LibGDX " + Version.VERSION));
		add(okButton = new VisTextButton("OK")).expandX().fillX().right();

		okButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				fadeOut();
			}
		});

		pack();
		setSize(getWidth(), getHeight());
		centerWindow();
	}
}
