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

package com.kotcrab.vis.editor.assets.transaction;

import com.kotcrab.vis.editor.util.undo.UndoableAction;
import com.kotcrab.vis.editor.util.undo.UndoableActionGroup;

/**
 * UndoableActionGroup for assets transactions. The only difference is that this class does not allow immediate action execution.
 * @author Kotcrab
 */
public class AssetTransaction extends UndoableActionGroup {
	@Override
	public void execute (UndoableAction action) {
		throw new UnsupportedOperationException("#execute(UndoableAction) not supported for AssetTransaction use: #add(UndoableAction) instead!");
	}

	@Override
	public String getActionName () {
		return "Asset Transaction";
	}
}
