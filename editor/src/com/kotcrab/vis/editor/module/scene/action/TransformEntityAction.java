/*
 * Copyright 2014-2016 See AUTHORS file.
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

package com.kotcrab.vis.editor.module.scene.action;

import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.util.undo.UndoableAction;

/** @author Kotcrab */
public class TransformEntityAction implements UndoableAction {
	private EntityTransformData oldData = new EntityTransformData();
	private EntityTransformData newData = new EntityTransformData();
	private EntityProxy entity;

	public TransformEntityAction (EntityProxy entity) {
		this.entity = entity;
		oldData.saveFrom(entity);
	}

	public void saveNewData (EntityProxy entity) {
		newData.saveFrom(entity);
	}

	@Override
	public void execute () {
		entity.reload();
		newData.loadTo(entity);
	}

	@Override
	public void undo () {
		entity.reload();
		oldData.loadTo(entity);
	}

	@Override
	public String getActionName () {
		return "Entity Move";
	}

	private static class EntityTransformData {
		public float x;
		public float y;
		public float rotation;
		public float scaleX;
		public float scaleY;

		public void saveFrom (EntityProxy entity) {
			x = entity.getX();
			y = entity.getY();
			rotation = entity.getRotation();
			scaleX = entity.getScaleX();
			scaleY = entity.getScaleY();
		}

		public void loadTo (EntityProxy entity) {
			entity.setPosition(x, y);
			entity.setRotation(rotation);
			entity.setScale(scaleX, scaleY);
		}
	}

}

