
package pl.kotcrab.vis.sceneeditor.plugin;

import pl.kotcrab.vis.sceneeditor.EditorState;

import com.badlogic.gdx.utils.Array;

public class PluginManager {
	private EditorState state;
	private Array<Plugin> plugins = new Array<Plugin>();

	public PluginManager (EditorState state) {
		this.state = state;
	}

	public void registerPlugin (Plugin plugin) {
		plugins.add(plugin);
		plugin.init(state);
	}

	public void enable () {
		for (Plugin p : plugins)
			p.enable();
	}

	public void disable () {
		for (Plugin p : plugins)
			p.disable();
	}

	public void render () {
		for (Plugin p : plugins)
			p.render();
	}

	public void dispose () {
		for (Plugin p : plugins)
			p.dispose();
	}

	public void resize () {
		for (Plugin p : plugins)
			p.resize();
	}
	
	public void keyDown (int keycode) {
		for (Plugin p : plugins)
			if (p.keyDown(keycode)) break;
	}

	public void keyUp (int keycode) {
		for (Plugin p : plugins)
			if (p.keyUp(keycode)) break;
	}

	public void keyTyped (char character) {
		for (Plugin p : plugins)
			if (p.keyTyped(character)) break;
	}

	public void touchDown (int sceneX, int sceneY, int pointer, int button) {
		for (Plugin p : plugins)
			if (p.touchDown(sceneX, sceneY, pointer, button)) break;
	}

	public void touchUp (int sceneX, int sceneY, int pointer, int button) {
		for (Plugin p : plugins)
			if (p.touchUp(sceneX, sceneY, pointer, button)) break;
	}

	public void touchDragged (int sceneX, int sceneY, int pointer) {
		for (Plugin p : plugins)
			if (p.touchDragged(sceneX, sceneY, pointer)) break;
	}

	public void mouseMoved (int sceneX, int sceneY) {
		for (Plugin p : plugins)
			if (p.mouseMoved(sceneX, sceneY)) break;
	}

	public void scrolled (int amount) {
		for (Plugin p : plugins)
			if (p.scrolled(amount)) break;
	}

	public void touchDown (float x, float y, int pointer, int button) {
		for (Plugin p : plugins)
			if (p.touchDown(x, y, pointer, button)) break;
	}
}
