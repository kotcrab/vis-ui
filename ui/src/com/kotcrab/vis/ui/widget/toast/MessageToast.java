package com.kotcrab.vis.ui.widget.toast;

import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Toast with provided user message and arbitrary amount of {@link LinkLabel} bellow it acting as action buttons.
 * @author Kotcrab
 * @see ToastTable
 * @since 1.1.0
 */
public class MessageToast extends ToastTable {
	private VisTable linkLabelTable = new VisTable();

	public MessageToast (String message) {
		super();
		add(new VisLabel(message)).left().row();
		add(linkLabelTable).right();
	}

	/**
	 * Adds new link label below toast message.
	 * @param text link label text
	 * @param labelListener will be called upon label click. Note that toast won't be closed automatically so {@link Toast#fadeOut()}
	 * must be called
	 */
	public void addLinkLabel (String text, LinkLabel.LinkLabelListener labelListener) {
		LinkLabel label = new LinkLabel(text);
		label.setListener(labelListener);
		linkLabelTable.add(label).spaceRight(12);
	}
}
