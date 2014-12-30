
package pl.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class RendererModule extends SceneModule {
	private ShapeRenderer shapeRenderer;
	private CameraModule camera;

	private int cellSize = 256;

	@Override
	public void init () {
		camera = sceneContainter.get(CameraModule.class);

		shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void render (Batch batch) {
		batch.end();

		shapeRenderer.setProjectionMatrix(camera.getCombinedMatrix());
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(new Color(0.32f, 0.32f, 0.32f, 1f));

		drawVerticalLines();
		drawHorizontalLines();

		shapeRenderer.end();
		batch.begin();
	}

	private void drawVerticalLines () {
		float xStart = camera.getX() - camera.getWidth() / 2;
		float xEnd = xStart + camera.getWidth();

		int leftDownY = (int)(camera.getY() - camera.getHeight() / 2);
		int linesToDraw = (int)(camera.getHeight() / cellSize) + 1;

		int drawingPointStart = leftDownY / cellSize;
		int drawingPonintEnd = drawingPointStart + linesToDraw;

		for (int i = drawingPointStart; i < drawingPonintEnd; i++)
			shapeRenderer.line(xStart, i * cellSize, xEnd, i * cellSize);
	}

	private void drawHorizontalLines () {
		float yStart = camera.getY() - camera.getHeight() / 2;
		float yEnd = yStart + camera.getHeight();

		int leftDownX = (int)(camera.getX() - camera.getWidth() / 2);
		int linesToDraw = (int)(camera.getWidth() / cellSize) + 1;

		int drawingPointStart = leftDownX / cellSize;
		int drawingPonintEnd = drawingPointStart + linesToDraw;

		for (int i = drawingPointStart; i < drawingPonintEnd; i++)
			shapeRenderer.line(i * cellSize, yStart, i * cellSize, yEnd);
	}

	@Override
	public void dispose () {
		shapeRenderer.dispose();
	}
}
