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

package com.kotcrab.vis.ui.layout;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.SnapshotArray;

/**
 * Arranges actors in single column filling available vertical space. Creates new columns and expands horizontally as
 * necessary.
 * Children automatically overflow to next column when necessary.
 * <p>
 * Can be embedded in scroll pane however in that case scrolling in Y direction must be disabled.
 * @author Kotcrab
 * @since 1.0.0
 */
public class VerticalFlowGroup extends WidgetGroup {
	private float prefWidth;
	private float prefHeight;
	private float lastPrefHeight;
	private boolean sizeInvalid = true;

	private float spacing = 0;

	public VerticalFlowGroup () {
		setTouchable(Touchable.childrenOnly);
	}

	public VerticalFlowGroup (float spacing) {
		this.spacing = spacing;
		setTouchable(Touchable.childrenOnly);
	}

	private void computeSize () {
		prefWidth = 0;
		prefHeight = getHeight();
		sizeInvalid = false;

		SnapshotArray<Actor> children = getChildren();

		float y = 0;
		float columnWidth = 0;

		for (int i = 0; i < children.size; i++) {
			Actor child = children.get(i);
			float width = child.getWidth();
			float height = child.getHeight();
			if (child instanceof Layout) {
				Layout layout = (Layout) child;
				width = layout.getPrefWidth();
				height = layout.getPrefHeight();
			}

			if (y + height > getHeight()) {
				y = 0;
				prefWidth += columnWidth + spacing;
				columnWidth = width;
			} else {
				columnWidth = Math.max(width, columnWidth);
			}

			y += height + spacing;
		}

		//handle last column width
		prefWidth += columnWidth + spacing;
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

		float x = 0;
		float y = getHeight();
		float columnWidth = 0;

		for (int i = 0; i < children.size; i++) {
			Actor child = children.get(i);
			float width = child.getWidth();
			float height = child.getHeight();
			if (child instanceof Layout) {
				Layout layout = (Layout) child;
				width = layout.getPrefWidth();
				height = layout.getPrefHeight();
			}

			if (y - height < 0) {
				y = getHeight();
				x += columnWidth + spacing;
				columnWidth = width;
			} else {
				columnWidth = Math.max(width, columnWidth);
			}

			child.setBounds(x, y - height, width, height);
			y -= height + spacing;
		}
	}

	public float getSpacing () {
		return spacing;
	}

	public void setSpacing (float spacing) {
		this.spacing = spacing;
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
