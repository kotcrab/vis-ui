/*
 * Copyright 2014-2017 See AUTHORS file.
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

package com.kotcrab.vis.ui.widget.file.internal;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.SnapshotArray;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisImage;

/** @author Kotcrab */
public class IconStack extends WidgetGroup {
	private float prefWidth, prefHeight, minWidth, minHeight, maxWidth, maxHeight;
	private boolean sizeInvalid = true;
	private VisImage icon;
	private VisCheckBox checkBox;

	public IconStack (VisImage icon, VisCheckBox checkBox) {
		this.icon = icon;
		this.checkBox = checkBox;
		setTransform(false);
		setTouchable(Touchable.childrenOnly);
		addActor(icon);
		addActor(checkBox);
	}

	@Override
	public void invalidate () {
		super.invalidate();
		sizeInvalid = true;
	}

	private void computeSize () {
		sizeInvalid = false;
		prefWidth = 0;
		prefHeight = 0;
		minWidth = 0;
		minHeight = 0;
		maxWidth = 0;
		maxHeight = 0;
		SnapshotArray<Actor> children = getChildren();
		for (int i = 0, n = children.size; i < n; i++) {
			Actor child = children.get(i);
			float childMaxWidth, childMaxHeight;
			if (child instanceof Layout) {
				Layout layout = (Layout) child;
				prefWidth = Math.max(prefWidth, layout.getPrefWidth());
				prefHeight = Math.max(prefHeight, layout.getPrefHeight());
				minWidth = Math.max(minWidth, layout.getMinWidth());
				minHeight = Math.max(minHeight, layout.getMinHeight());
				childMaxWidth = layout.getMaxWidth();
				childMaxHeight = layout.getMaxHeight();
			} else {
				prefWidth = Math.max(prefWidth, child.getWidth());
				prefHeight = Math.max(prefHeight, child.getHeight());
				minWidth = Math.max(minWidth, child.getWidth());
				minHeight = Math.max(minHeight, child.getHeight());
				childMaxWidth = 0;
				childMaxHeight = 0;
			}
			if (childMaxWidth > 0) maxWidth = maxWidth == 0 ? childMaxWidth : Math.min(maxWidth, childMaxWidth);
			if (childMaxHeight > 0) maxHeight = maxHeight == 0 ? childMaxHeight : Math.min(maxHeight, childMaxHeight);
		}
	}

	public void add (Actor actor) {
		addActor(actor);
	}

	@Override
	public void layout () {
		if (sizeInvalid) computeSize();
		float width = getWidth(), height = getHeight();
		icon.setBounds(0, 0, width, height);
		icon.validate();
		float checkHeight = checkBox.getStyle().checkBackground.getMinHeight();
		checkBox.setBounds(3, height - checkHeight - 3, checkBox.getPrefWidth(), checkBox.getPrefHeight());
		checkBox.validate();
	}

	@Override
	public float getPrefWidth () {
		if (sizeInvalid) computeSize();
		return prefWidth;
	}

	@Override
	public float getPrefHeight () {
		if (sizeInvalid) computeSize();
		return prefHeight;
	}

	@Override
	public float getMinWidth () {
		if (sizeInvalid) computeSize();
		return minWidth;
	}

	@Override
	public float getMinHeight () {
		if (sizeInvalid) computeSize();
		return minHeight;
	}

	@Override
	public float getMaxWidth () {
		if (sizeInvalid) computeSize();
		return maxWidth;
	}

	@Override
	public float getMaxHeight () {
		if (sizeInvalid) computeSize();
		return maxHeight;
	}
}
