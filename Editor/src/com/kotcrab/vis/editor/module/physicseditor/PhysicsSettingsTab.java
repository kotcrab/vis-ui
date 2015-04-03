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

package com.kotcrab.vis.editor.module.physicseditor;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.editor.util.TableBuilder;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

public class PhysicsSettingsTab extends Tab {
	private VisTable contentTab;

	public PhysicsSettingsTab () {
		super(false, false);

		VisTable autoTraceTab = new VisTable(true);
		autoTraceTab.defaults().left();
		autoTraceTab.add("Autotrace").row();
		VisSelectBox<String> polygonizerSelect = new VisSelectBox<>();
		polygonizerSelect.setItems("Bayazit", "Ewjordan");

		autoTraceTab.add(new VisLabel("Polygnozier"));
		autoTraceTab.add(polygonizerSelect).row();
		autoTraceTab.add(new VisLabel("Hull tolerance"));
		autoTraceTab.add(new VisValidableTextField("2.5f")).row();
		autoTraceTab.add(new VisLabel("Alpha tolerance"));
		autoTraceTab.add(new VisValidableTextField("128")).row();
		autoTraceTab.add(TableBuilder.build(new VisCheckBox("Multi part detection"), new VisCheckBox("Hole detection"))).colspan(2).row();

		VisTable renderTab = new VisTable(true);
		renderTab.defaults().left();
		renderTab.add("Rendering").row();
		renderTab.add(TableBuilder.build(new VisCheckBox("Draw image"), new VisCheckBox("Draw shape"), new VisCheckBox("Draw polygon"))).row();
		renderTab.add(TableBuilder.build(new VisCheckBox("Draw grid"), new VisCheckBox("Snap to grid"), new VisLabel("Grid gap"), new VisValidableTextField("0.10f"))).row();
		renderTab.add(new VisCheckBox("Debug physics")).row();

		VisTable sectionsTable = new VisTable();
		sectionsTable.defaults().top();
		sectionsTable.left();
		sectionsTable.add(renderTab).pad(3).padTop(0);
		sectionsTable.addSeparator(true);
		sectionsTable.add(autoTraceTab).pad(3).padTop(0);

		contentTab = new VisTable(true);
		contentTab.setBackground(VisUI.getSkin().getDrawable("window-bg"));

		contentTab.add(new VisLabel("Physics Editor Settings", "small")).space(0).left().row();
		contentTab.addSeparator().space(0);
		contentTab.add(sectionsTable).left().fillY().expandY();
	}

	@Override
	public String getTabTitle () {
		return "Physics Editor Settings";
	}

	@Override
	public Table getContentTable () {
		return contentTab;
	}
}
