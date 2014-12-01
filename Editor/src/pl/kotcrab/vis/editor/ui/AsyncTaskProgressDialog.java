
package pl.kotcrab.vis.editor.ui;

import pl.kotcrab.vis.editor.AsyncTask;
import pl.kotcrab.vis.editor.AsyncTaskListener;
import pl.kotcrab.vis.ui.TableUtils;
import pl.kotcrab.vis.ui.widget.VisLabel;
import pl.kotcrab.vis.ui.widget.VisProgressBar;
import pl.kotcrab.vis.ui.widget.VisTextButton;
import pl.kotcrab.vis.ui.widget.VisWindow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class AsyncTaskProgressDialog extends VisWindow {

	private boolean failed;

	public AsyncTaskProgressDialog (String title, AsyncTask task) {
		super(title);
		setTitleAlignment(Align.center);
		setModal(true);

		TableUtils.setSpaceDefaults(this);

		final VisLabel statusLabel = new VisLabel("Please wait...");
		final VisProgressBar progressBar = new VisProgressBar(0, 100, 1, false);

		defaults().padLeft(6).padRight(6);

		add(statusLabel).padTop(6).left().row();
		add(progressBar).width(300).padTop(6).padBottom(6);

		task.setListener(new AsyncTaskListener() {

			@Override
			public void progressChanged (int newProgressPercent) {
				progressBar.setValue(newProgressPercent);
			}

			@Override
			public void messageChanged (String newMsg) {
				statusLabel.setText(newMsg);
			}

			@Override
			public void finished () {
				if (failed == false) fadeOut();
			}

			@Override
			public void failed (String reason) {
				failed = true;
				statusLabel.setText("Error: " + reason);
				statusLabel.setColor(Color.RED);

				VisTextButton okButton = new VisTextButton("OK");
				okButton.addListener(new ChangeListener() {
					@Override
					public void changed (ChangeEvent event, Actor actor) {
						fadeOut();
					}
				});

				row();
				add(okButton).right();
				pack();
				centerWindow();
			}
		});

		task.start();

		pack();
		centerWindow();
	}

}
