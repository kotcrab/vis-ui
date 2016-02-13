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

package com.kotcrab.vis.ui.util.adapter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.kotcrab.vis.ui.widget.ListView;
import com.kotcrab.vis.ui.widget.ListView.ItemClickListener;
import com.kotcrab.vis.ui.widget.ListView.ListAdapterListener;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.Comparator;

/**
 * Basic {@link ListAdapter} implementation using {@link CachedItemAdapter}. Supports item selection. Classes
 * extending this should store provided list and provide delegates for all common methods that change array state.
 * Those delegates should call {@link #itemAdded(Object)} or {@link #itemRemoved(Object)} in order to properly update
 * view cache. When changes to array are too big to be handled by those two methods {@link #itemsChanged()} should be
 * called. When only items fields has changed, and no new item were added or removed you should call
 * {@link #itemsDataChanged()}.
 * <p>
 * When view does not existed in cache and must be created {@link #createView(Object)} is called. When item view exists
 * in cache {@link #updateView(Actor, Object)} will be called.
 * <p>
 * Enabling item selection requires calling {@link #setSelectionMode(SelectionMode)} and overriding
 * {@link #selectView(Actor)} and {@link #deselectView(Actor)}.
 * @author Kotcrab
 * @see ArrayAdapter
 * @see ArrayListAdapter
 * @since 1.0.0
 */
public abstract class AbstractListAdapter<ItemT, ViewT extends Actor> extends CachedItemAdapter<ItemT, ViewT>
		implements ListAdapter<ItemT> {
	protected ListView<ItemT> view;
	protected ListAdapterListener viewListener;

	private ItemClickListener<ItemT> clickListener;

	private SelectionMode selectionMode = SelectionMode.DISABLED;
	private ListSelection<ItemT, ViewT> selection = new ListSelection<ItemT, ViewT>(this);

	private Comparator<ItemT> itemsComparator;

	@Override
	public void fillTable (VisTable itemsTable) {
		if (itemsComparator != null) sort(itemsComparator);
		for (final ItemT item : iterable()) {
			final ViewT view = getView(item);

			boolean listenerMissing = true;
			for (EventListener listener : view.getListeners()) {
				if (ClassReflection.isInstance(ListClickListener.class, listener)) {
					listenerMissing = false;
					break;
				}
			}
			if (listenerMissing) {
				view.setTouchable(Touchable.enabled);
				view.addListener(new ListClickListener(view, item));
			}

			itemsTable.add(view).growX();
			itemsTable.row();
		}
	}

	@Override
	public void setListView (ListView<ItemT> view, ListAdapterListener viewListener) {
		this.view = view;
		this.viewListener = viewListener;
	}

	@Override
	public void setItemClickListener (ItemClickListener<ItemT> listener) {
		clickListener = listener;
	}

	protected void itemAdded (ItemT item) {
		viewListener.invalidateDataSet();
	}

	protected void itemRemoved (ItemT item) {
		getViews().remove(item);
		viewListener.invalidateDataSet();
	}

	/**
	 * Notifies adapter that underlying collection has changed, ie. some items were added or removed. This does not need to
	 * be called when only the fields of stored objects changed see {@link #itemsDataChanged()}.
	 */
	public void itemsChanged () {
		getViews().clear();
		viewListener.invalidateDataSet();
	}

	/**
	 * Notifies adapter that data of items has changed. This means that objects fields in underlying collection has changed
	 * and views needs updating. This must not be called if some items were removed or added from collection for that
	 * {@link #itemsChanged()}
	 */
	public void itemsDataChanged () {
		viewListener.invalidateDataSet();
	}

	@Override
	protected void updateView (ViewT view, ItemT item) {

	}

	public SelectionMode getSelectionMode () {
		return selectionMode;
	}

	public void setSelectionMode (SelectionMode selectionMode) {
		if (selectionMode == null) throw new IllegalArgumentException("selectionMode can't be null");
		this.selectionMode = selectionMode;
	}

	/**
	 * Sets items comparator allowing to define order in which items will be displayed in list view. This will sort
	 * underlaying array before building views.
	 * @param comparator that will be used to compare items
	 */
	public void setItemsSorter (Comparator<ItemT> comparator) {
		this.itemsComparator = comparator;
	}

	/** @return selected items, must not be modified */
	public Array<ItemT> getSelection () {
		return selection.getSelection();
	}

	public ListSelection<ItemT, ViewT> getSelectionManager () {
		return selection;
	}

	protected void selectView (ViewT view) {
		if (selectionMode == SelectionMode.DISABLED) return;
		throw new UnsupportedOperationException("selectView must be implemented when `selectionMode` is different than SelectionMode.DISABLED");
	}

	protected void deselectView (ViewT view) {
		if (selectionMode == SelectionMode.DISABLED) return;
		throw new UnsupportedOperationException("deselectView must be implemented when `selectionMode` is different than SelectionMode.DISABLED");
	}

	private class ListClickListener extends ClickListener {
		private ViewT view;
		private ItemT item;

		public ListClickListener (ViewT view, ItemT item) {
			this.view = view;
			this.item = item;
		}

		@Override
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			super.touchDown(event, x, y, pointer, button);
			selection.touchDown(view, item);
			return true;
		}

		@Override
		public void clicked (InputEvent event, float x, float y) {
			if (clickListener != null) clickListener.clicked(item);
		}
	}

	protected abstract void sort (Comparator<ItemT> comparator);

	public enum SelectionMode {
		/** Selecting items is not possible. */
		DISABLED,
		/**
		 * Only one element can be selected. {@link AbstractListAdapter#selectView(Actor)} and
		 * {@link AbstractListAdapter#deselectView(Actor)} must be implemented.
		 */
		SINGLE,
		/**
		 * Multiple elements can be selected. {@link AbstractListAdapter#selectView(Actor)} and
		 * {@link AbstractListAdapter#deselectView(Actor)} must be implemented.
		 */
		MULTIPLE
	}

	/**
	 * Manages selection of {@link AbstractListAdapter} items.
	 * @author Kotcrab
	 */
	public static class ListSelection<ItemT, ViewT extends Actor> {
		private AbstractListAdapter<ItemT, ViewT> adapter;

		public static final int DEFAULT_KEY = -1;
		private int groupMultiSelectKey = DEFAULT_KEY; //shift by default
		private int multiSelectKey = DEFAULT_KEY; //ctrl (or command on mac) by default

		private Array<ItemT> selection = new Array<ItemT>();

		private ListSelection (AbstractListAdapter<ItemT, ViewT> adapter) {
			this.adapter = adapter;
		}

		public void select (ItemT item) {
			select(item, adapter.getViews().get(item));
		}

		public void deselect (ItemT item) {
			deselect(item, adapter.getViews().get(item));
		}

		void select (ItemT item, ViewT view) {
			if (adapter.getSelectionMode() == SelectionMode.MULTIPLE && selection.size >= 1 && isGroupMultiSelectKeyPressed()) {
				selectGroup(item);
			}

			doSelect(item, view);
		}

		private void doSelect (ItemT item, ViewT view) {
			if (selection.contains(item, true) == false) {
				adapter.selectView(view);
				selection.add(item);
			}
		}

		private void selectGroup (ItemT newItem) {
			int thisSelectionIndex = adapter.indexOf(newItem);
			int lastSelectionIndex = adapter.indexOf(selection.peek());

			int start;
			int end;

			if (thisSelectionIndex > lastSelectionIndex) {
				start = lastSelectionIndex;
				end = thisSelectionIndex;
			} else {
				start = thisSelectionIndex;
				end = lastSelectionIndex;
			}

			for (int i = start; i < end; i++) {
				ItemT item = adapter.get(i);
				doSelect(item, adapter.getViews().get(item));
			}
		}

		void deselect (ItemT item, ViewT view) {
			if (selection.contains(item, true) == false) return;
			adapter.deselectView(view);
			selection.removeValue(item, true);
		}

		public void deselectAll () {
			Array<ItemT> items = new Array<ItemT>(selection);
			for (ItemT item : items) {
				deselect(item);
			}
		}

		/** @return internal array, MUST NOT be modified directly */
		public Array<ItemT> getSelection () {
			return selection;
		}

		void touchDown (ViewT view, ItemT item) {
			if (adapter.getSelectionMode() == SelectionMode.DISABLED) return;

			if (isMultiSelectKeyPressed() == false && isGroupMultiSelectKeyPressed() == false) {
				deselectAll();
			}

			if (selection.contains(item, true) == false) {
				if (adapter.getSelectionMode() == SelectionMode.SINGLE) deselectAll();
				select(item, view);
			} else {
				deselect(item, view);
			}
		}

		public int getMultiSelectKey () {
			return multiSelectKey;
		}

		/** @param multiSelectKey from {@link Keys} or {@link ListSelection#DEFAULT_KEY} to restore default */
		public void setMultiSelectKey (int multiSelectKey) {
			this.multiSelectKey = multiSelectKey;
		}

		public int getGroupMultiSelectKey () {
			return groupMultiSelectKey;
		}

		/** @param groupMultiSelectKey from {@link Keys} or {@link ListSelection#DEFAULT_KEY} to restore default */
		public void setGroupMultiSelectKey (int groupMultiSelectKey) {
			this.groupMultiSelectKey = groupMultiSelectKey;
		}

		private boolean isMultiSelectKeyPressed () {
			if (multiSelectKey == DEFAULT_KEY)
				return UIUtils.ctrl();
			else
				return Gdx.input.isKeyPressed(multiSelectKey);
		}

		private boolean isGroupMultiSelectKeyPressed () {
			if (groupMultiSelectKey == DEFAULT_KEY)
				return UIUtils.shift();
			else
				return Gdx.input.isKeyPressed(groupMultiSelectKey);
		}
	}
}
