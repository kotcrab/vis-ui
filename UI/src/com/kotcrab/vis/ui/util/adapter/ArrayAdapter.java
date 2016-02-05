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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

/**
 * Built-in adapter implementation for {@link Array}.
 * @author Kotcrab
 * @since 1.0.0
 */
public abstract class ArrayAdapter<ItemT, ViewT extends Actor> extends AbstractListAdapter<ItemT, ViewT> {
	private Array<ItemT> array;

	public ArrayAdapter (Array<ItemT> array) {
		this.array = array;
	}

	@Override
	public Iterable<ItemT> iterable () {
		return array;
	}

	//Delegates

	public void add (ItemT value) {
		array.add(value);
		invalidateDataSet();
	}

	public void addAll (Array<? extends ItemT> array) {
		this.array.addAll(array);
		invalidateDataSet();
	}

	public void addAll (Array<? extends ItemT> array, int start, int count) {
		this.array.addAll(array, start, count);
		invalidateDataSet();
	}

	public void addAll (ItemT... array) {
		this.array.addAll(array);
		invalidateDataSet();
	}

	public void addAll (ItemT[] array, int start, int count) {
		this.array.addAll(array, start, count);
		invalidateDataSet();
	}

	public void set (int index, ItemT value) {
		array.set(index, value);
		invalidateDataSet();
	}

	public void insert (int index, ItemT value) {
		array.insert(index, value);
		invalidateDataSet();
	}

	public void swap (int first, int second) {
		array.swap(first, second);
		invalidateDataSet();
	}

	public boolean removeValue (ItemT value, boolean identity) {
		boolean res = array.removeValue(value, identity);
		invalidateDataSet();
		return res;
	}

	public ItemT removeIndex (int index) {
		ItemT item = array.removeIndex(index);
		invalidateDataSet();
		return item;
	}

	public void removeRange (int start, int end) {
		array.removeRange(start, end);
		invalidateDataSet();
	}

	public boolean removeAll (Array<? extends ItemT> array, boolean identity) {
		boolean res = this.array.removeAll(array, identity);
		invalidateDataSet();
		return res;
	}

	public void clear () {
		array.clear();
		invalidateDataSet();
	}

	public void shuffle () {
		array.shuffle();
		invalidateDataSet();
	}

	public void reverse () {
		array.reverse();
		invalidateDataSet();
	}

	public ItemT pop () {
		ItemT item = array.pop();
		invalidateDataSet();
		return item;
	}
}
