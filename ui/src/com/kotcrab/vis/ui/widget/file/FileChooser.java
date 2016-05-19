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
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.util.OsUtils;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.util.value.ConstantIfVisibleValue;
import com.kotcrab.vis.ui.util.value.PrefHeightIfVisibleValue;
import com.kotcrab.vis.ui.util.value.PrefWidthIfVisibleValue;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.ButtonBar.ButtonType;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.file.internal.*;
import com.kotcrab.vis.ui.widget.file.internal.DriveCheckerService.DriveCheckerListener;
import com.kotcrab.vis.ui.widget.file.internal.DriveCheckerService.RootMode;
import com.kotcrab.vis.ui.widget.file.internal.FileChooserWinService.RootNameListener;
import com.kotcrab.vis.ui.widget.file.internal.FileHistoryManager.FileHistoryCallback;
import com.kotcrab.vis.ui.widget.file.internal.FilePopupMenu.FilePopupMenuCallback;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import static com.kotcrab.vis.ui.widget.file.internal.FileChooserText.*;

/**
 * Widget allowing user to choose files. FileChooser is heavy widget and should be reused whenever possible, typically
 * one instance is enough for application. Chooser is platform dependent and can be only used on desktop.
 * <p>
 * FileChooser will be centered on screen after adding to Stage use {@link #setCenterOnAdd(boolean)} to change this.
 * @author Kotcrab
 * @since 0.1.0
 */
public class FileChooser extends VisWindow implements FileHistoryCallback {
	private static final long FILE_WATCHER_CHECK_DELAY_MILLIS = 2000;

	private static final ShortcutsComparator SHORTCUTS_COMPARATOR = new ShortcutsComparator();

	private Mode mode;
	private ViewMode viewMode = ViewMode.DETAILS;
	private SelectionMode selectionMode = SelectionMode.FILES;
	private FileChooserListener listener = new FileChooserAdapter();
	private FileFilter fileFilter = new DefaultFileFilter(this);
	private FileTypeFilter fileTypeFilter = null;
	private FileTypeFilter.Rule activeFileTypeRule = null;
	private FileIconProvider iconProvider;

	private DriveCheckerService driveCheckerService = DriveCheckerService.getInstance();
	private FileChooserWinService chooserWinService = FileChooserWinService.getInstance();

	private FileDeleter fileDeleter = new DefaultFileDeleter();

	public static final int DEFAULT_KEY = -1;
	private boolean multiSelectionEnabled = false;
	private int groupMultiSelectKey = DEFAULT_KEY; //shift by default
	private int multiSelectKey = DEFAULT_KEY; //ctrl (or command on mac) by default

	private PreferencesIO preferencesIO;
	private Array<FileHandle> favorites;
	private Array<FileHandle> recentDirectories;

	private FileHandle currentDirectory;
	private Array<FileHandle> currentFiles = new Array<FileHandle>();
	private FileListAdapter fileListAdapter;
	private Array<FileItem> selectedItems = new Array<FileItem>();
	private ShortcutItem selectedShortcut;

	private boolean watchingFilesEnabled = true;
	private Thread fileWatcherThread;
	private boolean shortcutsListRebuildScheduled;
	private boolean filesListRebuildScheduled;

	private FileHistoryManager historyManager;

	// UI
	private FileChooserStyle style;

	private Sizes sizes;

	private VisSplitPane mainSplitPane;

	private VisTable shortcutsTable;
	private VerticalGroup shortcutsMainPanel;
	private VerticalGroup shortcutsRootsPanel;
	private VerticalGroup shortcutsFavoritesPanel;
	private ListView<FileHandle> fileListView;

	private VisImageButton favoriteFolderButton;
	private VisImageButton viewModeButton;
	private Tooltip favoriteFolderButtonTooltip;
	private VisTextField currentPath;
	private VisTextField selectedFileTextField;
	private VisSelectBox<FileTypeFilter.Rule> fileTypeSelectBox;

	private VisTextButton confirmButton;
	private FilePopupMenu fileMenu;
	private FileSuggestionPopup fileNameSuggestionPopup;
	private DirsSuggestionPopup dirsSuggestionPopup;
	private VisLabel fileTypeLabel;
	private PopupMenu viewModePopupMenu;

	/** @param mode whether this chooser will be used to open or save files */
	public FileChooser (Mode mode) {
		this((FileHandle) null, mode);
	}

	/**
	 * @param directory starting chooser directory
	 * @param mode whether this chooser will be used to open or save files
	 */
	public FileChooser (FileHandle directory, Mode mode) {
		super("");

		this.mode = mode;

		getTitleLabel().setText(TITLE_CHOOSE_FILES.get());

		style = VisUI.getSkin().get(FileChooserStyle.class);
		sizes = VisUI.getSizes();

		init(directory);
	}

	/**
	 * @param title chooser window title
	 * @param mode whether this chooser will be used to open or save files
	 */
	public FileChooser (String title, Mode mode) {
		this("default", title, mode);
	}

	/**
	 * @param styleName skin style name
	 * @param title chooser window title
	 * @param mode whether this chooser will be used to open or save files
	 */
	public FileChooser (String styleName, String title, Mode mode) {
		super(title);
		this.mode = mode;

		style = VisUI.getSkin().get(styleName, FileChooserStyle.class);
		sizes = VisUI.getSizes();

		init(null);
	}

	/**
	 * @param prefsName file name that will be used to store chooser preferences such as favorites or recent directories
	 * Should be your application package name e.g. com.seriouscompay.seriousprogram
	 */
	public static void setDefaultPrefsName (String prefsName) {
		PreferencesIO.setDefaultPrefsName(prefsName);
	}

	/** @deprecated replaced by {@link #setDefaultPrefsName(String)} */
	@Deprecated
	public static void setFavoritesPrefsName (String name) {
		PreferencesIO.setDefaultPrefsName(name);
	}

	private void init (FileHandle directory) {
		setModal(true);
		setResizable(true);
		setMovable(true);
		addCloseButton();
		closeOnEscape();

		iconProvider = new DefaultFileIconProvider(this);
		preferencesIO = new PreferencesIO();
		reloadPreferences(false);

		createToolbar();
		viewModePopupMenu = new PopupMenu(style.popupMenuStyleName);
		createViewModePopupMenu();
		createCenterContentPanel();
		createFileTextBox();
		createBottomButtons();

		createShortcutsMainPanel();
		shortcutsRootsPanel = new VerticalGroup();
		shortcutsFavoritesPanel = new VerticalGroup();
		rebuildShortcutsFavoritesPanel();

		fileMenu = new FilePopupMenu(this, new FilePopupMenuCallback() {
			@Override
			public void showNewDirDialog () {
				showNewDirectoryDialog();
			}

			@Override
			public void showFileDelDialog (FileHandle file) {
				showFileDeleteDialog(file);
			}
		});

		fileNameSuggestionPopup = new FileSuggestionPopup(this);

		rebuildShortcutsList();

		if (directory == null)
			setDirectory(Gdx.files.absolute(System.getProperty("user.home")), HistoryPolicy.IGNORE);
		else
			setDirectory(directory, HistoryPolicy.IGNORE);

		setSize(500, 600);
		centerWindow();

		createListeners();

		setFileTypeFilter(null);
		setFavoriteFolderButtonVisible(false);
//		setViewModeButtonVisible(false);
	}

	private void createToolbar () {
		VisTable toolbarTable = new VisTable(true);
		toolbarTable.defaults().minWidth(30).right();
		add(toolbarTable).fillX().expandX().pad(3).padRight(2);

		historyManager = new FileHistoryManager(style, this);

		currentPath = new VisTextField();
		final VisImageButton showRecentDirButton = new VisImageButton(style.expandDropdown);
		showRecentDirButton.setFocusBorderEnabled(false);

		dirsSuggestionPopup = new DirsSuggestionPopup(this, currentPath);
		dirsSuggestionPopup.setListener(new PopupMenu.PopupMenuListener() {
			@Override
			public void activeItemChanged (MenuItem newItem, boolean changedByKeyboard) {
				if (changedByKeyboard == false || newItem == null) return;

				currentPath.setText(newItem.getText().toString());
				currentPath.setCursorPosition(currentPath.getText().length());
			}
		});

		currentPath.addListener(new InputListener() {
			@Override
			public boolean keyTyped (InputEvent event, char character) {
				if (event.getKeyCode() == Keys.ENTER) {
					dirsSuggestionPopup.remove();
					return false;
				}
				float targetWidth = currentPath.getWidth() + showRecentDirButton.getWidth();
				dirsSuggestionPopup.pathFieldKeyTyped(getStage(), targetWidth - 20);
				dirsSuggestionPopup.setWidth(targetWidth);
				dirsSuggestionPopup.layout();
				return false;
			}

			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					FileHandle file = Gdx.files.absolute(currentPath.getText());
					if (file.exists()) {
						if (file.isDirectory() == false) file = file.parent();
						setDirectory(file, HistoryPolicy.ADD);
						addRecentDirectory(file);
					} else {
						showDialog(POPUP_DIRECTORY_DOES_NOT_EXIST.get());
						currentPath.setText(currentDirectory.path());
					}
					event.stop();
				}

				return false;
			}
		});

		currentPath.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
				if (focused == false) {
					currentPath.setText(currentDirectory.path());
				}
			}
		});

		showRecentDirButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				float targetWidth = currentPath.getWidth() + showRecentDirButton.getWidth();
				dirsSuggestionPopup.showRecentDirectories(getStage(), recentDirectories, targetWidth - 20);
				dirsSuggestionPopup.setWidth(targetWidth);
				dirsSuggestionPopup.layout();
			}
		});

		VisImageButton folderParentButton = new VisImageButton(style.iconFolderParent, PARENT_DIRECTORY.get());
		favoriteFolderButton = new VisImageButton(style.iconStar);
		favoriteFolderButtonTooltip = new Tooltip.Builder(CONTEXT_MENU_ADD_TO_FAVORITES.get()).target(favoriteFolderButton).build();
		viewModeButton = new VisImageButton(style.iconListSettings);
		new Tooltip.Builder(CHANGE_VIEW_MODE.get()).target(viewModeButton).build();
		VisImageButton folderNewButton = new VisImageButton(style.iconFolderNew, NEW_DIRECTORY.get());

		toolbarTable.add(historyManager.getButtonsTable());
		toolbarTable.add(currentPath).spaceRight(0).expand().fill();
		toolbarTable.add(showRecentDirButton).width(15 * sizes.scaleFactor).growY();
		toolbarTable.add(folderParentButton);
		toolbarTable.add(favoriteFolderButton).width(PrefWidthIfVisibleValue.INSTANCE).spaceRight(new ConstantIfVisibleValue(sizes.spacingRight));
		toolbarTable.add(viewModeButton).width(PrefWidthIfVisibleValue.INSTANCE).spaceRight(new ConstantIfVisibleValue(sizes.spacingRight));
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

		favoriteFolderButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (favorites.contains(currentDirectory, false)) {
					removeFavorite(currentDirectory);
				} else {
					addFavorite(currentDirectory);
				}
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

	private void createViewModePopupMenu () {
		rebuildViewModePopupMenu();
		viewModeButton.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				viewModePopupMenu.showMenu(getStage(), viewModeButton);
				event.stop();
				return true;
			}
		});
	}

	private void rebuildViewModePopupMenu () {
		viewModePopupMenu.clear();
		for (final ViewMode mode : ViewMode.values()) {
			if (mode.thumbnailMode && iconProvider.isThumbnailModesSupported() == false) continue;
			viewModePopupMenu.addItem(new MenuItem(mode.getBundleText(), new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					setViewMode(mode);
				}
			}));
		}
	}

	private void updateFavoriteFolderButton () {
		VisLabel label = (VisLabel) favoriteFolderButtonTooltip.getContent();

		if (favorites.contains(currentDirectory, false)) {
			favoriteFolderButton.getStyle().imageUp = style.iconStar;
			label.setText(CONTEXT_MENU_REMOVE_FROM_FAVORITES.get());
		} else {
			favoriteFolderButton.getStyle().imageUp = style.iconStarOutline;
			label.setText(CONTEXT_MENU_ADD_TO_FAVORITES.get());
		}

		favoriteFolderButtonTooltip.pack();
	}

	private void createCenterContentPanel () {
		fileListAdapter = new FileListAdapter(this, currentFiles);
		fileListView = new ListView<FileHandle>(fileListAdapter);
		setDefaultScrollPaneAttr(fileListView.getScrollPane());

		VisTable fileScrollPaneTable = new VisTable();
		fileScrollPaneTable.add(fileListView.getMainTable()).pad(2).top().expand().fillX();
		fileScrollPaneTable.setTouchable(Touchable.enabled);

		// shortcutsTable is contained in shortcutsScrollPane contained in shortcutsScrollPaneTable contained in mainSplitPane
		shortcutsTable = new VisTable();
		final VisScrollPane shortcutsScrollPane = setDefaultScrollPaneAttr(new VisScrollPane(shortcutsTable));
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
		mainSplitPane.setMaxSplitAmount(0.80f);

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

		fileTypeLabel = new VisLabel(FILE_TYPE.get());
		fileTypeSelectBox = new VisSelectBox<FileTypeFilter.Rule>();
		fileTypeSelectBox.getSelection().setProgrammaticChangeEvents(false);

		fileTypeSelectBox.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				activeFileTypeRule = fileTypeSelectBox.getSelected();
				rebuildFileList();
			}
		});

		table.defaults().left();
		table.add(nameLabel).spaceBottom(new ConstantIfVisibleValue(fileTypeSelectBox, 5f));
		table.add(selectedFileTextField).expandX().fillX()
				.spaceBottom(new ConstantIfVisibleValue(fileTypeSelectBox, 5f)).row();
		table.add(fileTypeLabel).height(PrefHeightIfVisibleValue.INSTANCE)
				.spaceBottom(new ConstantIfVisibleValue(sizes.spacingBottom));
		table.add(fileTypeSelectBox).height(PrefHeightIfVisibleValue.INSTANCE)
				.spaceBottom(new ConstantIfVisibleValue(sizes.spacingBottom)).expand().fill();

		selectedFileTextField.addListener(new InputListener() {
			@Override
			public boolean keyTyped (InputEvent event, char character) {
				deselectAll(false);
				fileNameSuggestionPopup.pathFieldKeyTyped(getStage(), currentFiles, selectedFileTextField);

				FileHandle enteredFile = currentDirectory.child(selectedFileTextField.getText());
				if (currentFiles.contains(enteredFile, false)) {
					highlightFiles(enteredFile);
				}
				return false;
			}
		});

		add(table).expandX().fillX().pad(3f).padRight(2f).padBottom(2f);
		row();
	}

	private void updateFileTypeSelectBox () {
		if (fileTypeFilter == null) {
			fileTypeLabel.setVisible(false);
			fileTypeSelectBox.setVisible(false);
			fileTypeSelectBox.invalidateHierarchy();
			return;
		} else {
			fileTypeLabel.setVisible(true);
			fileTypeSelectBox.setVisible(true);
			fileTypeSelectBox.invalidateHierarchy();
		}

		Array<FileTypeFilter.Rule> rules = new Array<FileTypeFilter.Rule>(fileTypeFilter.getRules());
		if (fileTypeFilter.isAllTypesAllowed()) {
			FileTypeFilter.Rule allTypesRule = new FileTypeFilter.Rule(ALL_FILES.get());
			rules.add(allTypesRule);
		}

		fileTypeSelectBox.setItems(rules);
		fileTypeSelectBox.setSelected(activeFileTypeRule);
	}

	private void createBottomButtons () {
		VisTextButton cancelButton = new VisTextButton(CANCEL.get());
		confirmButton = new VisTextButton(mode == Mode.OPEN ? OPEN.get() : SAVE.get());

		VisTable buttonTable = new VisTable(true);
		buttonTable.defaults().minWidth(70).right();
		add(buttonTable).padTop(3).padBottom(3).padRight(2).fillX().expandX();

		ButtonBar buttonBar = new ButtonBar();
		buttonBar.setIgnoreSpacing(true);
		buttonBar.setButton(ButtonType.CANCEL, cancelButton);
		buttonBar.setButton(ButtonType.OK, confirmButton);
		buttonTable.add(buttonBar.createTable()).expand().right();

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
				if (keycode == Keys.A && UIUtils.ctrl() && getStage().getKeyboardFocus() instanceof VisTextField == false) {
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

		if (files.size != 0) {
			listener.selected(files);
		}

		fadeOut();
	}

	private VisScrollPane setDefaultScrollPaneAttr (VisScrollPane scrollPane) {
		scrollPane.setOverscroll(false, false);
		scrollPane.setFlickScroll(false);
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
		Dialogs.showOKDialog(getStage(), POPUP_TITLE.get(), text);
	}

	private void showOverwriteQuestion (final Array<FileHandle> filesList) {
		String text = filesList.size == 1 ? POPUP_FILE_EXIST_OVERWRITE.get() : POPUP_MULTIPLE_FILE_EXIST_OVERWRITE.get();
		Dialogs.showOptionDialog(getStage(), POPUP_TITLE.get(), text, OptionDialogType.YES_NO, new OptionDialogAdapter() {
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
		FileHandle[] selectedFiles = new FileHandle[selectedItems.size];
		for (int i = 0; i < selectedFiles.length; i++) {
			selectedFiles[i] = selectedItems.get(i).file;
		}
		deselectAll();

		FileHandle[] files = listFilteredCurrentDirectory();
		currentPath.setText(currentDirectory.path());

		currentFiles.clear();

		if (files.length == 0) {
			fileListAdapter.itemsChanged();
			return;
		}

		currentFiles.addAll(FileUtils.sortFiles(files));
		fileListAdapter.itemsChanged();

		fileListView.getScrollPane().setScrollX(0);
		fileListView.getScrollPane().setScrollY(0);
		highlightFiles(selectedFiles);
	}

	/**
	 * Sets chooser selected files. All files that are invalid for current selection won't be selected. Files that doesn't
	 * exist will be ignored.
	 * @param files absolute {@link FileHandle}s of files to be selected
	 */
	public void setSelectedFiles (FileHandle... files) {
		deselectAll(false);

		for (FileHandle file : files) {
			FileItem item = fileListAdapter.getViews().get(file);
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
		preferencesIO.saveFavorites(favorites);
		rebuildShortcutsFavoritesPanel();
		rebuildShortcutsList(false);
		updateFavoriteFolderButton();
	}

	/**
	 * Removes favorite from current favorite list
	 * @param favorite to be removed (path to favorite)
	 * @return true if favorite was removed, false otherwise
	 */
	public boolean removeFavorite (FileHandle favorite) {
		boolean removed = favorites.removeValue(favorite, false);
		preferencesIO.saveFavorites(favorites);
		rebuildShortcutsFavoritesPanel();
		rebuildShortcutsList(false);
		updateFavoriteFolderButton();
		return removed;
	}

	private void addRecentDirectory (FileHandle file) {
		if (recentDirectories.contains(file, false)) return;
		recentDirectories.insert(0, file);
		if (recentDirectories.size > AbstractSuggestionPopup.MAX_SUGGESTIONS) recentDirectories.pop();
		preferencesIO.saveRecentDirectories(recentDirectories);
	}

	public void clearRecentDirectories () {
		recentDirectories.clear();
		preferencesIO.saveRecentDirectories(recentDirectories);
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
		for (FileItem item : fileListAdapter.getOrderedViews())
			item.select(false);

		removeInvalidSelections();
		updateSelectedFileFieldText();
	}

	/**
	 * Sets chooser selected files. Compared to {@link #setSelectedFiles(FileHandle...)} does not remove invalid files
	 * from selection.
	 */
	public void highlightFiles (FileHandle... files) {
		for (FileHandle file : files) {
			FileItem item = fileListAdapter.getViews().get(file);
			if (item != null) {
				item.select(false);
			}
		}
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
		selectedFileTextField.setCursorPosition(selectedFileTextField.getText().length());
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

	public ViewMode getViewMode () {
		return viewMode;
	}

	public void setViewMode (ViewMode viewMode) {
		if (this.viewMode == viewMode) return;
		this.viewMode = viewMode;
		iconProvider.viewModeChanged(viewMode);
		rebuildFileList();
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

	@Override
	public void setDirectory (FileHandle directory, HistoryPolicy historyPolicy) {
		if (directory.equals(currentDirectory)) return;
		if (directory.exists() == false) throw new IllegalStateException("Provided directory does not exist!");
		if (directory.isDirectory() == false)
			throw new IllegalStateException("Provided path is a file, not directory!");

		if (historyPolicy == HistoryPolicy.ADD) historyManager.historyAdd();

		currentDirectory = directory;
		iconProvider.directoryChanged(directory);

		rebuildFileList();

		if (historyPolicy == HistoryPolicy.CLEAR) historyManager.historyClear();

		updateFavoriteFolderButton();
	}

	@Override
	public FileHandle getCurrentDirectory () {
		return currentDirectory;
	}

	/** List currently set directory with all active filters */
	private FileHandle[] listFilteredCurrentDirectory () {
		FileHandle[] files = currentDirectory.list(fileFilter);
		if (fileTypeFilter == null || activeFileTypeRule == null) return files;

		FileHandle[] filtered = new FileHandle[files.length];

		int count = 0;
		for (FileHandle file : files) {
			if (file.isDirectory() == false && activeFileTypeRule.accept(file) == false) continue;
			filtered[count++] = file;
		}

		if (count == 0) return new FileHandle[0];

		FileHandle[] newFiltered = new FileHandle[count];
		System.arraycopy(filtered, 0, newFiltered, 0, count);
		return newFiltered;
	}

	public FileFilter getFileFilter () {
		return fileFilter;
	}

	public void setFileFilter (FileFilter fileFilter) {
		this.fileFilter = fileFilter;
		rebuildFileList();
	}

	/**
	 * Sets new {@link FileTypeFilter}. Note that if you modify {@link FileTypeFilter} you must call this method again with
	 * modified instance to apply changes.
	 */
	public void setFileTypeFilter (FileTypeFilter fileTypeFilter) {
		if (fileTypeFilter == null) {
			this.fileTypeFilter = null;
			this.activeFileTypeRule = null;
		} else {
			if (fileTypeFilter.getRules().size == 0)
				throw new IllegalArgumentException("FileTypeFilter doesn't have any rules added");
			this.fileTypeFilter = new FileTypeFilter(fileTypeFilter);
			this.activeFileTypeRule = this.fileTypeFilter.getRules().first();
		}

		updateFileTypeSelectBox();
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

	public void setFavoriteFolderButtonVisible (boolean favoriteFolderButtonVisible) {
		favoriteFolderButton.setVisible(favoriteFolderButtonVisible);
	}

	public boolean isFavoriteFolderButtonVisible () {
		return favoriteFolderButton.isVisible();
	}

	public void setViewModeButtonVisible (boolean viewModeButtonVisible) {
		viewModeButton.setVisible(viewModeButtonVisible);
	}

	public boolean isViewModeButtonVisible () {
		return viewModeButton.isVisible();
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

	/** @param multiSelectKey from {@link Keys} or {@link FileChooser#DEFAULT_KEY} to restore default */
	public void setMultiSelectKey (int multiSelectKey) {
		this.multiSelectKey = multiSelectKey;
	}

	public int getGroupMultiSelectKey () {
		return groupMultiSelectKey;
	}

	/** @param groupMultiSelectKey from {@link Keys} or {@link FileChooser#DEFAULT_KEY} to restore default */
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

	public FileChooserStyle getChooserStyle () {
		return style;
	}

	public Sizes getSizes () {
		return sizes;
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

	public void setPrefsName (String prefsName) {
		preferencesIO = new PreferencesIO(prefsName);
		reloadPreferences(true);
	}

	private void reloadPreferences (boolean rebuildUI) {
		favorites = preferencesIO.loadFavorites();
		recentDirectories = preferencesIO.loadRecentDirectories();
		if (rebuildUI) rebuildShortcutsFavoritesPanel();
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

		if (stage != null) {
			refresh();
			rebuildShortcutsFavoritesPanel(); //if by any chance multiple choosers changed favorites
			deselectAll();
		}

		if (watchingFilesEnabled) {
			if (stage != null) {
				startFileWatcher();
			} else {
				stopFileWatcher();
			}
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

	private void showNewDirectoryDialog () {
		Dialogs.showInputDialog(getStage(), NEW_DIRECTORY_DIALOG_TITLE.get(), NEW_DIRECTORY_DIALOG_TEXT.get(), true, new InputDialogAdapter() {
			@Override
			public void finished (String input) {
				if (FileUtils.isValidFileName(input) == false) {
					Dialogs.showErrorDialog(getStage(), NEW_DIRECTORY_DIALOG_ILLEGAL_CHARACTERS.get());
				}

				for (FileHandle file : currentDirectory.list()) {
					if (file.name().equals(input)) {
						Dialogs.showErrorDialog(getStage(), NEW_DIRECTORY_DIALOG_ALREADY_EXISTS.get());
						return;
					}
				}

				FileHandle newDir = currentDirectory.child(input);
				newDir.mkdirs();
				refresh();
				highlightFiles(newDir);
			}
		});
	}

	private void showFileDeleteDialog (final FileHandle fileToDelete) {
		Dialogs.showOptionDialog(getStage(), POPUP_TITLE.get(),
				fileDeleter.hasTrash() ? CONTEXT_MENU_MOVE_TO_TRASH_WARNING.get() : CONTEXT_MENU_DELETE_WARNING.get(),
				OptionDialogType.YES_NO, new OptionDialogAdapter() {
					@Override
					public void yes () {
						try {
							boolean success = fileDeleter.delete(fileToDelete);
							if (success == false) {
								Dialogs.showErrorDialog(getStage(), POPUP_DELETE_FILE_FAILED.get());
							}
						} catch (IOException e) {
							Dialogs.showErrorDialog(getStage(), POPUP_DELETE_FILE_FAILED.get(), e);
							e.printStackTrace();
						}
						refresh();
					}
				});
	}

	/**
	 * Sets {@link FileChooser.FileDeleter} that will be used for deleting files. You SHOULD NOT set your own file deleter.
	 * You should use either {@link DefaultFileDeleter} or JNAFileDeleter from vis-ui-contrib project. JNAFileDeleter
	 * supports moving file to system trash instead of deleting it permanently, however it requires JNA library in your
	 * project classpath.
	 */
	public void setFileDeleter (FileDeleter fileDeleter) {
		if (fileDeleter == null) throw new IllegalStateException("fileDeleter can't be null");
		this.fileDeleter = fileDeleter;
		fileMenu.fileDeleterChanged(fileDeleter.hasTrash());
	}

	public void setIconProvider (FileIconProvider iconProvider) {
		this.iconProvider = iconProvider;
		rebuildViewModePopupMenu();
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

	public enum ViewMode {
		DETAILS(false, VIEW_MODE_DETAILS),

		BIG_ICONS(true, VIEW_MODE_BIG_ICONS),
		MEDIUM_ICONS(true, VIEW_MODE_MEDIUM_ICONS),
		SMALL_ICONS(true, VIEW_MODE_SMALL_ICONS),

		LIST(false, VIEW_MODE_LIST);

		private final FileChooserText bundleText;
		private final boolean thumbnailMode;

		ViewMode (boolean thumbnailMode, FileChooserText bundleText) {
			this.thumbnailMode = thumbnailMode;
			this.bundleText = bundleText;
		}

		public String getBundleText () {
			return bundleText.get();
		}

		public void setupGridGroup (Sizes sizes, GridGroup group) {
			if (isGridMode() == false) return;
			if (this == LIST) {
				group.setItemSize(getGridSize(sizes), 22 * sizes.scaleFactor);
				return;
			}
			group.setItemSize(getGridSize(sizes));
		}

		public boolean isGridMode () {
			return isThumbnailMode() || this == LIST;
		}

		public boolean isThumbnailMode () {
			return thumbnailMode;
		}

		public float getGridSize (Sizes sizes) {
			switch (this) {
				case DETAILS:
					return -1;
				case BIG_ICONS:
					return sizes.fileChooserViewModeBigIconsSize;
				case MEDIUM_ICONS:
					return sizes.fileChooserViewModeMediumIconsSize;
				case SMALL_ICONS:
					return sizes.fileChooserViewModeSmallIconsSize;
				case LIST:
					return sizes.fileChooserViewModeListWidthSize;
				default:
					return -1;
			}
		}
	}

	/**
	 * Provides icons that will be used for file thumbnail on file list. If not set default is used that supports
	 * directories and few basic file types. If you want to add your custom icon your should extend {@link DefaultFileIconProvider}
	 */
	public interface FileIconProvider {
		/** @return icon that will be used for this file or null if no icon should be displayed */
		Drawable provideIcon (FileItem item, FileHandle file);

		/**
		 * @return true if this icon provider can supply proper icons for {@link ViewMode#BIG_ICONS}, {@link ViewMode#MEDIUM_ICONS}
		 * and {@link ViewMode#SMALL_ICONS} view modes, false otherwise. If false thumbnail view modes won't be available for selection.
		 */
		boolean isThumbnailModesSupported ();

		void directoryChanged (FileHandle newDirectory);

		void viewModeChanged (ViewMode newViewMode);
	}

	public static class DefaultFileIconProvider implements FileIconProvider {
		protected FileChooser chooser;
		protected FileChooserStyle style;

		protected FileItem currentItem;
		protected FileHandle currentFile;

		public DefaultFileIconProvider (FileChooser chooser) {
			this.chooser = chooser;
			this.style = chooser.style;
		}

		@Override
		public Drawable provideIcon (FileItem item, FileHandle file) {
			this.currentItem = item;
			this.currentFile = file;

			if (file.isDirectory()) return getDirIcon();
			String ext = file.extension().toLowerCase();
			if (ext.equals("jpg") || ext.equals("png") || ext.equals("bmp")) return getImageIcon();
			if (ext.equals("wav") || ext.equals("ogg") || ext.equals("mp3")) return getAudioIcon();
			if (ext.equals("pdf")) return getPdfIcon();
			if (ext.equals("txt")) return getTextIcon();
			return null;
		}

		protected Drawable getDirIcon () {
			return style.iconFolder;
		}

		protected Drawable getImageIcon () {
			return style.iconFileImage;
		}

		protected Drawable getAudioIcon () {
			return style.iconFileAudio;
		}

		protected Drawable getPdfIcon () {
			return style.iconFilePdf;
		}

		protected Drawable getTextIcon () {
			return style.iconFileText;
		}

		@Override
		public boolean isThumbnailModesSupported () {
			return false;
		}

		@Override
		public void directoryChanged (FileHandle newDirectory) {

		}

		@Override
		public void viewModeChanged (ViewMode newViewMode) {

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

	public interface FileDeleter {
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
			return file.deleteDirectory();
		}
	}

	/** Internal FileChooser API. */
	public class FileItem extends Table implements Focusable {
		private FileHandle file;
		private VisLabel name;
		private VisLabel size;
		private Image iconImage;

		public FileItem (final FileHandle file, ViewMode viewMode) {
			this.file = file;
			setTouchable(Touchable.enabled);

			name = new VisLabel(file.name(), viewMode == ViewMode.SMALL_ICONS ? "small" : "default");
			name.setEllipsis(true);

			if (file.isDirectory())
				size = new VisLabel("");
			else
				size = new VisLabel(FileUtils.readableFileSize(file.length()));

			Drawable icon = iconProvider.provideIcon(this, file);

			if (viewMode.isThumbnailMode()) {
				add(iconImage = new Image(icon, Scaling.none)).padTop(3).grow().row();
				add(name).minWidth(1);
			} else {
				if (icon != null) add(iconImage = new Image(icon)).padTop(3).minWidth(22 * sizes.scaleFactor);
				add(name).minWidth(1).padLeft(icon == null ? 22 : 0);
				add().growX().minWidth(0);
				if (viewMode == ViewMode.DETAILS) {
					add(size).right().padRight(6);
				}
			}

			addListener();
		}

		/**
		 * Updates file item icon, can be used for asynchronous icon loading. Note that icon provided must not return null
		 * even if this item icon will be loaded later.
		 */
		public void setIcon (Drawable icon) {
			iconImage.setDrawable(icon);
		}

		private void addListener () {
			addListener(new InputListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					FocusManager.switchFocus(getStage(), FileItem.this);
					getStage().setKeyboardFocus(FileItem.this);
					return true;
				}

				@Override
				public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
					if (event.getButton() == Buttons.RIGHT) {
						fileMenu.build(favorites, file);
						fileMenu.showMenu(getStage(), event.getStageX(), event.getStageY());
					}
				}

				@Override
				public boolean keyDown (InputEvent event, int keycode) {
					if (keycode == Keys.FORWARD_DEL) {
						showFileDeleteDialog(file);
						return true;
					}

					return false;
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
					Array<FileItem> actors = fileListAdapter.getOrderedViews();

					int thisSelectionIndex = getItemId(actors, FileItem.this);
					int lastSelectionIndex = getItemId(actors, selectedItems.get(selectedItems.size - 2));

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
						FileItem item = actors.get(i);
						item.select(false);
					}
				}

				private int getItemId (Array<FileItem> actors, FileItem item) {
					for (int i = 0; i < actors.size; i++) {
						if (actors.get(i) == item) return i;
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

		@Override
		public void focusLost () {

		}

		@Override
		public void focusGained () {

		}

		public FileHandle getFile () {
			return file;
		}
	}

	private class ShortcutItem extends Table implements RootNameListener, Focusable {
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
					FocusManager.switchFocus(getStage(), ShortcutItem.this);
					getStage().setKeyboardFocus(ShortcutItem.this);
					return true;
				}

				@Override
				public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
					if (event.getButton() == Buttons.RIGHT) {
						fileMenu.buildForFavorite(favorites, file);
						fileMenu.showMenu(getStage(), event.getStageX(), event.getStageY());
					}
				}

				@Override
				public boolean keyDown (InputEvent event, int keycode) {
					if (keycode == Keys.FORWARD_DEL) {
						FileHandle gdxFile = Gdx.files.absolute(file.getAbsolutePath());
						if (favorites.contains(gdxFile, false)) {
							removeFavorite(gdxFile);
						}
						return true;
					}
					return false;
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
							getStage().setScrollFocus(fileListView.getScrollPane());
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

		@Override
		public void focusGained () {
		}

		@Override
		public void focusLost () {
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
