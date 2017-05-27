/*
 * Copyright 2014-2017 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.vis.ui.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;

/**
 * {@link Actor} related utils.
 * @author Kotcrab
 */
public class ActorUtils {
	/**
	 * Makes sures that actor will be fully visible in stage. If it's necessary actor position will be changed to fit it
	 * on screen.
	 * @throws IllegalStateException if actor does not belong to any stage.
	 */
	public static void keepWithinStage (Actor actor) {
		Stage stage = actor.getStage();
		if (stage == null) {
			throw new IllegalStateException("keepWithinStage cannot be used on Actor that doesn't belong to any stage. ");
		}
		keepWithinStage(actor.getStage(), actor);
	}

	/**
	 * Makes sures that actor will be fully visible in stage. If it's necessary actor position will be changed to fit it
	 * on screen.
	 */
	public static void keepWithinStage (Stage stage, Actor actor) {
		//taken from scene2d.ui Window
		Camera camera = stage.getCamera();
		if (camera instanceof OrthographicCamera) {
			OrthographicCamera orthographicCamera = (OrthographicCamera) camera;
			float parentWidth = stage.getWidth();
			float parentHeight = stage.getHeight();
			if (actor.getX(Align.right) - camera.position.x > parentWidth / 2 / orthographicCamera.zoom)
				actor.setPosition(camera.position.x + parentWidth / 2 / orthographicCamera.zoom, actor.getY(Align.right), Align.right);
			if (actor.getX(Align.left) - camera.position.x < -parentWidth / 2 / orthographicCamera.zoom)
				actor.setPosition(camera.position.x - parentWidth / 2 / orthographicCamera.zoom, actor.getY(Align.left), Align.left);
			if (actor.getY(Align.top) - camera.position.y > parentHeight / 2 / orthographicCamera.zoom)
				actor.setPosition(actor.getX(Align.top), camera.position.y + parentHeight / 2 / orthographicCamera.zoom, Align.top);
			if (actor.getY(Align.bottom) - camera.position.y < -parentHeight / 2 / orthographicCamera.zoom)
				actor.setPosition(actor.getX(Align.bottom), camera.position.y - parentHeight / 2 / orthographicCamera.zoom, Align.bottom);
		} else if (actor.getParent() == stage.getRoot()) {
			float parentWidth = stage.getWidth();
			float parentHeight = stage.getHeight();
			if (actor.getX() < 0) actor.setX(0);
			if (actor.getRight() > parentWidth) actor.setX(parentWidth - actor.getWidth());
			if (actor.getY() < 0) actor.setY(0);
			if (actor.getTop() > parentHeight) actor.setY(parentHeight - actor.getHeight());
		}
	}
}
