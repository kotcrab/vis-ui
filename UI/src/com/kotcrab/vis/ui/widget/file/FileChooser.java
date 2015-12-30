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

package com.kotcrab.vis.ui.widget.file;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.OsUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.file.internal.DriveCheckerService;
import com.kotcrab.vis.ui.widget.file.internal.DriveCheckerService.DriveCheckerListener;
import com.kotcrab.vis.ui.widget.file.internal.DriveCheckerService.RootMode;
import com.kotcrab.vis.ui.widget.file.internal.FavoritesIO;
import com.kotcrab.vis.ui.widget.file.internal.FileChooserWinService;
import com.kotcrab.vis.ui.widget.file.internal.FileChooserWinService.RootNameListener;
import com.kotcrab.vis.ui.widget.file.internal.FileHistoryManager;
import com.kotcrab.vis.ui.widget.file.internal.FileHistoryManager.FileHistoryCallback;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import static com.kotcrab.vis.ui.widget.file.internal.FileChooserText.*;

/**
 * Chooser for files, before using {@link FileChooser#setFavoritesPrefsName(String)} should be called. FileChooser is heavy widget
 * and should be reused whenever possible. Chooser is platform dependent and can be only used on desktop.
 * FileChooser will be centered on screen after adding to Stage use {@link #setCenterOnAdd(boolean)} to change this.
 * @author Kotcrab
 * @since 0.1.0
 */
public class FileChooser extends VisWindow implements FileHistoryCallback {
	private static final long FILE_WATCHER_CHECK_DELAY_MILLIS = 2000;

	private static final ShortcutsComparator SHORTCUTS_COMPARATOR = new ShortcutsComparator();

	private Mode mode;
	private SelectionMode selectionMode = SelectionMode.FILES;
	private FileChooserListener listener = new FileChooserAdapter();
	private FileFilter fileFilter = new DefaultFileFilter(this);
	private FileIconProvider iconProvider = new DefaultFileIconProvider(this);

	private DriveCheckerService driveCheckerService = DriveCheckerService.getInstance();
	private FileChooserWinService chooserWinService = FileChooserWinService.getInstance();

	private FileDeleter fileDeleter = new DefaultFileDeleter();

	public static final int DEFAULT_KEY = -1;
	private boolean multiSelectionEnabled = false;
	private int groupMultiSelectKey = DEFAULT_KEY; //shift by default
	private int multiSelectKey = DEFAULT_KEY; //ctrl (or command on mac) by default

	private FavoritesIO favoritesIO;
	private Array<FileHandle> favorites;

	private FileHandle currentDirectory;
	private ObjectMap<FileHandle, FileItem> items = new ObjectMap();
	private Array<FileItem> selectedItems = new Array<FileItem>();
	private ShortcutItem selectedShortcut;

	private boolean watchingFilesEnabled = true;
	private Thread fileWatcherThread;
	private boolean shortcutsListRebuildScheduled;
	private boolean filesListRebuildScheduled;

	// UI
	private FileChooserStyle style;
	private Sizes sizes;

	private FileHistoryManager historyManager;

	private VisSplitPane mainSplitPane;

	private VisTable fileTable;
	private VisScrollPane fileScrollPane;

	private VisTable shortcutsTable;
	private VerticalGroup shortcutsMainPanel;
	private VerticalGroup shortcutsRootsPanel;
	private VerticalGroup shortcutsFavoritesPanel;

	private VisTextField currentPath;
	private VisTextField selectedFileTextField;
	private VisTextButton confirmButton;

	private FilePopupMenu fileMenu;

	public FileChooser (Mode mode) {
		this((FileHandle) null, mode);
	}

	public FileChooser (FileHandle directory, Mode mode) {
		super("");

		this.mode = mode;

		getTitleLabel().setText(TITLE_CHOOSE_FILES.get());

		style = VisUI.getSkin().get(FileChooserStyle.class);
		sizes = VisUI.getSizes();

		init(directory);
	}

	public FileChooser (String title, Mode mode) {
		this("default", title, mode);
	}

	public FileChooser (String styleName, String title, Mode mode) {
		super(title);
		this.mode = mode;

		style = VisUI.getSkin().get(styleName, FileChooserStyle.class);
		sizes = VisUI.getSizes();

		init(null);
	}

	/**
	 * Sets file name that will be used to store favorites, if not changed default will be used that may be shared with other
	 * programs, should be package name e.g. com.seriouscompay.seriousprogram
	 */
	public static void setFavoritesPrefsName (String name) {
		FavoritesIO.setFavoritesPrefsName(name);
	}

	private void init (FileHandle directory) {
		setModal(true);
		setResizable(true);
		setMovable(true);
		addCloseButton();
		closeOnEscape();

		favoritesIO = new FavoritesIO();
		favoritesIO.checkIfUsingDefaultName();
		favorites = favoritesIO.loadFavorites();

		createToolbar();
		createCenterContentPanel();
		createFileTextBox();
		createBottomButtons();

		createShortcutsMainPanel();
		shortcutsRootsPanel = new VerticalGroup();
		shortcutsFavoritesPanel = new VerticalGroup();
		rebuildShortcutsFavoritesPanel();

		fileMenu = new FilePopupMenu(style.popupMenuStyleName, this);

		rebuildShortcutsList();

		if (directory == null)
			setDirectory(Gdx.files.absolute(System.getProperty("user.home")), HistoryPolicy.IGNORE);
		else
			setDirectory(directory, HistoryPolicy.IGNORE);

		setSize(500, 600);
		centerWindow();

		createListeners();
	}

	private void createToolbar () {
		VisTable toolbarTable = new VisTable(true);
		toolbarTable.defaults().minWidth(30).right();
		add(toolbarTable).fillX().expandX().pad(3).padRight(2);

		historyManager = new FileHistoryManager(style, this);

		currentPath = new VisTextField();

		currentPath.addListener(new InputListener() {
			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					FileHandle file = Gdx.files.absolute(currentPath.getText());
					if (file.exists())
						setDirectory(file, HistoryPolicy.ADD);
					else {
						showDialog(POPUP_DIRECTORY_DOES_NOT_EXIST.get());
						currentPath.setText(currentDirectory.path());
					}
				}
				return false;
			}
		});

		VisImageButton folderParentButton = new VisImageButton(style.iconFolderParent, PARENT_DIRECTORY.get());
		VisImageButton folderNewButton = new VisImageButton(style.iconFolderNew, NEW_DIRECTORY.get());

		toolbarTable.add(historyManager.getButtonsTable());
		toolbarTable.add(currentPath).expand().fill();
		toolbarTable.add(folderParentButton);
		toolbarTable.add(folderNewButton);

		folderParentButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				FileHandle parent = currentDirectory.parent();

				// if current directory is drive root (eg. "C:/") navigating to parent
				// would navigate to "/" which would work but it is bad for UX
				if (OsUtils.isWindows() && currentDirectory.path().endsWith(":/")) return;

				setDirectory(parent, HistoryPolicy.ADD);
			}
		});

		folderNewButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				showNewDirectoryDialog();
			}
		});

		addListener(historyManager.getDefaultClickListener());
	}

	private void createCenterContentPanel () {
		// fileTable is contained in fileScrollPane contained in fileScrollPaneTable contained in mainSplitPane
		// same for shortcuts
		fileTable = new VisTable();
		fileScrollPane = createScrollPane(fileTable);
		VisTable fileScrollPaneTable = new VisTable();
		fileScrollPaneTable.add(fileScrollPane).pad(2).top().expand().fillX();
		fileScrollPaneTable.setTouchable(Touchable.enabled);

		shortcutsTable = new VisTable();
		final VisScrollPane shortcutsScrollPane = createScrollPane(shortcutsTable);
		VisTable shortcutsScrollPaneTable = new VisTable();
		shortcutsScrollPaneTable.add(shortcutsScrollPane).pad(2).top().expand().fillX();

		mainSplitPane = new VisSplitPane(shortcutsScrollPaneTable, fileScrollPaneTable, false) {
			@Override
			public void invalidate () {
				super.invalidate();
				invalidateChildHierarchy(shortcutsScrollPane);
			}
		};
		mainSplitPane.setSplitAmount(0.3f);
		mainSplitPane.setMinSplitAmount(0.05f);
		mainSplitPane.setMaxSplitAmount(0.8913f);

		row();
		add(mainSplitPane).expand().fill();
		row();

		fileScrollPaneTable.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if (button == Buttons.RIGHT && selectedItems.size == 0) {
					fileMenu.build();
					fileMenu.showMenu(getStage(), event.getStageX(), event.getStageY());
				}
			}
		});
	}

	private void invalidateChildHierarchy (WidgetGroup layout) {
		if (layout != null) {
			layout.invalidate();
			for (Actor actor : layout.getChildren()) {
				if (actor instanceof WidgetGroup)
					invalidateChildHierarchy((WidgetGroup) actor);
				else if (actor instanceof Layout)
					((Layout) actor).invalidate();
			}
		}
	}

	private void createFileTextBox () {
		VisTable table = new VisTable(true);
		VisLabel nameLabel = new VisLabel(FILE_NAME.get());
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
		VisTextButton cancelButton = new VisTextButton(CANCEL.get());
		confirmButton = new VisTextButton(mode == Mode.OPEN ? OPEN.get() : SAVE.get());

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

	private void createShortcutsMainPanel () {
		shortcutsMainPanel = new VerticalGroup();
		String userHome = System.getProperty("user.home");
		String userName = System.getProperty("user.name");
		File userDesktop = new File(userHome + "/Desktop");

		if (userDesktop.exists())
			shortcutsMainPanel.addActor(new ShortcutItem(userDesktop, DESKTOP.get(), style.iconFolder));
		shortcutsMainPanel.addActor(new ShortcutItem(new File(userHome), userName, style.iconFolder));
	}

	private void createListeners () {
		addListener(new InputListener() {
			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if (keycode == Keys.A && UIUtils.ctrl()) {
					selectAll();
					return true;
				}

				return false;
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
					setDirectory(selected, HistoryPolicy.ADD);
					return;
				}
			}

			// only directories allowed but file is selected?
			// display dialog :(
			if (selectionMode == SelectionMode.DIRECTORIES) {
				FileHandle selected = selectedItems.get(0).file;
				if (selected.isDirectory() == false) {
					showDialog(POPUP_ONLY_DIRECTORIES.get());
					return;
				}
			}
		}

		if (selectedItems.size > 0 || mode == Mode.SAVE) {
			Array<FileHandle> files = getFileListFromSelected();
			notifyListenerAndCloseDialog(files);
		} else {
			if (selectionMode == SelectionMode.FILES)
				showDialog(POPUP_CHOOSE_FILE.get());
			else {
				// this part is executed when nothing is selected but selection mode is `directories` or `files and directories`
				// it is perfectly valid, nothing is selected so that means the current chooser directory have to be
				// selected and passed to listener
				Array<FileHandle> files = new Array<FileHandle>();
				files.add(currentDirectory);
				notifyListenerAndCloseDialog(files);
			}
		}
	}

	@Override
	protected void close () {
		listener.canceled();
		super.close();
	}

	private void notifyListenerAndCloseDialog (Array<FileHandle> files) {
		if (files == null) return;

		if (mode == Mode.OPEN) {
			for (FileHandle file : files) {
				if (file.exists() == false) {
					showDialog(POPUP_SELECTED_FILE_DOES_NOT_EXIST.get());
					return;
				}
			}
		}

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
				showDialog(POPUP_FILENAME_INVALID.get());
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
		DialogUtils.showOKDialog(getStage(), POPUP_TITLE.get(), text);
	}

	private void showOverwriteQuestion (final Array<FileHandle> filesList) {
		String text = filesList.size == 1 ? POPUP_FILE_EXIST_OVERWRITE.get() : POPUP_MULTIPLE_FILE_EXIST_OVERWRITE.get();
		DialogUtils.showOptionDialog(getStage(), POPUP_TITLE.get(), text, OptionDialogType.YES_NO, new OptionDialogAdapter() {
			@Override
			public void yes () {
				notifyListenerAndCloseDialog(filesList);
			}
		});
	}

	private void rebuildShortcutsList (boolean rebuildRootCache) {
		shortcutsTable.clear();

		shortcutsTable.add(shortcutsMainPanel).left().row();
		shortcutsTable.addSeparator();

		if (rebuildRootCache) rebuildFileRootsCache();

		shortcutsTable.add(shortcutsRootsPanel).left().row();

		if (shortcutsFavoritesPanel.getChildren().size > 0)
			shortcutsTable.addSeparator();
		shortcutsTable.add(shortcutsFavoritesPanel).left().row();
	}

	private void rebuildShortcutsList () {
		shortcutsListRebuildScheduled = false;
		rebuildShortcutsList(true);
	}

	private void rebuildFileRootsCache () {
		shortcutsRootsPanel.clear();
		File[] roots = File.listRoots();

		for (final File root : roots) {
			driveCheckerService.addListener(root, mode == Mode.OPEN ? RootMode.READABLE : RootMode.WRITABLE, new DriveCheckerListener() {
				@Override
				public void rootMode (File root, RootMode mode) {
					String initialName = root.toString();

					if (initialName.equals("/")) initialName = COMPUTER.get();

					final ShortcutItem item = new ShortcutItem(root, initialName, style.iconDrive);

					if (OsUtils.isWindows()) chooserWinService.addListener(root, item);

					shortcutsRootsPanel.addActor(item);
					shortcutsRootsPanel.getChildren().sort(SHORTCUTS_COMPARATOR);
				}
			});
		}
	}

	private void rebuildShortcutsFavoritesPanel () {
		shortcutsFavoritesPanel.clear();
		if (favorites.size > 0) {
			for (FileHandle f : favorites)
				shortcutsFavoritesPanel.addActor(new ShortcutItem(f.file(), f.name(), style.iconFolder));
		}
	}

	private void rebuildFileList () {
		filesListRebuildScheduled = false;
		deselectAll();

		fileTable.clear();
		items.clear();
		FileHandle[] files = currentDirectory.list(fileFilter);
		currentPath.setText(currentDirectory.path());

		if (files.length == 0) return;

		Array<FileHandle> fileList = FileUtils.sortFiles(files);

		for (FileHandle file : fileList) {
			if (file.file() != null) {
				FileItem item = new FileItem(file);
				fileTable.add(item).expand().fill().row();
				items.put(file, item);
			}
		}

		fileScrollPane.setScrollX(0);
		fileScrollPane.setScrollY(0);
	}

	/**
	 * Sets chooser selected files. All files that are invalid for current selection won't be selected. Files that doesn't
	 * exist will be ignored.
	 * @param files absolute {@link FileHandle}s of files to be selected
	 */
	public void setSelectedFiles (FileHandle... files) {
		deselectAll(false);

		for (FileHandle file : files) {
			FileItem item = items.get(file);
			if (item != null) {
				item.select(false);
			}
		}

		removeInvalidSelections();
		updateSelectedFileFieldText();
	}

	/** Refresh chooser lists content */
	public void refresh () {
		rebuildShortcutsList();
		rebuildFileList();
	}

	/**
	 * Adds favorite to favorite list
	 * @param favourite to be added
	 */
	public void addFavorite (FileHandle favourite) {
		favorites.add(favourite);
		favoritesIO.saveFavorites(favorites);
		rebuildShortcutsFavoritesPanel();
		rebuildShortcutsList(false);
	}

	/**
	 * Removes favorite from current favorite list
	 * @param favorite to be removed (path to favorite)
	 * @return true if favorite was removed, false otherwise
	 */
	public boolean removeFavorite (FileHandle favorite) {
		boolean removed = favorites.removeValue(favorite, false);
		favoritesIO.saveFavorites(favorites);
		rebuildShortcutsFavoritesPanel();
		rebuildShortcutsList(false);
		return removed;
	}

	@Override
	public void setVisible (boolean visible) {
		if (isVisible() == false && visible)
			deselectAll(); // reset selected item when dialog is changed from invisible to visible

		super.setVisible(visible);
	}

	private void deselectAll () {
		deselectAll(true);
	}

	private void deselectAll (boolean updateTextField) {
		for (FileItem item : selectedItems)
			item.deselect(false);

		selectedItems.clear();
		if (updateTextField) updateSelectedFileFieldText();
	}

	private void selectAll () {
		for (FileItem item : items.values())
			item.select(false);

		removeInvalidSelections();
		updateSelectedFileFieldText();
	}

	private void updateSelectedFileFieldText () {
		if (selectedItems.size == 0) {
			selectedFileTextField.setText("");
		} else if (selectedItems.size == 1) {
			selectedFileTextField.setText(selectedItems.get(0).file.name());
		} else {
			StringBuilder b = new StringBuilder();

			for (FileItem item : selectedItems) {
				b.append('"');
				b.append(item.file.name());
				b.append("\" ");
			}

			selectedFileTextField.setText(b.toString());
		}
	}

	private void removeInvalidSelections () {
		if (selectionMode == SelectionMode.FILES) {
			Iterator<FileItem> it = selectedItems.iterator();
			while (it.hasNext()) {
				FileItem item = it.next();

				if (item.file.isDirectory()) {
					item.deselect(false);
					it.remove();
				}
			}
		}

		if (selectionMode == SelectionMode.DIRECTORIES) {
			Iterator<FileItem> it = selectedItems.iterator();
			while (it.hasNext()) {
				FileItem item = it.next();

				if (item.file.isDirectory() == false) {
					item.deselect(false);
					it.remove();
				}
			}
		}
	}

	public Mode getMode () {
		return mode;
	}

	public void setMode (Mode mode) {
		this.mode = mode;
		confirmButton.setText(mode == Mode.OPEN ? OPEN.get() : SAVE.get());
		refresh();
	}

	public void setDirectory (String directory) {
		setDirectory(Gdx.files.absolute(directory), HistoryPolicy.CLEAR);
	}

	public void setDirectory (File directory) {
		setDirectory(Gdx.files.absolute(directory.getAbsolutePath()), HistoryPolicy.CLEAR);
	}

	public void setDirectory (FileHandle directory) {
		setDirectory(directory, HistoryPolicy.CLEAR);
	}

	public void setDirectory (FileHandle directory, HistoryPolicy historyPolicy) {
		if (directory.equals(currentDirectory)) return;
		if (directory.exists() == false) throw new IllegalStateException("Provided directory does not exist!");
		if (directory.isDirectory() == false)
			throw new IllegalStateException("Provided path is a file, not directory!");

		if (historyPolicy == HistoryPolicy.ADD) historyManager.historyAdd();

		currentDirectory = directory;
		rebuildFileList();

		if (historyPolicy == HistoryPolicy.CLEAR) historyManager.historyClear();
	}

	@Override
	public FileHandle getCurrentDirectory () {
		return currentDirectory;
	}

	public FileFilter getFileFilter () {
		return fileFilter;
	}

	public void setFileFilter (FileFilter fileFilter) {
		this.fileFilter = fileFilter;
		rebuildFileList();
	}

	public SelectionMode getSelectionMode () {
		return selectionMode;
	}

	/**
	 * Changes selection mode, also updates the title of this file chooser to match current selection mode (eg. Choose file, Choose
	 * directory etc.)
	 */
	public void setSelectionMode (SelectionMode selectionMode) {
		if (selectionMode == null) selectionMode = SelectionMode.FILES;
		this.selectionMode = selectionMode;

		switch (selectionMode) {
			case FILES:
				getTitleLabel().setText(TITLE_CHOOSE_FILES.get());
				break;
			case DIRECTORIES:
				getTitleLabel().setText(TITLE_CHOOSE_DIRECTORIES.get());
				break;
			case FILES_AND_DIRECTORIES:
				getTitleLabel().setText(TITLE_CHOOSE_FILES_AND_DIRECTORIES.get());
				break;
		}
	}

	public boolean isMultiSelectionEnabled () {
		return multiSelectionEnabled;
	}

	public void setMultiSelectionEnabled (boolean multiSelectionEnabled) {
		this.multiSelectionEnabled = multiSelectionEnabled;
	}

	public void setListener (FileChooserListener newListener) {
		this.listener = newListener;
		if (listener == null) listener = new FileChooserAdapter();
	}

	public int getMultiSelectKey () {
		return multiSelectKey;
	}

	/** @param multiSelectKey from {@link Keys} or {@link FileChooser#DEFAULT_KEY} to restore to default */
	public void setMultiSelectKey (int multiSelectKey) {
		this.multiSelectKey = multiSelectKey;
	}

	public int getGroupMultiSelectKey () {
		return groupMultiSelectKey;
	}

	/** @param groupMultiSelectKey from {@link Keys} or {@link FileChooser#DEFAULT_KEY} to restore to default */
	public void setGroupMultiSelectKey (int groupMultiSelectKey) {
		this.groupMultiSelectKey = groupMultiSelectKey;
	}

	private boolean isMultiSelectKeyPressed () {
		if (multiSelectKey == DEFAULT_KEY)
			return UIUtils.ctrl();
		else
			return Gdx.input.isKeyPressed(multiSelectKey);
	}

	private boolean isGroupMultiSelectKeyPressed () {
		if (groupMultiSelectKey == DEFAULT_KEY)
			return UIUtils.shift();
		else
			return Gdx.input.isKeyPressed(groupMultiSelectKey);
	}

	FileChooserStyle getChooserStyle () {
		return style;
	}

	/**
	 * If false file chooser won't pool directories for changes, adding new files or connecting new drive won't refresh file list.
	 * This must be called when file chooser is not added to Stage
	 */
	public void setWatchingFilesEnabled (boolean watchingFilesEnabled) {
		if (getStage() != null)
			throw new IllegalStateException("Pooling setting cannot be changed when file chooser is added to Stage!");

		this.watchingFilesEnabled = watchingFilesEnabled;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		if (shortcutsListRebuildScheduled) rebuildShortcutsList();
		if (filesListRebuildScheduled) rebuildFileList();
	}

	@Override
	protected void setStage (Stage stage) {
		super.setStage(stage);

		if (watchingFilesEnabled) {
			if (stage != null)
				startFileWatcher();
			else
				stopFileWatcher();
		}
	}

	private void startFileWatcher () {
		if (fileWatcherThread != null) throw new IllegalStateException("FileWatcherThread already running");

		fileWatcherThread = new Thread(new Runnable() {
			File[] lastRoots;

			FileHandle lastCurrentDirectory;
			FileHandle[] lastCurrentFiles;

			@Override
			public void run () {
				lastRoots = File.listRoots();

				lastCurrentDirectory = currentDirectory;
				lastCurrentFiles = currentDirectory.list();

				while (fileWatcherThread != null) {
					File[] roots = File.listRoots();

					if (roots.length != lastRoots.length || Arrays.equals(lastRoots, roots) == false)
						shortcutsListRebuildScheduled = true;

					lastRoots = roots;

					// if current directory changed during pools then our lastCurrentDirectoryFiles list is outdated and we shouldn't
					// schedule files list rebuild
					if (lastCurrentDirectory.equals(currentDirectory) == true) {
						FileHandle[] currentFiles = currentDirectory.list();

						if (lastCurrentFiles.length != currentFiles.length || Arrays.equals(lastCurrentFiles, currentFiles) == false)
							filesListRebuildScheduled = true;

						lastCurrentFiles = currentFiles;
					} else
						lastCurrentFiles = currentDirectory.list(); // if list is outdated, refresh it

					lastCurrentDirectory = currentDirectory;

					try {
						Thread.sleep(FILE_WATCHER_CHECK_DELAY_MILLIS);
					} catch (InterruptedException ignored) {
					}
				}
			}
		}, "FileWatcherThread");

		fileWatcherThread.setDaemon(true);
		fileWatcherThread.start();
	}

	private void stopFileWatcher () {
		if (fileWatcherThread == null) throw new IllegalStateException("FileWatcherThread not running");
		fileWatcherThread.interrupt();
		fileWatcherThread = null;
	}

	void showNewDirectoryDialog () {
		DialogUtils.showInputDialog(getStage(), NEW_DIRECTORY_DIALOG_TITLE.get(), NEW_DIRECTORY_DIALOG_TEXT.get(), true, new InputDialogAdapter() {
			@Override
			public void finished (String input) {
				if (FileUtils.isValidFileName(input) == false) {
					DialogUtils.showErrorDialog(getStage(), NEW_DIRECTORY_DIALOG_ILLEGAL_CHARACTERS.get());
				}

				for (FileHandle file : currentDirectory.list()) {
					if (file.name().equals(input)) {
						DialogUtils.showErrorDialog(getStage(), NEW_DIRECTORY_DIALOG_ALREADY_EXISTS.get());
						return;
					}
				}

				currentDirectory.child(input).mkdirs();
				refresh();
			}
		});
	}

	/**
	 * Sets {@link FileDeleter} that will be used for deleting files. You cannot set your own file deleter, {@link FileDeleter}
	 * interface is public, delete must be either {@link DefaultFileDeleter} or {@link JNAFileDeleter}. {@link JNAFileDeleter}
	 * supports moving file to system trash instead of deleting it permanently, but it requires JNA library in your project.
	 */
	public void setFileDeleter (FileDeleter fileDeleter) {
		if (fileDeleter == null) throw new IllegalStateException("fileDeleter can't be null");
		this.fileDeleter = fileDeleter;
		fileMenu.fileDeleterChanged();
	}

	public FileDeleter getFileDeleter () {
		return fileDeleter;
	}

	public void setIconProvider (FileIconProvider iconProvider) {
		this.iconProvider = iconProvider;
	}

	public enum Mode {
		OPEN, SAVE
	}

	public enum SelectionMode {
		FILES, DIRECTORIES, FILES_AND_DIRECTORIES
	}

	public enum HistoryPolicy {
		ADD, CLEAR, IGNORE
	}

	/**
	 * Provides icons that will be used for file thumbnail on file list. If not set default is used that supports
	 * directories and few basic file types. If you want to add your custom icon your should extend {@link DefaultFileIconProvider}
	 */
	public interface FileIconProvider {
		/** @return icon that will be used for this file or null if no icon should be displayed */
		Drawable provideIcon (FileHandle file);
	}

	public static class DefaultFileIconProvider implements FileIconProvider {
		private FileChooser chooser;

		public DefaultFileIconProvider (FileChooser chooser) {
			this.chooser = chooser;
		}

		@Override
		public Drawable provideIcon (FileHandle file) {
			FileChooserStyle style = chooser.style;
			if (file.isDirectory()) return style.iconFolder;
			String ext = file.extension();
			if (ext.equals("jpg") || ext.equals("png")) return style.iconFileImage;
			if (ext.equals("wav") || ext.equals("ogg") || ext.equals("mp3")) return style.iconFileAudio;
			if (ext.equals("pdf")) return style.iconFilePdf;
			if (ext.equals("txt")) return style.iconFileText;
			return null;
		}
	}

	public static class DefaultFileFilter implements FileFilter {
		private FileChooser chooser;

		public DefaultFileFilter (FileChooser chooser) {
			this.chooser = chooser;
		}

		@Override
		public boolean accept (File f) {
			if (f.isHidden()) return false;
			if (chooser.getMode() == Mode.OPEN ? f.canRead() == false : f.canWrite() == false) return false;

			return true;
		}
	}

	interface FileDeleter {
		boolean hasTrash ();

		boolean delete (FileHandle file) throws IOException;
	}

	public static final class DefaultFileDeleter implements FileDeleter {
		@Override
		public boolean hasTrash () {
			return false;
		}

		@Override
		public boolean delete (FileHandle file) {
			return file.delete();
		}
	}

	private class FileItem extends Table {
		public FileHandle file;
		private VisLabel name;
		private VisLabel size;

		public FileItem (final FileHandle file) {
			this.file = file;
			setTouchable(Touchable.enabled);
			name = new VisLabel(file.name());
			name.setEllipsis(true);

			if (file.isDirectory())
				size = new VisLabel("");
			else
				size = new VisLabel(FileUtils.readableFileSize(file.length()));

			Drawable icon = iconProvider.provideIcon(file);

			if (icon != null) add(new Image(icon)).padTop(3);
			Cell<VisLabel> labelCell = add(name).padLeft(icon == null ? 22 : 0);

			labelCell.width(new Value() {
				@Override
				public float get (Actor context) {
					int padding = (int) (file.isDirectory() ? 35 * sizes.scaleFactor : 60 * sizes.scaleFactor);
					return mainSplitPane.getSecondWidgetBounds().width - getUsedWidth() - padding;
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
						fileMenu.showMenu(getStage(), event.getStageX(), event.getStageY());
					}
				}
			});

			addListener(new ClickListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					if (selectedShortcut != null) selectedShortcut.deselect();

					if (multiSelectionEnabled == false || (isMultiSelectKeyPressed() == false && isGroupMultiSelectKeyPressed() == false))
						deselectAll();

					boolean itemSelected = select();

					if (selectedItems.size > 1 && multiSelectionEnabled && isGroupMultiSelectKeyPressed())
						selectGroup();

					if (selectedItems.size > 1) removeInvalidSelections();

					updateSelectedFileFieldText();

					// very fast selecting and deselecting folder would navigate to that folder
					// return false will protect against that (tap count won't be increased)
					if (itemSelected == false) return false;

					return super.touchDown(event, x, y, pointer, button);
				}

				@Override
				public void clicked (InputEvent event, float x, float y) {
					super.clicked(event, x, y);
					if (getTapCount() == 2 && selectedItems.contains(FileItem.this, true)) {
						FileHandle file = FileItem.this.file;
						if (file.isDirectory()) {
							setDirectory(file, HistoryPolicy.ADD);
						} else
							selectionFinished();
					}
				}

				private void selectGroup () {
					Array<Cell> cells = fileTable.getCells();

					int thisSelectionIndex = getItemId(cells, FileItem.this);
					int lastSelectionIndex = getItemId(cells, selectedItems.get(selectedItems.size - 2));

					int start;
					int end;

					if (thisSelectionIndex > lastSelectionIndex) {
						start = lastSelectionIndex;
						end = thisSelectionIndex;
					} else {
						start = thisSelectionIndex;
						end = lastSelectionIndex;
					}

					for (int i = start; i < end; i++) {
						FileItem item = (FileItem) cells.get(i).getActor();
						item.select(false);
					}
				}

				private int getItemId (Array<Cell> cells, FileItem item) {
					for (int i = 0; i < cells.size; i++) {
						if (cells.get(i).getActor() == item) return i;
					}

					throw new IllegalStateException("Item not found in cells");
				}
			});
		}

		/** Selects this items, if item is already in selectedList it will be deselected */
		private boolean select () {
			return select(true);
		}

		private boolean select (boolean deselectIfAlreadySelected) {
			if (deselectIfAlreadySelected && selectedItems.contains(this, true)) {
				deselect();
				return false;
			}

			setBackground(style.highlight);
			if (selectedItems.contains(this, true) == false) selectedItems.add(this);
			return true;
		}

		private void deselect () {
			deselect(true);
		}

		private void deselect (boolean removeFromList) {
			setBackground((Drawable) null);
			if (removeFromList) selectedItems.removeValue(this, true);
		}

	}

	private class ShortcutItem extends Table implements RootNameListener {
		public File file;
		private VisLabel name;

		/** Used only by shortcuts panel */
		public ShortcutItem (final File file, String customName, Drawable icon) {
			this.file = file;
			name = new VisLabel(customName);
			name.setEllipsis(true);
			add(new Image(icon)).padTop(3);
			Cell<VisLabel> labelCell = add(name).padRight(6);
			labelCell.width(new Value() {
				@Override
				public float get (Actor context) {
					return mainSplitPane.getFirstWidgetBounds().width - 30;
				}
			});

			addListener();
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
						fileMenu.showMenu(getStage(), event.getStageX(), event.getStageY());
					}
				}
			});

			addListener(new ClickListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					deselectAll();
					updateSelectedFileFieldText();
					select();
					return super.touchDown(event, x, y, pointer, button);
				}

				@Override
				public void clicked (InputEvent event, float x, float y) {
					super.clicked(event, x, y);

					if (getTapCount() == 1) {
						File file = ShortcutItem.this.file;
						if (file.exists() == false) {
							showDialog(POPUP_DIRECTORY_DOES_NOT_EXIST.get());
							refresh();
							return;
						}

						if (file.isDirectory()) {
							setDirectory(Gdx.files.absolute(file.getAbsolutePath()), HistoryPolicy.ADD);
							getStage().setScrollFocus(fileScrollPane);
						}
					}
				}
			});
		}

		public void setLabelText (String text) {
			name.setText(text);
		}

		public String getLabelText () {
			return name.getText().toString();
		}

		private void select () {
			if (selectedShortcut != null) selectedShortcut.deselect();
			selectedShortcut = ShortcutItem.this;
			setBackground(style.highlight);
		}

		private void deselect () {
			setBackground((Drawable) null);
		}

		@Override
		public void setRootName (String newName) {
			setLabelText(newName);
		}
	}

	private static class ShortcutsComparator implements Comparator<Actor> {
		@Override
		public int compare (Actor o1, Actor o2) {
			ShortcutItem s1 = (ShortcutItem) o1;
			ShortcutItem s2 = (ShortcutItem) o2;
			return s1.getLabelText().compareTo(s2.getLabelText());
		}
	}
}
