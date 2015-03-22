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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
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
import com.kotcrab.vis.editor.ui.tabbedpane.Tab;
import com.kotcrab.vis.editor.ui.tabbedpane.TabbedPaneListener;
import com.kotcrab.vis.editor.util.DirectoriesOnlyFileFilter;
import com.kotcrab.vis.editor.util.DirectoryWatcher;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.MenuUtils;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.Separator;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTree;

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
	private int itemSize = 92;

	private VisTable mainTable;
	private VisTable treeTable;
	private FilesItemsTable filesTable;
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

		assetDragAndDrop = new AssetDragAndDrop(fontCache, particleCache, fileAccess);
	}

	private void initUI () {
		treeTable = new VisTable(true);
		toolbarTable = new VisTable(true);
		filesTable = new FilesItemsTable(false);

		VisTable contentsTable = new VisTable(false);
		contentsTable.add(toolbarTable).expandX().fillX().pad(3).padBottom(0);
		contentsTable.row();
		contentsTable.add(new Separator()).padTop(3).expandX().fillX();
		contentsTable.row();
		contentsTable.add(createScrollPane(filesTable, true)).expand().fill();

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
		treeTable.add(createScrollPane(contentTree, false)).expand().fill();

		contentTree.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Node node = contentTree.getSelection().first();

				if (node != null) {
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
		filesTable.clear();
		filesTable.top().left();
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

				rebuildFilesList(actors);
			}
		}

		assetDragAndDrop.rebuild(getActorsList());

		String currentPath = directory.path().substring(visFolder.path().length() + 1);
		contentTitleLabel.setText("Content [" + currentPath + "]");
	}

	private void refreshFilesList () {
		changeCurrentDirectory(currentDirectory);
	}

	private void rebuildFilesList (Array<Actor> actors) {
		filesTable.reset();
		filesTable.top().left();
		filesTable.defaults().pad(4);

		float maxWidth = filesTable.getWidth();
		float currentWidth = 0;
		float padding = filesTable.defaults().getPadLeft() + filesTable.defaults().getPadRight();
		float itemTotalSize = itemSize + padding + 2;

		for (int i = 0; i < actors.size; i++) {
			filesTable.add(actors.get(i)).size(itemSize);
			currentWidth += itemTotalSize;
			if (currentWidth + itemTotalSize >= maxWidth) {
				currentWidth = 0;
				filesTable.row();
			}
		}
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

	private Array<Actor> getActorsList () {
		Array<Cell> cells = filesTable.getCells();
		Array<Actor> actors = new Array<>(cells.size);

		for (Cell c : cells)
			actors.add(c.getActor());

		return actors;
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
		//altthoug fileChanged covers 'delete' event, that event is sent before the actual file is deleted from disk,
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
			assetDragAndDrop.rebuild(getActorsList());
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
		UNKNOWN, TEXTURE, TTF_FONT, BMP_FONT_FILE, BMP_FONT_TEXTURE, MUSIC, PARTICLE_EFFECT
	}

	private class AssetsPopupMenu extends PopupMenu {
		void build (FileItem item) {
			clearChildren();

			if (isOpenSupported(item.file.extension())) {
				addItem(MenuUtils.createMenuItem("Open", () -> openFile(item.file)));
			}

			addItem(MenuUtils.createMenuItem("Copy", () -> {
			}));
			addItem(MenuUtils.createMenuItem("Paste", () -> {
			}));
			addItem(MenuUtils.createMenuItem("Move", () -> {
			}));
			addItem(MenuUtils.createMenuItem("Rename", () -> {
			}));
			addItem(MenuUtils.createMenuItem("Delete", () -> showDeleteDialog(item.file)));
		}

		private void showDeleteDialog (FileHandle file) {
			getStage().addActor(new DeleteDialog(file, assetsUsageAnalyzer.canAnalyze(file), result -> {
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
			if (ext.equals("ttf")) {
				type = FileType.TTF_FONT;

				add(new VisLabel("TTF Font", Color.GRAY)).row();
				name = new VisLabel(file.nameWithoutExtension());

				return;
			}

			if (ext.equals("fnt") && file.sibling(file.nameWithoutExtension() + ".png").exists()) {
				type = FileType.BMP_FONT_FILE;

				add(new VisLabel("BMP Font", Color.GRAY)).row();
				name = new VisLabel(file.nameWithoutExtension());

				return;
			}

			if (ext.equals("png") && file.sibling(file.nameWithoutExtension() + ".fnt").exists()) {
				type = FileType.BMP_FONT_TEXTURE;

				VisLabel tagLabel = new VisLabel("BMP Font Texture", Color.GRAY);
				tagLabel.setWrap(true);
				tagLabel.setAlignment(Align.center);
				add(tagLabel).expandX().fillX().row();
				name = new VisLabel(file.nameWithoutExtension());

				return;
			}

			if (ext.equals("p")) {
				type = FileType.PARTICLE_EFFECT;

				VisLabel tagLabel = new VisLabel("Particle Effect", Color.GRAY);
				tagLabel.setWrap(true);
				tagLabel.setAlignment(Align.center);
				add(tagLabel).expandX().fillX().row();
				name = new VisLabel(file.nameWithoutExtension());

				return;
			}

			if (fileAccess.relativizeToAssetsFolder(file).startsWith("music") && ext.equals("wav") || ext.equals("ogg") || ext.equals("mp3")) {
				type = FileType.MUSIC;

				VisLabel tagLabel = new VisLabel(file.extension().toUpperCase() + " Music", Color.GRAY);
				tagLabel.setWrap(true);
				tagLabel.setAlignment(Align.center);
				add(tagLabel).expandX().fillX().row();
				name = new VisLabel(file.nameWithoutExtension());

				return;
			}

			if (fileAccess.relativizeToAssetsFolder(file).startsWith("gfx") && (ext.equals("jpg") || ext.equals("png"))) {
				type = FileType.TEXTURE;

				name = new VisLabel(file.nameWithoutExtension(), "small");
				TextureRegion region = textureCache.getRegion(file);

				Image img = new Image(region);
				img.setScaling(Scaling.fit);
				add(img).expand().fill().row();

				this.region = region;

				return;
			}

			type = FileType.UNKNOWN;
			name = new VisLabel(file.name());
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

	private class FilesItemsTable extends VisTable {
		public FilesItemsTable (boolean setVisDefaults) {
			super(setVisDefaults);
		}

		@Override
		protected void sizeChanged () {
			rebuildFilesList(getActorsList());
		}
	}
}
