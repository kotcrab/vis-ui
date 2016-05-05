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
