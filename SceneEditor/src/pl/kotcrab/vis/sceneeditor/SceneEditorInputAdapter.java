
package pl.kotcrab.vis.sceneeditor;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

class SceneEditorInputAdapater extends InputAdapter implements GestureListener {
	@Override
	public boolean touchDown (float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap (float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress (float x, float y) {
		return false;
	}

	@Override
	public boolean fling (float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan (float x, float y, float deltaX, float deltaY) {
		return false;
	}

	@Override
	public boolean panStop (float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean zoom (float initialDistance, float distance) {
		return false;
	}

	@Override
	public boolean pinch (Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}
}
