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
		LOAD, SAVE
	};

	public enum SelectionMode {
		FILES, DIRECTORIES, FILES_AND_DIRECTORIES
	}

	private Mode mode;
	private SelectionMode selectionMode = SelectionMode.FILES;
	private boolean multiselectionEnabled = false;
	
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
	private VisTextButton chooseButton;

	private FileItem selectedItem;

	private VisTextField currentPath;

	public FileChooser (Stage parent, String title, Mode mode) {
		super(parent, title);
		this.mode = mode;

		style = new FileChooserStyle();
		setTitleAlignment(Align.left);

		setModal(true);
		setResizable(true);
		setMovable(true);

		cancelButton = new VisTextButton("Cancel");
		chooseButton = new VisTextButton("Choose");

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
		createButtons();

		buildShortcutsList();

		setDirectory(System.getProperty("user.home"));
		setSize(500, 600);
		setPositionToCenter();
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
		rebuildList();
	}

	public void setDirectory (File directory) {
		currentDirectory = directory;
		rebuildList();
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
		fileScrollPane = createScrollPane(fileTable);
		fileScrollPaneTable = new VisTable();
		fileScrollPaneTable.add(fileScrollPane).pad(2).top().expand().fillX();

		shortcutsTable = new VisTable();
		shorcutsScrollPane = createScrollPane(shortcutsTable);
		shortcutsScrollPaneTable = new VisTable();
		shortcutsScrollPaneTable.add(shorcutsScrollPane).pad(2).top().expand().fillX();
	}

	private void crateFileTextBox () {
		VisTable table = new VisTable(true);
		VisLabel nameLabel = new VisLabel("File name:");
		VisTextField textBox = new VisTextField();

		table.add(nameLabel);
		table.add(textBox).expand().fill();

		add(table).expand().fill().pad(3).padRight(2).padBottom(2f);
		row();
	}

	private void createButtons () {
		VisTable buttonTable = new VisTable(true);
		buttonTable.defaults().minWidth(70).right();
		add(buttonTable).padTop(3).padBottom(3).padRight(2).fillX().expandX();

		buttonTable.add(cancelButton).expand().right();
		buttonTable.add(chooseButton);
	}

	private VisScrollPane createScrollPane (VisTable table) {
		VisScrollPane scrollPane = new VisScrollPane(table);
		scrollPane.setOverscroll(false, true);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setScrollingDisabled(true, false);
		return scrollPane;
	}

	private void buildShortcutsList () {
		shortcutsTable.clear();

		String userHome = System.getProperty("user.home");
		String userName = System.getProperty("user.name");

		shortcutsTable.add(new FileItem(fileSystemView.getHomeDirectory(), "Desktop", style.iconFolder)).expand().fill().row();
		shortcutsTable.add(new FileItem(new File(userHome), userName, style.iconFolder)).expand().fill().row();

		shortcutsTable.addSeparator();

		File[] roots = File.listRoots();

		for (int i = 0; i < roots.length; i++) {
			File root = roots[i];
			FileItem item = null;

			if (mode == Mode.LOAD ? root.canRead() : root.canWrite()) {
				String displayName = fileSystemView.getSystemDisplayName(root);

				if (displayName != null && displayName.equals("") == false)
					item = new FileItem(root, displayName, style.iconDrive);
				else
					item = new FileItem(root, root.toString(), style.iconDrive);

				shortcutsTable.add(item).expandX().fillX().row();
			}
		}

		shortcutsTable.addSeparator();

		//test
		shortcutsTable.add(new FileItem(null, "Favorite", style.iconFolder)).expand().fill().row();
	}

	private void rebuildList () {
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

	private class DefaultFileFilter implements FileFilter {
		@Override
		public boolean accept (File f) {
			if (f.isHidden()) return false;
			if (mode == Mode.LOAD ? f.canRead() == false : f.canWrite() == false) return false;
			if (f.isFile()) return true;
			if (f.list() == null) return false;

			return true;
		}
	}

	private class FileItem extends Table {
		private VisLabel name;
		private VisLabel size;
		public File file;

		/** Used only by shortcuts panel */
		public FileItem (final File file, String customName, Drawable icon) {
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

		/** Used only by file panel */
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
					highlight();
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

		private void highlight () {
			if (selectedItem != null) selectedItem.resetHighlight();
			selectedItem = FileItem.this;
			setBackground(highlightBg);
		}

		private void resetHighlight () {
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
