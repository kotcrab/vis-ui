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

package com.kotcrab.vis.ui.widget.tabbedpane;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;

/**
 * Base class for tabs used in TabbedPane. Tab can be savable, meaning that it can be saved and will display warning
 * dialog 'do you want to save changes' before closing. Tab can be also closeable by user meaning that user can close
 * this tab manually from tabbed pane (using 'X' button or by pressing mouse wheel on tab).
 * @author Kotcrab
 */
public abstract class Tab implements Disposable {
	private boolean activeTab;
	private TabbedPane pane;

	private boolean closeableByUser = true;
	private boolean savable = false;
	private boolean dirty = false;

	public Tab () {
	}

	/** @param savable if true tab can be saved and marked as dirty. */
	public Tab (boolean savable) {
		this.savable = savable;
	}

	/**
	 * @param savable if true tab can be saved and marked as dirty.
	 * @param closeableByUser if true tab can be closed by user from tabbed pane.
	 */
	public Tab (boolean savable, boolean closeableByUser) {
		this.savable = savable;
		this.closeableByUser = closeableByUser;
	}

	/** @return tab title used by tabbed pane. */
	public abstract String getTabTitle ();

	/**
	 * @return table that contains this tab view, will be passed to tabbed pane listener. Should
	 * return same table every time this is called.
	 */
	public abstract Table getContentTable ();

	/** Called by pane when this tab becomes shown. Class overriding this should call super.onShow(). */
	public void onShow () {
		activeTab = true;
	}

	/** Called by pane when this tab becomes hidden. Class overriding this should call super.onHide(). */
	public void onHide () {
		activeTab = false;
	}

	/** @return true is this tab is currently active. */
	public boolean isActiveTab () {
		return activeTab;
	}

	/** @return pane that this tab belongs to, or null. */
	public TabbedPane getPane () {
		return pane;
	}

	/** Should be called by TabbedPane only, when tab is added to pane. */
	public void setPane (TabbedPane pane) {
		this.pane = pane;
	}

	public boolean isSavable () {
		return savable;
	}

	public boolean isCloseableByUser () {
		return closeableByUser;
	}

	public boolean isDirty () {
		return dirty;
	}

	public void setDirty (boolean dirty) {
		checkSavable();

		boolean update = (dirty != this.dirty);

		if (update) {
			this.dirty = dirty;
			if (pane != null) getPane().updateTabTitle(this);
		}
	}

	/** Marks this tab as dirty */
	public void dirty () {
		setDirty(true);
	}

	/**
	 * Called when this tab should save its own state. After saving setDirty(false) must be called manually to remove dirty state.
	 * @return true when save succeeded, false otherwise.
	 */
	public boolean save () {
		checkSavable();

		return false;
	}

	private void checkSavable () {
		if (isSavable() == false) throw new IllegalStateException("Tab " + getTabTitle() + " is not savable!");
	}

	/** Removes this tab from pane (if any). */
	public void removeFromTabPane () {
		if (pane != null) pane.remove(this);
	}

	/** Called when tab is being removed from scene. */
	@Override
	public void dispose () {

	}
}
