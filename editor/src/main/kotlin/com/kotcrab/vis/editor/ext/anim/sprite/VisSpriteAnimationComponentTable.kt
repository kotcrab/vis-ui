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

import com.kotcrab.vis.editor.Icons
import com.kotcrab.vis.editor.entity.AnimationPreviewComponent
import com.kotcrab.vis.editor.module.ModuleInjector
import com.kotcrab.vis.editor.module.project.TextureCacheModule
import com.kotcrab.vis.editor.module.scene.system.VisComponentManipulator
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.AutoComponentTable
import com.kotcrab.vis.editor.util.addTooltip
import com.kotcrab.vis.editor.util.changed
import com.kotcrab.vis.editor.util.value.BooleanEntityValue
import com.kotcrab.vis.editor.util.vis.EntityUtils
import com.kotcrab.vis.editor.util.widgetRow
import com.kotcrab.vis.runtime.assets.AtlasRegionAsset
import com.kotcrab.vis.runtime.component.AssetReference
import com.kotcrab.vis.runtime.component.VisSpriteAnimation
import com.kotcrab.vis.ui.widget.*

/** @author Kotcrab */
class VisSpriteAnimationComponentTable(sceneMC: ModuleInjector?) : AutoComponentTable<VisSpriteAnimation>(sceneMC, VisSpriteAnimation::class.java, true) {
    private lateinit var textureCache: TextureCacheModule
    private lateinit var componentManipulator: VisComponentManipulator

    private val animSelectBox = VisSelectBox<String>()

    private lateinit var problemsTable: VisTable
    private lateinit var problemTooltip: Tooltip
    private val previewCheckbox = IndeterminateCheckbox("Preview in editor")

    override fun init() {
        super.init()

        animSelectBox.selection.setProgrammaticChangeEvents(false)
        animSelectBox.addListener(properties.sharedSelectBoxChangeListener)

        widgetRow(VisLabel("Animation"), animSelectBox)
        widgetRow(previewCheckbox)
        problemsTable = widgetRow(VisImage(Icons.WARNING.drawable()), VisLabel("Problems detected! (hover)"), managePreviousActorSpacing = true).actor

        problemTooltip = problemsTable.addTooltip("Unknown problem.")
        problemsTable.isVisible = false

        properties.setupStdCheckbox(previewCheckbox)
        previewCheckbox.changed {
            properties.selectedEntities.forEach {
                if (previewCheckbox.isChecked) {
                    componentManipulator.createComponent(it.entity, AnimationPreviewComponent::class.java)
                } else {
                    componentManipulator.removeComponent(it.entity, AnimationPreviewComponent::class.java)
                }
            }
        }
    }

    override fun updateUIValues() {
        super.updateUIValues()

        problemsTable.isVisible = false

        val proxies = properties.selectedEntities
        if (proxies.size() == 1) {
            val proxy = proxies.first()

            val asset = proxy.getComponent(AssetReference::class.java)
            if (asset == null) {
                problemExist("Entity is missing asset component.")
                return
            }
            val assetDes = asset.asset

            if (assetDes !is AtlasRegionAsset) {
                problemExist("Animation component can be only added to entity using texture atlas as texture source.")
                return
            }

            val atlas = textureCache.getAtlas(assetDes)

            if (atlas == null) {
                problemExist("Failed to load texture atlas, you might need to restart VisEditor.")
                return
            }

            val spriteAnim = proxy.getComponent(VisSpriteAnimation::class.java)!!

            animSelectBox.isDisabled = false
            animSelectBox.setItems(*textureCache.getSpriteSheetHelper(atlas).animationsList.toArray(String::class.java))
            animSelectBox.selected = spriteAnim.animationName

            if (animSelectBox.items.size == 0) {
                problemExist("No animations were found in texture atlas. Ensure that you have enabled `indexes` option when packaging atlas.")
            }

            problemsTable.invalidateHierarchy()
        } else {
            problemExist("Please select single entity to edit active animation.")
            return
        }

        EntityUtils.setCommonCheckBoxState(proxies, previewCheckbox, BooleanEntityValue { it.getComponent(AnimationPreviewComponent::class.java) != null })
    }

    override fun setValuesToEntities() {
        properties.selectedEntities.forEach { it.getComponent(VisSpriteAnimation::class.java).animationName = animSelectBox.selected }
        super.setValuesToEntities()
    }

    private fun problemExist(reason: String) {
        animSelectBox.isDisabled = true
        animSelectBox.setItems("<?>")
        problemsTable.isVisible = true
        problemTooltip.setText(reason)
    }
}
