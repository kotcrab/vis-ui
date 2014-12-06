
package pl.kotcrab.vis.editor.module;

import pl.kotcrab.utils.event.Event;
import pl.kotcrab.utils.event.EventListener;
import pl.kotcrab.vis.editor.App;
import pl.kotcrab.vis.editor.Editor;
import pl.kotcrab.vis.editor.event.StatusBarEvent;
import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.VisUI;
import pl.kotcrab.vis.ui.widget.VisLabel;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class StatusBar extends ModuleAdapter implements EventListener {
	public VisTable table;
	public VisLabel statusLabel;

	public Timer timer;

	public StatusBar () {
		timer = new Timer();

		statusLabel = new VisLabel("Ready");

		table = new VisTable();
		table.setBackground(VisUI.skin.getDrawable("button"));
		table.add(statusLabel);
		table.add(new Image(VisUI.skin.getRegion("button"))).expand().fill();

	}

	public void addToStage (Table root) {
		root.add(table).fillX().expandX().row();
	}

	public void setText (String newText, int timeSeconds) {
		statusLabel.setText(newText);
		timer.clear();
		timer.scheduleTask(resetTask, timeSeconds);
	}

	@Override
	public void added () {
		App.eventBus.register(this);
		addToStage(Editor.instance.getRoot());
	}

	@Override
	public void dispose () {
		App.eventBus.unregister(this);
	}

	private Task resetTask = new Task() {
		@Override
		public void run () {
			statusLabel.setText("Ready");
		}
	};

	@Override
	public boolean onEvent (Event e) {
		if (e instanceof StatusBarEvent) {
			StatusBarEvent event = (StatusBarEvent)e;
			setText(event.text, event.timeSeconds);
			return true;
		}

		return false;
	}
}
