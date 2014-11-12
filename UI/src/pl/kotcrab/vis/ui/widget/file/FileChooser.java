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

package pl.kotcrab.vis.ui.widget.file;

import java.io.File;
import java.io.FileFilter;

import javax.swing.filechooser.FileSystemView;

import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.VisUI;
import pl.kotcrab.vis.ui.widget.VisLabel;
import pl.kotcrab.vis.ui.widget.VisScrollPane;
import pl.kotcrab.vis.ui.widget.VisSplitPane;
import pl.kotcrab.vis.ui.widget.VisTextButton;
import pl.kotcrab.vis.ui.widget.VisWindow;
import pl.kotcrab.vis.ui.widget.file.FileUtils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class FileChooser extends VisWindow {
	public enum Mode {
		LOAD, SAVE
	};

	private FileFilter fileFilter = new DefaultFileFilter();

	private File currentDirectory;
	private VisTable shortcutsTable;
	private VisTable fileTable;

	private LabelStyle highlightedLabelStyle;
	private LabelStyle normalLabelStyle;

	private VisTextButton cancelButton;
	private VisTextButton chooseButton;

	private FileItem highlitedItem;

	private Mode mode;

	public FileChooser (Stage parent, String title, Mode mode) {
		super(parent, title);
		this.mode = mode;

		setTitleAlignment(Align.left);

		normalLabelStyle = VisUI.skin.get(LabelStyle.class);
		highlightedLabelStyle = createHighlightedStyle(normalLabelStyle);

		cancelButton = new VisTextButton("Cancel");
		chooseButton = new VisTextButton("Choose");

		createToolbar();

		shortcutsTable = new VisTable();
		fileTable = new VisTable();

		// debug();
		// fileTable.debug();

		VisTable fileScrollPaneTable = new VisTable();
		fileScrollPaneTable.add(createScrollPane(fileTable)).pad(2).top().expand().fillX();

		VisTable shortcutsScrollPane = new VisTable();
		shortcutsScrollPane.add(createScrollPane(shortcutsTable)).pad(2).top().expand().fillX();

		VisSplitPane splitPane = new VisSplitPane(shortcutsScrollPane, fileScrollPaneTable, false);
		splitPane.setSplitAmount(0.2f);

		row();
		add(splitPane).expand().fill();
		row();

		buildShortcutsList();
		createButtons();

		setDirectory(System.getProperty("user.home") + File.separator);
		setSize(500, 600);
		setPositionToCenter();
	}

	private VisScrollPane createScrollPane (VisTable table) {
		VisScrollPane scrollPane = new VisScrollPane(table);
		scrollPane.setOverscroll(false, true);
		scrollPane.setFadeScrollBars(false);
		return scrollPane;
	}

	private void createToolbar () {
		VisTable toolbarTable = new VisTable(true);
		toolbarTable.defaults().minWidth(70).right();
		add(toolbarTable).fillX().expandX();

		VisTextButton dirupButton = new VisTextButton("<-");
		toolbarTable.add(dirupButton);
		dirupButton.addListener(new ChangeListener() {

			@Override
			public void changed (ChangeEvent event, Actor actor) {
				File parent = currentDirectory.getParentFile();
				if (parent != null) setDirectory(parent);
			}
		});
	}

	private void createButtons () {
		VisTable buttonTable = new VisTable(true);
		buttonTable.defaults().minWidth(70).right();
		add(buttonTable).padTop(3).padBottom(3).padRight(2).fillX().expandX();

		buttonTable.add(cancelButton).expand().right();
		buttonTable.add(chooseButton);
	}

	private LabelStyle createHighlightedStyle (LabelStyle style) {
		LabelStyle newStyle = new LabelStyle();
		newStyle.background = VisUI.skin.getDrawable("list-selection");
		newStyle.font = style.font;
		newStyle.fontColor = style.fontColor;
		return newStyle;
	}

	public void setDirectory (String directory) {
		setDirectory(new File(directory));
	}

	public void setDirectory (FileHandle directory) {
		currentDirectory = directory.file();
		rebuildList();
	}

	public void setDirectory (File directory) {
		currentDirectory = directory;
		rebuildList();
	}

	private void buildShortcutsList () {
		File[] roots = File.listRoots();
		shortcutsTable.clear();

		for (int i = 0; i < roots.length; i++) {
			File root = roots[i];
			FileItem item = null;
			FileSystemView view = FileSystemView.getFileSystemView();

			if (mode == Mode.LOAD ? root.canRead() : root.canWrite()) {
				String displayName = view.getSystemDisplayName(root);

				if (displayName != null && displayName.equals("") == false)
					item = new FileItem(root, displayName);
				else
					item = new FileItem(root, root.toString());

				shortcutsTable.add(item).expand().fill().row();
			}
		}
	}

	private void rebuildList () {
		File[] files = currentDirectory.listFiles(fileFilter);
		Array<File> fileList = FileUtils.sortFiles(files);

		fileTable.clear();
		for (File f : fileList)
			if (f.isHidden() == false) fileTable.add(new FileItem(f)).expand().fill().row();
	}

	public FileFilter getFileFilter () {
		return fileFilter;
	}

	public void setFileFilter (FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	private class DefaultFileFilter implements FileFilter {

		@Override
		public boolean accept (File f) {
			if (f.isHidden()) return false;
			if (mode == Mode.LOAD ? f.canRead() == false : f.canWrite() == false) return false;
			if (f.list() == null) return false;
			return true;
		}
	}

	private class FileItem extends Table {
		private VisLabel name;
		private VisLabel size;
		public File file;

		public FileItem (File file, String customName) {
			this.file = file;
			this.name = new VisLabel(customName);

			add(name).expand().fill().left().padRight(6);
			pack();

			addListener();
		}

		public FileItem (File file) {
			this.file = file;
			this.name = new VisLabel(file.getName());

			if (file.isDirectory())
				size = new VisLabel("");
			else
				size = new VisLabel(FileUtils.readableFileSize(file.length()));

			add(name).expand().fill().left();
			add(size).padRight(6);
			pack();

			addListener();
		}

		private void addListener () {
			addListener(new ClickListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					highlightLabel();
					return super.touchDown(event, x, y, pointer, button);
				}

				@Override
				public void clicked (InputEvent event, float x, float y) {
					super.clicked(event, x, y);
					if (getTapCount() == 2) {
						File file = FileItem.this.file;
						if (file.isDirectory()) setDirectory(file.getAbsolutePath());
					}
				}
			});
		}

		private void highlightLabel () {
			if (highlitedItem != null) highlitedItem.resetHighlight();
			highlitedItem = FileItem.this;
			name.setStyle(highlightedLabelStyle);
			if (size != null) size.setStyle(highlightedLabelStyle);
		}

		private void resetHighlight () {
			name.setStyle(normalLabelStyle);
			if (size != null) size.setStyle(normalLabelStyle);
		}

	}

}
