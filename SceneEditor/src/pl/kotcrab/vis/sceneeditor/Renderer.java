package pl.kotcrab.vis.sceneeditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
class Renderer
{
	private SceneEditor editor;
	private CameraController camController;
	private ObjectMap<String, Object> objectMap;
	
	private ShapeRenderer shapeRenderer;
	private GUI gui;
	
	public Renderer(SceneEditor editor, CameraController camController, ObjectMap<String, Object> objectMap)
	{
		this.editor = editor;
		this.camController = camController;
		this.objectMap = objectMap;
		
		gui = new GUI(editor);
		shapeRenderer = new ShapeRenderer();
	}
	
	public void render(boolean cameraLocked, Object selectedObj, boolean pointerInsideRotateCircle, boolean pointerInsideScaleBox)
	{
		shapeRenderer.setProjectionMatrix(camController.getCamera().combined);
		shapeRenderer.begin(ShapeType.Line);

		for (Entry<String, Object> entry : objectMap.entries()) {
			Object obj = entry.value;

			SceneEditorSupport sup = editor.getSupportForClass(obj.getClass());

			if (sup.isMovingSupported())
				shapeRenderer.setColor(Color.WHITE);
			else
				shapeRenderer.setColor(Color.GRAY);

			renderObjectOutline(sup, obj);

			if (sup.isScallingSupported()) {
				if (obj == selectedObj && pointerInsideScaleBox)
					shapeRenderer.setColor(Color.RED);
				else
					shapeRenderer.setColor(Color.WHITE);

				renderObjectScaleBox(sup, obj);
			}
		}

		if (selectedObj != null) {
			SceneEditorSupport sup = editor.getSupportForClass(selectedObj.getClass());
			shapeRenderer.setColor(Color.RED);

			renderObjectOutline(sup, selectedObj);

			if (sup.isScallingSupported()) {
				if (pointerInsideScaleBox)
					shapeRenderer.setColor(Color.RED);
				else
					shapeRenderer.setColor(Color.WHITE);

				renderObjectScaleBox(sup, selectedObj);
			}

			if (sup.isRotatingSupported()) {
				if (pointerInsideRotateCircle)
					shapeRenderer.setColor(Color.RED);
				else
					shapeRenderer.setColor(Color.WHITE);

				renderObjectRotateCricle(sup, selectedObj);
			}

		}

		if (camController.isCameraDirty()) {
			if (cameraLocked)
				shapeRenderer.setColor(Color.RED);
			else
				shapeRenderer.setColor(Color.GREEN);
			renderRectangle(camController.getOrginalCameraRectangle());
		}

		shapeRenderer.end();
	}
	
	public void renderGUI(int entityNumber, boolean cameraLocked, boolean dirty, Object selectedObj)
	{
		gui.render(entityNumber, cameraLocked, dirty, selectedObj);
	}
	
	private void renderObjectOutline (SceneEditorSupport sup, Object obj) {
		renderRectangle(sup.getBoundingRectangle(obj));
	}

	private void renderObjectScaleBox (SceneEditorSupport sup, Object obj) {
		renderRectangle(Utils.buildRectangeForScaleBox(sup, obj));
	}

	private void renderObjectRotateCricle (SceneEditorSupport sup, Object obj) {
		renderCircle(Utils.buildCirlcleForRotateCircle(sup, obj));
	}

	private void renderRectangle (Rectangle rect) {
		shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
	}

	private void renderCircle (Circle cir) {
		shapeRenderer.circle(cir.x, cir.y, cir.radius);
	}
	
	public void resize()
	{
		gui.resize();
	}
	
	public void dispose()
	{
		gui.dispose();
		shapeRenderer.dispose();
	}
}