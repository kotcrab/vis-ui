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

package com.kotcrab.vis.editor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.editor.event.ProjectStatusEvent;
import com.kotcrab.vis.editor.event.ProjectStatusEvent.Status;
import com.kotcrab.vis.editor.module.editor.*;
import com.kotcrab.vis.editor.module.editor.PluginLoaderModule.PluginSettingsModule;
import com.kotcrab.vis.editor.module.project.*;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetsUIModule;
import com.kotcrab.vis.editor.module.scene.GridRendererSystem.GridSettingsModule;
import com.kotcrab.vis.editor.plugin.ContainerExtension.ExtensionScope;
import com.kotcrab.vis.editor.ui.EditorFrame;
import com.kotcrab.vis.editor.ui.WindowListener;
import com.kotcrab.vis.editor.ui.dialog.AsyncTaskProgressDialog;
import com.kotcrab.vis.editor.ui.dialog.NewProjectDialog;
import com.kotcrab.vis.editor.ui.dialog.SettingsDialog;
import com.kotcrab.vis.editor.ui.dialog.UnsavedResourcesDialog;
import com.kotcrab.vis.editor.ui.tabbedpane.MainContentTab;
import com.kotcrab.vis.editor.ui.tabbedpane.TabViewMode;
import com.kotcrab.vis.editor.util.AsyncTask;
import com.kotcrab.vis.editor.util.ThreadUtils;
import com.kotcrab.vis.editor.util.gdx.VisGroup;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils.OptionDialog;
import com.kotcrab.vis.ui.util.dialog.DialogUtils.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;

import java.lang.reflect.Field;

/**
 * VisEditor main ApplicationAdapter class. The main() method is located in {@link EditorFrame}
 * @author Kotcrab
 */
public class Editor extends ApplicationAdapter {
	public static Editor instance;

	private EditorFrame frame;

	private Stage stage;
	private VisGroup stageRoot;
	private Table uiRoot;

	private EditorModuleContainer editorMC;
	private ProjectModuleContainer projectMC;

	private InputModule inputModule;
	private TabsModule tabsModule;
	private StatusBarModule statusBar;
	private ProjectIOModule projectIO;
	private FileChooserModule fileChooser;
	private ExtensionStorageModule pluginContainer;

	private GeneralSettingsModule settings;
	private ExperimentalSettingsModule experimentalSettings;

	// TODO move to module
	private Table mainContentTable;
	private Table tabContentTable;
	private VisTable quickAccessContentTable;
	private VisSplitPane splitPane;

	private SettingsDialog settingsDialog;

	private boolean projectLoaded = false;

	private MainContentTab tab;

	private boolean exitInProgress;
	private Tab quickAccessTab;
	private ScreenViewport stageViewport;

	public Editor (EditorFrame frame) {
		this.frame = frame;
	}

	@Override
	public void create () {
		instance = this;

		Log.debug("Starting loading");

		Assets.load();

		VisUI.load();
		VisUI.setDefaultTitleAlign(Align.center);
		FileChooser.setFavoritesPrefsName("com.kotcrab.vis.editor");
		Log.debug("VisUI " + VisUI.VERSION + " loaded");

		stage = createStage();
		Gdx.input.setInputProcessor(stage);

		uiRoot = new Table();
		uiRoot.setFillParent(true);

		stage.addActor(uiRoot);

		createUI();
		createModuleContainers();
		createModulesUI();

		Log.debug("Loading completed");

		if (experimentalSettings.isUIScale() || App.scaleUIEnabledFromCmd) {
			stageViewport.setUnitsPerPixel(0.5f);
		}
	}

	private Stage createStage () {
		stageViewport = new ScreenViewport();
		Stage stage = new Stage(stageViewport);

		//the stage root is final field, by default group does not support actor changed events and we need that
		//here we just set our custom group to get those events
		try {
			stageRoot = new VisGroup(stage);
			Field field = stage.getClass().getDeclaredField("root");
			field.setAccessible(true);
			field.set(stage, stageRoot);
		} catch (ReflectiveOperationException e) {
			Log.exception(e);
		}

		return stage;
	}

	private void createUI () {
		mainContentTable = new Table();
		tabContentTable = new Table();
		quickAccessContentTable = new VisTable();
		splitPane = new VisSplitPane(null, null, true);
		splitPane.setSplitAmount(0.77f);

		settingsDialog = new SettingsDialog();
	}

	private void createModuleContainers () {
		editorMC = new EditorModuleContainer();
		projectMC = new ProjectModuleContainer(editorMC);

		editorMC.add(projectIO = new ProjectIOModule());
		editorMC.add(inputModule = new InputModule(stage, stageRoot));
		editorMC.add(new GlobalInputModule());

		editorMC.add(new PluginLoaderModule());
		editorMC.add(pluginContainer = new ExtensionStorageModule());
		editorMC.add(new VisTwitterReader());
		editorMC.add(new WebAPIModule());
		editorMC.add(new RecentProjectModule());
		editorMC.add(new PluginFilesAccessModule());
		editorMC.add(new ColorPickerModule());
		editorMC.add(new UpdateCheckerModule());
		editorMC.add(new DonateReminderModule());
		editorMC.add(tabsModule = new TabsModule(createTabsModuleListener()));
		editorMC.add(fileChooser = new FileChooserModule());
		editorMC.add(new MenuBarModule(projectMC));
		editorMC.add(new ToolbarModule());
		editorMC.add(new ToastModule());
		editorMC.add(new QuickAccessModule(createQuickAccessModuleListener()));
		editorMC.add(statusBar = new StatusBarModule());
		editorMC.add(new UIDebugControllerModule());
		editorMC.add(new EditorSettingsIOModule());
		editorMC.add(new AnalyticsModule());

		editorMC.add(settings = new GeneralSettingsModule());
		editorMC.add(experimentalSettings = new ExperimentalSettingsModule());
		editorMC.add(new PluginSettingsModule());
		editorMC.add(new GridSettingsModule());

		editorMC.add(new DevelopmentSpeedupModule(projectMC));

		editorMC.init();

		Array<EditorModule> modules = pluginContainer.getContainersExtensions(EditorModule.class, ExtensionScope.EDITOR);
		editorMC.addAll(modules);

		settingsDialog.addAll(editorMC.getModules());
	}

	private TabbedPaneListener createTabsModuleListener () {
		return new TabbedPaneAdapter() {
			@Override
			public void switchedTab (Tab tab) {
				mainContentTabChanged((MainContentTab) tab);
			}

			@Override
			public void removedAllTabs () {
				mainContentTabChanged(null);
			}
		};
	}

	private TabbedPaneListener createQuickAccessModuleListener () {
		return new TabbedPaneAdapter() {
			@Override
			public void switchedTab (Tab tab) {
				quickAccessViewChanged(tab);
			}

			@Override
			public void removedAllTabs () {
				quickAccessViewChanged(null);
			}
		};
	}

	private void createModulesUI () {
		uiRoot.add(editorMC.get(MenuBarModule.class).getTable()).fillX().expandX().row();
		uiRoot.add(editorMC.get(ToolbarModule.class).getTable()).fillX().expandX().row();
		uiRoot.add(editorMC.get(TabsModule.class).getTable()).fillX().expandX().row();
		uiRoot.add(mainContentTable).expand().fill().row();
		uiRoot.add(editorMC.get(QuickAccessModule.class).getTable()).fillX().expandX().row();
		uiRoot.add(editorMC.get(StatusBarModule.class).getTable()).fillX().expandX().row();
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
		editorMC.resize();
		projectMC.resize();
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		if (tab != null) tab.render(stage.getBatch());
		stage.draw();
	}

	@Override
	public void dispose () {
		frame.dispose();

		editorMC.dispose();
		if (projectLoaded) projectMC.dispose();

		stage.dispose();
		Assets.dispose();
		VisUI.dispose();

		Log.dispose();

		//make sure that application will exit eventually
		Thread exitThread = new Thread(() -> {
			ThreadUtils.sleep(5000);
			//System.exit(-2);
			//sometimes awt shutdown hook may deadlock on System.exit so I'm using runtime halt
			Runtime.getRuntime().halt(-2);
		}, "Force Exit");

		exitThread.setDaemon(true);
		exitThread.start();
	}

	public void showRestartDialog () {
		OptionDialog optionDialog = DialogUtils.showOptionDialog(Editor.instance.getStage(), "Restart?",
				"Editor restart is required to apply changes", OptionDialogType.YES_NO, new OptionDialogAdapter() {
					@Override
					public void yes () {
						Editor.instance.requestExit(true);
					}
				});

		optionDialog.setNoButtonText("Later");
		optionDialog.setYesButtonText("Restart");
	}

	public void requestExit () {
		requestExit(false);
	}

	/** @see #showRestartDialog() */
	private void requestExit (boolean restartAfterExit) {
		if (exitInProgress) return;
		exitInProgress = true;

		if (projectLoaded == false) {
			showExitDialogIfNeeded(restartAfterExit);
			return;
		}

		if (tabsModule.getDirtyTabCount() > 0) {
			getStage().addActor(new UnsavedResourcesDialog(tabsModule, new WindowListener() {
				@Override
				public void finished () {
					showExitDialogIfNeeded(restartAfterExit);
				}

				@Override
				public void canceled () {
					exitInProgress = false;
				}
			}).fadeIn());
		} else
			showExitDialogIfNeeded(restartAfterExit);
	}

	private void showExitDialogIfNeeded (boolean restartAfterExit) {
		//the "Do you want to restart" dialog was already displayed and user accepted so no need to display exit dialog even if it is enabled
		if (restartAfterExit) {
			exit(true);
			return;
		}

		if (settings.isConfirmExit()) {
			OptionDialog dialog = DialogUtils.showOptionDialog(getStage(), "Confirm Exit", "Are you sure you want to exit VisEditor?", OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
				@Override
				public void yes () {
					exit(false);
				}

				@Override
				public void cancel () {
					exitInProgress = false;
				}
			});

			dialog.setYesButtonText("Exit");
		} else
			exit(false);
	}

	private void exit (boolean restartAfterExit) {
		if (restartAfterExit) App.startNewInstance();
		Gdx.app.exit();
	}

	//TODO minimize usage of this method or remove it completly
	public Stage getStage () {
		return stage;
	}

	public void requestProjectUnload () {
		if (tabsModule.getDirtyTabCount() > 0)
			getStage().addActor(new UnsavedResourcesDialog(tabsModule, () -> doProjectUnloading()).fadeIn());
		else
			doProjectUnloading();
	}

	private void doProjectUnloading () {
		projectLoaded = false;
		settingsDialog.removeAll(projectMC.getModules());
		projectMC.dispose();

		statusBar.setText("Project unloaded");
		App.eventBus.post(new ProjectStatusEvent(Status.Unloaded, projectMC.getProject()));
	}

	public void loadProjectDialog () {
		fileChooser.pickFileOrDirectory(new FileChooserAdapter() {
			@Override
			public void selected (FileHandle file) {
				editorMC.get(ProjectIOModule.class).loadHandleError(getStage(), file);
			}
		});
	}

	public void newProjectDialog () {
		stage.addActor(new NewProjectDialog(fileChooser, projectIO).fadeIn());
	}

	public void projectLoaded (final Project project) {
		if (projectLoaded) {
			DialogUtils.showOptionDialog(getStage(), "Warning", "Other project is already loaded, unload it and continue?", OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
				@Override
				public void yes () {
					switchProject(project);
				}
			});

			return;
		}

		ProjectLoadingDialogController controller = new ProjectLoadingDialogController();

		AsyncTaskProgressDialog dialog = new AsyncTaskProgressDialog("Loading Project", new AsyncTask("ProjectLoaderThread") {
			@Override
			public void execute () {
				setProgressPercent(10);
				setMessage("Loading project data...");

				executeOnOpenGL(() -> {
					projectLoaded = true;
					projectMC.setProject(project);

					projectMC.add(new FileAccessModule());
					projectMC.add(new AssetsWatcherModule());
					projectMC.add(new TextureCacheModule());
					projectMC.add(new FontCacheModule());
					projectMC.add(new ParticleCacheModule());
					projectMC.add(new SceneCacheModule());
					projectMC.add(new ShaderCacheModule());
					projectMC.add(new ProjectVersionModule());
					projectMC.add(new SceneIOModule());
					projectMC.add(new ProjectSettingsIOModule());
					projectMC.add(new SupportModule());
					projectMC.add(new SceneMetadataModule());
					projectMC.add(new AssetsAnalyzerModule());

					projectMC.add(new ExportersManagerModule());
					projectMC.add(new ExportSettingsModule());

					projectMC.add(new SceneTabsModule());
					projectMC.add(new ProjectInfoTabModule());
					projectMC.add(new AssetsUIModule());
					projectMC.addAll(pluginContainer.getContainersExtensions(ProjectModule.class, ExtensionScope.PROJECT));
				});

				setMessage("Initializing...");
				setProgressPercent(50);
				ThreadUtils.sleep(10);

				executeOnOpenGL(() -> {
					projectMC.init();

					settingsDialog.addAll(projectMC.getModules());

					statusBar.setText("Project loaded");
					App.eventBus.post(new ProjectStatusEvent(Status.Loaded, project));
					controller.loading = false;
				});

				while (controller.loading) {
					ThreadUtils.sleep(10);
				}
			}
		});
		dialog.setVisible(true);
		Editor.instance.getStage().addActor(dialog);
	}

	private void switchProject (final Project project) {
		requestProjectUnload();

		Gdx.app.postRunnable(() -> projectLoaded(project));
	}

	public void showSettingsWindow () {
		stage.addActor(settingsDialog.fadeIn());
	}

	private void mainContentTabChanged (MainContentTab tab) {
		this.tab = tab;

		if (tab == null)
			frame.setTitle("VisEditor");
		else
			frame.setTitle("VisEditor - " + tab.getTabTitle());

		tabContentTable.clear();

		if (tab != null)
			tabContentTable.add(tab.getContentTable()).expand().fill();

		updateRootView();
	}

	private void quickAccessViewChanged (Tab tab) {
		quickAccessTab = tab;
		quickAccessContentTable.clear();

		if (tab != null)
			quickAccessContentTable.add(tab.getContentTable()).expand().fill();

		updateRootView();
	}

	private void updateRootView () {
		mainContentTable.clear();
		splitPane.setWidgets(null, null);

		if (tab != null && tab.getViewMode() == TabViewMode.TAB_ONLY || quickAccessTab == null)
			mainContentTable.add(tabContentTable).expand().fill();
		else {
			splitPane.setWidgets(tabContentTable, quickAccessContentTable);
			mainContentTable.add(splitPane).expand().fill();
		}
	}

	private class ProjectLoadingDialogController {
		public boolean loading = true;
	}
}
