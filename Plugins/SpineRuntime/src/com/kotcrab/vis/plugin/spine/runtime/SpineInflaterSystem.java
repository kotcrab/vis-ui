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

package com.kotcrab.vis.plugin.spine.runtime;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.esotericsoftware.spine.SkeletonData;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.component.AssetComponent;

/** @author Kotcrab */
@Wire
public class SpineInflaterSystem extends EntityProcessingSystem {
	private ComponentMapper<AssetComponent> assetCm;
	private ComponentMapper<SpineProtoComponent> protoCm;

	private EntityTransmuter transmuter;

	private RuntimeConfiguration configuration;
	private AssetManager manager;

	public SpineInflaterSystem (RuntimeConfiguration configuration, AssetManager manager) {
		super(Aspect.all(SpineProtoComponent.class, AssetComponent.class));
		this.configuration = configuration;
		this.manager = manager;
	}

	@Override
	protected void initialize () {
		EntityTransmuterFactory factory = new EntityTransmuterFactory(world).remove(SpineProtoComponent.class);
		if (configuration.removeAssetsComponentAfterInflating) factory.remove(AssetComponent.class);
		transmuter = factory.build();
	}

	@Override
	protected void process (Entity e) {
		AssetComponent assetComponent = assetCm.get(e);
		SpineProtoComponent protoComponent = protoCm.get(e);

		SpineAssetDescriptor asset = (SpineAssetDescriptor) assetComponent.asset;

		SkeletonData skeleton = manager.get(asset.getSkeletonPath(), SkeletonData.class);
		SpineComponent spineComponent = new SpineComponent(skeleton);

		spineComponent.setPosition(protoComponent.x, protoComponent.y);
		spineComponent.setFlip(protoComponent.flipX, protoComponent.flipY);

		spineComponent.setPlayOnStart(protoComponent.playOnStart);
		spineComponent.setDefaultAnimation(protoComponent.defaultAnimation);

		spineComponent.updateDefaultAnimations();

		transmuter.transmute(e);
		e.edit().add(spineComponent);
	}
}

