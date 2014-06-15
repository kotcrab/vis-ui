///*******************************************************************************
// * Copyright 2014 Pawel Pastuszak
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// ******************************************************************************/
//
//package pl.kotcrab.vis.sceneeditor.component;
//
//import pl.kotcrab.vis.sceneeditor.ObjectRepresentation;
//import pl.kotcrab.vis.sceneeditor.Utils;
//
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
//import com.badlogic.gdx.math.Circle;
//import com.badlogic.gdx.math.Rectangle;
//import com.badlogic.gdx.utils.Array;
//
//public class Renderer {
//	private CameraController camController;
//	private RectangularSelection rectangularSelection;
//	private Array<ObjectRepresentation> objectRepresentationList;
//	private Array<ObjectRepresentation> selectedObjs;
//
//	private ShapeRenderer shapeRenderer;
//	private GUI gui;
//
//	public Renderer (CameraController camController, KeyboardInputMode keyboardInputMode,
//		RectangularSelection rectangularSelection, Array<ObjectRepresentation> objectRepresentationList,
//		Array<ObjectRepresentation> selectedObjs) {
//		this.camController = camController;
//		this.rectangularSelection = rectangularSelection;
//		this.objectRepresentationList = objectRepresentationList;
//		this.selectedObjs = selectedObjs;
//
//		shapeRenderer = new ShapeRenderer();
//
//		gui = new GUI(keyboardInputMode, selectedObjs);
//	}
//
//	public void render (boolean cameraLocked) {
//		shapeRenderer.setProjectionMatrix(camController.getCamera().combined);
//		shapeRenderer.begin(ShapeType.Line);
//
//		for (ObjectRepresentation orep : objectRepresentationList) {
//			if (orep.isMovingSupported())
//				shapeRenderer.setColor(Color.WHITE);
//			else
//				shapeRenderer.setColor(Color.GRAY);
//
//			renderRectangle(orep.getBoundingRectangle());
//
//			if (orep.isScallingSupported()) {
//				if (orep.isPointerInsideScaleArea())
//					shapeRenderer.setColor(Color.RED);
//				else
//					shapeRenderer.setColor(Color.WHITE);
//
//				renderObjectScaleBox(orep);
//			}
//		}
//
//		for (ObjectRepresentation orep : selectedObjs) {
//			shapeRenderer.setColor(Color.RED);
//
//			renderObjectOutline(orep);
//
//			if (orep.isScallingSupported()) {
//				if (orep.isPointerInsideScaleArea())
//					shapeRenderer.setColor(Color.RED);
//				else
//					shapeRenderer.setColor(Color.WHITE);
//
//				renderObjectScaleBox(orep);
//			}
//
//			if (orep.isRotatingSupported()) {
//				if (orep.isPointerInsideRotateArea())
//					shapeRenderer.setColor(Color.RED);
//				else
//					shapeRenderer.setColor(Color.WHITE);
//
//				renderObjectRotateCricle(orep);
//			}
//
//		}
//
//		if (camController.isCameraDirty()) {
//			if (cameraLocked)
//				shapeRenderer.setColor(Color.RED);
//			else
//				shapeRenderer.setColor(Color.GREEN);
//			renderRectangle(camController.getOrginalCameraRectangle());
//		}
//
//		shapeRenderer.end();
//		rectangularSelection.render(shapeRenderer);
//	}
//
//	public void renderGUI (boolean cameraLocked, boolean dirty, boolean exitingEditMode) {
//		gui.render(objectRepresentationList.size, cameraLocked, dirty, exitingEditMode);
//	}
//
//	private void renderObjectOutline (ObjectRepresentation orep) {
//		renderRectangle(orep.getBoundingRectangle());
//	}
//
//	private void renderObjectScaleBox (ObjectRepresentation orep) {
//		renderRectangle(Utils.buildRectangeForScaleArea(orep));
//	}
//
//	private void renderObjectRotateCricle (ObjectRepresentation orep) {
//		renderCircle(Utils.buildCirlcleForRotateArea(orep));
//	}
//
//	private void renderRectangle (Rectangle rect) {
//		shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
//	}
//
//	private void renderCircle (Circle cir) {
//		shapeRenderer.circle(cir.x, cir.y, cir.radius);
//	}
//
//	public void resize () {
//		gui.resize();
//	}
//
//	public void dispose () {
//		gui.dispose();
//		shapeRenderer.dispose();
//	}
//}
