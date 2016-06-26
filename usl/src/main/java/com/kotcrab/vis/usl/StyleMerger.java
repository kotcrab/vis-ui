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

import com.kotcrab.vis.usl.lang.GroupIdentifier;
import com.kotcrab.vis.usl.lang.Identifier;
import com.kotcrab.vis.usl.lang.StyleBlock;
import com.kotcrab.vis.usl.lang.StyleIdentifier;

import java.util.ArrayList;

/** Performs styles mering during USL parsing. */
public class StyleMerger {
	private ArrayList<StyleIdentifier> globalStyles;
	private ArrayList<StyleBlock> styleBlocks;
	private ArrayList<StyleBlock> styleBlocksOverride;

	public StyleMerger (ArrayList<StyleIdentifier> globalStyles, ArrayList<StyleBlock> styleBlocks, ArrayList<StyleBlock> styleBlocksOverride) {
		this.globalStyles = globalStyles;
		this.styleBlocks = styleBlocks;
		this.styleBlocksOverride = styleBlocksOverride;
	}

	public ArrayList<StyleBlock> merge () {
		mergeBlocksOverrides();
		mergeGlobalStyles();

		for (StyleBlock block : styleBlocks) {
			//merge 'style a extends style b'
			if (block.extendsStyle != null) mergeBlocksExtends(block, block.extendsStyle);

			//merge inner styles
			for (StyleIdentifier baseStyle : block.styles) {
				for (String inherit : baseStyle.inherits) {
					if (inherit.startsWith(".")) continue; //global styles was already merged at this point
					StyleIdentifier styleToBeMerged = findStyle(block.styles, inherit);

					if (styleToBeMerged == null)
						throw new USLException("Style to inherit: '" + inherit + "' not found, block: " + block.fullName);

					mergeStylesContent(styleToBeMerged, baseStyle);
				}
			}
		}

		return styleBlocks;
	}

	private void mergeBlocksOverrides () {
		for (StyleBlock overrider : styleBlocksOverride) {
			StyleBlock mergeTarget = findBlock(styleBlocks, overrider.fullName);

			for (StyleIdentifier styleToBeMerged : overrider.styles) {
				StyleIdentifier mergeTargetStyle = findStyle(mergeTarget.styles, styleToBeMerged.name);

				if (mergeTargetStyle != null) {
					System.out.println("Warn: overriding existing super style inside block: '" + overrider.fullName + "', style: '" + mergeTargetStyle.name + "'");
					mergeTarget.styles.remove(mergeTargetStyle);
				}

				mergeTarget.styles.add(styleToBeMerged);
			}
		}
	}

	private void mergeGlobalStyles () {
		for (StyleBlock block : styleBlocks) {
			for (StyleIdentifier style : block.styles) {
				mergeGlobalStyles(block.fullName, style.inherits, style.content);
				for (String inherit : style.inherits) {
					if (inherit.startsWith(".")) {
						StyleIdentifier styleToInherit = findStyle(globalStyles, inherit);

						if (styleToInherit == null) {
							throw new USLException("Style to inherit: '" + inherit + "' not found, block: " + block.fullName);
						}

						for (Identifier id : styleToInherit.content) {
							if (findIdentifier(style.content, id.name) == null)
								style.content.add(id);
						}
					}
				}
			}
		}

		for (StyleBlock block : styleBlocks) {
			for (StyleIdentifier style : block.styles) {
				mergeGlobalStylesForIds(block.fullName, style.content);
			}
		}
	}

	private void mergeGlobalStylesForIds (String blockName, ArrayList<Identifier> content) {
		for (Identifier id : content) {
			if (id instanceof GroupIdentifier) {
				GroupIdentifier gid = (GroupIdentifier) id;
				mergeGlobalStyles(blockName, gid.inherits, gid.content);
				mergeGlobalStylesForIds(blockName, gid.content);
			}
		}
	}

	private void mergeGlobalStyles (String blockName, ArrayList<String> inherits, ArrayList<Identifier> content) {
		for (String inherit : inherits) {
			if (inherit.startsWith(".")) {
				StyleIdentifier styleToInherit = findStyle(globalStyles, inherit);

				if (styleToInherit == null) {
					throw new USLException("Style to inherit: '" + inherit + "' not found, block: " + blockName);
				}

				for (Identifier id : styleToInherit.content) {
					if (findIdentifier(content, id.name) == null) {
						content.add(id);
					}
				}
			}
		}
	}

	private void mergeBlocksExtends (StyleBlock mergeTarget, StyleBlock blockToMerge) {
		if (blockToMerge.extendsStyle != null) mergeBlocksExtends(mergeTarget, blockToMerge.extendsStyle);

		for (StyleIdentifier styleToBeMerged : blockToMerge.styles) {
			StyleIdentifier mergeTargetStyle = findStyle(mergeTarget.styles, styleToBeMerged.name);

			if (mergeTargetStyle != null)
				mergeStyles(styleToBeMerged, mergeTargetStyle);
			else if (mergeTarget.extendsInheritOnlyDefinedStyles == false)
				mergeTarget.styles.add(new StyleIdentifier(styleToBeMerged));
		}
	}

	private void mergeStyles (StyleIdentifier styleToBeMerge, StyleIdentifier mergeTargetStyle) {
		for (String inherit : styleToBeMerge.inherits)
			if (mergeTargetStyle.inherits.contains(inherit) == false) mergeTargetStyle.inherits.add(inherit);

		mergeStylesContent(styleToBeMerge, mergeTargetStyle);
	}

	private void mergeStylesContent (StyleIdentifier styleToBeMerged, StyleIdentifier mergeTargetStyle) {
		for (Identifier id : styleToBeMerged.content) {
			if (findIdentifier(mergeTargetStyle.content, id.name) == null) //mergeTargetStyle.content.remove(id);
				mergeTargetStyle.content.add(id);
		}
	}

	private StyleBlock findBlock (ArrayList<StyleBlock> target, String fullName) {
		for (StyleBlock block : target)
			if (block.fullName.equals(fullName)) return block;

		throw new USLException("Block '" + fullName + "' not found");
	}

	private StyleIdentifier findStyle (ArrayList<StyleIdentifier> target, String name) {
		for (StyleIdentifier style : target)
			if (style.name.equals(name)) return style;

		return null;
	}

	private Identifier findIdentifier (ArrayList<? extends Identifier> target, String name) {
		for (Identifier identifier : target)
			if (identifier.name.equals(name)) return identifier;

		return null;
	}
}
