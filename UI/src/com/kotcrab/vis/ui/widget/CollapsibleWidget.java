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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * Widget containing table that can be collapsed
 * @since 0.3.1
 */
public class CollapsibleWidget extends WidgetGroup {
	private Table table;

	private boolean collapsed;
	private boolean actionRunning;

	private float currentHeight;

	public CollapsibleWidget (Table table) {
		this(table, false);
	}

	public CollapsibleWidget (Table table, boolean collapsed) {
		this.collapsed = collapsed;
		this.table = table;

		updateTouchable();

		addActor(table);
	}

	public void setCollapsed (boolean collapse) {
		this.collapsed = collapse;

		actionRunning = true;

		updateTouchable();

		addAction(new Action() {
			@Override
			public boolean act (float delta) {

				if (collapsed) {
					currentHeight -= delta * 1000;
					if (currentHeight <= 0) {
						currentHeight = 0;
						collapsed = true;
						actionRunning = false;
					}
				} else {
					currentHeight += delta * 1000;
					if (currentHeight > table.getPrefHeight()) {
						currentHeight = table.getPrefHeight();
						collapsed = false;
						actionRunning = false;
					}
				}

				invalidateHierarchy();
				return !actionRunning;
			}
		});
	}

	private void updateTouchable () {
		if (collapsed)
			setTouchable(Touchable.disabled);
		else
			setTouchable(Touchable.enabled);
	}

	public boolean isCollapsed () {
		return collapsed;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		if (currentHeight > 1) {
			batch.flush();
			boolean clipEnabled = clipBegin(getX(), getY(), getWidth(), currentHeight);

			super.draw(batch, parentAlpha);

			batch.flush();
			if (clipEnabled) clipEnd();
		}
	}

	@Override
	public void layout () {
		super.layout();
		table.setBounds(0, 0, table.getPrefWidth(), table.getPrefHeight());

		if (actionRunning == false) {
			if (collapsed)
				currentHeight = 0;
			else
				currentHeight = table.getPrefHeight();
		}
	}

	@Override
	public float getPrefWidth () {
		return table.getPrefWidth();
	}

	@Override
	public float getPrefHeight () {
		if (actionRunning == false) {
			if (collapsed)
				return 0;
			else
				return table.getPrefHeight();
		}

		return currentHeight;
	}

	@Override
	protected void setStage (Stage stage) {
		super.setStage(stage);
	}
}
