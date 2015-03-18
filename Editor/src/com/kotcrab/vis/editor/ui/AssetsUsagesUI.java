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

package com.kotcrab.vis.editor.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.editor.module.project.AssetsUsages;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisSplitPane;

public class AssetsUsagesUI  {

	public AssetsUsagesUI (Table target, AssetsUsages analyze) {
		VisTable mainContentTable = new VisTable();
		mainContentTable.setBackground(VisUI.getSkin().getDrawable("window-bg"));

		VisSplitPane mainSplitPane = new OverlaySplitPane(mainContentTable);
		mainSplitPane.setSplitAmount(0.7f);

		target.clear();
		target.add(mainSplitPane).expand().fill();
	}
}
