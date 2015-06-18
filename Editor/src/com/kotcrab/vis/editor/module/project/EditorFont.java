/*
 * Copyright 2014-2015 See AUTHORS file.
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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.editor.scene.TextObject;

/**
 * Generic interface for VisEditor font providers used in {@link TextObject}.
 * @see BMPEditorFont
 * @see TTFEditorFont
 */
public abstract class EditorFont implements Disposable {
	protected FileHandle file;
	protected String relativePath;

	public EditorFont (FileHandle file, String relativePath) {
		this.file = file;
		this.relativePath = relativePath;
	}

	public abstract BitmapFont get ();

	public abstract BitmapFont get (int size);

	public FileHandle getFile () {
		return file;
	}

	public String getRelativePath () {
		return relativePath;
	}
}
