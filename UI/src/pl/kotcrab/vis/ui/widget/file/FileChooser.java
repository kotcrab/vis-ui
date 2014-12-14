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
import pl.kotcrab.vis.ui.widget.VisDialog;
import pl.kotcrab.vis.ui.widget.VisImageButton;
import pl.kotcrab.vis.ui.widget.VisLabel;
import pl.kotcrab.vis.ui.widget.VisScrollPane;
import pl.kotcrab.vis.ui.widget.VisSplitPane;
import pl.kotcrab.vis.ui.widget.VisTextButton;
import pl.kotcrab.vis.ui.widget.VisTextField;
import pl.kotcrab.vis.ui.widget.VisWindow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

/** Chooser for files, before using {@link FileChooser#setFavoritesPrefsName(String)} should be called
 * @author Pawel Pastuszak */
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
	private int multiselectKey = Keys.CONTROL_LEFT;

	private FileFilter fileFilter = new DefaultFileFilter();
	private FileHandle currentDirectory;

	private FileChooserStyle style;
	private FileChooserLocale locale;

	private FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	private Array<FileItem> selectedItems = new Array<FileItem>();
	private ShortcutItem selectedShortcut;

	private Array<FileHandle> history;
	private int historyIndex = 0;

	private FavoritesIO favoritesIO;
	private Array<FileHandle> favorites;

	private Array<ShortcutItem> fileRootsCache = new Array<ShortcutItem>();

	// UI
	private VisSplitPane splitPane;

	private VisTable fileTable;
	private VisScrollPane fileScrollPane;
	private VisTable fileScrollPaneTable;

	private VisTable shortcutsTable;
	private VisScrollPane shorcutsScrollPane;
	private VisTable shortcutsScrollPaneTable;

	private VisTextButton cancelButton;
	private VisTextButton confirmButton;

	private VisImageButton backButton;
	private VisImageButton forwardButton;
	private VisTextField currentPath;
	private VisTextField selectedFileTextField;

	private FilePopupMenu fileMenu;

	public static String getFavoritesPrefsName () {
		return FavoritesIO.getFavoritesPrefsName();
	}

	/** Sets file name that will be used to store favorites, if not changed default will be used that may be shared with other
	 * programs, should be package name e.g. com.seriouscompay.seriousprogram */
	public static void setFavoritesPrefsName (String name) {
		FavoritesIO.setFavoritesPrefsName(name);
	}

	public FileChooser (Mode mode) {
		super("");

		this.locale = new FileChooserLocale();
		this.mode = mode;

		setTitle(locale.title);

		init();
	}

	public FileChooser (String title, Mode mode) {
		super(title);
		this.mode = mode;
		this.locale = new FileChooserLocale();

		init();
	}

	public FileChooser (FileChooserLocale locale, Mode mode) {
		super(locale.title);
		this.mode = mode;
		this.locale = locale;

		init();
	}

	public FileChooser (FileChooserLocale locale, String title, Mode mode) {
		super(title);
		this.mode = mode;
		this.locale = locale;

		init();
	}

	private void init () {
		style = new FileChooserStyle();

		setModal(true);
		setResizable(true);
		setMovable(true);

		favoritesIO = new FavoritesIO();
		favorites = favoritesIO.loadFavorites();

		createToolbar();
		createCenterContentPanel();
		createFileTextBox();
		createBottomButtons();

		fileMenu = new FilePopupMenu(this, locale);

		rebuildShortcutsList();

		setDirectory(System.getProperty("user.home"));
		setSize(500, 600);
		centerWindow();

		validateSettings();
	}

	private void createToolbar () {
		VisTable toolbarTable = new VisTable(true);
		toolbarTable.defaults().minWidth(30).right();
		add(toolbarTable).fillX().expandX().pad(3).padRight(2);

		backButton = new VisImageButton(style.iconArrowLeft);
		forwardButton = new VisImageButton(style.iconArrowRight);
		forwardButton.setDisabled(true);
		forwardButton.setGenerateDisabledImage(true);
		backButton.setGenerateDisabledImage(true);

		currentPath = new VisTextField();

		currentPath.addListener(new InputListener() {
			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					FileHandle file = Gdx.files.absolute(currentPath.getText());
					if (file.exists())
						setDirectory(file);
					else {
						showDialog(locale.popupDirectoryDoesNotExist);
						currentPath.setText(currentDirectory.path());
					}
				}
				return false;
			}
		});

		toolbarTable.add(backButton);
		toolbarTable.add(forwardButton);
		toolbarTable.add(currentPath).expand().fill();

		backButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				historyBack();
			}
		});

		forwardButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				historyForward();
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

		splitPane = new VisSplitPane(shortcutsScrollPaneTable, fileScrollPaneTable, false);
		splitPane.setSplitAmount(0.3f);
		splitPane.setMinSplitAmount(0.05f);
		splitPane.setMaxSplitAmount(0.8913f);

		row();
		add(splitPane).expand().fill();
		row();
	}

	private void createFileTextBox () {
		VisTable table = new VisTable(true);
		VisLabel nameLabel = new VisLabel(locale.fileName);
		selectedFileTextField = new VisTextField();

		table.add(nameLabel);
		table.add(selectedFileTextField).expand().fill();

		selectedFileTextField.addListener(new InputListener() {
			@Override
			public boolean keyTyped (InputEvent event, char character) {
				deselectAll(false);
				return false;
			}
		});

		add(table).expandX().fillX().pad(3).padRight(2).padBottom(2f);
		row();

	}

	private void createBottomButtons () {
		cancelButton = new VisTextButton(locale.cancel);
		confirmButton = new VisTextButton(mode == Mode.OPEN ? locale.open : locale.save);

		VisTable buttonTable = new VisTable(true);
		buttonTable.defaults().minWidth(70).right();
		add(buttonTable).padTop(3).padBottom(3).padRight(2).fillX().expandX();

		buttonTable.add(cancelButton).expand().right();
		buttonTable.add(confirmButton);

		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				fadeOut();
				listener.canceled();
			}
		});

		confirmButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				selectionFinished();
			}
		});
	}

	private void selectionFinished () {
		if (selectedItems.size == 1) {
			// only files allowed but directory is selected?
			// navigate to that directory!
			if (selectionMode == SelectionMode.FILES) {
				FileHandle selected = selectedItems.get(0).file;
				if (selected.isDirectory()) {
					setDirectory(selected);
					return;
				}
			}

			// only directories allowed but file is selected?
			// display dialog :(
			if (selectionMode == SelectionMode.DIRECTORIES) {
				FileHandle selected = selectedItems.get(0).file;
				if (selected.isDirectory() == false) {
					showDialog(locale.popupOnlyDirectoreis);
					return;
				}
			}
		}

		if (selectedItems.size > 0 || mode == Mode.SAVE) {
			Array<FileHandle> files = getFileListFromSelected();
			notifyListnerAndCloseDialog(files);
		} else {
			if (selectionMode == SelectionMode.FILES)
				showDialog(locale.popupChooseFile);
			else {
				Array<FileHandle> files = new Array<FileHandle>();
				files.add(currentDirectory);
				notifyListnerAndCloseDialog(files);
			}
		}
	}

	private void notifyListnerAndCloseDialog (Array<FileHandle> files) {
		if (files == null) return;

		listener.selected(files);
		listener.selected(files.get(0));

		fadeOut();
	}

	private VisScrollPane createScrollPane (VisTable table) {
		VisScrollPane scrollPane = new VisScrollPane(table);
		scrollPane.setOverscroll(false, true);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setScrollingDisabled(true, false);
		return scrollPane;
	}

	private Array<FileHandle> getFileListFromSelected () {
		Array<FileHandle> list = new Array<FileHandle>();

		if (mode == Mode.OPEN) {
			for (FileItem f : selectedItems)
				list.add(f.file);

			return list;
		} else if (selectedItems.size > 0) {
			for (FileItem f : selectedItems)
				list.add(f.file);

			showOverwriteQuestion(list);
			return null;
		} else {
			String fileName = selectedFileTextField.getText();
			FileHandle file = currentDirectory.child(fileName);

			if (FileUtils.isValidFileName(fileName) == false) {
				showDialog(locale.popupFilenameInvalid);
				return null;
			}

			if (file.exists()) {
				list.add(file);
				showOverwriteQuestion(list);

				return null;
			} else {
				list.add(file);
				return list;
			}
		}

	}

	private void showDialog (String text) {
		VisDialog dialog = new VisDialog(locale.popupTitle);
		dialog.text(text);
		dialog.button(locale.popupOK);
		dialog.pack();
		dialog.centerWindow();
		getStage().addActor(dialog.fadeIn());
	}

	private void showOverwriteQuestion (Array<FileHandle> filesList) {
		VisDialog dialog = new VisDialog(locale.popupTitle) {
			@Override
			@SuppressWarnings("unchecked")
			protected void result (Object object) {
				notifyListnerAndCloseDialog((Array<FileHandle>)object);
			}
		};
		dialog.text(filesList.size == 1 ? locale.popupFileExistOverwrite : locale.popupMutipleFileExistOverwrite);
		dialog.button(locale.popupNo, null);
		dialog.button(locale.popupYes, filesList);
		dialog.pack();
		dialog.centerWindow();
		getStage().addActor(dialog.fadeIn());
	}

	private void rebuildShortcutsList (boolean rebuildRootCache) {
		shortcutsTable.clear();

		String userHome = System.getProperty("user.home");
		String userName = System.getProperty("user.name");

		// yes, getHomeDirectory returns path to desktop
		File desktop = fileSystemView.getHomeDirectory();
		shortcutsTable.add(new ShortcutItem(desktop, locale.desktop, style.iconFolder)).expand().fill().row();
		shortcutsTable.add(new ShortcutItem(new File(userHome), userName, style.iconFolder)).expand().fill().row();

		shortcutsTable.addSeparator();

		if (rebuildRootCache) rebuildFileRootsCache();

		for (ShortcutItem item : fileRootsCache)
			shortcutsTable.add(item).expandX().fillX().row();

		Array<FileHandle> favourites = favoritesIO.loadFavorites();

		if (favourites.size > 0) {
			shortcutsTable.addSeparator();

			for (FileHandle f : favourites)
				shortcutsTable.add(new ShortcutItem(f.file(), f.name(), style.iconFolder)).expand().fill().row();
		}
	}

	private void rebuildShortcutsList () {
		rebuildShortcutsList(true);
	}

	private void rebuildFileRootsCache () {
		fileRootsCache.clear();

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

				fileRootsCache.add(item);
			}
		}
	}

	private void rebuildFileList () {
		deselectAll();

		fileTable.clear();
		FileHandle[] files = currentDirectory.list(fileFilter);
		currentPath.setText(currentDirectory.path());

		if (files.length == 0) return;

		Array<FileHandle> fileList = FileUtils.sortFiles(files);

		for (FileHandle f : fileList)
			if (f.file() == null || f.file().isHidden() == false) fileTable.add(new FileItem(f, null)).expand().fill().row();

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

	/** Refresh chooser lists content */
	public void refresh () {
		rebuildShortcutsList();
		rebuildFileList();
	}

	/** Adds favorite to favorite list
	 * @param favourite to be added */
	public void addFavorite (FileHandle favourite) {
		favorites.add(favourite);
		favoritesIO.saveFavorites(favorites);
		rebuildShortcutsList(false);
	}

	/** Removes favorite from current favorite list
	 * @param favorite to be removed (path to favorite)
	 * @return true if favorite was removed, false otherwise */
	public boolean removeFavorite (FileHandle favorite) {
		boolean removed = favorites.removeValue(favorite, false);
		favoritesIO.saveFavorites(favorites);
		rebuildShortcutsList(false);
		setVisble(false);
		return removed;

	}

	public void setVisble (boolean visible) {
		if (isVisible() == false && visible) deselectAll(); // reset selected item when dialog is changed from invisible to visible

		super.setVisible(visible);
	}

	private void setSelectedFileFieldText () {
		if (selectedItems.size == 0)
			selectedFileTextField.setText("");
		else if (selectedItems.size == 1)
			selectedFileTextField.setText(selectedItems.get(0).file.name());
		else {
			StringBuilder b = new StringBuilder();

			for (FileItem item : selectedItems) {
				b.append('"');
				b.append(item.file.name());
				b.append("\" ");
			}

			selectedFileTextField.setText(b.toString());
		}
	}

	private void deselectAll () {
		deselectAll(true);
	}

	private void deselectAll (boolean updateTextField) {
		for (FileItem item : selectedItems)
			item.deselect(false);

		selectedItems.clear();
		if (updateTextField) setSelectedFileFieldText();
	}

	private void historyBuild () {
		Array<FileHandle> fileTree = new Array<FileHandle>();
		fileTree.add(currentDirectory);
		FileHandle next = currentDirectory;

		while (true) {
			FileHandle parent = next.parent();
			if (next.file().getParent() == null) break;
			next = parent;

			fileTree.add(parent);
		}

		fileTree.reverse();
		history = fileTree;
		historyIndex = fileTree.size - 1;

		if (historyIndex == 0)
			backButton.setDisabled(true);
		else
			backButton.setDisabled(false);

		forwardButton.setDisabled(true);
	}

	private void historyAdd (FileHandle file) {
		history.add(file);
		historyIndex++;
		backButton.setDisabled(false);
	}

	private void historyBack () {
		if (historyIndex > 0) {
			historyIndex--;
			setDirectory(history.get(historyIndex), false);
			forwardButton.setDisabled(false);
		}

		if (historyIndex == 0) backButton.setDisabled(true);
	}

	private void historyForward () {
		historyIndex++;
		setDirectory(history.get(historyIndex), false);

		if (historyIndex == history.size - 1) forwardButton.setDisabled(true);

		backButton.setDisabled(false);
	}

	public Mode getMode () {
		return mode;
	}

	public void setMode (Mode mode) {
		this.mode = mode;
	}

	public void setDirectory (String directory) {
		setDirectory(Gdx.files.absolute(directory));
	}

	public void setDirectory (File directory) {
		setDirectory(Gdx.files.absolute(directory.getAbsolutePath()));
	}

	public void setDirectory (FileHandle directory) {
		setDirectory(directory, true);
	}

	private void setDirectory (FileHandle directory, boolean rebuildHistory) {
		if (directory.exists() == false) throw new IllegalStateException("Provided directory does not exist!");
		if (directory.isDirectory() == false) throw new IllegalStateException("Provided directory path is a file, not directory!");

		currentDirectory = directory;
		rebuildFileList();
		if (rebuildHistory) historyBuild();
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

	public int getMultiselectKey () {
		return multiselectKey;
	}

	/** @param multiselectKey from {@link Keys} */
	public void setMultiselectKey (int multiselectKey) {
		this.multiselectKey = multiselectKey;
	}

	private void validateSettings () {
		if (listener == null) listener = new FileChooserAdapter();
	}

	private class DefaultFileFilter implements FileFilter {
		@Override
		public boolean accept (File f) {
			if (f.isHidden()) return false;
			if (mode == Mode.OPEN ? f.canRead() == false : f.canWrite() == false) return false;

			return true;
		}
	}

	private class FileItem extends Table {
		private VisLabel name;
		private VisLabel size;
		public FileHandle file;

		public FileItem (final FileHandle file, Drawable icon) {
			this.file = file;
			name = new VisLabel(file.name());
			name.setEllipsis(true);

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
			addListener(new InputListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					return true;
				}

				@Override
				public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
					if (event.getButton() == Buttons.RIGHT) {
						fileMenu.build(favorites, file);
						fileMenu.displayMenu(getStage(), event.getStageX(), event.getStageY());
					}

				}
			});

			addListener(new ClickListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					if (selectedShortcut != null) selectedShortcut.deselect();

					if (multiselectionEnabled == false || Gdx.input.isKeyPressed(multiselectKey) == false) deselectAll();
					boolean itemSelected = select();

					if (selectedItems.size > 1) removeInvalidSelections();

					setSelectedFileFieldText();

					// very fast selecting and deselecting folder would navigate to that folder
					// return false will protect against that (tap count won't be increased)
					if (itemSelected == false) return false;

					return super.touchDown(event, x, y, pointer, button);
				}

				private void removeInvalidSelections () {
					if (selectionMode == SelectionMode.FILES) {
						for (FileItem item : selectedItems)
							if (item.file.isDirectory()) item.deselect();
					}

					if (selectionMode == SelectionMode.DIRECTORIES) {
						for (FileItem item : selectedItems)
							if (item.file.isDirectory() == false) item.deselect();
					}
				}

				@Override
				public void clicked (InputEvent event, float x, float y) {
					super.clicked(event, x, y);
					if (getTapCount() == 2 && selectedItems.contains(FileItem.this, true)) {
						FileHandle file = FileItem.this.file;
						if (file.isDirectory()) {
							historyAdd(file);
							setDirectory(file);
						} else
							selectionFinished();
					}
				}
			});
		}

		/** Selects this items, if item is already in selectedList it will be deselected */
		private boolean select () {
			if (selectedItems.contains(this, true)) {
				deselect();
				return false;
			}

			setBackground(highlightBg);
			selectedItems.add(this);
			return true;
		}

		private void deselect () {
			deselect(true);
		}

		private void deselect (boolean removeFromList) {
			setBackground((Drawable)null);
			if (removeFromList) selectedItems.removeValue(this, true);
		}

	}

	private class ShortcutItem extends Table {
		private VisLabel name;
		public File file;

		/** Used only by shortcuts panel */
		public ShortcutItem (final File file, String customName, Drawable icon) {
			this.file = file;
			name = new VisLabel(customName);
			name.setEllipsis(true);
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
			addListener(new InputListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					return true;
				}

				@Override
				public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
					if (event.getButton() == Buttons.RIGHT) {
						fileMenu.buildForFavorite(favorites, file);
						fileMenu.displayMenu(getStage(), event.getStageX(), event.getStageY());
					}
				}
			});

			addListener(new ClickListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					deselectAll();
					setSelectedFileFieldText();
					select();
					return super.touchDown(event, x, y, pointer, button);
				}

				@Override
				public void clicked (InputEvent event, float x, float y) {
					super.clicked(event, x, y);

					if (getTapCount() == 1) {
						File file = ShortcutItem.this.file;
						if (file.exists() == false) {
							showDialog(locale.popupDirectoryDoesNotExist);
							return;
						}
						if (file.isDirectory()) {
							setDirectory(file.getAbsolutePath());
							getStage().setScrollFocus(fileScrollPane);
						}
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

	@Override
	protected void setStage (Stage stage) {
		super.setStage(stage);
		deselectAll();
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
