/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.scene;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.editor.util.gdx.SpriteUtils;
import com.kotcrab.vis.runtime.entity.Entity;
import com.kotcrab.vis.runtime.entity.EntityGroup;

public class ObjectGroup extends EntityGroup implements EditorObject {
	private boolean preserveForRuntime;

	private Rectangle bounds;

	public ObjectGroup () {
		super(null);
		bounds = new Rectangle();
	}

	private void calcBounds () {
		if (entities.size > 0) {
			bounds.set(((EditorObject) entities.get(0)).getBoundingRectangle());

			for (Entity entity : entities) {
				EditorObject obj = (EditorObject) entity;
				bounds.merge(obj.getBoundingRectangle());
			}
		} else
			bounds.set(0, 0, 0, 0);
	}

	public boolean isPreserveOnRuntime () {
		return preserveForRuntime;
	}

	public void setPreserveForRuntime (boolean preserveForRuntime) {
		this.preserveForRuntime = preserveForRuntime;
	}

	@Override
	public void addEntity (Entity entity) {
		if (entity instanceof EditorObject == false)
			throw new IllegalArgumentException("ObjectGroup entities must be instance of EditorObject");

		super.addEntity(entity);
		calcBounds();
	}

	@Override
	public void removeEntity (Entity entity) {
		super.removeEntity(entity);
		calcBounds();
	}

	public void addEntities (Array entites) {
		entities.addAll(entites);
		calcBounds();
	}

	@Override
	public float getWidth () {
		return bounds.getWidth();
	}

	@Override
	public float getHeight () {
		return bounds.getHeight();
	}

	@Override
	public Rectangle getBoundingRectangle () {
		return bounds;
	}

	@Override
	public void setPosition (float x, float y) {
		setX(x);
		setY(y);
	}

	@Override
	public float getX () {
		return bounds.x;
	}

	@Override
	public void setX (float x) {
		float delta = x - bounds.x;

		bounds.x = x;

		for (Entity entity : entities)
			translateX((EditorObject) entity, delta);
	}

	@Override
	public float getY () {
		return bounds.y;
	}

	@Override
	public void setY (float y) {
		float delta = y - bounds.y;

		bounds.y = y;

		for (Entity entity : entities)
			translateY((EditorObject) entity, delta);
	}

	private void translateX (EditorObject object, float x) {
		object.setX(object.getX() + x);
	}

	private void translateY (EditorObject object, float y) {
		object.setY(object.getY() + y);
	}

	public Array<EditorObject> getObjects () {
		Array<EditorObject> list = new Array<>(entities.size);

		for (Entity entity : entities)
			list.add((EditorObject) entity);

		return list;
	}

	public void setEntities (Array<Entity> entities) {
		this.entities = entities;
	}

	public void reloadTextures (TextureCacheModule cacheModule) {
		for (Entity entity : entities) {
			if (entity instanceof ObjectGroup) {
				ObjectGroup objectGroup = (ObjectGroup) entity;
				objectGroup.reloadTextures(cacheModule);
			}

			if (entity instanceof SpriteObject) {
				SpriteObject spriteObject = (SpriteObject) entity;
				SpriteUtils.setRegion(spriteObject.getSprite(), cacheModule.getRegion(spriteObject.getAssetPath()));
			}
		}
	}
}
