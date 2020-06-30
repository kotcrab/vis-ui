package com.kotcrab.vis.ui.test.manual;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

public class TestIssue326 extends VisWindow {
	public TestIssue326 () {
		super("issue #326");

		final VisTextField passwordTextField = new VisTextField("password");

		final IntSpinnerModel intModel = new IntSpinnerModel(10, -5, 20, 2);
		Spinner intSpinner = new Spinner("int", intModel);
		intSpinner.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				passwordTextField.setDisabled(true);
			}
		});

		add(new LinkLabel("issue #326 - FocusManager crash", "https://github.com/kotcrab/vis-ui/issues/326")).colspan(2).row();
		add(passwordTextField);
		row();
		add(intSpinner);

		setResizable(false);
		setModal(false);
		addCloseButton();
		closeOnEscape();
		pack();
		centerWindow();
	}
}
