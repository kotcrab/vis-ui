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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.editor.module.ContentTable;
import com.kotcrab.vis.editor.module.editor.QuickAccessModule;
import com.kotcrab.vis.editor.module.physicseditor.models.RigidBodyModel;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.editor.ui.tabbedpane.MainContentTab;
import com.kotcrab.vis.editor.ui.tabbedpane.TabViewMode;
import com.kotcrab.vis.editor.util.gdx.FocusUtils;

public class PhysicsEditorTab extends MainContentTab {
	private PhysicsEditorModuleContainer physicsMC;

	private QuickAccessModule quickAccess;

	private PhysicsSettingsTab settingsTab;
	private ContentTable content;

	public PhysicsEditorTab (ProjectModuleContainer projectMC) {
		quickAccess = projectMC.getEditorContainer().get(QuickAccessModule.class);

		physicsMC = new PhysicsEditorModuleContainer(projectMC, this);
		physicsMC.add(new PSettingsModule());
		physicsMC.add(new PCameraModule());
		physicsMC.add(new PRenderer());
		physicsMC.add(new PRigidBodiesScreen());
		physicsMC.add(new PModeController());
		physicsMC.init();

		settingsTab = new PhysicsSettingsTab(physicsMC.get(PRigidBodiesScreen.class), physicsMC.get(PSettingsModule.class).getSettings());

		//debug
		PRigidBodiesScreen screen = physicsMC.get(PRigidBodiesScreen.class);
		RigidBodyModel model = new RigidBodyModel();
		String path = "gfx/plane/Planes/planeBlue1.png";
		model.setRegion(projectMC.get(TextureCacheModule.class).getRegion(path), path);
		screen.switchedSelection(model);
		//debug end

		content = new ContentTable(physicsMC);

		content = new ContentTable(physicsMC);
		content.add(physicsMC.get(PModeController.class).getControllerTable()).expandX().fillX().row();
		content.add().fill().expand();
	}

	@Override
	public void render (Batch batch) {
		Color oldColor = batch.getColor().cpy();
		batch.setColor(1, 1, 1, 1);
		batch.begin();

		physicsMC.render(batch);

		batch.end();
		batch.setColor(oldColor);
	}

	@Override
	public String getTabTitle () {
		return "Physics Editor";
	}

	@Override
	public Table getContentTable () {
		return content;
	}

	@Override
	public TabViewMode getViewMode () {
		return TabViewMode.SPLIT;
	}

	@Override
	public void onShow () {
		super.onShow();
		physicsMC.onShow();
		FocusUtils.focus(content);
		quickAccess.insertTab(quickAccess.getTabs().size, settingsTab);
	}

	@Override
	public void onHide () {
		super.onHide();
		physicsMC.onHide();
		quickAccess.removeTab(settingsTab);
	}

	@Override
	public void dispose () {
		physicsMC.dispose();
		quickAccess.removeTab(settingsTab);
	}

}
