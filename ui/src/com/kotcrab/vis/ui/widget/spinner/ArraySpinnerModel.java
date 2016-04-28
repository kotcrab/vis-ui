package com.kotcrab.vis.ui.widget.spinner;

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/**
 * {@link Spinner} model allowing to browse through items from object {@link Array}. Once created model items can't be
 * changed. To change items
 * you must create new model and set it in {@link Spinner}.
 * <p>
 * Note that this (by default) uses item's toString() method to get string representation of objects used to validate that user has
 * entered valid value which due to {@link VisValidatableTextField} nature has to be done for every entered letter. Item's toString()
 * should cache it's result internally to optimize this check. To customize how string representation is obtained
 * override {@link #itemToString(Object)}
 * @author Kotcrab
 * @since 1.0.2
 */
public class ArraySpinnerModel<T> implements SpinnerModel {
	private Spinner spinner;

	private Array<T> items;
	private T current;
	private int currentIndex;

	public ArraySpinnerModel (Array<T> items) {
		if (items.size == 0) throw new IllegalArgumentException("items array can't be empty");
		this.items = new Array<T>(items);
	}

	@Override
	public void bind (Spinner spinner) {
		if (this.spinner != null)
			throw new IllegalStateException("ArraySpinnerModel can be only used by single instance of Spinner");
		this.spinner = spinner;
		setCurrent(0);
		spinner.getTextField().addValidator(new InputValidator() {
			@Override
			public boolean validateInput (String input) {
				return getItemIndexForText(input) != -1;
			}
		});

		spinner.notifyValueChanged(true);
	}

	/**
	 * Creates string representation displayed in {@link Spinner} for given object. By default toString() is used.
	 * @param item that string representation should be created
	 * @return string representation of item
	 */
	protected String itemToString (T item) {
		return item.toString();
	}

	private int getItemIndexForText (String text) {
		for (int i = 0; i < items.size; i++) {
			T item = items.get(i);
			if (itemToString(item).equals(text)) return i;

		}

		return -1;
	}

	@Override
	public void textChanged () {
		String text = spinner.getTextField().getText();
		int index = getItemIndexForText(text);
		if (index == -1) return;
		setCurrent(index);
	}

	@Override
	public void increment () {
		if (currentIndex + 1 >= items.size) return;
		setCurrent(currentIndex + 1);
	}

	@Override
	public void decrement () {
		if (currentIndex - 1 < 0) return;
		setCurrent(currentIndex - 1);
	}

	@Override
	public String getText () {
		return itemToString(current);
	}

	public T getCurrent () {
		return current;
	}

	public void setCurrent (int newIndex) {
		this.currentIndex = newIndex;
		this.current = items.get(newIndex);
	}

	public void setCurrent (T item) {
		int index = items.indexOf(item, true);
		if (index != -1) setCurrent(index);
	}
}
