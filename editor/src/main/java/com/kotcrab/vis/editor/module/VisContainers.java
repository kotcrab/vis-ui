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

package com.kotcrab.vis.editor.module;

import com.kotcrab.vis.editor.module.editor.*;
import com.kotcrab.vis.editor.module.editor.PluginLoaderModule.PluginSettingsModule;
import com.kotcrab.vis.editor.module.project.*;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetsUIModule;
import com.kotcrab.vis.editor.module.scene.*;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityManipulatorModule;
import com.kotcrab.vis.editor.module.scene.system.render.GridRendererSystem.GridSettingsModule;
import com.kotcrab.vis.editor.plugin.api.ContainerExtension.ExtensionScope;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;

/** @author Kotcrab */
public class VisContainers {
	public static void createEditorModules (EditorModuleContainer editorMC, TabbedPaneListener tabsModuleListener, TabbedPaneListener quickAccessModuleListener) {
		editorMC.add(new InputModule());
		editorMC.add(new GlobalInputModule());

		editorMC.add(new ToastModule());
		editorMC.add(new EditorSettingsIOModule());

		editorMC.add(new PluginSettingsModule());
		editorMC.add(new PluginLoaderModule());

		editorMC.add(new DefaultExtensionRegistrarModule());
		editorMC.add(new KotlinExtensionRegistrarModule());
		editorMC.add(new ExtensionStorageModule());

		editorMC.add(new ClonerModule());
		editorMC.add(new GsonModule());
		editorMC.add(MouseLoopingModule.newInstance());
		editorMC.add(new StyleProviderModule());
		editorMC.add(new AppFileAccessModule());
		editorMC.add(new VisTwitterReader());
		editorMC.add(new EventBusExceptionMonitorModule());
		editorMC.add(new RecentProjectModule());
		editorMC.add(new PluginFilesAccessModule());
		editorMC.add(new ColorPickerModule());
		editorMC.add(new UpdateCheckerModule());
		editorMC.add(new DonateReminderModule());
		editorMC.add(new DisableableDialogsModule());
		editorMC.add(new TabsModule(tabsModuleListener));
		editorMC.add(new FileChooserModule());
		editorMC.add(new MenuBarModule());
		editorMC.add(new ToolbarModule());
		editorMC.add(new QuickAccessModule(quickAccessModuleListener));
		editorMC.add(new StatusBarModule());
		editorMC.add(new DebugFeaturesControllerModule());
		editorMC.add(new ProjectIOModule());
		editorMC.add(new EditingSettingsModule());
		editorMC.add(new EmptyMenuFillerModule());

		editorMC.add(new GeneralSettingsModule());
		editorMC.add(new ColorSettingsModule());
		editorMC.add(new ExperimentalSettingsModule());
		editorMC.add(new GridSettingsModule());

		editorMC.add(new ProjectAutoLoader());
		editorMC.add(new DevelopmentSpeedupModule());
	}

	public static void createProjectModules (ProjectModuleContainer projectMC, ExtensionStorageModule extensionStorage) {
		projectMC.add(new FileAccessModule());
		projectMC.add(new AssetsWatcherModule());
		projectMC.add(new AssetsMetadataModule());
		projectMC.add(new TextureCacheModule());
		projectMC.add(new FontCacheModule());
		projectMC.add(new ParticleCacheModule());
		projectMC.add(new SceneCacheModule());
		projectMC.add(new ShaderCacheModule());
		projectMC.add(new ProjectVersionModule());
		projectMC.add(new SceneIOModule());
		projectMC.add(new ProjectSettingsIOModule());
		projectMC.add(new ProjectExtensionStorageModule());
		projectMC.add(new SceneMetadataModule());
		projectMC.add(new AssetsAnalyzerModule());
		projectMC.add(new TextureNameCheckerModule());

		projectMC.add(new ExportersManagerModule());
		projectMC.add(new ExportSettingsModule());

		projectMC.add(new SceneTabsModule());
		projectMC.add(new AssetsUIModule());
		projectMC.addAll(extensionStorage.getContainersExtensions(ProjectModule.class, ExtensionScope.PROJECT));
	}

	public static void createSceneModules (SceneModuleContainer sceneMC, ExtensionStorageModule extensionStorage) {
		sceneMC.add(new CameraModule());
		sceneMC.add(new RendererModule());
		sceneMC.add(new UndoModule());
		sceneMC.add(new EntityManipulatorModule());
		sceneMC.add(new SceneAccessModule());
		sceneMC.add(new AssetsLoadingMonitorModule());
		sceneMC.addAll(extensionStorage.getContainersExtensions(SceneModule.class, ExtensionScope.SCENE));
	}
}
