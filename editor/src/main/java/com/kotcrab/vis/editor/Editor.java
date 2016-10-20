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

package com.kotcrab.vis.editor;

import com.artemis.annotations.SkipWire;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.editor.event.ProjectStatusEvent;
import com.kotcrab.vis.editor.event.ProjectStatusEvent.Status;
import com.kotcrab.vis.editor.module.VisContainers;
import com.kotcrab.vis.editor.module.editor.*;
import com.kotcrab.vis.editor.module.project.Project;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.plugin.api.ContainerExtension.ExtensionScope;
import com.kotcrab.vis.editor.ui.NoProjectFilesOpenView;
import com.kotcrab.vis.editor.ui.WindowListener;
import com.kotcrab.vis.editor.ui.dialog.NewProjectDialog;
import com.kotcrab.vis.editor.ui.dialog.SettingsDialog;
import com.kotcrab.vis.editor.ui.dialog.UnsavedResourcesDialog;
import com.kotcrab.vis.editor.ui.tabbedpane.MainContentTab;
import com.kotcrab.vis.editor.ui.tabbedpane.TabViewMode;
import com.kotcrab.vis.editor.util.ApplicationUtils;
import com.kotcrab.vis.editor.util.GLFWIconSetter;
import com.kotcrab.vis.editor.util.ThreadUtils;
import com.kotcrab.vis.editor.util.async.Async;
import com.kotcrab.vis.editor.util.scene2d.VisGroup;
import com.kotcrab.vis.editor.util.vis.LaunchConfiguration;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.async.AsyncTask;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialog;
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.util.value.PrefHeightIfVisibleValue;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;

import java.lang.reflect.Field;

/**
 * VisEditor main ApplicationAdapter class. The main() method is located in {@link Main}.
 * @author Kotcrab
 */
public class Editor extends ApplicationAdapter {
	public static Editor instance;

	private LaunchConfiguration launchConfig;

	@SkipWire private Stage stage;
	private PolygonSpriteBatch polygonSpriteBatch;
	private VisGroup stageRoot;
	private VisTable uiRoot;

	private EditorModuleContainer editorMC;
	private ProjectModuleContainer projectMC;

	private TabsModule tabsModule;
	private StatusBarModule statusBar;
	private ProjectIOModule projectIO;
	private FileChooserModule fileChooser;
	private ExtensionStorageModule extensionStorage;

	private GeneralSettingsModule settings;
	private ColorSettingsModule colorSettings;
	private ExperimentalSettingsModule experimentalSettings;

	// TODO move to module
	private Table mainContentTable;
	private Table tabContentTable;
	private VisTable quickAccessContentTable;
	private VisSplitPane splitPane;

	private NoProjectFilesOpenView noProjectFilesOpenView;

	private SettingsDialog settingsDialog;

	private boolean projectLoaded = false;

	private MainContentTab tab;

	private boolean exitInProgress;
	private Tab quickAccessTab;
	private ScreenViewport stageViewport;

	public Editor (LaunchConfiguration launchConfig) {
		this.launchConfig = launchConfig;
	}

	@Override
	public void create () {
		instance = this;

		Log.debug("Starting loading");

		try {
			GLFWIconSetter.newInstance().setIcon(Gdx.files.absolute(App.APP_FOLDER_PATH).child("cache/iconCache"),
					Gdx.files.internal("icon.ico"), Gdx.files.internal("icon.png"));
		} catch (IllegalStateException e) {
			Log.exception(e);
		}

		Assets.load();

		VisUI.load();
		VisUI.setDefaultTitleAlign(Align.center);
		Log.debug("VisUI " + VisUI.VERSION + " loaded");

		polygonSpriteBatch = new PolygonSpriteBatch();
		stage = createStage();
		Gdx.input.setInputProcessor(stage);

		uiRoot = new VisTable();
		uiRoot.setFillParent(true);

		stage.addActor(uiRoot);

		createUI();
		createModuleContainers();
		createModulesUI();

		Log.debug("Loading completed");

		if (experimentalSettings.isUIScale() || launchConfig.scaleUIEnabled) {
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
		noProjectFilesOpenView = new NoProjectFilesOpenView(projectMC);

		VisContainers.createEditorModules(editorMC, createTabsModuleListener(), createQuickAccessModuleListener());

		editorMC.init();
		editorMC.injectModules(this);

		Array<EditorModule> modules = extensionStorage.getContainersExtensions(EditorModule.class, ExtensionScope.EDITOR);
		editorMC.addAll(modules);

		settingsDialog.addAll(editorMC.getModules());
	}

	public EditorModuleContainer getEditorModuleContainer () {
		return editorMC;
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
		uiRoot.add(editorMC.get(MenuBarModule.class).getTable()).growX().row();
		uiRoot.add(editorMC.get(ToolbarModule.class).getTable()).growX().row();
		uiRoot.add(editorMC.get(TabsModule.class).getTable()).height(PrefHeightIfVisibleValue.INSTANCE).growX().row();
		uiRoot.add(mainContentTable).grow().row();
		uiRoot.add(editorMC.get(QuickAccessModule.class).getTable()).height(PrefHeightIfVisibleValue.INSTANCE).growX().row();
		uiRoot.add(editorMC.get(StatusBarModule.class).getTable()).growX().row();
	}

	@Override
	public void resize (int width, int height) {
		if (width == 0 && height == 0) return;
		stage.getViewport().update(width, height, true);
		PopupMenu.removeEveryMenu(stage);
		editorMC.resize();
		projectMC.resize();
	}

	@Override
	public void render () {
		Color bgColor = colorSettings.getBackgroundColor();
		Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		if (tab != null) tab.render(polygonSpriteBatch);
		stage.draw();
	}

	@Override
	public void dispose () {
		if (editorMC != null) editorMC.dispose();
		if (projectLoaded) projectMC.dispose();

		polygonSpriteBatch.dispose();
		if (stage != null) stage.dispose();
		Assets.dispose();
		VisUI.dispose();

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
		OptionDialog optionDialog = Dialogs.showOptionDialog(stage, "Restart?",
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
			stage.addActor(new UnsavedResourcesDialog(tabsModule, new WindowListener() {
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
			OptionDialog dialog = Dialogs.showOptionDialog(stage, "Confirm Exit", "Are you sure you want to exit VisEditor?", OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
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
		if (restartAfterExit) ApplicationUtils.startNewInstance();
		Gdx.app.exit();
	}

	public LaunchConfiguration getLaunchConfig () {
		return launchConfig;
	}

	public Stage getStage () {
		return stage;
	}

	public void requestProjectUnload () {
		if (tabsModule.getDirtyTabCount() > 0)
			stage.addActor(new UnsavedResourcesDialog(tabsModule, () -> doProjectUnloading()).fadeIn());
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
		fileChooser.pickFileOrDirectory(new SingleFileChooserListener() {
			@Override
			public void selected (FileHandle file) {
				editorMC.get(ProjectIOModule.class).loadHandleError(stage, file);
			}
		});
	}

	public void newProjectDialog () {
		stage.addActor(new NewProjectDialog(fileChooser, projectIO).fadeIn());
	}

	public void projectLoaded (final Project project) {
		if (projectLoaded) {
			Dialogs.showOptionDialog(stage, "Warning", "Other project is already loaded, unload it and continue?", OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
				@Override
				public void yes () {
					switchProject(project);
				}
			});

			return;
		}

		ProjectLoadingDialogController controller = new ProjectLoadingDialogController();

		Async.startTask(stage, "Loading Project", new AsyncTask("ProjectLoaderThread") {
			@Override
			protected void doInBackground () throws Exception {
				setProgressPercent(10);
				setMessage("Loading project data...");

				executeOnGdx(() -> {
					projectLoaded = true;
					projectMC.setProject(project);
					VisContainers.createProjectModules(projectMC, extensionStorage);
				});

				setMessage("Initializing...");
				setProgressPercent(50);
				ThreadUtils.sleep(10);

				executeOnGdx(() -> {
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

		String newTitle;
		if (tab == null)
			newTitle = "VisEditor";
		else
			newTitle = "VisEditor - " + tab.getTabTitle();
		Gdx.graphics.setTitle(newTitle);

		tabContentTable.clear();

		if (tab != null)
			tabContentTable.add(tab.getContentTable()).expand().fill();
		else if (projectLoaded)
			tabContentTable.add(noProjectFilesOpenView).center();

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

	public PolygonSpriteBatch getPolygonSpriteBatch () {
		return polygonSpriteBatch;
	}

	private class ProjectLoadingDialogController {
		public boolean loading = true;
	}
}
