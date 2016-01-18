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

import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.plugin.PluginDescriptor;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Dialog used to display plugin details such as name, version etc.
 * @author Kotcrab
 */
public class PluginDetailsDialog extends VisWindow {
	public PluginDetailsDialog (PluginDescriptor descriptor) {
		super("Plugin Details");

		setModal(true);
		addCloseButton();
		closeOnEscape();
		TableUtils.setSpacingDefaults(this);

		VisTable contentTable = new VisTable(false);
		contentTable.defaults().spaceBottom(2).expand().left();

		contentTable.add(new VisLabel("Name: " + descriptor.name + " (" + descriptor.id + ")")).row();
		contentTable.add(new VisLabel("Description: " + descriptor.description)).row();
		contentTable.add(new VisLabel("Provider: " + descriptor.provider)).row();
		contentTable.add(new VisLabel("Version: " + descriptor.version)).row();
		contentTable.add(new VisLabel("Compatibility: " + descriptor.compatibility + " " +
				(descriptor.compatibility != App.PLUGIN_COMPATIBILITY_CODE ? "(doesn't matches editor!)" : "(matches editor)"))).row();

		VisTextButton okButton;

		add(contentTable).pad(3).expand().fill().row();
		add(okButton = new VisTextButton("OK")).right();

		okButton.addListener(new VisChangeListener((event, actor) -> fadeOut()));
		pack();
		setSize(getWidth(), getHeight());
		centerWindow();
	}
}
