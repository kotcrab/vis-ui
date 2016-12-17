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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.DefaultOnscreenKeyboard;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.OnscreenKeyboard;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.BorderOwner;
import com.kotcrab.vis.ui.util.CursorManager;

import java.lang.StringBuilder;

/**
 * Extends functionality of standard {@link TextField}. Style supports over, and focus border. Improved text input.
 * Due to scope of changes made this widget is not compatible with {@link TextField}.
 * @author mzechner
 * @author Nathan Sweet
 * @author Kotcrab
 * @see TextField
 */
public class VisTextField extends Widget implements Disableable, Focusable, BorderOwner {
	static private final char BACKSPACE = 8;
	static protected final char ENTER_DESKTOP = '\r';
	static protected final char ENTER_ANDROID = '\n';
	static private final char TAB = '\t';
	static private final char DELETE = 127;
	static private final char BULLET = 8226;

	static private final Vector2 tmp1 = new Vector2();
	static private final Vector2 tmp2 = new Vector2();
	static private final Vector2 tmp3 = new Vector2();

	static public float keyRepeatInitialTime = 0.4f;
	/** Repeat times for keys handled by {@link InputListener#keyDown(InputEvent, int)} such as navigation arrows */
	static public float keyRepeatTime = 0.04f;

	protected String text;
	protected int cursor, selectionStart;
	protected boolean hasSelection;
	protected boolean writeEnters;
	protected final GlyphLayout layout = new GlyphLayout();
	protected final FloatArray glyphPositions = new FloatArray();

	private String messageText;
	protected CharSequence displayText;
	Clipboard clipboard;
	InputListener inputListener;
	TextFieldListener listener;
	TextFieldFilter filter;
	OnscreenKeyboard keyboard = new DefaultOnscreenKeyboard();
	boolean focusTraversal = true, onlyFontChars = true, disabled;
	boolean enterKeyFocusTraversal = false;
	private int textHAlign = Align.left;
	private float selectionX, selectionWidth;

	String undoText = "";
	int undoCursorPos = 0;
	long lastChangeTime;

	boolean passwordMode;
	private StringBuilder passwordBuffer;
	private char passwordCharacter = BULLET;

	protected float fontOffset, textHeight, textOffset;
	float renderOffset;
	private int visibleTextStart, visibleTextEnd;
	private int maxLength = 0;

	private float blinkTime = 0.45f;
	boolean cursorOn = true;
	long lastBlink;

	KeyRepeatTask keyRepeatTask = new KeyRepeatTask();
	boolean programmaticChangeEvents;

	// vis fields
	VisTextFieldStyle style;
	private ClickListener clickListener;
	private boolean drawBorder;
	private boolean focusBorderEnabled = true;
	private boolean inputValid = true;
	private boolean ignoreEqualsTextChange = true;
	private boolean readOnly = false;
	private float cursorPercentHeight = 0.8f;

	public VisTextField () {
		this("", VisUI.getSkin().get(VisTextFieldStyle.class));
	}

	public VisTextField (String text) {
		this(text, VisUI.getSkin().get(VisTextFieldStyle.class));
	}

	public VisTextField (String text, String styleName) {
		this(text, VisUI.getSkin().get(styleName, VisTextFieldStyle.class));
	}

	public VisTextField (String text, VisTextFieldStyle style) {
		setStyle(style);
		clipboard = Gdx.app.getClipboard();
		initialize();
		setText(text);
		setSize(getPrefWidth(), getPrefHeight());
	}

	protected void initialize () {
		addListener(inputListener = createInputListener());
		addListener(clickListener = new ClickListener() {
			@Override
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				super.enter(event, x, y, pointer, fromActor);
				if (pointer == -1 && isDisabled() == false) {
					Gdx.graphics.setSystemCursor(SystemCursor.Ibeam);
				}
			}

			@Override
			public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
				super.exit(event, x, y, pointer, toActor);
				if (pointer == -1) {
					CursorManager.restoreDefaultCursor();
				}
			}
		});
	}

	protected InputListener createInputListener () {
		return new TextFieldClickListener();
	}

	protected int letterUnderCursor (float x) {
		x -= textOffset + fontOffset - style.font.getData().cursorX - glyphPositions.get(visibleTextStart);
		int n = this.glyphPositions.size;
		float[] glyphPositions = this.glyphPositions.items;
		for (int i = 1; i < n; i++) {
			if (glyphPositions[i] > x) {
				if (glyphPositions[i] - x <= x - glyphPositions[i - 1]) return i;
				return i - 1;
			}
		}
		return n - 1;
	}

	protected boolean isWordCharacter (char c) {
		return Character.isLetterOrDigit(c);
	}

	protected int[] wordUnderCursor (int at) {
		String text = this.text;
		int start = Math.min(text.length(), at), right = text.length(), left = 0, index = start;
		for (; index < right; index++) {
			if (!isWordCharacter(text.charAt(index))) {
				right = index;
				break;
			}
		}
		for (index = start - 1; index > -1; index--) {
			if (!isWordCharacter(text.charAt(index))) {
				left = index + 1;
				break;
			}
		}
		return new int[]{left, right};
	}

	int[] wordUnderCursor (float x) {
		return wordUnderCursor(letterUnderCursor(x));
	}

	boolean withinMaxLength (int size) {
		return maxLength <= 0 || size < maxLength;
	}

	public int getMaxLength () {
		return this.maxLength;
	}

	public void setMaxLength (int maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * When false, text set by {@link #setText(String)} may contain characters not in the font, a space will be displayed instead.
	 * When true (the default), characters not in the font are stripped by setText. Characters not in the font are always stripped
	 * when typed or pasted.
	 */
	public void setOnlyFontChars (boolean onlyFontChars) {
		this.onlyFontChars = onlyFontChars;
	}

	/**
	 * Returns the text field's style. Modifying the returned style may not have an effect until
	 * {@link #setStyle(VisTextFieldStyle)} is called.
	 */
	public VisTextFieldStyle getStyle () {
		return style;
	}

	public void setStyle (VisTextFieldStyle style) {
		if (style == null) throw new IllegalArgumentException("style cannot be null.");
		this.style = style;
		textHeight = style.font.getCapHeight() - style.font.getDescent() * 2;
		invalidateHierarchy();
	}

	@Override
	public String toString () {
		return getText();
	}

	protected void calculateOffsets () {
		float visibleWidth = getWidth();
		if (style.background != null)
			visibleWidth -= style.background.getLeftWidth() + style.background.getRightWidth();

		int glyphCount = glyphPositions.size;
		float[] glyphPositions = this.glyphPositions.items;

		// Check if the cursor has gone out the left or right side of the visible area and adjust renderOffset.
		float distance = glyphPositions[Math.max(0, cursor - 1)] + renderOffset;
		if (distance <= 0)
			renderOffset -= distance;
		else {
			int index = Math.min(glyphCount - 1, cursor + 1);
			float minX = glyphPositions[index] - visibleWidth;
			if (-renderOffset < minX) renderOffset = -minX;
		}

		// Prevent renderOffset from starting too close to the end, eg after text was deleted.
		float maxOffset = 0;
		float width = glyphPositions[glyphCount - 1];
		for (int i = glyphCount - 2; i >= 0; i--) {
			float x = glyphPositions[i];
			if (width - x > visibleWidth) break;
			maxOffset = x;
		}
		if (-renderOffset > maxOffset) renderOffset = -maxOffset;

		// calculate first visible char based on render offset
		visibleTextStart = 0;
		float startX = 0;
		for (int i = 0; i < glyphCount; i++) {
			if (glyphPositions[i] >= -renderOffset) {
				visibleTextStart = Math.max(0, i);
				startX = glyphPositions[i];
				break;
			}
		}

		// calculate last visible char based on visible width and render offset
		int length = Math.min(displayText.length(), glyphPositions.length - 1);
		visibleTextEnd = Math.min(length, cursor + 1);
		for (; visibleTextEnd <= length; visibleTextEnd++)
			if (glyphPositions[visibleTextEnd] > startX + visibleWidth) break;
		visibleTextEnd = Math.max(0, visibleTextEnd - 1);

		if ((textHAlign & Align.left) == 0) {
			textOffset = visibleWidth - (glyphPositions[visibleTextEnd] - startX);
			if ((textHAlign & Align.center) != 0) textOffset = Math.round(textOffset * 0.5f);
		} else
			textOffset = startX + renderOffset;

		// calculate selection x position and width
		if (hasSelection) {
			int minIndex = Math.min(cursor, selectionStart);
			int maxIndex = Math.max(cursor, selectionStart);
			float minX = Math.max(glyphPositions[minIndex] - glyphPositions[visibleTextStart], -textOffset);
			float maxX = Math.min(glyphPositions[maxIndex] - glyphPositions[visibleTextStart], visibleWidth - textOffset);
			selectionX = minX;
			selectionWidth = maxX - minX - style.font.getData().cursorX;
		}
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		Stage stage = getStage();
		boolean focused = stage != null && stage.getKeyboardFocus() == this;
		if (!focused) keyRepeatTask.cancel();

		final BitmapFont font = style.font;
		final Color fontColor = (disabled && style.disabledFontColor != null) ? style.disabledFontColor
				: ((focused && style.focusedFontColor != null) ? style.focusedFontColor : style.fontColor);
		final Drawable selection = style.selection;
		final Drawable cursorPatch = style.cursor;
		Drawable background = (disabled && style.disabledBackground != null) ? style.disabledBackground
				: ((focused && style.focusedBackground != null) ? style.focusedBackground : style.background);

		// vis
		if (!disabled && clickListener.isOver() && style.backgroundOver != null) background = style.backgroundOver;

		Color color = getColor();
		float x = getX();
		float y = getY();
		float width = getWidth();
		float height = getHeight();

		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		float bgLeftWidth = 0, bgRightWidth = 0;
		if (background != null) {
			background.draw(batch, x, y, width, height);
			bgLeftWidth = background.getLeftWidth();
			bgRightWidth = background.getRightWidth();
		}

		float textY = getTextY(font, background);
		calculateOffsets();

		if (focused && hasSelection && selection != null) {
			drawSelection(selection, batch, font, x + bgLeftWidth, y + textY);
		}

		float yOffset = font.isFlipped() ? -textHeight : 0;
		if (displayText.length() == 0) {
			if (!focused && messageText != null) {
				if (style.messageFontColor != null) {
					font.setColor(style.messageFontColor.r, style.messageFontColor.g, style.messageFontColor.b,
							style.messageFontColor.a * color.a * parentAlpha);
				} else
					font.setColor(0.7f, 0.7f, 0.7f, color.a * parentAlpha);
				BitmapFont messageFont = style.messageFont != null ? style.messageFont : font;
				messageFont.draw(batch, messageText, x + bgLeftWidth, y + textY + yOffset, 0, messageText.length(),
						width - bgLeftWidth - bgRightWidth, textHAlign, false, "...");
			}
		} else {
			font.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a * color.a * parentAlpha);
			drawText(batch, font, x + bgLeftWidth, y + textY + yOffset);
		}
		if (drawBorder && focused && !disabled) {
			blink();
			if (cursorOn && cursorPatch != null) {
				drawCursor(cursorPatch, batch, font, x + bgLeftWidth, y + textY);
			}
		}

		// vis
		if (isDisabled() == false && inputValid == false && style.errorBorder != null)
			style.errorBorder.draw(batch, getX(), getY(), getWidth(), getHeight());
		else if (focusBorderEnabled && drawBorder && style.focusBorder != null)
			style.focusBorder.draw(batch, getX(), getY(), getWidth(), getHeight());

	}

	protected float getTextY (BitmapFont font, Drawable background) {
		float height = getHeight();
		float textY = textHeight / 2 + font.getDescent();
		if (background != null) {
			float bottom = background.getBottomHeight();
			textY = textY + (height - background.getTopHeight() - bottom) / 2 + bottom;
		} else {
			textY = textY + height / 2;
		}
		if (font.usesIntegerPositions()) textY = (int) textY;
		return textY;
	}

	/** Draws selection rectangle **/
	protected void drawSelection (Drawable selection, Batch batch, BitmapFont font, float x, float y) {
		selection.draw(batch, x + selectionX + textOffset + fontOffset, y - textHeight - font.getDescent(), selectionWidth,
				textHeight);
	}

	protected void drawText (Batch batch, BitmapFont font, float x, float y) {
		font.draw(batch, displayText, x + textOffset, y, visibleTextStart, visibleTextEnd, 0, Align.left, false);
	}

	protected void drawCursor (Drawable cursorPatch, Batch batch, BitmapFont font, float x, float y) {
		float cursorHeight = textHeight * cursorPercentHeight;
		float cursorYPadding = (textHeight - cursorHeight) / 2;
		cursorPatch.draw(batch,
				x + textOffset + glyphPositions.get(cursor) - glyphPositions.get(visibleTextStart) + fontOffset + font.getData().cursorX,
				y - textHeight - font.getDescent() + cursorYPadding, cursorPatch.getMinWidth(), cursorHeight);
	}

	void updateDisplayText () {
		BitmapFont font = style.font;
		BitmapFontData data = font.getData();
		String text = this.text;
		int textLength = text.length();

		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < textLength; i++) {
			char c = text.charAt(i);
			buffer.append(data.hasGlyph(c) ? c : ' ');
		}
		String newDisplayText = buffer.toString();

		if (passwordMode && data.hasGlyph(passwordCharacter)) {
			if (passwordBuffer == null) passwordBuffer = new StringBuilder(newDisplayText.length());
			if (passwordBuffer.length() > textLength)
				passwordBuffer.setLength(textLength);
			else {
				for (int i = passwordBuffer.length(); i < textLength; i++)
					passwordBuffer.append(passwordCharacter);
			}
			displayText = passwordBuffer;
		} else
			displayText = newDisplayText;

		layout.setText(font, displayText);
		glyphPositions.clear();
		float x = 0;
		if (layout.runs.size > 0) {
			GlyphRun run = layout.runs.first();
			fontOffset = run.xAdvances.first();

			for (GlyphRun glyphRun : layout.runs) {
				FloatArray xAdvances = glyphRun.xAdvances;
				for (int i = 1, n = xAdvances.size; i < n; i++) {
					glyphPositions.add(x);
					x += xAdvances.get(i);
				}
				glyphPositions.add(x);
			}
		} else {
			fontOffset = 0;
		}
		glyphPositions.add(x);

		if (selectionStart > newDisplayText.length()) selectionStart = textLength;
	}

	private void blink () {
		if (!Gdx.graphics.isContinuousRendering()) {
			cursorOn = true;
			return;
		}
		long time = TimeUtils.nanoTime();
		if ((time - lastBlink) / 1000000000.0f > blinkTime) {
			cursorOn = !cursorOn;
			lastBlink = time;
		}
	}

	/** Copies the contents of this TextField to the {@link Clipboard} implementation set on this TextField. */
	public void copy () {
		if (hasSelection && !passwordMode) {
			int beginIndex = Math.min(cursor, selectionStart);
			int endIndex = Math.max(cursor, selectionStart);
			clipboard.setContents(text.substring(Math.max(0, beginIndex), Math.min(text.length(), endIndex)));
		}
	}

	/**
	 * Copies the selected contents of this TextField to the {@link Clipboard} implementation set on this TextField, then removes
	 * it.
	 */
	public void cut () {
		cut(programmaticChangeEvents);
	}

	void cut (boolean fireChangeEvent) {
		if (hasSelection && !passwordMode) {
			copy();
			cursor = delete(fireChangeEvent);
			updateDisplayText();
		}
	}

	void paste (String content, boolean fireChangeEvent) {
		if (content == null) return;
		StringBuilder buffer = new StringBuilder();
		int textLength = text.length();
		if (hasSelection) textLength -= Math.abs(cursor - selectionStart);
		BitmapFontData data = style.font.getData();
		for (int i = 0, n = content.length(); i < n; i++) {
			if (!withinMaxLength(textLength + buffer.length())) break;
			char c = content.charAt(i);
			if (!(writeEnters && (c == ENTER_ANDROID || c == ENTER_DESKTOP))) {
				if (c == '\r' || c == '\n') continue;
				if (onlyFontChars && !data.hasGlyph(c)) continue;
				if (filter != null && !filter.acceptChar(this, c)) continue;
			}
			buffer.append(c);
		}
		content = buffer.toString();

		if (hasSelection) cursor = delete(fireChangeEvent);
		if (fireChangeEvent)
			changeText(text, insert(cursor, content, text));
		else
			text = insert(cursor, content, text);
		updateDisplayText();
		cursor += content.length();
	}

	String insert (int position, CharSequence text, String to) {
		if (to.length() == 0) return text.toString();
		return to.substring(0, position) + text + to.substring(position, to.length());
	}

	int delete (boolean fireChangeEvent) {
		int from = selectionStart;
		int to = cursor;
		int minIndex = Math.min(from, to);
		int maxIndex = Math.max(from, to);
		String newText = (minIndex > 0 ? text.substring(0, minIndex) : "")
				+ (maxIndex < text.length() ? text.substring(maxIndex, text.length()) : "");
		if (fireChangeEvent)
			changeText(text, newText);
		else
			text = newText;
		clearSelection();
		return minIndex;
	}

	/**
	 * Focuses the next TextField. If none is found, the keyboard is hidden. Does nothing if the text field is not in a stage.
	 * @param up If true, the TextField with the same or next smallest y coordinate is found, else the next highest.
	 */
	public void next (boolean up) {
		Stage stage = getStage();
		if (stage == null) return;
		getParent().localToStageCoordinates(tmp1.set(getX(), getY()));
		VisTextField textField = findNextTextField(stage.getActors(), null, tmp2, tmp1, up);
		if (textField == null) { // Try to wrap around.
			if (up)
				tmp1.set(Float.MIN_VALUE, Float.MIN_VALUE);
			else
				tmp1.set(Float.MAX_VALUE, Float.MAX_VALUE);
			textField = findNextTextField(getStage().getActors(), null, tmp2, tmp1, up);
		}
		if (textField != null) {
			textField.focusField();
			textField.setCursorPosition(textField.getText().length());
		} else
			Gdx.input.setOnscreenKeyboardVisible(false);
	}

	private VisTextField findNextTextField (Array<Actor> actors, VisTextField best, Vector2 bestCoords, Vector2 currentCoords, boolean up) {
		Window modalWindow = findModalWindow(this);

		for (int i = 0, n = actors.size; i < n; i++) {
			Actor actor = actors.get(i);
			if (actor == this) continue;
			if (actor instanceof VisTextField) {
				VisTextField textField = (VisTextField) actor;

				if (modalWindow != null) {
					Window nextFieldModalWindow = findModalWindow(textField);
					if (nextFieldModalWindow != modalWindow) continue;
				}

				if (textField.isDisabled() || !textField.focusTraversal || isActorVisibleInStage(textField) == false)
					continue;

				Vector2 actorCoords = actor.getParent().localToStageCoordinates(tmp3.set(actor.getX(), actor.getY()));
				if ((actorCoords.y < currentCoords.y || (actorCoords.y == currentCoords.y && actorCoords.x > currentCoords.x)) ^ up) {
					if (best == null
							|| (actorCoords.y > bestCoords.y || (actorCoords.y == bestCoords.y && actorCoords.x < bestCoords.x)) ^ up) {
						best = (VisTextField) actor;
						bestCoords.set(actorCoords);
					}
				}
			} else if (actor instanceof Group)
				best = findNextTextField(((Group) actor).getChildren(), best, bestCoords, currentCoords, up);
		}
		return best;
	}

	/**
	 * Checks if actor is visible in stage acknowledging parent visibility.
	 * If any parent returns false from isVisible then this method return false.
	 * True is returned when this actor and all its parent are visible.
	 */
	private boolean isActorVisibleInStage (Actor actor) {
		if (actor == null) return true;
		if (actor.isVisible() == false) return false;
		return isActorVisibleInStage(actor.getParent());
	}

	private Window findModalWindow (Actor actor) {
		if (actor == null) return null;
		if (actor instanceof Window && ((Window) actor).isModal()) return (Window) actor;
		return findModalWindow(actor.getParent());
	}

	public InputListener getDefaultInputListener () {
		return inputListener;
	}

	/** @param listener May be null. */
	public void setTextFieldListener (TextFieldListener listener) {
		this.listener = listener;
	}

	/** @param filter May be null. */
	public void setTextFieldFilter (TextFieldFilter filter) {
		this.filter = filter;
	}

	public TextFieldFilter getTextFieldFilter () {
		return filter;
	}

	/** If true (the default), tab/shift+tab will move to the next text field. */
	public void setFocusTraversal (boolean focusTraversal) {
		this.focusTraversal = focusTraversal;
	}

	/**
	 * If true, enter will move to the next text field with has focus traversal enabled.
	 * False by default. Note that to enable or disable focus traversal completely you must
	 * use {@link #setFocusTraversal(boolean)}
	 */
	public void setEnterKeyFocusTraversal (boolean enterKeyFocusTraversal) {
		this.enterKeyFocusTraversal = enterKeyFocusTraversal;
	}

	/** @return May be null. */
	public String getMessageText () {
		return messageText;
	}

	/**
	 * Sets the text that will be drawn in the text field if no text has been entered.
	 * @param messageText may be null.
	 */
	public void setMessageText (String messageText) {
		this.messageText = messageText;
	}

	/** @param str If null, "" is used. */
	public void appendText (String str) {
		if (str == null) str = "";

		clearSelection();
		cursor = text.length();
		paste(str, programmaticChangeEvents);
	}

	/** @param str If null, "" is used. */
	public void setText (String str) {
		if (str == null) str = "";
		if (ignoreEqualsTextChange && str.equals(text)) return;

		clearSelection();
		String oldText = text;
		text = "";
		paste(str, false);
		if (programmaticChangeEvents) changeText(oldText, text);
		cursor = 0;
	}

	/** @return Never null, might be an empty string. */
	public String getText () {
		return text;
	}

	/**
	 * @param oldText May be null.
	 * @return True if the text was changed.
	 */
	boolean changeText (String oldText, String newText) {
		if (ignoreEqualsTextChange && newText.equals(oldText)) return false;
		text = newText;
		beforeChangeEventFired();
		ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
		boolean cancelled = fire(changeEvent);
		text = cancelled ? oldText : newText;
		Pools.free(changeEvent);
		return !cancelled;
	}

	void beforeChangeEventFired () {

	}

	public boolean getProgrammaticChangeEvents () {
		return programmaticChangeEvents;
	}

	/**
	 * If false, methods that change the text will not fire {@link ChangeEvent}, the event will be fired only when user changes
	 * the text.
	 */
	public void setProgrammaticChangeEvents (boolean programmaticChangeEvents) {
		this.programmaticChangeEvents = programmaticChangeEvents;
	}

	public int getSelectionStart () {
		return selectionStart;
	}

	public String getSelection () {
		return hasSelection ? text.substring(Math.min(selectionStart, cursor), Math.max(selectionStart, cursor)) : "";
	}

	public boolean isTextSelected () {
		return hasSelection;
	}

	/** Sets the selected text. */
	public void setSelection (int selectionStart, int selectionEnd) {
		if (selectionStart < 0) throw new IllegalArgumentException("selectionStart must be >= 0");
		if (selectionEnd < 0) throw new IllegalArgumentException("selectionEnd must be >= 0");
		selectionStart = Math.min(text.length(), selectionStart);
		selectionEnd = Math.min(text.length(), selectionEnd);
		if (selectionEnd == selectionStart) {
			clearSelection();
			return;
		}
		if (selectionEnd < selectionStart) {
			int temp = selectionEnd;
			selectionEnd = selectionStart;
			selectionStart = temp;
		}

		hasSelection = true;
		this.selectionStart = selectionStart;
		cursor = selectionEnd;
	}

	public void selectAll () {
		setSelection(0, text.length());
	}

	public void clearSelection () {
		hasSelection = false;
	}

	/** Clears VisTextField text. If programmatic change events are disabled then this will not fire change event. */
	public void clearText () {
		setText("");
	}

	/** Sets the cursor position and clears any selection. */
	public void setCursorPosition (int cursorPosition) {
		if (cursorPosition < 0) throw new IllegalArgumentException("cursorPosition must be >= 0");
		clearSelection();
		cursor = Math.min(cursorPosition, text.length());
	}

	public int getCursorPosition () {
		return cursor;
	}

	public void setCursorAtTextEnd () {
		setCursorPosition(0);
		calculateOffsets();
		setCursorPosition(getText().length());
	}

	/** @param cursorPercentHeight cursor size, value from 0..1 range */
	public void setCursorPercentHeight (float cursorPercentHeight) {
		if (cursorPercentHeight < 0 || cursorPercentHeight > 1)
			throw new IllegalArgumentException("cursorPercentHeight must be >= 0 and <= 1");
		this.cursorPercentHeight = cursorPercentHeight;
	}

	/** Default is an instance of {@link DefaultOnscreenKeyboard}. */
	public OnscreenKeyboard getOnscreenKeyboard () {
		return keyboard;
	}

	public void setOnscreenKeyboard (OnscreenKeyboard keyboard) {
		this.keyboard = keyboard;
	}

	public void setClipboard (Clipboard clipboard) {
		this.clipboard = clipboard;
	}

	@Override
	public float getPrefWidth () {
		return 150;
	}

	@Override
	public float getPrefHeight () {
		float prefHeight = textHeight;
		if (style.background != null) {
			prefHeight = Math.max(prefHeight + style.background.getBottomHeight() + style.background.getTopHeight(),
					style.background.getMinHeight());
		}
		return prefHeight;
	}

	/**
	 * Sets text horizontal alignment (left, center or right).
	 * @see Align
	 */
	public void setAlignment (int alignment) {
		this.textHAlign = alignment;
	}

	/**
	 * If true, the text in this text field will be shown as bullet characters.
	 * @see #setPasswordCharacter(char)
	 */
	public void setPasswordMode (boolean passwordMode) {
		this.passwordMode = passwordMode;
		updateDisplayText();
	}

	public boolean isPasswordMode () {
		return passwordMode;
	}

	/**
	 * Sets the password character for the text field. The character must be present in the {@link BitmapFont}. Default is 149
	 * (bullet).
	 */
	public void setPasswordCharacter (char passwordCharacter) {
		this.passwordCharacter = passwordCharacter;
		if (passwordMode) updateDisplayText();
	}

	public void setBlinkTime (float blinkTime) {
		this.blinkTime = blinkTime;
	}

	public boolean isDisabled () {
		return disabled;
	}

	@Override
	public void setDisabled (boolean disabled) {
		this.disabled = disabled;
		if (disabled) {
			FocusManager.resetFocus(getStage(), this);
			keyRepeatTask.cancel();
		}
	}

	public boolean isReadOnly () {
		return readOnly;
	}

	public void setReadOnly (boolean readOnly) {
		this.readOnly = readOnly;
	}

	protected void moveCursor (boolean forward, boolean jump) {
		int limit = forward ? text.length() : 0;
		int charOffset = forward ? 0 : -1;
		while ((forward ? ++cursor < limit : --cursor > limit) && jump) {
			if (!continueCursor(cursor, charOffset)) break;
		}
	}

	protected boolean continueCursor (int index, int offset) {
		char c = text.charAt(index + offset);
		return isWordCharacter(c);
	}

	/** Focuses this field, field must be added to stage before this method can be called */
	public void focusField () {
		if (disabled) return;
		Stage stage = getStage();
		FocusManager.switchFocus(stage, VisTextField.this);
		setCursorPosition(0);
		selectionStart = 0;
		//make sure textOffset was updated, prevent issue when there was long text selected and it was changed to short text
		//and field was focused. Without it textOffset would stay at max value and only one last letter will be visible in field
		calculateOffsets();
		if (stage != null) stage.setKeyboardFocus(VisTextField.this);
		keyboard.show(true);
		hasSelection = true;
	}

	@Override
	public void focusLost () {
		drawBorder = false;
	}

	@Override
	public void focusGained () {
		drawBorder = true;
	}

	public boolean isEmpty () {
		return text.length() == 0;
	}

	public boolean isInputValid () {
		return inputValid;
	}

	public void setInputValid (boolean inputValid) {
		this.inputValid = inputValid;
	}

	@Override
	public boolean isFocusBorderEnabled () {
		return focusBorderEnabled;
	}

	@Override
	public void setFocusBorderEnabled (boolean focusBorderEnabled) {
		this.focusBorderEnabled = focusBorderEnabled;
	}

	/** @see #setIgnoreEqualsTextChange(boolean) */
	public boolean isIgnoreEqualsTextChange () {
		return ignoreEqualsTextChange;
	}

	/**
	 * Allows to control whether change event is sent when text field's text is changed to same same as was it before.
	 * Eg. current text field is 'abc' and {@link #setText(String)} is called it with 'abc' again.
	 * @param ignoreEqualsTextChange if true then setting text to the same as it was before will NOT fire change event.
	 * Default is true however it is false default {@link VisValidatableTextField} to prevent form refreshment issues -
	 * see issue VisEditor#165
	 */
	public void setIgnoreEqualsTextChange (boolean ignoreEqualsTextChange) {
		this.ignoreEqualsTextChange = ignoreEqualsTextChange;
	}

	static public class VisTextFieldStyle extends TextFieldStyle {
		public Drawable focusBorder;
		public Drawable errorBorder;
		public Drawable backgroundOver;

		public VisTextFieldStyle () {
		}

		public VisTextFieldStyle (BitmapFont font, Color fontColor, Drawable cursor, Drawable selection, Drawable background) {
			super(font, fontColor, cursor, selection, background);
		}

		public VisTextFieldStyle (VisTextFieldStyle style) {
			super(style);
			this.focusBorder = style.focusBorder;
			this.errorBorder = style.errorBorder;
			this.backgroundOver = style.backgroundOver;
		}
	}

	/**
	 * Interface for listening to typed characters.
	 * @author mzechner
	 */
	static public interface TextFieldListener {
		public void keyTyped (VisTextField textField, char c);
	}

	/**
	 * Interface for filtering characters entered into the text field.
	 * @author mzechner
	 */
	static public interface TextFieldFilter {
		public boolean acceptChar (VisTextField textField, char c);

		static public class DigitsOnlyFilter implements TextFieldFilter {
			@Override
			public boolean acceptChar (VisTextField textField, char c) {
				return Character.isDigit(c);
			}

		}
	}

	class KeyRepeatTask extends Task {
		int keycode;

		@Override
		public void run () {
			inputListener.keyDown(null, keycode);
		}
	}

	/** Basic input listener for the text field */
	public class TextFieldClickListener extends ClickListener {
		@Override
		public void clicked (InputEvent event, float x, float y) {
			int count = getTapCount() % 4;
			if (count == 0) clearSelection();
			if (count == 2) {
				int[] array = wordUnderCursor(x);
				setSelection(array[0], array[1]);
			}
			if (count == 3) selectAll();
		}

		@Override
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			if (!super.touchDown(event, x, y, pointer, button)) return false;
			if (pointer == 0 && button != 0) return false;
			if (disabled) return true;
			Stage stage = getStage();
			FocusManager.switchFocus(stage, VisTextField.this);
			setCursorPosition(x, y);
			selectionStart = cursor;
			if (stage != null) stage.setKeyboardFocus(VisTextField.this);
			if (readOnly == false) keyboard.show(true);
			hasSelection = true;
			return true;
		}

		@Override
		public void touchDragged (InputEvent event, float x, float y, int pointer) {
			super.touchDragged(event, x, y, pointer);
			setCursorPosition(x, y);
		}

		@Override
		public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
			if (selectionStart == cursor) hasSelection = false;
			super.touchUp(event, x, y, pointer, button);
		}

		protected void setCursorPosition (float x, float y) {
			lastBlink = 0;
			cursorOn = false;
			cursor = Math.min(letterUnderCursor(x), text.length());
		}

		protected void goHome (boolean jump) {
			cursor = 0;
		}

		protected void goEnd (boolean jump) {
			cursor = text.length();
		}

		@Override
		public boolean keyDown (InputEvent event, int keycode) {
			if (disabled) return false;

			lastBlink = 0;
			cursorOn = false;

			Stage stage = getStage();
			if (stage == null || stage.getKeyboardFocus() != VisTextField.this) return false;
			if (drawBorder == false) return false;

			boolean repeat = false;
			boolean ctrl = UIUtils.ctrl();
			boolean jump = ctrl && !passwordMode;

			if (ctrl) {
				if (keycode == Keys.V && readOnly == false) {
					paste(clipboard.getContents(), true);
					repeat = true;
				}
				if (keycode == Keys.C || keycode == Keys.INSERT) {
					copy();
					return true;
				}
				if (keycode == Keys.X && readOnly == false) {
					cut(true);
					return true;
				}
				if (keycode == Keys.A) {
					selectAll();
					return true;
				}
				if (keycode == Keys.Z && readOnly == false) {
					String oldText = text;
					int oldCursorPos = getCursorPosition();
					setText(undoText);
					VisTextField.this.setCursorPosition(undoCursorPos);
					undoText = oldText;
					undoCursorPos = oldCursorPos;
					updateDisplayText();
					return true;
				}
			}

			if (UIUtils.shift()) {
				if (keycode == Keys.INSERT && readOnly == false) paste(clipboard.getContents(), true);
				if (keycode == Keys.FORWARD_DEL && readOnly == false) cut(true);
				selection:
				{
					int temp = cursor;
					keys:
					{
						if (keycode == Keys.LEFT) {
							moveCursor(false, jump);
							repeat = true;
							break keys;
						}
						if (keycode == Keys.RIGHT) {
							moveCursor(true, jump);
							repeat = true;
							break keys;
						}
						if (keycode == Keys.HOME) {
							goHome(jump);
							break keys;
						}
						if (keycode == Keys.END) {
							goEnd(jump);
							break keys;
						}
						break selection;
					}
					if (!hasSelection) {
						selectionStart = temp;
						hasSelection = true;
					}
				}
			} else {
				// Cursor movement or other keys (kills selection).
				if (keycode == Keys.LEFT) {
					moveCursor(false, jump);
					clearSelection();
					repeat = true;
				}
				if (keycode == Keys.RIGHT) {
					moveCursor(true, jump);
					clearSelection();
					repeat = true;
				}
				if (keycode == Keys.HOME) {
					goHome(jump);
					clearSelection();
				}
				if (keycode == Keys.END) {
					goEnd(jump);
					clearSelection();
				}
			}
			cursor = MathUtils.clamp(cursor, 0, text.length());

			if (repeat) {
				scheduleKeyRepeatTask(keycode);
			}
			return true;
		}

		protected void scheduleKeyRepeatTask (int keycode) {
			if (!keyRepeatTask.isScheduled() || keyRepeatTask.keycode != keycode) {
				keyRepeatTask.keycode = keycode;
				keyRepeatTask.cancel();
				if (Gdx.input.isKeyPressed(keyRepeatTask.keycode)) { //issue #179
					Timer.schedule(keyRepeatTask, keyRepeatInitialTime, keyRepeatTime);
				}
			}
		}

		@Override
		public boolean keyUp (InputEvent event, int keycode) {
			if (disabled) return false;
			keyRepeatTask.cancel();
			return true;
		}

		@Override
		public boolean keyTyped (InputEvent event, char character) {
			if (disabled || readOnly) return false;

			// Disallow "typing" most ASCII control characters, which would show up as a space when onlyFontChars is true.
			switch (character) {
				case BACKSPACE:
				case TAB:
				case ENTER_ANDROID:
				case ENTER_DESKTOP:
					break;
				default:
					if (character < 32) return false;
			}

			Stage stage = getStage();
			if (stage == null || stage.getKeyboardFocus() != VisTextField.this) return false;

			if (UIUtils.isMac && Gdx.input.isKeyPressed(Keys.SYM)) return true;

			if (focusTraversal && (character == TAB || (character == ENTER_ANDROID && enterKeyFocusTraversal))) {
				next(UIUtils.shift());
			} else {
				boolean delete = character == DELETE;
				boolean backspace = character == BACKSPACE;
				boolean enter = character == ENTER_DESKTOP || character == ENTER_ANDROID;
				boolean add = enter ? writeEnters : (!onlyFontChars || style.font.getData().hasGlyph(character));
				boolean remove = backspace || delete;
				if (add || remove) {
					String oldText = text;
					int oldCursor = cursor;
					if (hasSelection)
						cursor = delete(false);
					else {
						if (backspace && cursor > 0) {
							text = text.substring(0, cursor - 1) + text.substring(cursor--);
							renderOffset = 0;
						}
						if (delete && cursor < text.length()) {
							text = text.substring(0, cursor) + text.substring(cursor + 1);
						}
					}
					if (add && !remove) {
						// Character may be added to the text.
						if (!enter && filter != null && !filter.acceptChar(VisTextField.this, character)) return true;
						if (!withinMaxLength(text.length())) return true;
						String insertion = enter ? "\n" : String.valueOf(character);
						text = insert(cursor++, insertion, text);
					}
					if (changeText(oldText, text)) {
						long time = System.currentTimeMillis();
						if (time - 750 > lastChangeTime) {
							undoText = oldText;
							undoCursorPos = getCursorPosition() - 1;
						}
						lastChangeTime = time;
					} else
						cursor = oldCursor;
					updateDisplayText();
				}
			}
			if (listener != null) listener.keyTyped(VisTextField.this, character);
			return true;
		}
	}
}
