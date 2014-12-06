
package pl.kotcrab.vis.editor.event;

import pl.kotcrab.utils.event.Event;

public class StatusBarEvent implements Event {
	public String text;
	public int timeSeconds;

	public StatusBarEvent (String text, int timeSeconds) {
		this.text = text;
		this.timeSeconds = timeSeconds;
	}
}
