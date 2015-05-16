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

package com.kotcrab.vis.editor.module.physicseditor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.module.physicseditor.util.Clipper.Polygonizer;
import com.kotcrab.vis.editor.ui.EnumSelectBox;
import com.kotcrab.vis.editor.util.VisChangeListener;
import com.kotcrab.vis.editor.util.gdx.FieldUtils;
import com.kotcrab.vis.editor.util.gdx.FloatDigitsOnlyFilter;
import com.kotcrab.vis.editor.util.gdx.IntDigitsOnlyFilter;
import com.kotcrab.vis.editor.util.gdx.TableBuilder;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;
import com.kotcrab.vis.ui.util.Validators.LesserThanValidator;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

public class PhysicsSettingsTab extends Tab {
	private PRigidBodiesScreen screen;
	private PhysicsEditorSettings settings;

	private VisScrollPane scrollPane;

	public PhysicsSettingsTab (PRigidBodiesScreen screen, PhysicsEditorSettings settings) {
		super(false, false);
		this.screen = screen;
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
	}

	private VisTable createAutoTracingTable () {
		VisTable table = new VisTable(true);
		table.defaults().left();

		VisTextButton autoTraceButton = new VisTextButton("Autotrace");
		autoTraceButton.addListener(new VisChangeListener((event, actor) -> screen.autoTrace()));

		VisValidableTextField hullToleranceField = new VisValidableTextField(Validators.INTEGERS, new GreaterThanValidator(100, true), new LesserThanValidator(400, true));
		hullToleranceField.setRestoreLastValid(true);
		hullToleranceField.setText(String.valueOf((int) settings.autoTraceHullTolerance * 100));
		hullToleranceField.setTextFieldFilter(new IntDigitsOnlyFilter());
		hullToleranceField.addListener(new VisChangeListener(
				(event, actor) -> settings.autoTraceHullTolerance = FieldUtils.getFloat(hullToleranceField, settings.autoTraceHullTolerance)));

		VisValidableTextField alphaToleranceField = new VisValidableTextField(Validators.INTEGERS, new LesserThanValidator(255, true), new GreaterThanValidator(0, true));
		alphaToleranceField.setRestoreLastValid(true);
		alphaToleranceField.setText(String.valueOf(settings.autoTraceAlphaTolerance));
		alphaToleranceField.setTextFieldFilter(new IntDigitsOnlyFilter());
		alphaToleranceField.addListener(new VisChangeListener(
				(event, actor) -> settings.autoTraceAlphaTolerance = FieldUtils.getInt(alphaToleranceField, settings.autoTraceAlphaTolerance)));

		table.add(new VisLabel("Autotracing")).colspan(2).row();
		table.add(new VisLabel("Hull tolerance"));
		table.add(hullToleranceField).row();
		table.add(new VisLabel("Alpha tolerance"));
		table.add(alphaToleranceField).row();
		table.add(TableBuilder.build(
				createCheckBox("Multi part detection", () -> settings.autoTraceMultiPartDetection, newValue -> settings.autoTraceMultiPartDetection = newValue),
				createCheckBox("Hole detection", () -> settings.autoTraceHoleDetection, newValue -> settings.autoTraceHoleDetection = newValue))).colspan(2).row();

		table.add(autoTraceButton).colspan(2).row();
		return table;
	}

	private VisTable createTracingTable () {
		VisTable table = new VisTable(true);
		table.defaults().left();
		table.add("Tracing").row();

		EnumSelectBox<Polygonizer> enumSelectBox = new EnumSelectBox<>(Polygonizer.class);
		enumSelectBox.setSelectedEnum(settings.polygonizer);
		enumSelectBox.setListener(result -> settings.polygonizer = result);

		VisTextButton retraceButton = new VisTextButton("Retrace");
		retraceButton.addListener(new VisChangeListener((event, actor) -> screen.recomputePhysics()));

		table.add(new VisLabel("Polygonizer"));
		table.add(enumSelectBox).row();
		table.add(retraceButton);
		return table;
	}

	private VisTable createRenderTable () {
		VisTable table = new VisTable(true);
		table.defaults().left();
		table.add("Rendering").row();

		VisValidableTextField gridGapField = new VisValidableTextField(Validators.FLOATS);
		gridGapField.setRestoreLastValid(true);
		gridGapField.setText(String.valueOf(settings.gridGap));
		gridGapField.setTextFieldFilter(new FloatDigitsOnlyFilter());
		gridGapField.addListener(new VisChangeListener((event, actor) -> settings.gridGap = FieldUtils.getFloat(gridGapField, settings.gridGap)));

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
