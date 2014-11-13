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
import pl.kotcrab.vis.ui.widget.Separator;
import pl.kotcrab.vis.ui.widget.VisLabel;
import pl.kotcrab.vis.ui.widget.VisScrollPane;
import pl.kotcrab.vis.ui.widget.VisSplitPane;
import pl.kotcrab.vis.ui.widget.VisTextButton;
import pl.kotcrab.vis.ui.widget.VisWindow;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

public class FileChooser extends VisWindow {
	public enum Mode {
		LOAD, SAVE
	};

	private FileFilter fileFilter = new DefaultFileFilter();
	
	private static final Drawable highlightBg = VisUI.skin.getDrawable("list-selection");

	private File currentDirectory;
	private VisTable shortcutsTable;
	private VisTable fileTable;

	private VisTextButton cancelButton;
	private VisTextButton chooseButton;

	private FileItem highlitedItem;

	private FileChooserStyle style;

	private Mode mode;

	public FileChooser (Stage parent, String title, Mode mode) {
		super(parent, title);
		this.mode = mode;

		style = new FileChooserStyle();
		setTitleAlignment(Align.left);

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
		splitPane.setSplitAmount(0.3f);

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
		shortcutsTable.clear();

		shortcutsTable.add(new FileItem(new File(System.getProperty("user.home") + "/Desktop"), style.iconFolder)).expand().fill()
			.row();
		shortcutsTable.add(new FileItem(new File(System.getProperty("user.home")), style.iconFolder)).expand().fill().row();

		shortcutsTable.add(new Separator()).fill().expand().row();

		File[] roots = File.listRoots();

		for (int i = 0; i < roots.length; i++) {
			File root = roots[i];
			FileItem item = null;
			FileSystemView view = FileSystemView.getFileSystemView();

			if (mode == Mode.LOAD ? root.canRead() : root.canWrite()) {
				String displayName = view.getSystemDisplayName(root);

				if (displayName != null && displayName.equals("") == false)
					item = new FileItem(root, displayName, style.iconDrive);
				else
					item = new FileItem(root, root.toString(), style.iconDrive);

				shortcutsTable.add(item).expand().fill().row();
			}
		}

		shortcutsTable.add(new Separator()).fill().expand().row();

		shortcutsTable.add(new FileItem(null, "Favorite", style.iconFolder)).expand().fill().row();

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
			if (f.isFile()) return true;
			if (f.list() == null) return false;

			return true;
		}
	}

	private class FileItem extends Table {
		
		private Image icon;
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

		public FileItem (File file, String customName, Drawable icon) {
			this.file = file;
			this.name = new VisLabel(customName);

			add(new Image(icon)).padTop(3);
			add(name).expand().fill().left().padRight(6);
			pack();

			addListener();
		}

		public FileItem (File file) {
			this.file = file;
			this.name = new VisLabel(file.getName());

			if (file.isDirectory())
			{
				add(new Image(style.iconFolder)).padTop(3);
				size = new VisLabel("");
			}
			else
				size = new VisLabel(FileUtils.readableFileSize(file.length()));

			add(name).expand().fill().left();
			add(size).padRight(6);
			pack();

			addListener();
		}

		public FileItem (File file, Drawable icon) {
			this.file = file;
			this.name = new VisLabel(file.getName());

			if (file.isDirectory())
				size = new VisLabel("");
			else
				size = new VisLabel(FileUtils.readableFileSize(file.length()));

			add(new Image(icon)).padTop(3);
			add(name).expand().fill().left();
			add(size).padRight(6);
			pack();

			addListener();
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
			if (highlitedItem != null) highlitedItem.resetHighlight();
			highlitedItem = FileItem.this;
			setBackground(highlightBg);
		}

		private void resetHighlight () {
			setBackground((Drawable)null);
		}

	}

	static public class FileChooserStyle {
		public Drawable iconFolder;
		public Drawable iconDrive;

		public FileChooserStyle () {
			iconFolder = VisUI.skin.getDrawable("icon-folder");
			iconDrive = VisUI.skin.getDrawable("icon-drive");
		}

		public FileChooserStyle (Drawable iconFolder, Drawable iconDrive) {
			this.iconFolder = iconFolder;
			this.iconDrive = iconDrive;
		}

		public FileChooserStyle (FileChooserStyle style) {
			iconFolder = style.iconFolder;
			iconDrive = style.iconDrive;
		}
	}

}
