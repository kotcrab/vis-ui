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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.editor.module.ContentTable;
import com.kotcrab.vis.editor.module.editor.PluginContainerModule;
import com.kotcrab.vis.editor.module.editor.QuickAccessModule;
import com.kotcrab.vis.editor.module.physicseditor.models.RigidBodyModel;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.editor.plugin.ContainerExtension.ExtensionScope;
import com.kotcrab.vis.editor.ui.tabbedpane.MainContentTab;
import com.kotcrab.vis.editor.ui.tabbedpane.TabViewMode;
import com.kotcrab.vis.editor.util.gdx.FocusUtils;

public class PhysicsEditorTab extends MainContentTab {
	private PluginContainerModule pluginContainer;
	private QuickAccessModule quickAccess;

	private PhysicsEditorModuleContainer physicsMC;

	private PhysicsSettingsTab settingsTab;
	private ContentTable content;

	public PhysicsEditorTab (ProjectModuleContainer projectMC) {
		pluginContainer = projectMC.getEditorContainer().get(PluginContainerModule.class);
		quickAccess = projectMC.getEditorContainer().get(QuickAccessModule.class);

		physicsMC = new PhysicsEditorModuleContainer(projectMC, this);
		physicsMC.add(new PSettingsModule());
		physicsMC.add(new PCameraModule());
		physicsMC.add(new PRenderer());
		physicsMC.add(new PRigidBodiesScreen());
		physicsMC.add(new PModeController());
		physicsMC.addAll(pluginContainer.getContainersExtensions(PhysicsEditorModule.class, ExtensionScope.PHYSICS_EDITOR));

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
