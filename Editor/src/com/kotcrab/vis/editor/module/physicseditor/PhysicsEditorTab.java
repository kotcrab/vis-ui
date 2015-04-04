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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.editor.module.ContentTable;
import com.kotcrab.vis.editor.module.editor.QuickAccessModule;
import com.kotcrab.vis.editor.module.physicseditor.models.RigidBodyModel;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.editor.ui.tabbedpane.MainContentTab;
import com.kotcrab.vis.editor.ui.tabbedpane.TabViewMode;
import com.kotcrab.vis.editor.util.EventStopper;
import com.kotcrab.vis.editor.util.FocusUtils;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class PhysicsEditorTab extends MainContentTab {
	private PhysicsEditorModuleContainer physicsMC;

	private ContentTable content;

	public PhysicsEditorTab (ProjectModuleContainer projectMC) {
		physicsMC = new PhysicsEditorModuleContainer(projectMC, this);
		physicsMC.add(new PSettingsModule());
		physicsMC.add(new PCameraModule());
		physicsMC.add(new PRenderer());
		physicsMC.add(new PRigidBodiesScreen());
		physicsMC.init();

		QuickAccessModule quickAccess = projectMC.getEditorContainer().get(QuickAccessModule.class);
		quickAccess.addTab(new PhysicsSettingsTab());

		//debug
		PRigidBodiesScreen screen = physicsMC.get(PRigidBodiesScreen.class);
		RigidBodyModel model = new RigidBodyModel();
		String path = "gfx/plane/Planes/planeBlue1.png";
		model.setRegion(projectMC.get(TextureCacheModule.class).getRegion(path), path);
		screen.switchedSelection(model);
		//debug end

		content = new ContentTable(physicsMC);

		VisTable table = new VisTable(true);
		table.setTouchable(Touchable.enabled);
		table.setBackground(VisUI.getSkin().getDrawable("window-bg"));
		table.left();
		table.padTop(1);
		table.add(new VisLabel("Mode: "), new VisTextButton("Create"), new VisTextButton("Edit"), new VisTextButton("Test"));
		table.addListener(new EventStopper());

		content = new ContentTable(physicsMC);
		content.add(table).expandX().fillX().row();
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
	}

	@Override
	public void onHide () {
		super.onHide();
		physicsMC.onHide();
	}

	@Override
	public void dispose () {
		physicsMC.dispose();
	}

}
