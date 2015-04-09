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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.module.editor.QuickAccessModule;
import com.kotcrab.vis.editor.module.editor.TabsModule;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.dialog.DeleteDialog;
import com.kotcrab.vis.editor.ui.tab.AssetsUsageTab;
import com.kotcrab.vis.editor.ui.tabbedpane.DragAndDropTarget;
import com.kotcrab.vis.editor.util.DirectoriesOnlyFileFilter;
import com.kotcrab.vis.editor.util.DirectoryWatcher;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.gdx.MenuUtils;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;

//TODO filter particle images and bitmap font images
//TODO this probably shouldn't be a module, should extend tab and should be loaded when project is loaded
public class AssetsUIModule extends ProjectModule implements DirectoryWatcher.WatchListener, TabbedPaneListener {
	private TabsModule tabsModule;
	private SceneTabsModule sceneTabsModule;
	private QuickAccessModule quickAccessModule;
	private SceneIOModule sceneIO;
	private FileAccessModule fileAccess;
	private TextureCacheModule textureCache;
	private AssetsWatcherModule assetsWatcher;
	private AssetsUsageAnalyzerModule assetsUsageAnalyzer;

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
	private VisTextField searchTextField;

	private AssetsTab assetsTab;

	private AssetDragAndDrop assetDragAndDrop;

	private AssetsPopupMenu popupMenu;

	@Override
	public void init () {
		initModule();
		initUI();

		rebuildFolderTree();
		contentTree.getSelection().set(contentTree.getNodes().get(0)); // select first item in tree

		tabsModule.addListener(this);
		assetsWatcher.addListener(this);
	}

	private void initModule () {
		tabsModule = container.get(TabsModule.class);
		sceneTabsModule = projectContainer.get(SceneTabsModule.class);
		quickAccessModule = container.get(QuickAccessModule.class);
		sceneIO = projectContainer.get(SceneIOModule.class);
		fileAccess = projectContainer.get(FileAccessModule.class);
		textureCache = projectContainer.get(TextureCacheModule.class);
		assetsWatcher = projectContainer.get(AssetsWatcherModule.class);
		assetsUsageAnalyzer = projectContainer.get(AssetsUsageAnalyzerModule.class);

		FontCacheModule fontCache = projectContainer.get(FontCacheModule.class);
		ParticleCacheModule particleCache = projectContainer.get(ParticleCacheModule.class);

		visFolder = fileAccess.getVisFolder();
		assetsFolder = fileAccess.getAssetsFolder();

		assetDragAndDrop = new AssetDragAndDrop(fileAccess, textureCache, fontCache, particleCache);
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
	}

	@Override
	public void dispose () {
		tabsModule.removeListener(this);
		assetsWatcher.removeListener(this);
		assetsTab.removeFromTabPane();
	}

	private void createToolbarTable () {
		contentTitleLabel = new VisLabel("Content");
		searchTextField = new VisTextField();

		VisImageButton exploreButton = new VisImageButton(Assets.getIcon(Icons.FOLDER_OPEN), "Explore");
		VisImageButton settingsButton = new VisImageButton(Assets.getIcon(Icons.SETTINGS_VIEW), "Change view");
		VisImageButton importButton = new VisImageButton(Assets.getIcon(Icons.IMPORT), "Import");

		toolbarTable.add(contentTitleLabel).expand().left().padLeft(3);
		toolbarTable.add(exploreButton);
		toolbarTable.add(settingsButton);
		toolbarTable.add(importButton);
		toolbarTable.add(new Image(Assets.getIcon(Icons.SEARCH))).spaceRight(3);

		toolbarTable.add(searchTextField).width(200);

		exploreButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				FileUtils.browse(currentDirectory);
			}
		});

		searchTextField.addListener(new InputListener() {
			@Override
			public boolean keyTyped (InputEvent event, char character) {
				refreshFilesList();

				if (filesDisplayed == 0)
					searchTextField.setInputValid(false);
				else
					searchTextField.setInputValid(true);

				return false;
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
					searchTextField.setText("");
					searchTextField.setInputValid(true);

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
		this.currentDirectory = directory;
		filesView.clear();
		filesDisplayed = 0;

		FileHandle[] files = directory.list(file -> {
			if (searchTextField.getText().equals("")) return true;

			return file.getName().contains(searchTextField.getText());
		});

		Array<Actor> actors = new Array<>(files.length);

		for (FileHandle file : files) {
			if (file.isDirectory() == false) {
				final FileItem item = new FileItem(file);
				actors.add(item);
				filesDisplayed++;

				for (int i = 0; i < actors.size; i++)
					filesView.addActor(actors.get(i));
			}
		}

		assetDragAndDrop.rebuild(filesView.getChildren());

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
			EditorScene scene = sceneIO.load(file);
			sceneTabsModule.open(scene);
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
	public void fileCreated (FileHandle file) {
	}

	@Override
	public void switchedTab (Tab tab) {
		if (tab instanceof DragAndDropTarget) {
			assetDragAndDrop.setDropTarget((DragAndDropTarget) tab);
			assetDragAndDrop.rebuild(filesView.getChildren());
		} else
			assetDragAndDrop.clear();
	}

	@Override
	public void removedTab (Tab tab) {
	}

	@Override
	public void removedAllTabs () {
	}

	enum FileType {
		UNKNOWN, TEXTURE, TTF_FONT, BMP_FONT_FILE, BMP_FONT_TEXTURE, MUSIC, SOUND, PARTICLE_EFFECT
	}

	private class AssetsPopupMenu extends PopupMenu {
		void build (FileItem item) {
			clearChildren();

			if (isOpenSupported(item.file.extension())) {
				addItem(MenuUtils.createMenuItem("Open", () -> openFile(item.file)));
			}

			addItem(MenuUtils.createMenuItem("Copy", () -> DialogUtils.showOKDialog(getStage(), "Message", "Not implemented yet!")));
			addItem(MenuUtils.createMenuItem("Paste", () -> DialogUtils.showOKDialog(getStage(), "Message", "Not implemented yet!")));
			addItem(MenuUtils.createMenuItem("Move", () -> DialogUtils.showOKDialog(getStage(), "Message", "Not implemented yet!")));
			addItem(MenuUtils.createMenuItem("Rename", () -> DialogUtils.showOKDialog(getStage(), "Message", "Not implemented yet!")));
			addItem(MenuUtils.createMenuItem("Delete", () -> showDeleteDialog(item.file)));
		}

		private void showDeleteDialog (FileHandle file) {
			boolean canBeSafeDeleted = assetsUsageAnalyzer.canAnalyze(file);
			getStage().addActor(new DeleteDialog(file, canBeSafeDeleted, result -> {
				if (canBeSafeDeleted == false) {
					FileUtils.delete(file);
					return;
				}

				if (result.safeDelete) {
					AssetsUsages usages = assetsUsageAnalyzer.analyze(file);
					if (usages.count == 0)
						FileUtils.delete(file);
					else
						quickAccessModule.addTab(new AssetsUsageTab(assetsUsageAnalyzer, sceneTabsModule, usages));
				} else
					FileUtils.delete(file);
			}));
		}
	}

	public class FileItem extends Table {
		FileHandle file;

		TextureRegion region;
		FileType type;

		VisLabel name;

		public FileItem (FileHandle file) {
			super(VisUI.getSkin());
			this.file = file;

			setTouchable(Touchable.enabled);

			createContent();

			setBackground("menu-bg");
			name.setWrap(true);
			name.setAlignment(Align.center);
			add(name).expandX().fillX();

			addListener();
		}

		private void createContent () {
			String ext = file.extension();
			String relativePath = fileAccess.relativizeToAssetsFolder(file);

			if (ext.equals("ttf")) {
				createDefaultView(FileType.TTF_FONT, "TTF Font", true);
				return;
			}

			if (ext.equals("fnt") && file.sibling(file.nameWithoutExtension() + ".png").exists()) {
				createDefaultView(FileType.BMP_FONT_FILE, "BMP Font", true);
				return;
			}

			if (ext.equals("png") && file.sibling(file.nameWithoutExtension() + ".fnt").exists()) {
				createDefaultView(FileType.BMP_FONT_TEXTURE, "BMP Font Texture", true);
				return;
			}

			if (ext.equals("p")) {
				createDefaultView(FileType.PARTICLE_EFFECT, "Particle Effect", true);
				return;
			}

			if (relativePath.startsWith("music") && (ext.equals("wav") || ext.equals("ogg") || ext.equals("mp3"))) {
				createDefaultView(FileType.MUSIC, "Music");
				return;
			}

			if (relativePath.startsWith("sound") && (ext.equals("wav") || ext.equals("ogg") || ext.equals("mp3"))) {
				createDefaultView(FileType.SOUND, "Sound");
				return;
			}

			if (relativePath.startsWith("gfx") && (ext.equals("jpg") || ext.equals("png"))) {
				type = FileType.TEXTURE;

				name = new VisLabel(file.nameWithoutExtension(), "small");
				TextureRegion region = textureCache.getRegion(relativePath);

				Image img = new Image(region);
				img.setScaling(Scaling.fit);
				add(img).expand().fill().row();

				this.region = region;

				return;
			}

			type = FileType.UNKNOWN;
			name = new VisLabel(file.name());
		}

		private void createDefaultView (FileType type, String itemTypeName) {
			createDefaultView(type, itemTypeName, false);
		}

		private void createDefaultView (FileType type, String itemTypeName, boolean hideExtension) {
			this.type = type;

			VisLabel tagLabel = new VisLabel((hideExtension ? "" : file.extension().toUpperCase() + " ") + itemTypeName, Color.GRAY);
			tagLabel.setWrap(true);
			tagLabel.setAlignment(Align.center);
			add(tagLabel).expandX().fillX().row();
			name = new VisLabel(file.nameWithoutExtension());
		}

		private void addListener () {
			addListener(new InputListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					if (button == Buttons.RIGHT) popupMenu.build(FileItem.this);
					return false;
				}
			});

			addListener(popupMenu.getDefaultInputListener());

			addListener(new ClickListener() {
				@Override
				public void clicked (InputEvent event, float x, float y) {
					super.clicked(event, x, y);

					if (getTapCount() == 2) openFile(file);
				}

			});
		}
	}

	private class FolderItem extends Table {
		public FileHandle file;
		private VisLabel name;

		public FolderItem (FileHandle file) {
			this.file = file;
			name = new VisLabel(file.name(), "small");
			name.setEllipsis(true);
			add(new Image(VisUI.getSkin().getDrawable("icon-folder"))).size(20).padTop(3);
			add(name).expand().fill().padRight(6);
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
