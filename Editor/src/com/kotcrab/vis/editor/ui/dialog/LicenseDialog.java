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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.editor.util.scene2d.TableBuilder;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.ui.widget.*;

/**
 * Dialog displayed when user must accept some license agreement
 * @author Kotcrab
 */
public class LicenseDialog extends VisWindow {
	public LicenseDialog (String license, LicenseDialogListener listener) {
		super("License Agreement");

		addCloseButton();
		closeOnEscape();
		setModal(true);

		VisTextButton declineButton = new VisTextButton("Decline");
		VisTextButton acceptButton = new VisTextButton("Accept");
		VisLabel errorLabel = new VisLabel(license);

		VisTable detailsTable = new VisTable(true);
		detailsTable.add(createScrollPane(errorLabel)).colspan(2).width(600).height(300);

		add("To continue you must read and agree to the following license agreement").row();
		add(detailsTable).row();
		add(TableBuilder.build(declineButton, acceptButton)).padBottom(3).padTop(3);

		acceptButton.addListener(new VisChangeListener((event, actor) -> {
			listener.licenseAccepted();
			fadeOut();
		}));
		declineButton.addListener(new VisChangeListener((event, actor) -> {
			listener.licenseDeclined();
			fadeOut();
		}));

		pack();
		centerWindow();
	}

	private VisScrollPane createScrollPane (Actor widget) {
		VisScrollPane scrollPane = new VisScrollPane(widget);
		scrollPane.setOverscroll(false, true);
		scrollPane.setFadeScrollBars(false);
		return scrollPane;
	}

	public interface LicenseDialogListener {
		default void licenseAccepted () {

		}

		default void licenseDeclined () {

		}
	}
}
