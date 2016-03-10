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

package com.kotcrab.vis.editor.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.util.NumberUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/** @author Kotcrab */
public class Vector2ArrayView extends VisTable {
	private Array<Vector2> vectors = new Array<>();

	private boolean multipleSelected;

	public Vector2ArrayView () {
		updateUI();

		left();
		defaults().left();

//		ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
//		fire(changeEvent);
//		Pools.free(changeEvent);
	}

	public void setVectors (Array<Vector2> vectors) {
		this.vectors.clear();
		this.vectors.addAll(vectors);
		multipleSelected = false;
		updateUI();
	}

	public void setMultipleSelected (boolean multipleSelected) {
		this.multipleSelected = multipleSelected;
		updateUI();
	}

	private void updateUI () {
		clearChildren();

		if (multipleSelected == false) {
			if (vectors.size == 0) {
				add("No points");
				return;
			}

			for (int i = 0; i < vectors.size; i++) {
				Vector2 vec = vectors.get(i);

				add(new VisLabel(i + ": X: " + NumberUtils.floatToString(vec.x) + " Y: " + NumberUtils.floatToString(vec.y))).row();
			}
		} else {
			add("<select only one entity>");
		}
	}
}
