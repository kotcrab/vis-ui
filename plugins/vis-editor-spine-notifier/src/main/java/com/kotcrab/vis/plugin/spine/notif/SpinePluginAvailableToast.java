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

import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.plugin.PluginFileHandle;
import com.kotcrab.vis.editor.ui.dialog.LicenseDialog;
import com.kotcrab.vis.editor.ui.dialog.LicenseDialog.LicenseDialogListener;
import com.kotcrab.vis.editor.util.scene2d.TableBuilder;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.toast.ToastTable;

public class SpinePluginAvailableToast extends ToastTable {
	public SpinePluginAvailableToast (SpineNotifier spineNotifier) {
		LinkLabel ignoreForever = new LinkLabel("Don't show again");
		LinkLabel ignore = new LinkLabel("Later");
		LinkLabel enable = new LinkLabel("Enable");

		add("Spine integration plugin can be enabled!").row();
		add(TableBuilder.build(12, ignoreForever, ignore, enable)).right();

		ignoreForever.setListener(url -> {
			spineNotifier.ignoreForever();
			fadeOut();
		});

		ignore.setListener(url -> fadeOut());

		enable.setListener(url -> {
			getStage().addActor(new LicenseDialog(new PluginFileHandle(SpinePluginAvailableToast.class, "LICENSE").readString(), new LicenseDialogListener() {
				@Override
				public void licenseAccepted () {
					spineNotifier.enableSpinePlugin();
					Editor.instance.showRestartDialog();
				}
			}).fadeIn());

			fadeOut();
		});

		pack();
	}
}
