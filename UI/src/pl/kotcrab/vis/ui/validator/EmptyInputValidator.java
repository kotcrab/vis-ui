
package pl.kotcrab.vis.ui.validator;

import pl.kotcrab.vis.ui.widget.VisLabel;

public class EmptyInputValidator implements InputValidator {
	private String errorMsg;
	private VisLabel msgTarget;

	public EmptyInputValidator () {

	}

	public EmptyInputValidator (String errorMsg, VisLabel msgTarget) {
		this.errorMsg = errorMsg;
		this.msgTarget = msgTarget;
	}

	@Override
	public boolean validateInput (String input) {
		if (input.isEmpty()) {
			if (msgTarget != null) msgTarget.setText(errorMsg);
			return false;
		}
		return true;
	}
}
