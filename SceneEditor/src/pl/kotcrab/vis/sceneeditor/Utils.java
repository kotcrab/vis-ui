
package pl.kotcrab.vis.sceneeditor;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

@SuppressWarnings({"rawtypes", "unchecked"})
class Utils {

	public static Rectangle buildRectangeForScaleBox (SceneEditorSupport sup, Object obj) {
		Rectangle rect = sup.getBoundingRectangle(obj);
		return new Rectangle(rect.x + rect.width - 15, rect.y + rect.height - 15, 15, 15);
	}

	public static Circle buildCirlcleForRotateCircle (SceneEditorSupport sup, Object obj) {
		Rectangle rect = sup.getBoundingRectangle(obj);

		int cWidth = 5;

		return new Circle(rect.x + rect.width / 2 + cWidth, rect.y + rect.height + cWidth, cWidth);
	}
}
