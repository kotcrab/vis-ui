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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.kotcrab.vis.ui.util.highlight.BaseHighlighter;
import com.kotcrab.vis.ui.util.highlight.Highlight;
import com.kotcrab.vis.ui.util.highlight.Highlighter;

/**
 * Text area implementation supporting highlighting words and scrolling in both X and Y directions.
 * <p>
 * For best scroll pane settings you should create scroll pane using {@link #createCompatibleScrollPane()}.
 * <p>
 * Note about overlapping highlights: this text area can handle overlapping highlights, highlights that starts earlier
 * have higher priority. If two highlights have the exactly the same start point, then it is undefined which highlight
 * will be used and depends on how array containing highlights will be sorted.
 * @author Kotcrab
 * @see Highlighter
 * @since 1.1.2
 */
public class HighlightTextArea extends ScrollableTextArea {
	private Array<Highlight> highlights = new Array<Highlight>();
	private Array<Chunk> renderChunks = new Array<Chunk>();
	private boolean chunkUpdateScheduled = true;
	private Color defaultColor = Color.WHITE;

	private BaseHighlighter highlighter;

	private float maxAreaWidth = 0;
	private float maxAreaHeight = 0;

	public HighlightTextArea (String text) {
		super(text);
		softwrap = false;
	}

	public HighlightTextArea (String text, String styleName) {
		super(text, styleName);
	}

	public HighlightTextArea (String text, VisTextFieldStyle style) {
		super(text, style);
	}

	@Override
	void updateDisplayText () {
		super.updateDisplayText();
		processHighlighter();
	}

	@Override
	protected void calculateOffsets () {
		super.calculateOffsets();
		if (chunkUpdateScheduled == false) return;
		chunkUpdateScheduled = false;
		highlights.sort();
		renderChunks.clear();

		String text = getText();

		Pool<GlyphLayout> layoutPool = Pools.get(GlyphLayout.class);
		GlyphLayout layout = layoutPool.obtain();
		boolean carryHighlight = false;
		for (int lineIdx = 0, highlightIdx = 0; lineIdx < linesBreak.size; lineIdx += 2) {
			int lineStart = linesBreak.items[lineIdx];
			int lineEnd = linesBreak.items[lineIdx + 1];
			int lineProgress = lineStart;
			float chunkOffset = 0;

			for (; highlightIdx < highlights.size; ) {
				Highlight highlight = highlights.get(highlightIdx);
				if (highlight.getStart() > lineEnd) {
					break;
				}

				if (highlight.getStart() == lineProgress || carryHighlight) {
					renderChunks.add(new Chunk(text.substring(lineProgress, Math.min(highlight.getEnd(), lineEnd)), highlight.getColor(), chunkOffset, lineIdx));
					lineProgress = Math.min(highlight.getEnd(), lineEnd);

					if (highlight.getEnd() > lineEnd) {
						carryHighlight = true;
					} else {
						carryHighlight = false;
						highlightIdx++;
					}
				} else {
					//protect against overlapping highlights
					boolean noMatch = false;
					while (highlight.getStart() <= lineProgress) {
						highlightIdx++;
						if (highlightIdx >= highlights.size) {
							noMatch = true;
							break;
						}
						highlight = highlights.get(highlightIdx);
						if (highlight.getStart() > lineEnd) {
							noMatch = true;
							break;
						}
					}
					if (noMatch) break;
					renderChunks.add(new Chunk(text.substring(lineProgress, highlight.getStart()), defaultColor, chunkOffset, lineIdx));
					lineProgress = highlight.getStart();
				}

				Chunk chunk = renderChunks.peek();
				layout.setText(style.font, chunk.text);
				chunkOffset += layout.width;
				//current highlight needs to be applied to next line meaning that there is no other highlights that can be applied to currently parsed line
				if (carryHighlight) break;
			}

			if (lineProgress < lineEnd) {
				renderChunks.add(new Chunk(text.substring(lineProgress, lineEnd), defaultColor, chunkOffset, lineIdx));
			}
		}

		maxAreaWidth = 0;
		for (String line : text.split("\\n")) {
			layout.setText(style.font, line);
			maxAreaWidth = Math.max(maxAreaWidth, layout.width + 30);
		}

		layoutPool.free(layout);
		updateScrollLayout();
	}

	@Override
	protected void drawText (Batch batch, BitmapFont font, float x, float y) {
		maxAreaHeight = 0;
		float offsetY = 0;
		for (int i = firstLineShowing * 2; i < (firstLineShowing + linesShowing) * 2 && i < linesBreak.size; i += 2) {
			for (Chunk chunk : renderChunks) {
				if (chunk.lineIndex == i) {
					font.setColor(chunk.color);
					font.draw(batch, chunk.text, x + chunk.offsetX, y + offsetY);
				}
			}

			offsetY -= font.getLineHeight();
			maxAreaHeight += font.getLineHeight();
		}

		maxAreaHeight += 30;
	}

	/**
	 * Processes highlighter rules, collects created highlights and schedules text area displayed text update. This should be called
	 * after highlighter rules has changed to update highlights.
	 */
	public void processHighlighter () {
		if (highlights == null) return;
		highlights.clear();
		if (highlighter != null) highlighter.process(this, highlights);
		chunkUpdateScheduled = true;
	}

	/**
	 * Changes highlighter of text area. Note that you don't have to call {@link #processHighlighter()} after changing
	 * highlighter - you only have to call it when highlighter rules has changed.
	 */
	public void setHighlighter (BaseHighlighter highlighter) {
		this.highlighter = highlighter;
		processHighlighter();
	}

	public BaseHighlighter getHighlighter () {
		return highlighter;
	}

	@Override
	public float getPrefWidth () {
		return maxAreaWidth + 5;
	}

	@Override
	public float getPrefHeight () {
		return maxAreaHeight + 5;
	}

	@Override
	public ScrollPane createCompatibleScrollPane () {
		ScrollPane scrollPane = super.createCompatibleScrollPane();
		scrollPane.setScrollingDisabled(false, false);
		return scrollPane;
	}

	private static class Chunk {
		String text;
		Color color;
		float offsetX;
		int lineIndex;

		public Chunk (String text, Color color, float offsetX, int lineIndex) {
			this.text = text;
			this.color = color;
			this.offsetX = offsetX;
			this.lineIndex = lineIndex;
		}
	}
}
