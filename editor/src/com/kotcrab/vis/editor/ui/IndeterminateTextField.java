package com.kotcrab.vis.editor.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/** @author Kotcrab */
public class IndeterminateTextField {
	private VisValidatableTextField textField;
	private String text;
	private boolean indeterminate = false;

	public IndeterminateTextField () {
		this(new VisValidatableTextField());
	}

	public IndeterminateTextField (VisValidatableTextField textField) {
		this.textField = textField;
		textField.setStyle(new VisTextField.VisTextFieldStyle(textField.getStyle()));
		textField.setProgrammaticChangeEvents(false);
		textField.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if(indeterminate) {
					textField.getStyle().fontColor = Color.WHITE;
					indeterminate = false;
				}

				text = textField.getText();
			}
		});
		text = textField.getText();
	}

	public void setText (String text) {
		this.text = text;
		updateText();
	}

	public String getText () {
		if (indeterminate) throw new IllegalStateException("Cannot get text when field is in indeterminate state");
		return text;
	}

	private void updateText () {
		if (indeterminate) {
			textField.setText("<?>");
			textField.getStyle().fontColor = Color.LIGHT_GRAY;
		} else {
			textField.setText(text);
			textField.getStyle().fontColor = Color.WHITE;
		}
	}

	public VisValidatableTextField getTextField () {
		return textField;
	}

	public boolean isIndeterminate () {
		return indeterminate;
	}

	public void setIndeterminate (boolean indeterminate) {
		this.indeterminate = indeterminate;
		updateText();
	}

	public void setDisabled (boolean disabled) {
		textField.setDisabled(disabled);
	}
}
