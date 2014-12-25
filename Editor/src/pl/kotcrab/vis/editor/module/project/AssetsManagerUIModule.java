/**
 * Copyright 2014 Pawel Pastuszak
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

package pl.kotcrab.vis.editor.module.project;

import java.io.File;
import java.io.FileFilter;

import pl.kotcrab.vis.editor.Assets;
import pl.kotcrab.vis.editor.Editor;
import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.VisUI;
import pl.kotcrab.vis.ui.widget.Separator;
import pl.kotcrab.vis.ui.widget.VisImageButton;
import pl.kotcrab.vis.ui.widget.VisLabel;
import pl.kotcrab.vis.ui.widget.VisScrollPane;
import pl.kotcrab.vis.ui.widget.VisSplitPane;
import pl.kotcrab.vis.ui.widget.VisTextField;
import pl.kotcrab.vis.ui.widget.VisTree;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;

@SuppressWarnings("rawtypes")
public class AssetsManagerUIModule extends ProjectModule {
	private Editor editor;
	private VisTable editorTable;

	private FileAccessModule fileAccess;
	private FileHandle visFolder;
	private FileHandle assetsFolder;

	private VisTable treeTable;
	private FilesItemsTable filesTable;
	private VisTable toolbarTable;

	private VisTree contentTree;

	private VisLabel contentTtileLabel;

	private int itemSize = 92;
	private VisTable contentsTable;
	private VisTextField searchTextField;
	private FileHandle currenDirectory;

	public AssetsManagerUIModule () {
		this.editor = Editor.instance;
		editorTable = editor.getProjectContentTable();
		editorTable.setBackground("window-bg");
	}

	@Override
	public void init () {
		fileAccess = containter.get(FileAccessModule.class);
		visFolder = fileAccess.getVisFolder();
		assetsFolder = fileAccess.getAssetsFolder();

		treeTable = new VisTable(true);
		toolbarTable = new VisTable(true);
		filesTable = new FilesItemsTable(false);

		contentsTable = new VisTable(false);
		contentsTable.add(toolbarTable).expandX().fillX().pad(3).padBottom(0);
		contentsTable.row();
		contentsTable.add(new Separator()).padTop(3).expandX().fillX();
		contentsTable.row();
		contentsTable.add(createScrollPane(filesTable, true)).expand().fill();

		VisSplitPane splitPane = new VisSplitPane(treeTable, contentsTable, false);
		splitPane.setSplitAmount(0.2f);

		createToolbarTable();
		createContentTree();

		editorTable.clear();
		editorTable.add(splitPane).expand().fill();

		rebuildFolderTree();
		contentTree.getSelection().set(contentTree.getNodes().get(0)); // select first item in tree
	}

	private void createToolbarTable () {
		contentTtileLabel = new VisLabel("Content");
		searchTextField = new VisTextField();
		
		toolbarTable.add(contentTtileLabel).expand().left().padLeft(3);
		toolbarTable.add(new VisImageButton(Assets.getIcon("folder-open")));
		toolbarTable.add(new VisImageButton(Assets.getIcon("settings-view")));
		toolbarTable.add(new VisImageButton(Assets.getIcon("import")));
		toolbarTable.add(new Image(Assets.getIcon("search"))).spaceRight(3);

		toolbarTable.add(searchTextField).width(200);

		searchTextField.addListener(new InputListener() {
			@Override
			public boolean keyTyped (InputEvent event, char character) {
				refreshFilesList();
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
					FolderItem item = (FolderItem)node.getActor();
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
		this.currenDirectory = directory;
		disposeFilesTableCells();
		filesTable.clear();
		filesTable.top().left();

		FileHandle[] files = directory.list(new FileFilter() {

			@Override
			public boolean accept (File file) {
				if (searchTextField.getText().equals("")) return true;

				return file.getName().contains(searchTextField.getText());
			}
		});

		Array<Actor> actors = new Array<Actor>(files.length);

		for (int i = 0; i < files.length; i++) {
			FileHandle file = files[i];

			if (file.isDirectory() == false) {
				actors.add(new FileItem(file));
				rebuildFilesList(actors);
			}
		}

		String currentPath = directory.path().substring(visFolder.path().length() + 1);
		contentTtileLabel.setText("Content [" + currentPath + "]");
	}

	private void refreshFilesList () {
		changeCurrentDirectory(currenDirectory);
	}

	private void disposeFilesTableCells () {
		Array<Cell> cells = filesTable.getCells();
		for (Cell c : cells)
			((Disposable)c.getActor()).dispose();
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

		Node mainNode = new Node(new FolderItem(assetsFolder, "Assets"));
		mainNode.setExpanded(true);
		contentTree.add(mainNode);

		processFolder(mainNode, assetsFolder);
	}

	private void processFolder (Node node, FileHandle dir) {
		FileHandle[] files = dir.list();

		for (int i = 0; i < files.length; i++) {
			FileHandle file = files[i];

			if (file.isDirectory()) {
				Node currentNode = new Node(new FolderItem(file, file.name()));
				node.add(currentNode);

				processFolder(currentNode, file);
			}
		}
	}

	private class FileItem extends Table implements Disposable {
		private Texture texture;

		public FileItem (FileHandle file) {
			super(VisUI.skin);
			VisLabel name = new VisLabel(file.name());

			if (file.extension().equals("jpg") || file.extension().equals("png")) {
				texture = new Texture(file);

				Image img = new Image(texture);
				img.setScaling(Scaling.fit);
				add(img).row();
			}

			setBackground("menu-bg");
			name.setWrap(true);
			name.setAlignment(Align.center);
			add(name).expand().fill();
		}

		@Override
		public void dispose () {
			if (texture != null) texture.dispose();
		}
	}

	private class FolderItem extends Table {
		private VisLabel name;
		public FileHandle file;

		public FolderItem (final FileHandle file, String customName) {
			this.file = file;
			name = new VisLabel(customName);
			name.setEllipsis(true);
			add(new Image(VisUI.skin.getDrawable("icon-folder"))).padTop(3);
			add(name).expand().fill().padRight(6);
		}
	}

	private class FilesItemsTable extends VisTable {
		public FilesItemsTable (boolean setVisDefautls) {
			super(setVisDefautls);
		}

		@Override
		protected void sizeChanged () {
			Array<Cell> cells = filesTable.getCells();
			Array<Actor> actors = new Array<Actor>(cells.size);

			for (Cell c : cells)
				actors.add(c.getActor());

			rebuildFilesList(actors);
		}
	}
}
