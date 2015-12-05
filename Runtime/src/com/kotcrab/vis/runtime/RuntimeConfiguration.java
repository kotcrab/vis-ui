/*
 * Copyright 2014-2015 See AUTHORS file.
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

package com.kotcrab.vis.runtime;

import com.artemis.Entity;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.VisGroup;
import com.kotcrab.vis.runtime.component.PhysicsBody;
import com.kotcrab.vis.runtime.system.VisGroupManager;
import com.kotcrab.vis.runtime.system.physics.PhysicsSpriteUpdateSystem;
import com.kotcrab.vis.runtime.util.EntityEngine;

/**
 * Holds runtime configurations values
 * @author Kotcrab
 */
public class RuntimeConfiguration {
	/**
	 * Controls whether to store {@link AssetReference} in {@link Entity} after inflating it. Set this to false if you
	 * need to access {@link AssetReference} during runtime. Default is true. Certain inflaters may ignore this setting
	 * if asset is still always required later for example to render entity.
	 */
	public boolean removeAssetsComponentAfterInflating = true;

	/**
	 * Controls whether to add {@link VisGroupManager} into Artemis. Set this to false if you don't need to retrieve
	 * groups from VisEditor by id or by string id. Default is true. Even if false {@link VisGroup} (which
	 * stores all groups int ids) is not removed so it can be accessed if needed.
	 */
	public boolean useVisGroupManager = true;

	/**
	 * Controls whether to use box2d debug renderer that draw outlines of bodies, useful for debbuging. If true debug
	 * renderer is used. Default is false. This settings has no effect if physics is disabled.
	 */
	public boolean useBox2dDebugRenderer = false;

	/**
	 * Controls whether to use {@link PhysicsSpriteUpdateSystem} to automatically update sprite state depending on
	 * box2d state. If false this system won't be added into entity engine. Default is true. This settings has no
	 * effect if physics is disabled.
	 */
	public boolean useBox2dSpriteUpdateSystem = true;

	/**
	 * If true body stored in {@link PhysicsBody} will be automatically disposed when entity was removed from entity
	 * engine (for example after calling {@link EntityEngine#deleteEntity(Entity)} or {@link Entity#deleteFromWorld()}).
	 * Note that actual body will be disposed during next {@link EntityEngine} update.
	 */
	public boolean autoDisposeBox2dBodyOnEntityRemove = true;
}
