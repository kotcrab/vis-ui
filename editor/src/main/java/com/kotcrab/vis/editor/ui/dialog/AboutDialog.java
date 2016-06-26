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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.Version;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;

/**
 * VisEditor about dialog
 * @author Kotcrab
 */
public class AboutDialog extends VisWindow {
	public AboutDialog () {
		super("About");

		setModal(true);
		addCloseButton();
		closeOnEscape();
		TableUtils.setSpacingDefaults(this);

		VisTable contentTable = new VisTable(false);
		contentTable.defaults().expand().left();

		contentTable.add(new VisLabel("VisEditor - game level editor\nCopyright 2014-2016 Paweł Pastuszak\nLicensed under Apache2 license")).spaceBottom(8).row();
		contentTable.add(new VisLabel("Farseer Physics Engine - polygon decomposition algorithms")).spaceBottom(8).row();
		contentTable.add(new VisLabel("Thanks to all contributors and supporters,\nand thanks to you for using this software. <3", Align.center)).center().row();

		VisTextButton okButton;
		VisTextButton openAppDirectoryButton;

		add(contentTable).pad(3).colspan(3).expand().fill().row();
		VisLabel versionLabel = new VisLabel("Hover here to see\nlibraries versions", Align.center);
		new Tooltip.Builder("VisEditor " + App.VERSION + " \nBuild " + App.getBuildTimestamp() + "\nVisUI " + VisUI.VERSION + "\nLibGDX " + Version.VERSION, Align.left).target(versionLabel).build();
		add(versionLabel).expandX().fillX();
		add(openAppDirectoryButton = new VisTextButton("Open App Data Folder"));
		add(okButton = new VisTextButton("OK")).right();

		openAppDirectoryButton.addListener(new VisChangeListener((event, actor) -> FileUtils.browse(FileUtils.toFileHandle(App.APP_FOLDER_PATH))));
		okButton.addListener(new VisChangeListener((event, actor) -> fadeOut()));
		pack();
		centerWindow();
	}
}
