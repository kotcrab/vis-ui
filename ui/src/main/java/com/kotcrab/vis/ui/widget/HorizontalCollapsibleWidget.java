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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Widget containing table that can be horizontally collapsed.
 * @author Kotcrab
 * @see CollapsibleWidget
 * @since 1.2.5
 */
public class HorizontalCollapsibleWidget extends WidgetGroup {
	private Table table;

	private CollapseAction collapseAction = new CollapseAction();

	private boolean collapsed;
	private boolean actionRunning;

	private float currentWidth;

	public HorizontalCollapsibleWidget () {
	}

	public HorizontalCollapsibleWidget (Table table) {
		this(table, false);
	}

	public HorizontalCollapsibleWidget (Table table, boolean collapsed) {
		this.collapsed = collapsed;
		this.table = table;

		updateTouchable();

		if (table != null) addActor(table);
	}

	public void setCollapsed (boolean collapse, boolean withAnimation) {
		this.collapsed = collapse;
		updateTouchable();

		if (table == null) return;

		actionRunning = true;

		if (withAnimation) {
			addAction(collapseAction);
		} else {
			if (collapse) {
				currentWidth = 0;
				collapsed = true;
			} else {
				currentWidth = table.getPrefWidth();
				collapsed = false;
			}

			actionRunning = false;
			invalidateHierarchy();
		}
	}

	public void setCollapsed (boolean collapse) {
		setCollapsed(collapse, true);
	}

	public boolean isCollapsed () {
		return collapsed;
	}

	private void updateTouchable () {
		if (collapsed)
			setTouchable(Touchable.disabled);
		else
			setTouchable(Touchable.enabled);
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		if (currentWidth > 1) {
			batch.flush();
			boolean clipEnabled = clipBegin(getX(), getY(), currentWidth, getHeight());

			super.draw(batch, parentAlpha);

			batch.flush();
			if (clipEnabled) clipEnd();
		}
	}

	@Override
	public void layout () {
		if (table == null) return;

		table.setBounds(0, 0, table.getPrefWidth(), table.getPrefHeight());

		if (actionRunning == false) {
			if (collapsed)
				currentWidth = 0;
			else
				currentWidth = table.getPrefWidth();
		}
	}

	@Override
	public float getPrefHeight () {
		return table == null ? 0 : table.getPrefHeight();
	}

	@Override
	public float getPrefWidth () {
		if (table == null) return 0;

		if (actionRunning == false) {
			if (collapsed)
				return 0;
			else
				return table.getPrefWidth();
		}

		return currentWidth;
	}

	public void setTable (Table table) {
		this.table = table;
		clearChildren();
		addActor(table);
	}

	@Override
	protected void childrenChanged () {
		super.childrenChanged();
		if (getChildren().size > 1) throw new GdxRuntimeException("Only one actor can be added to CollapsibleWidget");
	}

	private class CollapseAction extends Action {
		@Override
		public boolean act (float delta) {
			if (collapsed) {
				currentWidth -= delta * 1000;
				if (currentWidth <= 0) {
					currentWidth = 0;
					collapsed = true;
					actionRunning = false;
				}
			} else {
				currentWidth += delta * 1000;
				if (currentWidth > table.getPrefWidth()) {
					currentWidth = table.getPrefWidth();
					collapsed = false;
					actionRunning = false;
				}
			}

			invalidateHierarchy();
			return !actionRunning;
		}
	}
}
