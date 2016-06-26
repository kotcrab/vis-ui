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

package com.kotcrab.vis.plugin.spine;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.module.scene.AssetsLoadingMonitorModule;
import com.kotcrab.vis.plugin.spine.components.SpinePreview;
import com.kotcrab.vis.plugin.spine.runtime.ProtoVisSpine;
import com.kotcrab.vis.plugin.spine.runtime.VisSpine;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.system.inflater.InflaterSystem;

/** @author Kotcrab */
public class EditorSpineInflaterSystem extends InflaterSystem {
	private SpineCacheModule spineCache;
	private AssetsLoadingMonitorModule loadingMonitor;

	private ComponentMapper<AssetReference> assetCm;
	private ComponentMapper<VisSpine> spineCm;
	private ComponentMapper<ProtoVisSpine> protoCm;
	private ComponentMapper<SpinePreview> previewCm;

	public EditorSpineInflaterSystem () {
		super(Aspect.all(ProtoVisSpine.class, AssetReference.class));
	}

	@Override
	public void inserted (int entityId) {
		AssetReference assetRef = assetCm.get(entityId);
		ProtoVisSpine protoComponent = protoCm.get(entityId);

		try {
			VisSpine spine = new VisSpine(spineCache.get(assetRef.asset));

			spine.setFlip(protoComponent.flipX, protoComponent.flipY);

			spine.setPlayOnStart(protoComponent.playOnStart);
			spine.setDefaultAnimation(protoComponent.defaultAnimation);

			spine.updateDefaultAnimations();

			SpinePreview spinePreview = previewCm.get(entityId);
			spinePreview.updateAnimation = true;

			world.getEntity(entityId).edit().add(spine);
		} catch (GdxRuntimeException e) {
			Log.exception(e);
			loadingMonitor.addFailedResource(assetRef.asset, e);
		}

		protoCm.remove(entityId);
	}
}

