/*
 * Spine Runtimes Software License
 * Version 2.3
 *
 * Copyright (c) 2013-2015, Esoteric Software
 * All rights reserved.
 *
 * You are granted a perpetual, non-exclusive, non-sublicensable and
 * non-transferable license to use, install, execute and perform the Spine
 * Runtimes Software (the "Software") and derivative works solely for personal
 * or internal use. Without the written permission of Esoteric Software (see
 * Section 2 of the Spine Software License Agreement), you may not (a) modify,
 * translate, adapt or otherwise create derivative works, improvements of the
 * Software or develop new applications using the Software or (b) remove,
 * delete, alter or obscure any trademarks or any copyright, trademark, patent
 * or other intellectual property or proprietary rights notices on or in the
 * Software, including any copy thereof. Redistributions in binary or source
 * form must include this license and terms.
 *
 * THIS SOFTWARE IS PROVIDED BY ESOTERIC SOFTWARE "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL ESOTERIC SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.kotcrab.vis.plugin.spine.support.vis030;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.plugin.api.support.ComponentTransformer;
import com.kotcrab.vis.plugin.spine.runtime.SpineComponent;

/** @author Kotcrab */
public class SpineComponentTransformer extends ComponentTransformer<SpineComponent> {
	@Override
	public void transform (Entity entity, Array<Component> components, SpineComponent component) {
		ProtoVisSpine spine = new ProtoVisSpine();
		spine.flipX = component.isFlipX();
		spine.flipY = component.isFlipY();
		spine.defaultAnimation = component.getDefaultAnimation();
		spine.playOnStart = component.isPlayOnStart();
		components.add(spine);

		try {
			//halp please
			Object transform = Class.forName("com.kotcrab.vis.editor.converter.support.vis030.runtime.component.Transform").getConstructor().newInstance();
			transform.getClass().getMethod("setPosition", float.class, float.class).invoke(transform, component.getX(), component.getY());

			Object tint = Class.forName("com.kotcrab.vis.editor.converter.support.vis030.runtime.component.Tint").getConstructor().newInstance();
			tint.getClass().getMethod("setTint", Color.class).invoke(tint, component.getColor());

			components.add((Component) transform);
			components.add((Component) tint);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}
}
