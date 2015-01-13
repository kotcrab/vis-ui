/**
 * Copyright 2014-2015 Pawel Pastuszak
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

package com.kotcrab.vis.editor.ui.tab;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class Tab {
	private boolean activeTab;
	private TabbedPane pane;

	public abstract String getButtonText ();

	public abstract TabViewMode getViewMode ();

	public Table getContentTable () {
		return null;
	}

	public void render (Batch batch) {

	}

	public void onShow () {
		activeTab = true;
	}

	public void onHide () {
		activeTab = false;
	}
	
	public boolean isActiveTab () {
		return activeTab;
	}

	public TabbedPane getPane () {
		return pane;
	}

	public void setPane (TabbedPane pane) {
		this.pane = pane;
	}
}
