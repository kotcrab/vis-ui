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

package com.kotcrab.vis.editor.module.project.assetsmanager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.google.common.eventbus.Subscribe;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.event.ResourceReloadedEvent;
import com.kotcrab.vis.editor.event.ResourceReloadedEvent.ResourceType;
import com.kotcrab.vis.editor.module.EventBusSubscriber;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.module.editor.QuickAccessModule;
import com.kotcrab.vis.editor.module.editor.StatusBarModule;
import com.kotcrab.vis.editor.module.editor.TabsModule;
import com.kotcrab.vis.editor.module.project.*;
import com.kotcrab.vis.editor.plugin.api.AssetsFileSorter;
import com.kotcrab.vis.editor.plugin.api.AssetsUIContextGeneratorProvider;
import com.kotcrab.vis.editor.ui.SearchField;
import com.kotcrab.vis.editor.ui.dialog.DeleteDialog;
import com.kotcrab.vis.editor.ui.dialog.EnterPathDialog;
import com.kotcrab.vis.editor.ui.tab.AssetsUsagesTab;
import com.kotcrab.vis.editor.ui.tab.DeleteMultipleFilesTab;
import com.kotcrab.vis.editor.ui.tabbedpane.DragAndDropTarget;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.Holder;
import com.kotcrab.vis.editor.util.async.Async;
import com.kotcrab.vis.editor.util.async.CopyFileTaskDescriptor;
import com.kotcrab.vis.editor.util.async.CopyFilesAsyncTask;
import com.kotcrab.vis.editor.util.scene2d.*;
import com.kotcrab.vis.editor.util.vis.ProjectPathUtils;
import com.kotcrab.vis.editor.util.vis.WikiPages;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.file.FileChooser.HistoryPolicy;
import com.kotcrab.vis.ui.widget.file.FileChooserStyle;
import com.kotcrab.vis.ui.widget.file.internal.FileChooserText;
import com.kotcrab.vis.ui.widget.file.internal.FileHistoryManager;
import com.kotcrab.vis.ui.widget.file.internal.FileHistoryManager.FileHistoryCallback;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

import java.util.Optional;

/**
 * Provides UI module for managing assets.
 * @author Kotcrab
 */
@EventBusSubscriber
public class AssetsUIModule extends ProjectModule implements WatchListener, VisTabbedPaneListener {
	//ConfirmDialog buttons
	private static final int OK = 0;
	private static final int HELP = 1;
	private static final int CANCEL = 2;

	private TabsModule tabsModule;
	private QuickAccessModule quickAccessModule;
	private StatusBarModule statusBar;
	private ExtensionStorageModule extensionStorage;

	private FileAccessModule fileAccess;
	private SceneTabsModule sceneTabsModule;
	private SceneCacheModule sceneCache;
	private AssetsMetadataModule assetsMetadata;
	private AssetsWatcherModule assetsWatcher;
	private AssetsAnalyzerModule assetsAnalyzer;

	private TextureCacheModule textureCache;

	private Stage stage;

	private FileHandle visFolder;
	private FileHandle assetsFolder;
	private FileHandle currentDirectory;
	private AssetDirectoryDescriptor currentDirectoryDescriptor;
	private AssetsFileSorter fileSorter;

	//metadata
	private Json json;
	private FileHandle metadataFile;
	private AssetsUIModuleMetadata metadata;

	private Array<FileItem> filesClipboard = new Array<>();
	private Array<FileItem> selectedFiles = new Array<>();

	private ObjectMap<FileHandle, TextureAtlasViewTab> atlasViews = new ObjectMap<>();

	private Array<AssetsUIContextGenerator> contextGenerators = new Array<>();

	private int filesDisplayed;

	private AssetsTab assetsTab;
	private AssetDragAndDrop assetDragAndDrop;
	private AssetsPopupMenu popupMenu;

	//UI
	private VisTable mainTable;
	private VisTable treeTable;
	private VisTable filesViewContextContainer;
	private VisScrollPane filesViewScrollPane;
	private VisTable filesView;
	private GridGroup mainFilesView;
	private GridGroup miscFilesView;
	private VisTable toolbarTable;
	private VisTree contentTree;

	private VisImageButton navigateToParentButton;
	private VisImageButton createFolderButton;

	private VisLabel contentTitleLabel;
	private VisLabel dirDescriptorTitleLabel;
	private SearchField searchField;

	private FileHistoryManager fileHistoryManager;

	@Override
	public void init () {
		initModule();
		initUI();

		rebuildFolderTree();
		Node node = contentTree.getNodes().get(0);
		contentTree.getSelection().set(node); // select first item in tree
		changeCurrentDirectory(((FolderItem) node.getActor()).getFile(), HistoryPolicy.IGNORE);

		Array<AssetsUIContextGeneratorProvider> providers = extensionStorage.getAssetsContextGeneratorsProviders();
		for (AssetsUIContextGeneratorProvider provider : providers) {
			contextGenerators.add(provider.provide());
		}

		for (AssetsUIContextGenerator generator : contextGenerators) {
			projectContainer.injectModules(generator);
			generator.init();
		}

		tabsModule.addListener(this);
		assetsWatcher.addListener(this);

		json = new Json();
		metadataFile = fileAccess.getModuleFolder(".metadata").child("assetsUIMetadata");

		if (metadataFile.exists())
			metadata = json.fromJson(AssetsUIModuleMetadata.class, metadataFile);
		else
			metadata = new AssetsUIModuleMetadata();
	}

	@Override
	public void postInit () {
		if (metadata.lastDirectory != null) {
			FileHandle dir = Gdx.files.absolute(metadata.lastDirectory);
			if (dir.exists() && dir.path().startsWith(project.getVisDirectory().path())) {
				changeCurrentDirectory(dir, HistoryPolicy.IGNORE);
			}
		}
	}

	private boolean highlightDir (FileHandle dir) {
		return highlightDir(dir, contentTree.getNodes());
	}

	private boolean highlightDir (FileHandle dir, Array<Node> nodes) {
		for (Node node : nodes) {
			if (((FolderItem) node.getActor()).getFile().equals(dir)) {
				contentTree.getSelection().set(node);
				return true;
			}

			if (node.getChildren().size > 0) {
				boolean prevNodeState = node.isExpanded();
				node.setExpanded(true);
				if (highlightDir(dir, node.getChildren())) return true;
				node.setExpanded(prevNodeState);
			}
		}

		return false;
	}

	private void initModule () {
		visFolder = fileAccess.getVisFolder();
		assetsFolder = fileAccess.getAssetsFolder();

		assetDragAndDrop = new AssetDragAndDrop(projectContainer);

		quickAccessModule.addListener(new TabbedPaneAdapter() {
			@Override
			public void removedTab (Tab tab) {
				FileHandle atlasTabFile = atlasViews.findKey(tab, true);
				if (atlasTabFile != null) atlasViews.remove(atlasTabFile);
			}
		});
	}

	private void initUI () {
		treeTable = new VisTable(true);
		toolbarTable = new VisTable(true);
		filesViewContextContainer = new VisTable(false);

		filesView = new VisTable();
		mainFilesView = new GridGroup(92, 4);
		miscFilesView = new GridGroup(92, 4);

		filesView.setTouchable(Touchable.enabled);
		mainFilesView.setTouchable(Touchable.enabled);
		miscFilesView.setTouchable(Touchable.enabled);

		VisTable contentsTable = new VisTable(false);
		contentsTable.add(toolbarTable).expandX().fillX().pad(3).padBottom(0);
		contentsTable.row();
		contentsTable.add(new Separator()).padTop(3).expandX().fillX();
		contentsTable.row();
		contentsTable.add(filesViewContextContainer).expandX().fillX();
		contentsTable.row();
		contentsTable.add(filesViewScrollPane = createScrollPane(filesView, true)).expand().fill();

		VisSplitPane splitPane = new VisSplitPane(treeTable, contentsTable, false);
		splitPane.setSplitAmount(0.2f);

		createToolbarTable();
		createContentTree();

		mainTable = new VisTable();
		mainTable.setBackground("window-bg");
		mainTable.add(splitPane).expand().fill();

		assetsTab = new AssetsTab();
		quickAccessModule.addTab(assetsTab);

		popupMenu = new AssetsPopupMenu();
		filesView.addListener(popupMenu.getDefaultInputListener());
		filesView.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (event.getTarget() == filesView || event.getTarget() == mainFilesView || event.getTarget() == miscFilesView) {
					popupMenu.build(null);
				}

				return false;
			}
		});
	}

	@Override
	public void dispose () {
		assetDragAndDrop.dispose();
		tabsModule.removeListener(this);
		assetsWatcher.removeListener(this);
		assetsTab.removeFromTabPane();

		json.toJson(metadata, metadataFile);
	}

	private void createToolbarTable () {
		fileHistoryManager = new FileHistoryManager(VisUI.getSkin().get(FileChooserStyle.class), new FileHistoryCallback() {
			@Override
			public FileHandle getCurrentDirectory () {
				return currentDirectory;
			}

			@Override
			public void setDirectory (FileHandle directory, HistoryPolicy policy) {
				changeCurrentDirectory(directory, policy);
			}

			@Override
			public Stage getStage () {
				return stage;
			}
		});

		contentTitleLabel = new VisLabel("Content");
		dirDescriptorTitleLabel = new VisLabel("", Color.GRAY);
		searchField = new SearchField(newText -> {
			if (currentDirectory == null) return true;
			if (currentDirectory.list().length == 0 || searchField.getText().length() == 0) return true;

			refreshFilesList();

			return filesDisplayed != 0;
		});

		navigateToParentButton = new VisImageButton(Icons.FOLDER_PARENT.drawable(), "Go to Parent Directory");
		navigateToParentButton.setGenerateDisabledImage(true);

		createFolderButton = new VisImageButton(Icons.FOLDER_NEW.drawable(), "Create New Folder");

		VisImageButton exploreButton = new VisImageButton(Icons.FOLDER_OPEN.drawable(), "Open in Explorer");
//		VisImageButton settingsButton = new VisImageButton(Icons.SETTINGS_VIEW.drawable(), "Change view");
//		VisImageButton importButton = new VisImageButton(Icons.IMPORT.drawable(), "Import");

		toolbarTable.add(navigateToParentButton);
		toolbarTable.add(createFolderButton);
		toolbarTable.add(fileHistoryManager.getButtonsTable());
		toolbarTable.add(contentTitleLabel).left().expand();
		toolbarTable.add(dirDescriptorTitleLabel);
		toolbarTable.add(exploreButton);
		//toolbarTable.add(settingsButton); //FIXME buttons
		//toolbarTable.add(importButton);
		toolbarTable.add(searchField);

		navigateToParentButton.addListener(new VisChangeListener((event, actor) -> {
			if (currentDirectory.equals(assetsFolder)) return;
			changeCurrentDirectory(currentDirectory.parent());
		}));

		createFolderButton.addListener(new VisChangeListener((event, actor) -> {
			Dialogs.showInputDialog(mainTable.getStage(), FileChooserText.NEW_DIRECTORY_DIALOG_TITLE.get(), FileChooserText.NEW_DIRECTORY_DIALOG_TEXT.get(), true, new InputDialogAdapter() {
				@Override
				public void finished (String input) {
					if (FileUtils.isValidFileName(input) == false) {
						Dialogs.showErrorDialog(mainTable.getStage(), FileChooserText.NEW_DIRECTORY_DIALOG_ILLEGAL_CHARACTERS.get());
						return;
					}

					for (FileHandle file : currentDirectory.list()) {
						if (file.name().equals(input)) {
							Dialogs.showErrorDialog(mainTable.getStage(), FileChooserText.NEW_DIRECTORY_DIALOG_ALREADY_EXISTS.get());
							return;
						}
					}

					currentDirectory.child(input).mkdirs();
					refreshFilesList();
				}
			});
		}));

		exploreButton.addListener(new VisChangeListener((event, actor) -> FileUtils.browse(currentDirectory)));
	}

	private void createContentTree () {
		contentTree = new VisTree();
		contentTree.getSelection().setMultiple(false);
		contentTree.getSelection().setRequired(true);
		contentTree.getSelection().setProgrammaticChangeEvents(false);
		treeTable.add(createScrollPane(contentTree, false)).expand().fill();

		contentTree.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Node node = contentTree.getSelection().first();

				if (node != null) {
					searchField.clearSearch();

					FolderItem item = (FolderItem) node.getActor();
					changeCurrentDirectory(item.getFile(), HistoryPolicy.ADD);
				}
			}
		});
	}

	private VisScrollPane createScrollPane (Actor actor, boolean disableX) {
		VisScrollPane scrollPane = new VisScrollPane(actor);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setScrollingDisabled(disableX, false);
		return scrollPane;
	}

	public void changeCurrentDirectory (FileHandle directory) {
		changeCurrentDirectory(directory, HistoryPolicy.CLEAR);
	}

	public void changeCurrentDirectory (FileHandle directory, HistoryPolicy historyPolicy) {
		clearSelection();

		if (historyPolicy == HistoryPolicy.ADD) fileHistoryManager.historyAdd();

		this.currentDirectory = directory;
		if (metadata != null) metadata.lastDirectory = directory.path();
		mainFilesView.clearChildren();
		miscFilesView.clearChildren();

		updateContextGeneratorContainer(directory);

		currentDirectoryDescriptor = assetsMetadata.getAsDirectoryDescriptorRecursively(directory);

		if (currentDirectory.equals(assetsFolder))
			navigateToParentButton.setDisabled(true);
		else
			navigateToParentButton.setDisabled(false);

		FileHandle[] files = directory.list(file -> {
			if (searchField.getText().equals("")) return true;

			return file.getName().contains(searchField.getText());
		});

		fileSorter = null;
		String relativePath = fileAccess.relativizeToAssetsFolder(directory);
		for (AssetsFileSorter sorter : extensionStorage.getAssetsFileSorters()) {
			if (sorter.isSupported(assetsMetadata, directory, relativePath)) {
				fileSorter = sorter;
				break;
			}
		}

		Array<FileHandle> sortedFiles = FileUtils.sortFiles(files);

		filesDisplayed = 0;
		boolean miscFileViewUsed = false;
		boolean mainFileViewUsed = false;
		for (FileHandle file : sortedFiles) {
			String ext = file.extension();

			if (file.name().equals(".vis")) continue;

			if (relativePath.startsWith("atlas") && (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg")))
				continue;

			boolean isMain = fileSorter == null ? true : fileSorter.isMainFile(file);
			FileItem item = createFileItem(file, isMain);

			if (isMain) {
				mainFilesView.addActor(item);
				mainFileViewUsed = true;
			} else {
				miscFilesView.addActor(item);
				miscFileViewUsed = true;
			}
			filesDisplayed++;
		}

		assetDragAndDrop.rebuild(mainFilesView.getChildren(), miscFilesView.getChildren(), atlasViews.values());

		filesView.clearChildren();
		filesView.top();
		ScrollPaneScrollWidthValue scrollWidthValue = new ScrollPaneScrollWidthValue(filesViewScrollPane);
		if (miscFileViewUsed) {
			if (mainFileViewUsed) {
				filesView.add(mainFilesView).width(scrollWidthValue).growX();
				filesView.row();
			}
			filesView.add(new VisLabel("Other files")).padLeft(mainFilesView.getSpacing()).left().row();
			filesView.add(miscFilesView).width(scrollWidthValue).growX();
		} else {
			filesView.add(mainFilesView).width(scrollWidthValue).growX();
		}

		String currentPath = directory.path().substring(visFolder.path().length() + 1);
		contentTitleLabel.setText("Content [" + currentPath + "]");
		if (currentDirectoryDescriptor != null) {
			dirDescriptorTitleLabel.setText("[" + currentDirectoryDescriptor.getUIName() + "]");
		} else {
			dirDescriptorTitleLabel.setText("");
		}

		highlightDir(directory);

		if (historyPolicy == HistoryPolicy.CLEAR) fileHistoryManager.historyClear();
	}

	private void updateContextGeneratorContainer (FileHandle directory) {
		filesViewContextContainer.clearChildren();
		String relativePath = fileAccess.relativizeToAssetsFolder(directory);
		for (AssetsUIContextGenerator generator : contextGenerators) {
			Table content = generator.provideContext(directory, relativePath);
			if (content != null) {
				filesViewContextContainer.add(content).fillX().expandX();
				break;
			}
		}
	}

	private void refreshFilesList () {
		changeCurrentDirectory(currentDirectory, HistoryPolicy.IGNORE);
	}

	private void rebuildFolderTree () {
		contentTree.clearChildren();

		contentTree.add(new Node(new FolderItem(assetsFolder, true)));

		for (FileHandle contentRoot : assetsFolder.list(DirectoriesOnlyFileFilter.FILTER)) {

			//hide empty dirs except 'gfx' and 'scene'
			if (contentRoot.list().length != 0 || contentRoot.name().equals("gfx") || contentRoot.name().equals("scene")) {
				Node node = new Node(new FolderItem(contentRoot));
				processFolder(node, contentRoot);
				contentTree.add(node);
			}
		}
	}

	private void processFolder (Node node, FileHandle dir) {
		FileHandle[] files = dir.list(DirectoriesOnlyFileFilter.FILTER);

		for (FileHandle file : files) {
			if (file.name().startsWith(".")) continue; //hide folders starting with dot

			Node currentNode = new Node(new FolderItem(file));
			node.add(currentNode);

			processFolder(currentNode, file);
		}
	}

	private void openFile (FileHandle file) {
		if (file.isDirectory()) {
			changeCurrentDirectory(file, HistoryPolicy.ADD);
			return;
		}

		if (fileSorter != null && fileSorter.isMainFile(file) == false) {
			Dialogs.showOKDialog(stage, "Message", "This file type is unsupported in this marked directory.");
			return;
		}

		if (ProjectPathUtils.isScene(file)) {
			sceneTabsModule.open(file);
			return;
		}

		if (file.extension().equals("atlas")) {
			TextureAtlasViewTab tab = atlasViews.get(file);

			if (tab == null) {
				String relativePath = fileAccess.relativizeToAssetsFolder(file);
				TextureAtlas atlas = textureCache.getAtlas(relativePath);
				if (atlas != null) {
					tab = new TextureAtlasViewTab(relativePath, atlas, file.name());
					quickAccessModule.addTab(tab);
					atlasViews.put(file, tab);
				} else {
					Dialogs.showErrorDialog(stage, "Unknown error encountered during atlas loading");
					return;
				}
			} else
				quickAccessModule.switchTab(tab);

			assetDragAndDrop.addSources(tab.getItems());

			return;
		}
	}

	private boolean isOpenSupported (String extension) {
		return extension.equals("scene");
	}

	private void refreshAllIfNeeded (FileHandle file) {
		if (file.isDirectory()) rebuildFolderTree();
		if (file.parent().equals(currentDirectory)) refreshFilesList();

		updateContextGeneratorContainer(currentDirectory);
	}

	@Override
	public void fileChanged (FileHandle file) {
		refreshAllIfNeeded(file);
	}

	@Override
	public void fileDeleted (FileHandle file) {
		//although fileChanged covers 'delete' event, that event is sent before the actual file is deleted from disk,
		//thus refreshing list at that moment would be pointless (the file is still on the disk)
		refreshAllIfNeeded(file);
	}

	@Override
	public void switchedTab (Tab tab) {
		if (tab instanceof DragAndDropTarget) {
			assetDragAndDrop.setDropTarget((DragAndDropTarget) tab);
			assetDragAndDrop.rebuild(mainFilesView.getChildren(), miscFilesView.getChildren(), atlasViews.values());
		} else
			assetDragAndDrop.clear();
	}

	@Subscribe
	public void handleResourceReloaded (ResourceReloadedEvent event) {
		if (event.resourceTypes.contains(ResourceType.TEXTURE_ATLASES)) refreshFilesList();
	}

	private class AssetsPopupMenu extends PopupMenu {
		private PopupMenu markDirSubMenu;

		public AssetsPopupMenu () {
			markDirSubMenu = new PopupMenu();
		}

		@SuppressWarnings("Convert2MethodRef")
		void build (FileItem item) {
			clearChildren();

			if (item == null) {
				addItem(MenuUtils.createMenuItem("Paste", () -> clipboardPasteFiles()));
				addItem(MenuUtils.createMenuItem("Delete", () -> deleteSelectedFiles()));
			} else {
				FileHandle file = item.getFile();
				boolean directory = file.isDirectory();

				if (directory && currentDirectoryDescriptor == null) {
					addItem(MenuUtils.createMenuItem("Mark Directory As", markDirSubMenu));
					markDirSubMenu.clearChildren();

					Optional<AssetDirectoryDescriptor> currentDirDesc = Optional.ofNullable(assetsMetadata.getAsDirectoryDescriptor(file));
					Holder<Boolean> atLeastOneAdded = Holder.of(false);

					extensionStorage.getAssetDirectoryDescriptors().forEach(desc -> {
						if (currentDirDesc.isPresent() && currentDirDesc.get().getId().equals(desc.getId())) return;

						markDirSubMenu.addItem(MenuUtils.createMenuItem(desc.getUIName(), desc.getMenuItemIcon(),
								() -> safeMarkDirectory(file, currentDirDesc.isPresent(), desc.getId())));
						atLeastOneAdded.value = true;
					});

					if (currentDirDesc.isPresent()) {
						if (atLeastOneAdded.get()) markDirSubMenu.addSeparator();
						markDirSubMenu.addItem(MenuUtils.createMenuItem("Unmark directory", () -> safeMarkDirectory(file, currentDirDesc.isPresent(), null)));
					}
				}

				if (directory || isOpenSupported(file.extension())) {
					addItem(MenuUtils.createMenuItem("Open", () -> openFile(file)));
					addSeparator();
				}

				//TODO cache canAnalyze and isSafeFileMoveSupported results too speed up opening menus
				if (directory == false && assetsAnalyzer.canAnalyzeUsages(file)) {
					addItem(MenuUtils.createMenuItem("Find Usages", () -> analyzeUsages(file)));
					addSeparator();
				}

				if (directory == false) { //TODO: add support for copying directories
					addItem(MenuUtils.createMenuItem("Copy", () -> clipboardCopyFiles()));
					addItem(MenuUtils.createMenuItem("Paste", () -> clipboardPasteFiles()));
				}

				if (assetsAnalyzer.isSafeFileMoveSupported(file)) {
					addItem(MenuUtils.createMenuItem("Move", () -> moveFiles(file)));
					addItem(MenuUtils.createMenuItem("Rename", () -> moveFiles(file)));
				}

				addItem(MenuUtils.createMenuItem("Delete", () -> deleteSelectedFiles()));
			}
		}

		private void safeMarkDirectory (FileHandle dir, boolean dirMarked, String fullCodeName) {
			if (dirMarked) {
				//TODO: add safe remark feature
				String[] buttons = {"Help", "Change Anyway", "Cancel"};
				Integer[] returns = {HELP, OK, CANCEL};
				Dialogs.showConfirmDialog(getStage(), "Warning", "This directory is already marked, changing it may\n" +
						"result in unexpected errors if asset files are used in scenes.", buttons, returns, result -> {
					if (result == HELP) WikiPages.MARKING_DIRECTORIES.open();
					if (result == OK) markDirectory(dir, fullCodeName);
				});
			} else {
				if (dir.list().length > 0) {
					Dialogs.showOptionDialog(getStage(), "Warning", "This directory already contains files. Marking it \n" +
									"may cause unexpected errors if assets files are used in scenes.", OptionDialogType.YES_CANCEL,
							new OptionDialogAdapter() {
								@Override
								public void yes () {
									markDirectory(dir, fullCodeName);
								}
							}).setYesButtonText("Mark Anyway");
				} else
					markDirectory(dir, fullCodeName);
			}
		}

		private void markDirectory (FileHandle dir, String fullCodeName) {
			if (fullCodeName == null) {
				assetsMetadata.remove(dir);
			} else {
				assetsMetadata.commit(dir, fullCodeName);
			}
			refreshFilesList();
		}

		private void moveFiles (FileHandle file) {
			String relativePath = fileAccess.relativizeToAssetsFolder(file);

			getStage().addActor(new EnterPathDialog(fileAccess.getAssetsFolder(), relativePath, result -> {
				FileHandle target = Gdx.files.absolute(fileAccess.derelativizeFromAssetsFolder(result.relativePath));
				assetsAnalyzer.moveFileSafely(file, target);
			}));
		}

		private void analyzeUsages (FileHandle file) {
			AssetsUsages usages = assetsAnalyzer.analyzeUsages(file);
			if (usages.count() == 0)
				statusBar.setText("No usages found");
			else
				quickAccessModule.addTab(new AssetsUsagesTab(projectContainer, usages, false));
		}
	}

	private void deleteSelectedFiles () {
		if (selectedFiles.size == 0) {
			statusBar.setText("Nothing to delete");
			return;
		}

		if (selectedFiles.size == 1)
			showFileDeleteDialog(selectedFiles.get(0).getFile());
		else
			quickAccessModule.addTab(new DeleteMultipleFilesTab(projectContainer, selectedFiles));
	}

	private void showFileDeleteDialog (FileHandle file) {
		boolean canBeSafeDeleted = assetsAnalyzer.canAnalyzeUsages(file);
		stage.addActor(new DeleteDialog(file, canBeSafeDeleted, result -> {
			if (canBeSafeDeleted == false) {
				deleteWithErrorDialogIfNeeded(file);
				return;
			}

			if (result.safeDelete) {
				AssetsUsages usages = assetsAnalyzer.analyzeUsages(file);
				if (usages.count() == 0)
					deleteWithErrorDialogIfNeeded(file);
				else
					quickAccessModule.addTab(new AssetsUsagesTab(projectContainer, usages, true));
			} else
				deleteWithErrorDialogIfNeeded(file);
		}));
	}

	private void deleteWithErrorDialogIfNeeded (FileHandle file) {
		if (FileUtils.delete(file) == false) {
			Dialogs.showErrorDialog(stage, "Error occurred while deleting file, file may be used by system");
		}
	}

	private void clipboardCopyFiles () {
		filesClipboard.clear();
		filesClipboard.addAll(selectedFiles);
	}

	private void clipboardPasteFiles () {
		if (filesClipboard.size == 0) {
			statusBar.setText("Nothing to paste");
			return;
		}

		if (filesClipboard.get(0).getFile().parent().equals(currentDirectory)) {
			statusBar.setText("Paste destination is the same as source directory");
			return;
		}

		Array<FileHandle> targetContents = new Array<>(currentDirectory.list());
		Array<CopyFileTaskDescriptor> tasks = new Array<>();

		for (FileItem item : filesClipboard) {
			boolean overwrites = doesFileExists(targetContents, item.getFile().name());
			tasks.add(new CopyFileTaskDescriptor(item.getFile(), currentDirectory, overwrites));
		}

		Async.startTask(stage, "Copying files", new CopyFilesAsyncTask(stage, tasks));
	}

	private boolean doesFileExists (Array<FileHandle> files, String name) {
		for (FileHandle file : files) {
			if (file.name().equals(name)) return true;
		}

		return false;
	}

	private FileItem createFileItem (FileHandle file, boolean isMainFile) {
		FileItem fileItem = new FileItem(projectContainer, file, isMainFile);

		fileItem.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (button == Buttons.RIGHT) {
					selectItem(fileItem, true);
					popupMenu.build(fileItem);
				}

				return false;
			}
		});

		fileItem.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				super.clicked(event, x, y);

				selectItem(fileItem, false);
				if (getTapCount() == 2) openFile(file);
			}
		});

		return fileItem;
	}

	private void clearSelection () {
		for (FileItem item : selectedFiles)
			item.setSelected(false);
		selectedFiles.clear();
	}

	private void selectItem (FileItem fileItem, boolean rightClick) {
		if (UIUtils.ctrl() == false) {
			if (rightClick) {
				if (selectedFiles.contains(fileItem, true) == false)
					clearSelection();
			} else
				clearSelection();
		}

		boolean contains = selectedFiles.contains(fileItem, true);

		if (contains && rightClick == false) {
			selectedFiles.removeValue(fileItem, true);
			fileItem.setSelected(false);
		} else {
			if (contains == false) selectedFiles.add(fileItem);
			fileItem.setSelected(true);
		}
	}

	private class AssetsTab extends Tab {
		@Override
		public String getTabTitle () {
			return "Assets";
		}

		@Override
		public Table getContentTable () {
			return mainTable;
		}

		@Override
		public boolean isCloseableByUser () {
			return false;
		}
	}

	private static class AssetsUIModuleMetadata {
		public String lastDirectory;
	}
}
