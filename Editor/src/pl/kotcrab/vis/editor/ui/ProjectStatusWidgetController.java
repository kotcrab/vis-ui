
package pl.kotcrab.vis.editor.ui;

import pl.kotcrab.utils.event.Event;
import pl.kotcrab.utils.event.EventListener;
import pl.kotcrab.vis.editor.App;
import pl.kotcrab.vis.editor.event.ProjectStatusEvent;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class ProjectStatusWidgetController implements EventListener, Disposable {
	private Array<Button> buttons;
	private boolean loaded = false;

	public ProjectStatusWidgetController () {
		buttons = new Array<Button>();
		App.eventBus.register(this);
	}

	@Override
	public void dispose () {
		App.eventBus.unregister(this);
	}

	@Override
	public boolean onEvent (Event e) {
		if (e instanceof ProjectStatusEvent) {
			ProjectStatusEvent event = (ProjectStatusEvent)e;
			if (event.status == ProjectStatusEvent.Status.Loaded)
				loaded = true;
			else
				loaded = false;

			updateWidgets();
		}

		return false;

	}

	private void updateWidgets () {
		for (Button b : buttons)
			b.setDisabled(!loaded);
	}

	public void addButton (Button button) {
		buttons.add(button);
		
		button.setDisabled(!loaded);
	}
}
