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

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.scene.FailedAssetDescriptor;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class LoadingAssetsFailedDialog extends VisWindow {

	public LoadingAssetsFailedDialog (ImmutableArray<FailedAssetDescriptor> descriptors) {
		super("Failed Assets Details");
		TableUtils.setSpacingDefaults(this);

		addCloseButton();
		closeOnEscape();
		setModal(true);

		VisTable list = new VisTable(true);
		list.defaults().top();
		list.top().left();

		Array<VisAssetDescriptor> addedDescriptors = new Array<>();
		for (FailedAssetDescriptor desc : descriptors) {
			boolean alreadyAdded = false;
			for (VisAssetDescriptor addedDesc : addedDescriptors) {
				if (addedDesc.compare(desc.asset)) {
					alreadyAdded = true;
					break;
				}
			}
			if (alreadyAdded) continue;
			addedDescriptors.add(desc.asset);

			VisTextButton detailsButton = new VisTextButton("Details");
			detailsButton.addListener(new VisChangeListener((event, actor) -> getStage().addActor(new DetailsDialog(desc.throwable).fadeIn())));

			list.add(desc.asset.toString()).left().expandX().fillX();
			list.add(detailsButton).padRight(5);
			list.row();
		}

		add("Following assets couldn't be loaded:").left().row();

		VisScrollPane scrollPane = new VisScrollPane(list);
		scrollPane.setOverscroll(false, true);
		scrollPane.setFadeScrollBars(false);
		add(scrollPane).top().size(500, 300);

		pack();
		centerWindow();
	}
}
