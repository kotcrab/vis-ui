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

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.assets.AssetDescriptorProvider;
import com.kotcrab.vis.editor.module.project.AssetsMetadataModule;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.plugin.spine.runtime.SpineAssetDescriptor;
import com.kotcrab.vis.runtime.plugin.VisPlugin;

@VisPlugin
public class SpineAssetDescriptorProvider implements AssetDescriptorProvider<SpineAssetDescriptor> {
	@Override
	public SpineAssetDescriptor provide (AssetsMetadataModule metadata, FileHandle file, String relativePath) {
		if (metadata.isDirectoryMarkedAs(file, SpineAssetType.DIRECTORY_SPINE) == false) return null;

		if (relativePath.endsWith("atlas")) {
			String skelPath = findSkelPath(file, relativePath);
			if (skelPath == null) return null; //matching skeleton does not exist
			return new SpineAssetDescriptor(relativePath, skelPath, -1); //scale is ignored when comparing
		}

		if (relativePath.endsWith("skel") || relativePath.endsWith("json")) {
			if (FileUtils.siblingExists(file, "atlas")) {
				return new SpineAssetDescriptor(FileUtils.replaceExtension(relativePath, "atlas"), relativePath, -1); //scale is ignored when comparing
			}
		}

		return null;
	}

	@Override
	public SpineAssetDescriptor parametrize (SpineAssetDescriptor rawAsset, SpineAssetDescriptor other) {
		return new SpineAssetDescriptor(rawAsset.getAtlasPath(), rawAsset.getSkeletonPath(), other.getScale());
	}

	private String findSkelPath (FileHandle file, String relativePath) {
		if (FileUtils.siblingExists(file, "json")) return FileUtils.replaceExtension(relativePath, "json");
		if (FileUtils.siblingExists(file, "skel")) return FileUtils.replaceExtension(relativePath, "skel");
		return null;
	}
}
