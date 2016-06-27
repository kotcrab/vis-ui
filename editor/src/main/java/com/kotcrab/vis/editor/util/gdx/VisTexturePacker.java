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

package com.kotcrab.vis.editor.util.gdx;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePackerFileProcessor;

import java.io.File;
import java.io.FilenameFilter;

/** @author Kotcrab */
public class VisTexturePacker {
	static public void process (TexturePacker.Settings settings, String input, String output, String packFileName, FilenameFilter filter) {
		try {
			TexturePackerFileProcessor processor = new TexturePackerFileProcessor(settings, packFileName);
			processor.setInputFilter(filter);
			// Sort input files by name to avoid platform-dependent atlas output changes.
			processor.setComparator((file1, file2) -> file1.getName().compareTo(file2.getName()));
			processor.process(new File(input), new File(output));
		} catch (Exception ex) {
			throw new RuntimeException("Error packing images.", ex);
		}
	}
}
