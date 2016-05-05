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

package com.kotcrab.vis.ui.widget.spinner;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/**
 * {@link Spinner} model allowing to browse through items from object {@link Array}.
 * <p>
 * Note that this (by default) uses item's toString() method to get string representation of objects used to validate
 * that user has entered valid value which due to {@link VisValidatableTextField} nature has to be done for every
 * entered letter. Item's toString() should cache it's result internally to optimize this check. To customize how string
 * representation is obtained override {@link #itemToString(Object)}.
 * @author Kotcrab
 * @since 1.0.2
 */
public class ArraySpinnerModel<T> extends AbstractSpinnerModel {
	private Array<T> items = new Array<T>();
	private T current;
	private int currentIndex;

	/**
	 * Creates empty instance with no items set. Note that spinner with empty array model will be always treated as in
	 * invalid state.
	 */
	public ArraySpinnerModel () {
		super(false);
	}

	/**
	 * Creates new instance of {@link ArraySpinnerModel} using provided items.
	 * @param items array containing items for the model. It is copied to new array in order to prevent accidental
	 * modification. Array may be empty however in such case spinner will be always in invalid input state.
	 */
	public ArraySpinnerModel (Array<T> items) {
		super(false);
		this.items.addAll(items);
	}

	@Override
	public void bind (Spinner spinner) {
		super.bind(spinner);
		updateCurrentItem(0);
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
	 * @param item that string representation should be created. It is necessary to check if item is null!
	 * @return string representation of item
	 */
	protected String itemToString (T item) {
		if (item == null) return "";
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
		updateCurrentItem(index);
	}

	@Override
	public boolean incrementModel () {
		if (currentIndex + 1 >= items.size) {
			if (isWrap()) {
				updateCurrentItem(0);
				return true;
			}

			return false;
		}
		updateCurrentItem(currentIndex + 1);
		return true;
	}

	@Override
	public boolean decrementModel () {
		if (currentIndex - 1 < 0) {
			if (isWrap()) {
				updateCurrentItem(items.size - 1);
				return true;
			}

			return false;
		}
		updateCurrentItem(currentIndex - 1);
		return true;
	}

	@Override
	public String getText () {
		return itemToString(current);
	}

	/** Notifies model that items has changed and view must be refreshed. This will trigger a change event. */
	public void invalidateDataSet () {
		updateCurrentItem(MathUtils.clamp(currentIndex, 0, items.size - 1));
		spinner.notifyValueChanged(true);
	}

	/** @return array containing model items. If you modify returned array you must call {@link #invalidateDataSet()}. */
	public Array<T> getItems () {
		return items;
	}

	/** Changes items of this model. Current index is not preserved. This will trigger a change event. */
	public void setItems (Array<T> newItems) {
		items.clear();
		items.addAll(newItems);
		currentIndex = 0;
		invalidateDataSet();
	}

	/** @return current item index or -1 if items array is empty */
	public int getCurrentIndex () {
		return currentIndex;
	}

	/** @return current item or null if items array is empty */
	public T getCurrent () {
		return current;
	}

	/** Sets current item. If array is empty then current value will be set to null. */
	public void setCurrent (int newIndex) {
		setCurrent(newIndex, spinner.isProgrammaticChangeEvents());
	}

	/** Sets current item. If array is empty then current value will be set to null. */
	public void setCurrent (int newIndex, boolean fireEvent) {
		updateCurrentItem(newIndex);
		spinner.notifyValueChanged(fireEvent);
	}

	/** @param item if does not exist in items array, model item will be set to first item. */
	public void setCurrent (T item) {
		setCurrent(item, spinner.isProgrammaticChangeEvents());
	}

	/** @param item if does not exist in items array, model item will be set to first item. */
	public void setCurrent (T item, boolean fireEvent) {
		int index = items.indexOf(item, true);
		if (index == -1) {
			setCurrent(0, fireEvent);
		} else {
			setCurrent(index, fireEvent);
		}
	}

	private void updateCurrentItem (int newIndex) {
		if (items.size == 0) {
			current = null;
			currentIndex = -1;
		} else {
			currentIndex = newIndex;
			current = items.get(newIndex);
		}
	}
}
