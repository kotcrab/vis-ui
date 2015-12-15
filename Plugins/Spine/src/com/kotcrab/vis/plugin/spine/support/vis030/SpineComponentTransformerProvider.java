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

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.VersionCodes;
import com.kotcrab.vis.editor.plugin.api.ComponentTransformerProvider;
import com.kotcrab.vis.editor.plugin.api.support.ComponentTransformer;
import com.kotcrab.vis.editor.plugin.api.support.ConditionalComponentTransformer;
import com.kotcrab.vis.editor.plugin.api.support.RemapTransformer;
import com.kotcrab.vis.plugin.spine.SpineBoundsComponent;
import com.kotcrab.vis.plugin.spine.SpinePreviewComponent;
import com.kotcrab.vis.plugin.spine.SpineScaleComponent;
import com.kotcrab.vis.plugin.spine.runtime.SpineComponent;
import com.kotcrab.vis.runtime.plugin.VisPlugin;

/** @author Kotcrab */
@VisPlugin
public class SpineComponentTransformerProvider implements ComponentTransformerProvider {
	@Override
	public void registerTransformers (ObjectMap<Class, ComponentTransformer> transformers) {
		transformers.put(SpineScaleComponent.class, new RemapTransformer(SpineScale.class));
		transformers.put(SpineBoundsComponent.class, new RemapTransformer(SpineBounds.class));
		transformers.put(SpinePreviewComponent.class, new RemapTransformer(SpinePreview.class));
		transformers.put(SpineComponent.class, new SpineComponentTransformer());
	}

	@Override
	public void registerConditionalTransformers (Array<ConditionalComponentTransformer> condTransformers) {

	}

	@Override
	public void registerClassMaps (ObjectMap<Class, Class> classMap) {
		classMap.put(SpinePreviewComponent.class, SpinePreview.class);
		classMap.put(SpineScaleComponent.class, SpineScale.class);
		classMap.put(SpineBoundsComponent.class, SpineBounds.class);
	}

	@Override
	public int getSourceProjectVersions () {
		return VersionCodes.EDITOR_026;
	}

	@Override
	public int getTargetProjectVersions () {
		return VersionCodes.EDITOR_030;
	}
}
