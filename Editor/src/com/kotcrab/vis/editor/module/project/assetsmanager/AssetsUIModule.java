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

package com.kotcrab.vis.editor.module.project.assetsmanager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryo.KryoException;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.event.StatusBarEvent;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.editor.ObjectSupportModule;
import com.kotcrab.vis.editor.module.editor.QuickAccessModule;
import com.kotcrab.vis.editor.module.editor.TabsModule;
import com.kotcrab.vis.editor.module.project.*;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.SearchField;
import com.kotcrab.vis.editor.ui.dialog.AsyncTaskProgressDialog;
import com.kotcrab.vis.editor.ui.dialog.DeleteDialog;
import com.kotcrab.vis.editor.ui.dialog.EnterPathDialog;
import com.kotcrab.vis.editor.ui.tab.AssetsUsagesTab;
import com.kotcrab.vis.editor.ui.tab.DeleteMultipleFilesTab;
import com.kotcrab.vis.editor.ui.tabbedpane.DragAndDropTarget;
import com.kotcrab.vis.editor.util.*;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.editor.util.gdx.MenuUtils;
import com.kotcrab.vis.editor.util.gdx.VisTabbedPaneListener;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

public class AssetsUIModule extends ProjectModule implements WatchListener, VisTabbedPaneListener {
	@InjectModule private TabsModule tabsModule;
	@InjectModule private QuickAccessModule quickAccessModule;

	@InjectModule private FileAccessModule fileAccess;
	@InjectModule private ObjectSupportModule supportModule;
	@InjectModule private SceneTabsModule sceneTabsModule;
	@InjectModule private SceneCacheModule sceneCache;
	@InjectModule private AssetsWatcherModule assetsWatcher;
	@InjectModule private AssetsAnalyzerModule assetsAnalyzer;

	@InjectModule private TextureCacheModule textureCache;
	@InjectModule private FontCacheModule fontCache;
	@InjectModule private ParticleCacheModule particleCache;

	private FileHandle visFolder;
	private FileHandle assetsFolder;
	private FileHandle currentDirectory;

	private int filesDisplayed;

	private VisTable mainTable;
	private VisTable treeTable;
	private GridGroup filesView;
	private VisTable toolbarTable;
	private VisTree contentTree;
	private VisLabel contentTitleLabel;
	private SearchField searchField;

	private Stage stage;

	private AssetsTab assetsTab;
	private AssetDragAndDrop assetDragAndDrop;
	private AssetsPopupMenu popupMenu;

	private ObjectMap<FileHandle, TextureAtlasViewTab> atlasViews = new ObjectMap<>();

	private Array<FileItem> filesClipboard = new Array<>();
	private Array<FileItem> selectedFiles = new Array<>();

	@Override
	public void init () {
		stage = Editor.instance.getStage();

		initModule();
		initUI();

		rebuildFolderTree();
		contentTree.getSelection().set(contentTree.getNodes().get(0)); // select first item in tree

		tabsModule.addListener(this);
		assetsWatcher.addListener(this);
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
		filesView = new GridGroup(92, 4);

		VisTable contentsTable = new VisTable(false);
		contentsTable.add(toolbarTable).expandX().fillX().pad(3).padBottom(0);
		contentsTable.row();
		contentsTable.add(new Separator()).padTop(3).expandX().fillX();
		contentsTable.row();
		contentsTable.add(createScrollPane(filesView, true)).expand().fill();

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
		filesView.setTouchable(Touchable.enabled);
		filesView.addListener(popupMenu.getDefaultInputListener());
		filesView.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (event.getTarget() == filesView)
					popupMenu.build(null);

				return false;
			}
		});
	}

	@Override
	public void dispose () {
		tabsModule.removeListener(this);
		assetsWatcher.removeListener(this);
		assetsTab.removeFromTabPane();
	}

	private void createToolbarTable () {
		contentTitleLabel = new VisLabel("Content");
		searchField = new SearchField(newText -> {
			if (currentDirectory == null) return true;

			refreshFilesList();

			return filesDisplayed != 0;
		});

		VisImageButton exploreButton = new VisImageButton(Assets.getIcon(Icons.FOLDER_OPEN), "Explore");
		VisImageButton settingsButton = new VisImageButton(Assets.getIcon(Icons.SETTINGS_VIEW), "Change view");
		VisImageButton importButton = new VisImageButton(Assets.getIcon(Icons.IMPORT), "Import");

		toolbarTable.add(contentTitleLabel).expand().left().padLeft(3);
		toolbarTable.add(exploreButton);
		//toolbarTable.add(settingsButton); //FIXME buttons
		//toolbarTable.add(importButton);
		toolbarTable.add(searchField);

		exploreButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				FileUtils.browse(currentDirectory);
			}
		});
	}

	private void createContentTree () {
		contentTree = new VisTree();
		contentTree.getSelection().setMultiple(false);
		contentTree.getSelection().setRequired(true);
		treeTable.add(createScrollPane(contentTree, false)).expand().fill();

		contentTree.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Node node = contentTree.getSelection().first();

				if (node != null) {
					searchField.clearSearch();

					FolderItem item = (FolderItem) node.getActor();
					changeCurrentDirectory(item.file);
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

	private void changeCurrentDirectory (FileHandle directory) {
		clearSelection();
		this.currentDirectory = directory;
		filesView.clearChildren();
		filesDisplayed = 0;

		FileHandle[] files = directory.list(file -> {
			if (searchField.getText().equals("")) return true;

			return file.getName().contains(searchField.getText());
		});

		for (FileHandle file : files) {
			if (file.isDirectory() == false) {
				String relativePath = fileAccess.relativizeToAssetsFolder(file);
				String ext = file.extension();

				//TODO filter particle images and bitmap font images
				if (relativePath.startsWith("atlas") && (ext.equals("png") || ext.equals("jpg"))) continue;

				filesView.addActor(createFileItem(file));
				filesDisplayed++;
			}
		}

		assetDragAndDrop.rebuild(filesView.getChildren(), atlasViews.values());

		String currentPath = directory.path().substring(visFolder.path().length() + 1);
		contentTitleLabel.setText("Content [" + currentPath + "]");
	}

	private void refreshFilesList () {
		changeCurrentDirectory(currentDirectory);
	}

	private void rebuildFolderTree () {
		contentTree.clearChildren();

		for (FileHandle contentRoot : assetsFolder.list(DirectoriesOnlyFileFilter.filter)) {

			//hide empty dirs except 'gfx' and 'scene'
			if (contentRoot.list().length != 0 || contentRoot.name().equals("gfx") || contentRoot.name().equals("scene")) {
				Node node = new Node(new FolderItem(contentRoot));
				processFolder(node, contentRoot);
				contentTree.add(node);
			}
		}
	}

	private void processFolder (Node node, FileHandle dir) {
		FileHandle[] files = dir.list(DirectoriesOnlyFileFilter.filter);

		for (FileHandle file : files) {
			Node currentNode = new Node(new FolderItem(file));
			node.add(currentNode);

			processFolder(currentNode, file);
		}
	}

	private void openFile (FileHandle file) {
		if (file.extension().equals("scene")) {
			try {
				EditorScene scene = sceneCache.get(file);
				sceneTabsModule.open(scene);
			} catch (KryoException e) {
				DialogUtils.showErrorDialog(stage, "Failed to load scene due to corrupted file or missing required plugin.", e);
				Log.exception(e);
			}

			return;
		}

		if (file.extension().equals("atlas")) {
			TextureAtlasViewTab tab = atlasViews.get(file);

			if (tab == null) {
				String relativePath = fileAccess.relativizeToAssetsFolder(file);
				TextureAtlas atlas = textureCache.getAtlas(relativePath);
				tab = new TextureAtlasViewTab(relativePath, atlas, file.name());
				quickAccessModule.addTab(tab);
				atlasViews.put(file, tab);
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
		if (file.parent().equals(currentDirectory))
			refreshFilesList();
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
			assetDragAndDrop.rebuild(filesView.getChildren(), atlasViews.values());
		} else
			assetDragAndDrop.clear();
	}

	private class AssetsPopupMenu extends PopupMenu {
		@SuppressWarnings("Convert2MethodRef")
		void build (FileItem item) {
			clearChildren();

			if (item == null) {
				addItem(MenuUtils.createMenuItem("Paste", () -> clipboardPasteFiles()));
				addItem(MenuUtils.createMenuItem("Delete", () -> deleteSelectedFiles()));
			} else {

				if (isOpenSupported(item.getFile().extension())) {
					addItem(MenuUtils.createMenuItem("Open", () -> openFile(item.getFile())));
					addSeparator();
				}

				//TODO cache canAnalyze and isSafeFileMoveSupported results too speed up opening menus
				if (assetsAnalyzer.canAnalyzeUsages(item.getFile())) {
					addItem(MenuUtils.createMenuItem("Find Usages", () -> analyzeUsages(item.getFile())));
					addSeparator();
				}

				addItem(MenuUtils.createMenuItem("Copy", () -> clipboardCopyFiles()));
				addItem(MenuUtils.createMenuItem("Paste", () -> clipboardPasteFiles()));

				if(assetsAnalyzer.isSafeFileMoveSupported(item.getFile())) {
					addItem(MenuUtils.createMenuItem("Move", () -> moveFiles(item.getFile())));
					addItem(MenuUtils.createMenuItem("Rename", () -> moveFiles(item.getFile())));
				}

				addItem(MenuUtils.createMenuItem("Delete", () -> deleteSelectedFiles()));
			}
		}

		private void moveFiles (FileHandle file) {
			String relativePath = fileAccess.relativizeToAssetsFolder(file);
			String root = relativePath.substring(0, relativePath.indexOf('/') + 1);

			getStage().addActor(new EnterPathDialog(file, root, relativePath.substring(root.length()), result -> {
				FileHandle target = Gdx.files.absolute(fileAccess.derelativizeFromAssetsFolder(result.relativePath));
				assetsAnalyzer.moveFileSafely(file, target);
			}));
		}

		private void analyzeUsages (FileHandle file) {
			AssetsUsages usages = assetsAnalyzer.analyzeUsages(file);
			if (usages.count == 0)
				App.eventBus.post(new StatusBarEvent("No usages found"));
			else
				quickAccessModule.addTab(new AssetsUsagesTab(projectContainer, usages, false));
		}
	}

	private void deleteSelectedFiles () {
		if (selectedFiles.size == 0) {
			App.eventBus.post(new StatusBarEvent("Nothing to delete"));
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
				FileUtils.delete(file);
				return;
			}

			if (result.safeDelete) {
				AssetsUsages usages = assetsAnalyzer.analyzeUsages(file);
				if (usages.count == 0)
					FileUtils.delete(file);
				else
					quickAccessModule.addTab(new AssetsUsagesTab(projectContainer, usages, true));
			} else
				FileUtils.delete(file);
		}));
	}

	private void clipboardCopyFiles () {
		filesClipboard.clear();
		filesClipboard.addAll(selectedFiles);
	}

	private void clipboardPasteFiles () {
		if (filesClipboard.size == 0) {
			App.eventBus.post(new StatusBarEvent("Nothing to paste"));
			return;
		}

		if (filesClipboard.get(0).getFile().parent().equals(currentDirectory)) {
			App.eventBus.post(new StatusBarEvent("Paste destination is the same as source directory"));
			return;
		}

		Array<FileHandle> targetContents = new Array<>(currentDirectory.list());
		Array<CopyFileTaskDescriptor> tasks = new Array<>();

		for (FileItem item : filesClipboard) {
			boolean overwrites = doesFileExists(targetContents, item.getFile().name());
			tasks.add(new CopyFileTaskDescriptor(item.getFile(), currentDirectory, overwrites));
		}

		Editor.instance.getStage().addActor(new AsyncTaskProgressDialog("Copying files", new CopyFilesAsyncTask(tasks)).fadeIn());
	}

	private boolean doesFileExists (Array<FileHandle> files, String name) {
		for (FileHandle file : files) {
			if (file.name().equals(name)) return true;
		}

		return false;
	}

	private FileItem createFileItem (FileHandle file) {
		FileItem fileItem = new FileItem(projectContainer, file);

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
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) == false) {
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
}
