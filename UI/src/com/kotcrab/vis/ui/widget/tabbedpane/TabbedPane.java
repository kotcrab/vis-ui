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

package com.kotcrab.vis.ui.widget.tabbedpane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.i18n.BundleText;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle;

/**
 * A tabbed pane, allows to have multiple tabs open and switch between them. TabbedPane does not handle displaying tab content,
 * you have to do that manually using tabbed pane listener to get tab content table (see {@link Tab#getContentTable()} and {@link TabbedPaneListener}).
 * All tabs must extend {@link Tab} class.
 * @author Kotcrab
 * @since 0.7.0
 */
public class TabbedPane {
	private TabbedPaneStyle style;
	private VisImageButtonStyle sharedCloseActiveButtonStyle;

	private VisTable tabsTable;
	private VisTable mainTable;

	private Array<Tab> tabs;
	private ObjectMap<Tab, TabButtonTable> tabsButtonMap;
	private ButtonGroup<Button> group;

	private Tab activeTab;

	private Array<TabbedPaneListener> listeners;
	private boolean allowTabDeselect;

	public TabbedPane () {
		this(VisUI.getSkin().get(TabbedPaneStyle.class));
	}

	public TabbedPane (String styleName) {
		this(VisUI.getSkin().get(styleName, TabbedPaneStyle.class));
	}

	public TabbedPane (TabbedPaneStyle style) {
		this.style = style;
		listeners = new Array<TabbedPaneListener>();

		sharedCloseActiveButtonStyle = VisUI.getSkin().get("close-active-tab", VisImageButtonStyle.class);

		group = new ButtonGroup<Button>();

		mainTable = new VisTable();
		tabsTable = new VisTable();

		mainTable.setBackground(style.background);

		tabs = new Array<Tab>();
		tabsButtonMap = new ObjectMap<Tab, TabButtonTable>();

		mainTable.add(tabsTable).left().expandX();
		mainTable.row();

		// if height is not set bottomBar may sometimes disappear
		if (style.bottomBar != null) {
			mainTable.add(new Image(style.bottomBar)).expand().fill().height(style.bottomBar.getMinHeight());
		}
	}

	/**
	 * @param allowTabDeselect if true user may deselect tab, meaning that there won't be any active tab.
	 * Allows to create similar behaviour like in Intellij IDEA bottom quick access bar
	 */
	public void setAllowTabDeselect (boolean allowTabDeselect) {
		this.allowTabDeselect = allowTabDeselect;
		if (allowTabDeselect)
			group.setMinCheckCount(0);
		else
			group.setMinCheckCount(1);
	}

	public boolean isAllowTabDeselect () {
		return allowTabDeselect;
	}

	public void add (Tab tab) {
		tab.setPane(this);
		tabs.add(tab);

		rebuildTabsTable();
		switchTab(tab);
	}

	public void insert (int index, Tab tab) {
		tab.setPane(this);
		tabs.insert(index, tab);
		rebuildTabsTable();
	}

	/**
	 * Removes tab from pane, if tab is dirty this won't cause to display "Unsaved changes" dialog!
	 * @param tab to be removed
	 * @return true if tab was removed, false if that tab wasn't added to this pane
	 */
	public boolean remove (Tab tab) {
		return remove(tab, true);
	}

	/**
	 * Removes tab from pane, if tab is dirty and 'ignoreTabDirty == false' this will cause to display "Unsaved changes" dialog!
	 * @return true if tab was removed, false if that tab wasn't added to this pane or "Unsaved changes" dialog was started
	 */
	public boolean remove (final Tab tab, boolean ignoreTabDirty) {
		if (ignoreTabDirty) {
			return removeTab(tab);
		}

		if (tab.isDirty() && mainTable.getStage() != null) {
			DialogUtils.showOptionDialog(mainTable.getStage(), Text.UNSAVED_DIALOG_TITLE.get(), Text.UNSAVED_DIALOG_TEXT.get(),
					OptionDialogType.YES_NO_CANCEL, new OptionDialogAdapter() {
						@Override
						public void yes () {
							tab.save();
							removeTab(tab);
						}

						@Override
						public void no () {
							removeTab(tab);
						}
					});
		} else
			return removeTab(tab);

		return false;
	}

	private boolean removeTab (Tab tab) {
		int index = tabs.indexOf(tab, true);
		boolean success = tabs.removeValue(tab, true);

		if (success) {
			tabsButtonMap.remove(tab);

			tab.setPane(null);
			tab.onHide();
			tab.dispose();
			notifyListenersRemoved(tab);

			if (tabs.size == 0) {
				//all tabs were removed so notify listener
				notifyListenersRemovedAll();
			} else if (activeTab == tab && index != 0) {
				//switch to previous tab
				switchTab(--index);
			}

			rebuildTabsTable();
		}

		return success;
	}

	/** Removes all tabs, ignores if tab is dirty */
	public void removeAll () {
		for (Tab tab : tabs) {
			tab.setPane(null);
			tab.onHide();
			tab.dispose();
		}

		tabs.clear();
		tabsButtonMap.clear();

		rebuildTabsTable();
		notifyListenersRemovedAll();
	}

	public void switchTab (int index) {
		tabsButtonMap.get(tabs.get(index)).select();
	}

	public void switchTab (Tab tab) {
		tabsButtonMap.get(tab).select();
	}

	/**
	 * Must be called when you want to update tab title. If tab is dirty an '*' is added before title.
	 * This is called automatically if using {@link Tab#setDirty(boolean)}
	 * @param tab that title will be updated
	 */
	public void updateTabTitle (Tab tab) {
		tabsButtonMap.get(tab).button.setText(getTabTitle(tab));
	}

	private String getTabTitle (Tab tab) {
		String title = tab.getTabTitle();
		if (tab.isDirty()) title = "*" + title;
		return title;
	}

	private void rebuildTabsTable () {
		Tab lastSelectedTab = activeTab;
		tabsTable.clear();
		group.clear();

		for (final Tab tab : tabs) {
			TabButtonTable buttonTable = tabsButtonMap.get(tab);
			if (buttonTable == null) {
				buttonTable = new TabButtonTable(tab);
				tabsButtonMap.put(tab, buttonTable);
			}

			tabsTable.add(buttonTable);
			group.add(buttonTable.button);

			if (tabs.size == 1 && lastSelectedTab != null) {
				buttonTable.select();
				notifyListenersSwitched(tab);
			} else if (tab == lastSelectedTab) {
				buttonTable.select(); // maintains current previous tab while rebuilding
			}
		}
	}

	public Table getTable () {
		return mainTable;
	}

	/** @return active tab or null if no tab is selected. */
	public Tab getActiveTab () {
		return activeTab;
	}

	public void addListener (TabbedPaneListener listener) {
		listeners.add(listener);
	}

	public boolean removeListener (TabbedPaneListener listener) {
		return listeners.removeValue(listener, true);
	}

	private void notifyListenersSwitched (Tab tab) {
		for (TabbedPaneListener listener : listeners)
			listener.switchedTab(tab);
	}

	private void notifyListenersRemoved (Tab tab) {
		for (TabbedPaneListener listener : listeners)
			listener.removedTab(tab);
	}

	private void notifyListenersRemovedAll () {
		for (TabbedPaneListener listener : listeners)
			listener.removedAllTabs();
	}

	public Array<Tab> getTabs () {
		return tabs;
	}

	public static class TabbedPaneStyle {
		public Drawable bottomBar;
		public Drawable background;
		public VisTextButtonStyle buttonStyle;

		public TabbedPaneStyle () {
		}

		public TabbedPaneStyle (TabbedPaneStyle other) {
			this.bottomBar = other.bottomBar;
			this.background = other.background;
			this.buttonStyle = other.buttonStyle;
		}

		public TabbedPaneStyle (Drawable background, Drawable bottomBar, VisTextButtonStyle buttonStyle) {
			this.background = background;
			this.bottomBar = bottomBar;
			this.buttonStyle = buttonStyle;
		}

	}

	private class TabButtonTable extends VisTable {
		public VisTextButton button;
		public VisImageButton closeButton;
		private Tab tab;

		private VisTextButtonStyle buttonStyle;
		private VisImageButtonStyle closeButtonStyle;

		public TabButtonTable (final Tab tab) {
			this.tab = tab;
			button = new VisTextButton(getTabTitle(tab), style.buttonStyle);
			button.setFocusBorderEnabled(false);
			button.setProgrammaticChangeEvents(false);

			closeButtonStyle = new VisImageButtonStyle(VisUI.getSkin().get("close", VisImageButtonStyle.class));

			closeButton = new VisImageButton(closeButtonStyle);
			closeButton.getImage().setScaling(Scaling.fill);
			closeButton.getImage().setColor(Color.RED);

			addListeners();

			buttonStyle = (VisTextButtonStyle) button.getStyle();
			closeButtonStyle = closeButton.getStyle();

			add(button);
			if (tab.isCloseableByUser()) add(closeButton).size(14, button.getHeight());
		}

		private void addListeners () {
			closeButton.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					closeTabAsUser();
				}
			});

			button.addListener(new InputListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int mouseButton) {
					if (UIUtils.left()) {
						closeButtonStyle.up = buttonStyle.down;
					}

					if (mouseButton == Buttons.MIDDLE) closeTabAsUser();

					return true;
				}

				@Override
				public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
					closeButtonStyle.up = buttonStyle.up;
				}

				@Override
				public boolean mouseMoved (InputEvent event, float x, float y) {
					if (activeTab != tab) {
						setCloseButtonOnMouseMove();
					}

					return false;
				}

				@Override
				public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
					if (activeTab != tab) {
						closeButtonStyle.up = buttonStyle.up;
					}
				}

				@Override
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					if (activeTab != tab && Gdx.input.justTouched() == false) {
						setCloseButtonOnMouseMove();
					}
				}

				private void setCloseButtonOnMouseMove () {
					if (UIUtils.left()) {
						closeButtonStyle.up = buttonStyle.down;
					} else {
						closeButtonStyle.up = buttonStyle.over;
					}
				}
			});

			button.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					switchToNewTab();
				}
			});
		}

		private void switchToNewTab () {
			//there was some previous tab, deselect it
			if (activeTab != null && activeTab != tab) {
				TabButtonTable table = tabsButtonMap.get(activeTab);
				//table may no longer exists if tab was removed, no big deal since this only changes
				//button style, tab.onHide() will be already called by remove() method
				if (table != null) {
					table.deselect();
					activeTab.onHide();
				}
			}

			if (button.isChecked() && tab != activeTab) { //switch to new tab
				activeTab = tab;
				notifyListenersSwitched(tab);
				tab.onShow();
				closeButton.setStyle(sharedCloseActiveButtonStyle);
			} else if (group.getCheckedIndex() == -1) { //no tab selected (allowTabDeselect == true)
				activeTab = null;
				notifyListenersSwitched(null);
			}

		}

		/** Closes tab, does nothing if Tab is not closeable by User */
		private void closeTabAsUser () {
			if (tab.isCloseableByUser()) {
				TabbedPane.this.remove(tab, false);
			}
		}

		public void select () {
			button.setChecked(true);
			switchToNewTab();
		}

		public void deselect () {
			closeButton.setStyle(closeButtonStyle);
		}
	}

	private enum Text implements BundleText {
		// @formatter:off
		UNSAVED_DIALOG_TITLE		{public String getName () {return "unsavedDialogTitle";}},
		UNSAVED_DIALOG_TEXT			{public String getName () {return "unsavedDialogText";}};
		// @formatter:on

		@Override
		public String get () {
			return VisUI.getTabbedPaneBundle().get(getName());
		}

		@Override
		public String format () {
			throw new UnsupportedOperationException();
		}

		@Override
		public String format (Object... arguments) {
			throw new UnsupportedOperationException();
		}
	}
}
