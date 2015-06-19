/*
 * Copyright 2014-2015 See AUTHORS file.
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

package com.kotcrab.vis.editor.ui.toast;

import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.module.editor.ToastModule;
import com.kotcrab.vis.editor.ui.dialog.ExceptionDialog;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Used to display information about exception as toast.
 * @author Kotcrab
 * @see ToastModule
 */
public class ExceptionToast extends VisTable {
	public ExceptionToast (String text, Throwable cause) {

		LinkLabel label = new LinkLabel("Details");
		label.setListener(url -> Editor.instance.getStage().addActor(new ExceptionDialog(text, cause).fadeIn()));

		add(text).expand().fill().row();
		add(label).right();
	}
}
