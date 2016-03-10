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

package com.kotcrab.vis.editor.assets.transaction.action;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.util.undo.UndoableAction;

/**
 * Undoable action for deleting file. Requires providing transaction storage that deleted file will be stored in case it needs to be restored.
 * @author Kotcrab
 */
public class DeleteFileAction implements UndoableAction {
	private final FileHandle transactionStorage;

	private FileHandle source;
	private FileHandle backup;

	public DeleteFileAction (FileHandle source, FileHandle transactionStorage) {
		this.source = source;
		this.transactionStorage = transactionStorage;
		this.backup = transactionStorage.child(source.name());
	}

	@Override
	public void execute () {
		source.moveTo(backup);
	}

	@Override
	public void undo () {
		backup.copyTo(source);
	}
}
