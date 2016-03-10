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

package com.kotcrab.vis.editor.ui.toast;

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.editor.ToastModule;
import com.kotcrab.vis.editor.plugin.FailedPluginDescriptor;
import com.kotcrab.vis.editor.ui.dialog.DetailsDialog;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;

/**
 * Used to display information as toast that some plugin has failed loading.
 * @author Kotcrab
 * @see ToastModule
 */
public class LoadingPluginsFailedToast extends VisTable {
	public LoadingPluginsFailedToast (Array<FailedPluginDescriptor> failedPlugins) {
		LinkLabel label = new LinkLabel("Details");
		label.setListener(url -> getStage().addActor(new LoadingPluginsFailedDialog(failedPlugins).fadeIn()));

		add("Some plugins failed to load!").expand().fill().row();
		add(label).right();
	}

	public static class LoadingPluginsFailedDialog extends VisWindow {
		public LoadingPluginsFailedDialog (Array<FailedPluginDescriptor> failedPlugins) {
			super("Failed Plugin Details");
			TableUtils.setSpacingDefaults(this);

			addCloseButton();
			closeOnEscape();
			setModal(true);

			VisTable list = new VisTable(true);
			list.left();

			for (FailedPluginDescriptor desc : failedPlugins) {
				VisTextButton detailsButton = new VisTextButton("Details");
				detailsButton.addListener(new VisChangeListener((event, actor) -> getStage().addActor(new DetailsDialog(desc.exception).fadeIn())));

				list.add(desc.file.name()).left().expand().fill();
				list.add(detailsButton);
				list.row();
			}

			add("The following plugins couldn't be loaded!").left().row();
			add(new VisScrollPane(list)).expand().fill();

			pack();
			centerWindow();
		}
	}
}
