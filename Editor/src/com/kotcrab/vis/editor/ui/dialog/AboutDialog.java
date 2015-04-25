/*
 * Copyright 2014-2015 See AUTHORS file.
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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.Version;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
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

		contentTable.add(new VisLabel("VisEditor - game level editor\nCopyright 2014-2015 Paweł Pastuszak\nLicensed under Apache2 license")).spaceBottom(8).row();
		contentTable.add(new VisLabel("Physics editor based on Aurelien Ribon's Physics Body Editor\n(licensed under Apache2 license)")).spaceBottom(8).row();
		contentTable.add(new VisLabel("Thanks to all contributors and supporters,\nand thanks to you for using this software. <3", Align.center)).center().row();

		VisTextButton okButton;

		add(contentTable).pad(3).colspan(2).expand().fill().row();
		add(new VisLabel("VisEditor " + App.VERSION + ", VisUI " + VisUI.VERSION + ", LibGDX " + Version.VERSION)).expandX().fillX();
		add(okButton = new VisTextButton("OK")).right();

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
