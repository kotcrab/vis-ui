/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/

package pl.kotcrab.vis.ui.widget.file;

import java.io.File;
import java.io.FileFilter;

import javax.swing.filechooser.FileSystemView;

import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.VisUI;
import pl.kotcrab.vis.ui.widget.VisImageButton;
import pl.kotcrab.vis.ui.widget.VisLabel;
import pl.kotcrab.vis.ui.widget.VisScrollPane;
import pl.kotcrab.vis.ui.widget.VisSplitPane;
import pl.kotcrab.vis.ui.widget.VisTextButton;
import pl.kotcrab.vis.ui.widget.VisTextField;
import pl.kotcrab.vis.ui.widget.VisWindow;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

public class FileChooser extends VisWindow {
	private static final Drawable highlightBg = VisUI.skin.getDrawable("list-selection");

	public enum Mode {
		OPEN, SAVE
	};

	public enum SelectionMode {
		FILES, DIRECTORIES, FILES_AND_DIRECTORIES
	}

	private Mode mode;
	private SelectionMode selectionMode = SelectionMode.FILES;
	private boolean multiselectionEnabled = false;
	private FileChooserListener listener;

	private FileFilter fileFilter = new DefaultFileFilter();
	private File currentDirectory;

	private FileChooserStyle style;

	private FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	private VisSplitPane splitPane;

	private VisTable fileTable;
	private VisScrollPane fileScrollPane;
	private VisTable fileScrollPaneTable;

	private VisTable shortcutsTable;
	private VisScrollPane shorcutsScrollPane;
	private VisTable shortcutsScrollPaneTable;

	private VisTextButton cancelButton;
	private VisTextButton confirmButton;

	private Array<FileItem> selectedItems = new Array<FileItem>();
	private ShortcutItem selectedShortcut;

	private VisTextField currentPath;
	private VisTextField selectedFileTextBox;

	public FileChooser (Stage parent, String title, Mode mode) {
		super(parent, title);
		this.mode = mode;

		style = new FileChooserStyle();
		setTitleAlignment(Align.left);

		setModal(true);
		setResizable(true);
		setMovable(true);

		cancelButton = new VisTextButton("Cancel");
		confirmButton = new VisTextButton(mode == Mode.OPEN ? "Open" : "Save");

		createToolbar();

		createCenterContentPanel();

		splitPane = new VisSplitPane(shortcutsScrollPaneTable, fileScrollPaneTable, false);
		splitPane.setSplitAmount(0.3f);
		splitPane.setMinSplitAmount(0.05f);
		splitPane.setMaxSplitAmount(0.8913f);

		row();
		add(splitPane).expand().fill();
		row();

		crateFileTextBox();
		createBottomButtons();

		rebuildShortcutsList();

		setDirectory(System.getProperty("user.home"));
		setSize(500, 600);
		setPositionToCenter();

		validateSettings();
	}

	public Mode getMode () {
		return mode;
	}

	public void setMode (Mode mode) {
		this.mode = mode;
	}

	public void setDirectory (String directory) {
		setDirectory(new File(directory));
	}

	public void setDirectory (FileHandle directory) {
		currentDirectory = directory.file();
		rebuildFileList();
	}

	public void setDirectory (File directory) {
		currentDirectory = directory;
		rebuildFileList();
	}

	public FileFilter getFileFilter () {
		return fileFilter;
	}

	public void setFileFilter (FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public SelectionMode getSelectionMode () {
		return selectionMode;
	}

	public void setSelectionMode (SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
	}

	public boolean isMultiselectionEnabled () {
		return multiselectionEnabled;
	}

	public void setMultiselectionEnabled (boolean multiselectionEnabled) {
		this.multiselectionEnabled = multiselectionEnabled;
	}

	public void setListener (FileChooserListener listener) {
		this.listener = listener;
		validateSettings();
	}

	private void validateSettings () {
		if (listener == null) listener = new FileChooserAdapter();
	}

	private void createToolbar () {
		VisTable toolbarTable = new VisTable(true);
		toolbarTable.defaults().minWidth(30).right();
		add(toolbarTable).fillX().expandX().pad(3).padRight(2);

		VisImageButton backButton = new VisImageButton(style.iconArrowLeft);
		VisImageButton forwardButton = new VisImageButton(style.iconArrowRight);
		forwardButton.setDisabled(true);
		forwardButton.setGeneateDisabledImage(true);

		currentPath = new VisTextField();

		toolbarTable.add(backButton);
		toolbarTable.add(forwardButton);
		toolbarTable.add(currentPath).expand().fill();

		backButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				File parent = currentDirectory.getParentFile();
				if (parent != null) setDirectory(parent);
			}
		});
	}

	private void createCenterContentPanel () {
		// fileTable is contained in fileScrollPane contained in fileScrollPaneTable contained in splitPane
		// same for shortcuts
		fileTable = new VisTable();
		fileScrollPane = getScrollPane(fileTable);
		fileScrollPaneTable = new VisTable();
		fileScrollPaneTable.add(fileScrollPane).pad(2).top().expand().fillX();

		shortcutsTable = new VisTable();
		shorcutsScrollPane = getScrollPane(shortcutsTable);
		shortcutsScrollPaneTable = new VisTable();
		shortcutsScrollPaneTable.add(shorcutsScrollPane).pad(2).top().expand().fillX();
	}

	private void crateFileTextBox () {
		VisTable table = new VisTable(true);
		VisLabel nameLabel = new VisLabel("File name:");
		selectedFileTextBox = new VisTextField();

		table.add(nameLabel);
		table.add(selectedFileTextBox).expand().fill();

		add(table).expandX().fillX().pad(3).padRight(2).padBottom(2f);
		row();

	}

	private void createBottomButtons () {
		VisTable buttonTable = new VisTable(true);
		buttonTable.defaults().minWidth(70).right();
		add(buttonTable).padTop(3).padBottom(3).padRight(2).fillX().expandX();

		buttonTable.add(cancelButton).expand().right();
		buttonTable.add(confirmButton);

		cancelButton.addListener(new ChangeListener() {

			@Override
			public void changed (ChangeEvent event, Actor actor) {
				hide();
				listener.canceled();
			}
		});

		confirmButton.addListener(new ChangeListener() {

			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (selectedItems.size > 0) {
					hide();

					listener.selected(getFileListFromSelected());
					listener.selected(selectedItems.get(0).file);
				} else {
					Dialog dialog = new Dialog("Message", VisUI.skin);
					dialog.text("You must choose a file!");
					dialog.button("OK");
					dialog.pack();
					getStage().addActor(dialog);
				}
			}
		});
	}

	private VisScrollPane getScrollPane (VisTable table) {
		VisScrollPane scrollPane = new VisScrollPane(table);
		scrollPane.setOverscroll(false, true);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setScrollingDisabled(true, false);
		return scrollPane;
	}

	private Array<File> getFileListFromSelected () {
		Array<File> list = new Array<File>();

		for (FileItem f : selectedItems)
			list.add(f.file);

		return list;
	}

	private void rebuildShortcutsList () {
		shortcutsTable.clear();

		String userHome = System.getProperty("user.home");
		String userName = System.getProperty("user.name");

		shortcutsTable.add(new ShortcutItem(fileSystemView.getHomeDirectory(), "Desktop", style.iconFolder)).expand().fill().row();
		shortcutsTable.add(new ShortcutItem(new File(userHome), userName, style.iconFolder)).expand().fill().row();

		shortcutsTable.addSeparator();

		File[] roots = File.listRoots();

		for (int i = 0; i < roots.length; i++) {
			File root = roots[i];
			ShortcutItem item = null;

			if (mode == Mode.OPEN ? root.canRead() : root.canWrite()) {
				String displayName = fileSystemView.getSystemDisplayName(root);

				if (displayName != null && displayName.equals("") == false)
					item = new ShortcutItem(root, displayName, style.iconDrive);
				else
					item = new ShortcutItem(root, root.toString(), style.iconDrive);

				shortcutsTable.add(item).expandX().fillX().row();
			}
		}

		shortcutsTable.addSeparator();

		// test
		shortcutsTable.add(new ShortcutItem(new File(System.getProperty("user.home")), "Favorite", style.iconFolder)).expand()
			.fill().row();
	}

	private void rebuildFileList () {
		deselectAll();

		fileTable.clear();
		File[] files = currentDirectory.listFiles(fileFilter);
		currentPath.setText(currentDirectory.getAbsolutePath());

		if (files.length == 0) return;

		Array<File> fileList = FileUtils.sortFiles(files);

		for (File f : fileList)
			if (f.isHidden() == false) fileTable.add(new FileItem(f, null)).expand().fill().row();

		fileScrollPane.setScrollX(0);
		fileScrollPane.setScrollY(0);

		// because stupid scroll pane returns not valid size of taken space we calculate it manually,
		// if taken space if bigger than available (scroll bars will be showed) then add padding
		float scrollHeightY = files.length * fileTable.getCells().get(0).getPrefHeight();
		if (scrollHeightY > fileScrollPaneTable.getHeight()) {
			for (Cell<?> c : fileTable.getCells())
				c.padRight(22);

			fileTable.invalidate();
		}
	}

	public void setVisble (boolean visible) {
		if (isVisible() == false && visible) deselectAll(); // reset selected item when dialog is changed from invisible to visible

		super.setVisible(visible);
	}

	private void setSelectedFileTextField () {

		if (selectedItems.size == 0)
			selectedFileTextBox.setText("");
		else if (selectedItems.size == 1)
			selectedFileTextBox.setText(selectedItems.get(0).file.getName());
		else {
			StringBuilder b = new StringBuilder();

			for (FileItem item : selectedItems) {
				b.append('"');
				b.append(item.file.getName());
				b.append("\" ");
			}

			selectedFileTextBox.setText(b.toString());
		}
	}

	private void deselectAll () {
		for (FileItem item : selectedItems)
			item.deselect();

		selectedFileTextBox.setText("");
	}

	private class DefaultFileFilter implements FileFilter {
		@Override
		public boolean accept (File f) {
			if (f.isHidden()) return false;
			if (mode == Mode.OPEN ? f.canRead() == false : f.canWrite() == false) return false;
			if (f.isFile()) return true;
			if (f.list() == null) return false;

			return true;
		}
	}

	public void hide () {
		remove();
		// addAction(sequence(fadeOut(0.1f, Interpolation.fade), Actions.removeActor()));
	}

	private class FileItem extends Table {
		private VisLabel name;
		private VisLabel size;
		public File file;

		public FileItem (final File file, Drawable icon) {
			this.file = file;
			name = new VisLabel(file.getName());
			name.setEllipse(true);

			if (file.isDirectory())
				size = new VisLabel("");
			else
				size = new VisLabel(FileUtils.readableFileSize(file.length()));

			if (icon == null && file.isDirectory()) icon = style.iconFolder;

			if (icon != null) add(new Image(icon)).padTop(3);
			Cell<VisLabel> labelCell = add(name).padLeft(icon == null ? 22 : 0);

			labelCell.width(new Value() {
				@Override
				public float get (Actor context) {
					int padding = file.isDirectory() ? 35 : 60;
					return fileScrollPaneTable.getWidth() - getUsedWidth() - padding;
				}
			});

			add(size).expandX().right().padRight(6);

			addListener();
		}

		private int getUsedWidth () {
			@SuppressWarnings("rawtypes")
			Array<Cell> cells = getCells();

			int width = 0;
			for (Cell<?> cell : cells) {
				if (cell.getActor() == name) continue;
				width += cell.getActor().getWidth();
			}

			return width;
		}

		private void addListener () {
			addListener(new ClickListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					deselectAll();
					selectedShortcut.deselect();
					select();
					setSelectedFileTextField();
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

		private void select () {
			setBackground(highlightBg);
			selectedItems.add(this);
		}

		private void deselect () {
			setBackground((Drawable)null);
			selectedItems.removeValue(this, true);
		}
	}

	private class ShortcutItem extends Table {
		private VisLabel name;
		public File file;

		/** Used only by shortcuts panel */
		public ShortcutItem (final File file, String customName, Drawable icon) {
			this.file = file;
			name = new VisLabel(customName);
			name.setEllipse(true);
			add(new Image(icon)).padTop(3);
			Cell<VisLabel> labelCell = add(name).expand().fill().padRight(6);

			labelCell.width(new Value() {
				@Override
				public float get (Actor context) {
					return shortcutsScrollPaneTable.getWidth() - getUsedWidth() - 10;
				}
			});

			addListener();
		}

		private int getUsedWidth () {
			@SuppressWarnings("rawtypes")
			Array<Cell> cells = getCells();

			int width = 0;
			for (Cell<?> cell : cells) {
				if (cell.getActor() == name) continue;
				width += cell.getActor().getWidth();
			}

			return width;
		}

		private void addListener () {

			addListener(new ClickListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					deselectAll();
					setSelectedFileTextField();
					select();
					return super.touchDown(event, x, y, pointer, button);
				}

				@Override
				public void clicked (InputEvent event, float x, float y) {
					super.clicked(event, x, y);
					if (getTapCount() == 1) {
						File file = ShortcutItem.this.file;
						if (file.isDirectory()) setDirectory(file.getAbsolutePath());
					}
				}
			});
		}

		private void select () {
			if (selectedShortcut != null) selectedShortcut.deselect();
			selectedShortcut = ShortcutItem.this;
			setBackground(highlightBg);
		}

		private void deselect () {
			setBackground((Drawable)null);
		}

	}

	static public class FileChooserStyle {
		public Drawable iconArrowLeft;
		public Drawable iconArrowRight;
		public Drawable iconFolder;
		public Drawable iconDrive;

		public FileChooserStyle () {
			iconArrowLeft = VisUI.skin.getDrawable("icon-arrow-left");
			iconArrowRight = VisUI.skin.getDrawable("icon-arrow-right");
			iconFolder = VisUI.skin.getDrawable("icon-folder");
			iconDrive = VisUI.skin.getDrawable("icon-drive");
		}
	}

}
