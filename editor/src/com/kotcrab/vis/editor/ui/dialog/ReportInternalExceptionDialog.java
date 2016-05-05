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

import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.util.ExceptionUtils;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.editor.util.vis.CrashReporter;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextArea;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/** @author Kotcrab */
public class ReportInternalExceptionDialog extends BaseDialog {
	private static final String REPORT_URL = "http://apps.kotcrab.com/vis/crashes/report.php";
	private static final String TAG = "CrashReporter";
	private Throwable cause;
	private ReportInternalExceptionDialogListener listener;
	private VisTextArea detailsTextArea;

	public ReportInternalExceptionDialog (Throwable cause, ReportInternalExceptionDialogListener listener) {
		super("Report internal exception");
		this.cause = cause;
		this.listener = listener;
		init();
	}

	@Override
	protected void createUI () {
		defaults().left();
		left();

		detailsTextArea = new VisTextArea();

		add(new VisLabel("Internal exception occurred in VisEditor, would you like to send\nissue report to VisEditor team to help fix this problem?")).row();
		add(new VisLabel("Describe what happened: (optional)")).row();
		add(detailsTextArea).height(140).growX().row();

		VisTextButton sendButton = new VisTextButton("Send");

		add(sendButton).right();

		sendButton.addListener(new VisChangeListener((event, actor) -> {
			sendReport();
			fadeOut();
		}));
	}

	private void sendReport () {
		new Thread(() -> {
			try {
				HttpURLConnection connection = (HttpURLConnection) new URL(REPORT_URL + "?filename=" + "viseditor-internal " + new SimpleDateFormat(CrashReporter.DATE_FORMAT).format(new Date()) + ".txt").openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				OutputStream os = connection.getOutputStream();

				os.write(ExceptionUtils.getStackTrace(cause).getBytes());
				os.write(("Details: " + detailsTextArea.getText()).getBytes());
				os.flush();
				os.close();

				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				String s;
				while ((s = in.readLine()) != null)
					Log.info(TAG, "Server response: " + s);
				in.close();

				Log.info(TAG, "Crash report sent");
				listener.success();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public interface ReportInternalExceptionDialogListener {
		void success ();
	}
}
