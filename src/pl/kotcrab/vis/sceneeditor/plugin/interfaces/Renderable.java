/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/

package pl.kotcrab.vis.sceneeditor.plugin.interfaces;

import pl.kotcrab.vis.sceneeditor.plugin.impl.RendererPlugin;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** Plugins that want to render someting via ShapeRenerer should implement this interface and call
 * {@link RendererPlugin#addRenderable(Renderable)} */
public interface Renderable {
	public void renderSelf (ShapeRenderer shapeRenderer);
}
