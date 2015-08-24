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

import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kotcrab.vis.editor.plugin.PluginKryoSerializer;
import com.kotcrab.vis.editor.serializer.EntityComponentSerializer;
import com.kotcrab.vis.plugin.spine.runtime.SpineComponent;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetComponent;

public class SpineComponentSerializer extends EntityComponentSerializer<SpineComponent> implements PluginKryoSerializer {
	private static final int VERSION_CODE = 1;

	private SpineCacheModule spineCache;

	public SpineComponentSerializer (Kryo kryo, SpineCacheModule spineCache) {
		super(kryo, SpineComponent.class);
		this.spineCache = spineCache;
	}

	@Override
	public void write (Kryo kryo, Output output, SpineComponent object) {
		super.write(kryo, output, object);
		parentWrite(kryo, output, object);

		output.writeInt(VERSION_CODE);

		kryo.writeClassAndObject(output, getComponent(AssetComponent.class).asset);

		output.writeFloat(object.getX());
		output.writeFloat(object.getY());

		output.writeBoolean(object.isFlipX());
		output.writeBoolean(object.isFlipY());

		kryo.writeObject(output, object.getColor());
	}

	@Override
	public SpineComponent read (Kryo kryo, Input input, Class<SpineComponent> type) {
		super.read(kryo, input, type);
		SpineComponent object = parentRead(kryo, input, type);

		input.readInt(); //version code

		VisAssetDescriptor asset = (VisAssetDescriptor) kryo.readClassAndObject(input);

		object.onDeserialize(spineCache.get(asset));
		object.setPosition(input.readFloat(), input.readFloat());
		object.setFlip(input.readBoolean(), input.readBoolean());
		object.setColor(kryo.readObject(input, Color.class));
		return object;
	}

	@Override
	public SpineComponent copy (Kryo kryo, SpineComponent original) {
		super.copy(kryo, original);
		return new SpineComponent(original, spineCache.get(getComponent(AssetComponent.class).asset));
	}

	@Override
	public Class<SpineComponent> getSerializedClassType () {
		return SpineComponent.class;
	}
}

