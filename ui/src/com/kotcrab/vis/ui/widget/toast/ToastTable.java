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

package com.kotcrab.vis.ui.widget.toast;

import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Base class for all toast content tables. Note that using this class is not required ({@link VisTable} can be used directly)
 * however it's preferred because it provides access to {@link Toast} instance and {@link #fadeOut()} method. Using ToastTable
 * also allows to reuse {@link Toast} instance instead of creating new one everytime you want to show toast.
 * @author Kotcrab
 * @since 1.1.0
 */
public class ToastTable extends VisTable {
	protected Toast toast;

	public ToastTable () {
	}

	public ToastTable (boolean setVisDefaults) {
		super(setVisDefaults);
	}

	public void fadeOut () {
		if (toast == null)
			throw new IllegalStateException("fadeOut can't be called before toast was shown by ToastManager");
		toast.fadeOut();
	}

	/** Called by framework when this ToastTable was assigned to it's toast container. */
	public void setToast (Toast toast) {
		this.toast = toast;
	}

	/** @return toast that this table belongs to or null if none */
	public Toast getToast () {
		return toast;
	}
}
