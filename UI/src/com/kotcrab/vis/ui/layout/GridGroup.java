/*
 * Copyright 2014-2015 Pawel Pastuszak
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
 * @author Kotcrab
 * @since 0.7.2
 */
public class GridGroup extends WidgetGroup {
	private float prefWidth;
	private float prefHeight;
	private float lastPrefHeight;
	private boolean sizeInvalid = true;

	private int itemSize = 256;
	private float spacing = 8;

	public GridGroup () {
		setTouchable(Touchable.childrenOnly);
	}

	public GridGroup (int itemSize) {
		this.itemSize = itemSize;
	}

	public GridGroup (float spacing) {
		this.spacing = spacing;
	}

	public GridGroup (int itemSize, float spacing) {
		this.spacing = spacing;
		this.itemSize = itemSize;
	}

	private void computeSize () {
		prefWidth = getWidth();
		prefHeight = 0;
		sizeInvalid = false;
		float width = getWidth();

		float maxHeight = 0;
		float tempX = spacing;

		for (int i = 0, n = getChildren().size; i < n; i++) {
			if (tempX + itemSize + spacing >= width) {
				tempX = spacing;
				maxHeight += itemSize + spacing;
			}

			tempX += itemSize - spacing;
		}

		if (itemSize + spacing * 2 >= prefWidth)
			maxHeight += spacing;
		else
			maxHeight += itemSize + spacing * 2;

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
		boolean notEnoughSpace = itemSize + spacing * 2 > width;

		float x = spacing;
		float y = notEnoughSpace ? (getHeight()) : (getHeight() - itemSize - spacing);

		for (int i = 0, n = children.size; i < n; i++) {
			Actor child = children.get(i);

			if (x + itemSize + spacing > width) {
				x = spacing;
				y -= itemSize + spacing;
			}

			child.setBounds(x, y, itemSize, itemSize);
			x += itemSize + spacing;
		}
	}

	public float getSpacing () {
		return spacing;
	}

	public void setSpacing (float spacing) {
		this.spacing = spacing;
		invalidateHierarchy();
	}

	public int getItemSize () {
		return itemSize;
	}

	public void setItemSize (int itemSize) {
		this.itemSize = itemSize;
		invalidateHierarchy();
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
