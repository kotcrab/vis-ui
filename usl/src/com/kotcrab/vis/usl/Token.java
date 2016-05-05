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

package com.kotcrab.vis.usl;

/** Single token of USL file */
public class Token {
	public String usl;
	public int i;

	public Type type;
	public String content;

	public Token (String usl, int i, Type type) {
		this.usl = usl;
		this.i = i;
		this.type = type;
	}

	public Token (String usl, int i, Type type, String content) {
		this.usl = usl;
		this.i = i;
		this.type = type;
		this.content = content;
	}

	public enum Type {
		INVALID,
		LCURL, RCURL, // { }
		STYLE_BLOCK, STYLE_BLOCK_EXTENDS,
		STYLE_BLOCK_OVERRIDE,
		GLOBAL_STYLE, PACKAGE,
		IDENTIFIER, IDENTIFIER_CONTENT,
		INHERITS, INHERITS_NAME,
		META_STYLE
	}
}
