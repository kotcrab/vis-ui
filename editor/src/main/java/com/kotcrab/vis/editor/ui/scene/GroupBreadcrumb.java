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

package com.kotcrab.vis.editor.ui.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.IntArray;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/** @author Kotcrab */
public class GroupBreadcrumb extends VisTable {
	private GroupBreadcrumbListener listener;

	private Drawable arrow;
	private IntArray groupsHierarchy = new IntArray();

	public GroupBreadcrumb (GroupBreadcrumbListener listener) {
		this.listener = listener;

		setBackground(VisUI.getSkin().getDrawable("window-bg"));
		setTouchable(Touchable.enabled);
		setVisible(false);

		arrow = Assets.getMisc("breadcrumb-arrow");

		top().left();
		padTop(3).padLeft(4);
		defaults().padRight(4);

		pack();
	}

	public void rebuildBreadcrumb () {
		clearChildren();

		if (groupsHierarchy.size != 0) {
			setVisible(true);
		} else {
			setVisible(false);
			return;
		}

		LinkLabel root = new LinkLabel("Scene", Color.WHITE);
		root.setListener(url -> listener.rootClicked());
		add(root);
		add(new Image(arrow));

		for (int i = 0; i < groupsHierarchy.size; i++) {
			int gid = groupsHierarchy.get(i);

			LinkLabel groupLabel = new LinkLabel("Group (id: " + gid + ")", Color.WHITE);
			groupLabel.setListener(url -> {
				if (gid == groupsHierarchy.peek()) return;
				listener.clicked(gid);
				trimToGid(gid);
			});

			add(groupLabel);

			if (i != groupsHierarchy.size - 1)
				add(new Image(arrow));
		}
	}

	public int peekFirstGroupId () {
		if (groupsHierarchy.size != 0)
			return groupsHierarchy.first();
		else
			return -1;
	}

	public int peekLastGroupId () {
		if (groupsHierarchy.size != 0)
			return groupsHierarchy.peek();
		else
			return -1;
	}

	public boolean isInHierarchy (int gid) {
		return groupsHierarchy.contains(gid);
	}

	public void trimToGid (int gid) {
		int trimStartIndex = -1;

		for (int i = 0; i < groupsHierarchy.size; i++) {
			if (groupsHierarchy.get(i) == gid) {
				trimStartIndex = i + 1;
			}
		}

		groupsHierarchy.removeRange(trimStartIndex, groupsHierarchy.size - 1);
		rebuildBreadcrumb();
	}

	public void addGroup (int gid) {
		if (groupsHierarchy.contains(gid)) return;

		groupsHierarchy.add(gid);
		rebuildBreadcrumb();
	}

	public void resetHierarchy () {
		groupsHierarchy.clear();
		rebuildBreadcrumb();
	}

	@Override
	public void setVisible (boolean visible) {
		super.setVisible(visible);
		invalidateHierarchy();
	}

	@Override
	public float getPrefHeight () {
		if (isVisible())
			return super.getPrefHeight() + 4;
		else
			return 0;
	}

	public interface GroupBreadcrumbListener {
		void clicked (int gid);

		void rootClicked ();
	}
}
