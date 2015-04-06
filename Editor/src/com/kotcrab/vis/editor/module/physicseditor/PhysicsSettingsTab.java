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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.module.physicseditor.util.Clipper.Polygonizer;
import com.kotcrab.vis.editor.ui.EnumSelectBox;
import com.kotcrab.vis.editor.util.FieldUtils;
import com.kotcrab.vis.editor.util.FloatDigitsOnlyFilter;
import com.kotcrab.vis.editor.util.TableBuilder;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

public class PhysicsSettingsTab extends Tab {
	private PhysicsEditorSettings settings;

	private VisScrollPane scrollPane;

	public PhysicsSettingsTab (PhysicsEditorSettings settings) {
		super(false, false);
		this.settings = settings;

		VisTable autoTraceTab = createAutoTracingTable();
		VisTable tracingTab = createTracingTable();
		VisTable renderTab = createRenderTable();

		VisTable sectionsTable = new VisTable();
		sectionsTable.defaults().top();
		sectionsTable.left();
		sectionsTable.add(renderTab).pad(3).padTop(0);
		sectionsTable.addSeparator(true);
		sectionsTable.add(autoTraceTab).pad(3).padTop(0);
		sectionsTable.addSeparator(true);
		sectionsTable.add(tracingTab).pad(3).padTop(0);

		VisTable contentTab = new VisTable(true);
		contentTab.setBackground(VisUI.getSkin().getDrawable("window-bg"));

		contentTab.add(new VisLabel("Physics Editor Settings", "small")).space(0).left().row();
		contentTab.addSeparator().space(0);
		contentTab.add(sectionsTable).left().fillY().expandY();

		scrollPane = new VisScrollPane(contentTab);
		scrollPane.setFlickScroll(false);
		scrollPane.setFadeScrollBars(false);

		EnumSelectBox<Polygonizer> enumSelectBox = new EnumSelectBox<>(Polygonizer.class);
		enumSelectBox.setListener(result -> System.out.println(result.toString()));
		//enumList.getSelectedEnum();
		sectionsTable.add(enumSelectBox);

	}

	private VisTable createAutoTracingTable () {
		VisTable table = new VisTable(true);
		table.defaults().left();

		table.add(new VisLabel("Autotracing")).colspan(2).row();
		table.add(new VisLabel("Hull tolerance"));
		table.add(new VisValidableTextField("2.5f")).row();
		table.add(new VisLabel("Alpha tolerance"));
		table.add(new VisValidableTextField("128")).row();
		table.add(TableBuilder.build(new VisCheckBox("Multi part detection"), new VisCheckBox("Hole detection"))).colspan(2).row();
		table.add(new VisTextButton("Autotrace")).colspan(2).row();
		return table;
	}

	private VisTable createTracingTable () {
		VisTable table = new VisTable(true);
		table.defaults().left();
		table.add("Tracing").row();
		VisSelectBox<String> polygonizerSelect = new VisSelectBox<>();
		polygonizerSelect.setItems("Bayazit", "Ewjordan");

		table.add(new VisLabel("Polygonizer"));
		table.add(polygonizerSelect).row();
		return table;
	}

	private VisTable createRenderTable () {
		VisTable table = new VisTable(true);
		table.defaults().left();
		table.add("Rendering").row();

		VisValidableTextField gridGapField = new VisValidableTextField(Validators.FLOATS);
		gridGapField.setText(String.valueOf(settings.gridGap));
		gridGapField.setTextFieldFilter(new FloatDigitsOnlyFilter());
		gridGapField.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				settings.gridGap = FieldUtils.getFloat(gridGapField, settings.gridGap);
			}
		});

		table.add(TableBuilder.build(
				createCheckBox("Draw image", () -> settings.isImageDrawn, newValue -> settings.isImageDrawn = newValue),
				createCheckBox("Draw shape", () -> settings.isShapeDrawn, newValue -> settings.isShapeDrawn = newValue),
				createCheckBox("Draw polygon", () -> settings.isPolygonDrawn, newValue -> settings.isPolygonDrawn = newValue))).row();

		table.add(TableBuilder.build(
				createCheckBox("Draw grid", () -> settings.isGridShown, newValue -> settings.isGridShown = newValue),
				createCheckBox("Snap to grid", () -> settings.isSnapToGridEnabled, newValue -> settings.isSnapToGridEnabled = newValue),
				new VisLabel("Grid gap"),
				gridGapField)).row();

		table.add(createCheckBox("Debug physics", () -> settings.isPhysicsDebugEnabled, newValue -> settings.isPhysicsDebugEnabled = newValue)).row();
		return table;
	}

	@Override
	public String getTabTitle () {
		return "Physics Editor Settings";
	}

	@Override
	public Table getContentTable () {
		Table table = new Table();
		table.add(scrollPane).expand().fill();
		return table;
	}

	private VisCheckBox createCheckBox (String name, BoolPropertyGetter getter, BoolPropertySetter setter) {
		VisCheckBox checkBox = new VisCheckBox(name);
		checkBox.setChecked(getter.get());
		checkBox.addListener(new CheckBoxListener(setter));
		return checkBox;
	}

	private static class CheckBoxListener extends ChangeListener {
		private BoolPropertySetter setter;

		public CheckBoxListener (BoolPropertySetter setter) {
			this.setter = setter;
		}

		@Override
		public void changed (ChangeEvent event, Actor actor) {
			setter.set(((VisCheckBox) actor).isChecked());
		}
	}

	private interface BoolPropertyGetter {
		boolean get ();
	}

	private interface BoolPropertySetter {
		void set (boolean newValue);
	}
}
