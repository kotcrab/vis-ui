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

package com.kotcrab.vis.editor.ext.anim.sprite

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.g2d.Animation
import com.kotcrab.vis.editor.entity.AnimationPreviewComponent
import com.kotcrab.vis.editor.module.project.TextureCacheModule
import com.kotcrab.vis.runtime.assets.AtlasRegionAsset
import com.kotcrab.vis.runtime.component.AssetReference
import com.kotcrab.vis.runtime.component.Invisible
import com.kotcrab.vis.runtime.component.VisSprite
import com.kotcrab.vis.runtime.component.VisSpriteAnimation
import com.kotcrab.vis.runtime.system.delegate.DeferredEntityProcessingSystem
import com.kotcrab.vis.runtime.system.delegate.EntityProcessPrincipal

/** @author Kotcrab */
class EditorSpriteAnimationUpdateSystem(principal: EntityProcessPrincipal, val pixelsPerUnit: Float) : DeferredEntityProcessingSystem(
        Aspect.all(AssetReference::class.java, VisSprite::class.java, VisSpriteAnimation::class.java).exclude(Invisible::class.java),
        principal) {
    private lateinit var assetCm: ComponentMapper<AssetReference>
    private lateinit var spriteCm: ComponentMapper<VisSprite>
    private lateinit var spriteAnimCm: ComponentMapper<VisSpriteAnimation>
    private lateinit var previewCm: ComponentMapper<AnimationPreviewComponent>

    private lateinit var textureCache: TextureCacheModule

    override fun process(entity: Int) {
        val assetRef = assetCm.get(entity)
        val spriteAnim = spriteAnimCm.get(entity)
        val sprite = spriteCm.get(entity)

        if (assetRef.asset !is AtlasRegionAsset || spriteAnim.animationName == null || sprite.region == null) {
            return
        }

        if (spriteAnim.isDirty) {
            val keyFrames = textureCache
                    .getSpriteSheetHelper(textureCache.getAtlas(assetRef.asset as AtlasRegionAsset))
                    .getAnimationRegions(spriteAnim.animationName)
            spriteAnim.setAnimation(Animation(spriteAnim.frameDuration, keyFrames))
        }

        if (previewCm.has(entity)) {
            spriteAnim.updateTimer(world.delta)
            val region = spriteAnim.getKeyFrame(true)
            if(region != null) sprite.setRegion(region, pixelsPerUnit)
        }
    }
}
