/*
 * Copyright 2014-2017 See AUTHORS file.
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

package com.kotcrab.vis.ui.test.manual;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

public class TestVertical extends VisWindow {

	public TestVertical () {
		super("vertical");

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		if (TestApplication.USE_VIS_WIDGETS)
			addVisWidgets();
		else
			addNormalWidgets();

		setSize(100, 200);
		setPosition(1154, 20);
	}

	private void addNormalWidgets () {
		ProgressBar progressbar = new ProgressBar(0, 100, 1, true, VisUI.getSkin());
		Slider slider = new Slider(0, 100, 1, true, VisUI.getSkin());
		Slider sliderDisabled = new Slider(0, 100, 1, true, VisUI.getSkin());

		progressbar.setValue(50);
		slider.setValue(50);
		sliderDisabled.setValue(50);
		sliderDisabled.setDisabled(true);

		VisTable progressbarTable = new VisTable(true);
		progressbarTable.add(progressbar);
		progressbarTable.add(slider);
		progressbarTable.add(sliderDisabled);

		add(progressbarTable);
	}

	private void addVisWidgets () {
		VisProgressBar progressbar = new VisProgressBar(0, 100, 1, true);
		VisSlider slider = new VisSlider(0, 100, 1, true);
		VisSlider sliderDisabled = new VisSlider(0, 100, 1, true);

		progressbar.setValue(50);
		slider.setValue(50);
		sliderDisabled.setValue(50);
		sliderDisabled.setDisabled(true);

		VisTable progressbarTable = new VisTable(true);
		progressbarTable.add(progressbar);
		progressbarTable.add(slider);
		progressbarTable.add(sliderDisabled);

		add(progressbarTable);
	}
}
