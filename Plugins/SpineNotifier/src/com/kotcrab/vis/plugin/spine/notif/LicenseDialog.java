/*
 * Spine Runtimes Software License
 * Version 2.3
 *
 * Copyright (c) 2013-2015, Esoteric Software
 * All rights reserved.
 *
 * You are granted a perpetual, non-exclusive, non-sublicensable and
 * non-transferable license to use, install, execute and perform the Spine
 * Runtimes Software (the "Software") and derivative works solely for personal
 * or internal use. Without the written permission of Esoteric Software (see
 * Section 2 of the Spine Software License Agreement), you may not (a) modify,
 * translate, adapt or otherwise create derivative works, improvements of the
 * Software or develop new applications using the Software or (b) remove,
 * delete, alter or obscure any trademarks or any copyright, trademark, patent
 * or other intellectual property or proprietary rights notices on or in the
 * Software, including any copy thereof. Redistributions in binary or source
 * form must include this license and terms.
 *
 * THIS SOFTWARE IS PROVIDED BY ESOTERIC SOFTWARE "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL ESOTERIC SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.kotcrab.vis.plugin.spine.notif;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.StreamUtils;
import com.kotcrab.vis.editor.util.gdx.TableBuilder;
import com.kotcrab.vis.editor.util.gdx.VisChangeListener;
import com.kotcrab.vis.ui.widget.*;

import java.io.IOException;

public class LicenseDialog extends VisWindow {
	public LicenseDialog (LicenseDialogListener listener) {
		super("License Agreement");

		addCloseButton();
		closeOnEscape();
		setModal(true);

		VisTextButton declineButton = new VisTextButton("Decline");
		VisTextButton acceptButton = new VisTextButton("Accept");
		String license;

		try {
			license = StreamUtils.copyStreamToString(LicenseDialog.class.getResourceAsStream("LICENSE"));
		} catch (IOException e) {
			throw new IllegalStateException("Failed to load Spine Runtime license!", e);
		}

		VisLabel errorLabel = new VisLabel(license);

		VisTable detailsTable = new VisTable(true);
		detailsTable.add(createScrollPane(errorLabel)).colspan(2).width(600).height(300);

		add("To continue you must read and agree to 'Spine Runtimes Software License'").row();
		add(detailsTable).row();
		add(TableBuilder.build(declineButton, acceptButton)).padBottom(3).padTop(3);

		acceptButton.addListener(new VisChangeListener((event, actor) -> {
			listener.licenseAccepted();
			fadeOut();
		}));
		declineButton.addListener(new VisChangeListener((event, actor) -> fadeOut()));

		pack();
		centerWindow();
	}

	private VisScrollPane createScrollPane (Actor widget) {
		VisScrollPane scrollPane = new VisScrollPane(widget);
		scrollPane.setOverscroll(false, true);
		scrollPane.setFadeScrollBars(false);
		return scrollPane;
	}

	interface LicenseDialogListener {
		void licenseAccepted ();
	}
}
