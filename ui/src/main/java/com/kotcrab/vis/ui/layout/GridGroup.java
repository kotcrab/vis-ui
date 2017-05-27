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

package com.kotcrab.vis.ui.layout;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.SnapshotArray;

/**
 * Arrange actors in grid layout. You can set item width, height and spacing between items.
 * <p>
 * Grid group can be embedded in scroll pane. However in such case scrolling in X direction must be disabled.
 * @author Kotcrab
 * @since 0.7.2
 */
public class GridGroup extends WidgetGroup {
	private float prefWidth;
	private float prefHeight;
	private float lastPrefHeight;
	private boolean sizeInvalid = true;

	private float itemWidth = 256;
	private float itemHeight = 256;
	private float spacing = 8;

	public GridGroup () {
		setTouchable(Touchable.childrenOnly);
	}

	public GridGroup (float itemSize) {
		this.itemWidth = itemSize;
		this.itemHeight = itemSize;
		setTouchable(Touchable.childrenOnly);
	}

	public GridGroup (float itemSize, float spacing) {
		this.spacing = spacing;
		this.itemWidth = itemSize;
		this.itemHeight = itemSize;
		setTouchable(Touchable.childrenOnly);
	}

	private void computeSize () {
		prefWidth = getWidth();
		prefHeight = 0;
		sizeInvalid = false;

		SnapshotArray<Actor> children = getChildren();

		if (children.size == 0) {
			prefWidth = 0;
			prefHeight = 0;
			return;
		}

		float width = getWidth();

		float maxHeight = 0;
		float tempX = spacing;

		for (int i = 0; i < children.size; i++) {
			if (tempX + itemWidth + spacing > width) {
				tempX = spacing;
				maxHeight += itemHeight + spacing;
			}

			tempX += itemWidth + spacing;
		}

		if (itemWidth + spacing * 2 > prefWidth)
			maxHeight += spacing;
		else
			maxHeight += itemHeight + spacing * 2;

		prefHeight = maxHeight;
	}

	@Override
	public void layout () {
		if (sizeInvalid) {
			computeSize();
			if (lastPrefHeight != prefHeight) {
				lastPrefHeight = prefHeight;
				invalidateHierarchy();
			}
		}

		SnapshotArray<Actor> children = getChildren();

		float width = getWidth();
		boolean notEnoughSpace = itemWidth + spacing * 2 > width;

		float x = spacing;
		float y = notEnoughSpace ? (getHeight()) : (getHeight() - itemHeight - spacing);

		for (int i = 0; i < children.size; i++) {
			Actor child = children.get(i);

			if (x + itemWidth + spacing > width) {
				x = spacing;
				y -= itemHeight + spacing;
			}

			child.setBounds(x, y, itemWidth, itemHeight);
			x += itemWidth + spacing;
		}
	}

	public float getSpacing () {
		return spacing;
	}

	public void setSpacing (float spacing) {
		this.spacing = spacing;
		invalidateHierarchy();
	}

	public void setItemSize (float itemSize) {
		this.itemWidth = itemSize;
		this.itemHeight = itemSize;
		invalidateHierarchy();
	}

	public void setItemSize (float itemWidth, float itemHeight) {
		this.itemWidth = itemWidth;
		this.itemHeight = itemHeight;
		invalidateHierarchy();
	}

	public float getItemWidth () {
		return itemWidth;
	}

	public void setItemWidth (float itemWidth) {
		this.itemWidth = itemWidth;
	}

	public float getItemHeight () {
		return itemHeight;
	}

	public void setItemHeight (float itemHeight) {
		this.itemHeight = itemHeight;
	}

	@Override
	public void invalidate () {
		super.invalidate();
		sizeInvalid = true;
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
}
