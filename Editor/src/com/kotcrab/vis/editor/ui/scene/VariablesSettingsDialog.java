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

package com.kotcrab.vis.editor.ui.scene;

import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.StringStringMapView;
import com.kotcrab.vis.runtime.component.Variables;
import com.kotcrab.vis.ui.widget.VisScrollPane;

/** @author Kotcrab */
public class VariablesSettingsDialog extends AbstractSceneSettingsDialog {
	private Variables workingCopy;
	private StringStringMapView variablesMapView;

	public VariablesSettingsDialog (SceneTab sceneTab) {
		super("Scene Variables", sceneTab);

		setResizable(true);
		setSize(270, 350);
		centerWindow();
	}

	@Override
	protected void createUI () {
		workingCopy = new Variables(scene.variables);

		variablesMapView = new StringStringMapView("No variables set (press enter in field\nto create variable)", null);
		variablesMapView.setMap(workingCopy);

		VisScrollPane scrollPane = new VisScrollPane(variablesMapView);
		scrollPane.setOverscroll(false, true);
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setFadeScrollBars(false);

		top();
		defaults().top();

		add(scrollPane).growX();
		row();
		add().grow();
		row();
		add(getButtonTable()).growX();

		padBottom(5);
	}

	@Override
	protected void setValuesToScene () {
		variablesMapView.updateMapFromUI();
		scene.variables.setFrom(workingCopy);
	}
}
