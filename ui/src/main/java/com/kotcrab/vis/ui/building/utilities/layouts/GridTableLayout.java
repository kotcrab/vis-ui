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

package com.kotcrab.vis.ui.building.utilities.layouts;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.building.GridTableBuilder;
import com.kotcrab.vis.ui.building.utilities.CellWidget;

/**
 * Additional TableLayout with customizable variables. Converts passed widgets into a table using
 * GridTableBuilder.
 * @author MJ
 */
public class GridTableLayout implements ActorLayout {
	private final int rowSize;

	public GridTableLayout (final int rowSize) {
		this.rowSize = rowSize;
	}

	/**
	 * Default factory method.
	 * @return new GridTableLayout, building grid with the passed row size.
	 */
	public static GridTableLayout withRowSize (final int rowSize) {
		return new GridTableLayout(rowSize);
	}

	@Override
	public Actor convertToActor (final Actor... widgets) {
		return convertToActor(CellWidget.wrap(widgets));
	}

	@Override
	public Actor convertToActor (final CellWidget<?>... widgets) {
		return TableLayout.convertToTable(new GridTableBuilder(rowSize), widgets);
	}
}
