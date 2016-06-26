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

import com.artemis.Entity;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Animation;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.editor.ui.scene.entityproperties.NumberInputField;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.AutoComponentTable;
import com.kotcrab.vis.editor.util.scene2d.FieldUtils;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.plugin.spine.components.SpinePreview;
import com.kotcrab.vis.plugin.spine.components.SpineScale;
import com.kotcrab.vis.plugin.spine.runtime.VisSpine;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.HashSet;

/** @author Kotcrab */
public class VisSpineComponentTable extends AutoComponentTable<VisSpine> {
	private static final String NO_COMMON_ANIMATION = "<?>";

	private IndeterminateCheckbox preview;
	private NumberInputField scaleField;
	private VisSelectBox<String> animSelectBox;

	private Image warningImage;
	private Tooltip onlyCommonAnimationTooltip;
	private Tooltip noCommonAnimationTooltip;

	public VisSpineComponentTable (ModuleInjector injector) {
		super(injector, VisSpine.class, false);
	}

	@Override
	protected void init () {
		super.init();

		warningImage = new Image(Icons.WARNING.drawable());
		onlyCommonAnimationTooltip = new Tooltip.Builder("Only showing animations that are common for all selected objects!").build();
		noCommonAnimationTooltip = new Tooltip.Builder("There isn't any common animation for selected objects").build();

		preview = new IndeterminateCheckbox("Preview in editor");
		preview.addListener(properties.getSharedCheckBoxChangeListener());

		scaleField = new NumberInputField(properties.getSharedFocusListener(), properties.getSharedChangeListener());
		scaleField.addValidator(new GreaterThanValidator(0));

		VisTable scaleTable = new VisTable(true);
		scaleTable.add("Scale:");
		scaleTable.add(scaleField).width(EntityProperties.FIELD_WIDTH);

		animSelectBox = new VisSelectBox<>();
		animSelectBox.setItems("<none>");
		animSelectBox.getSelection().setProgrammaticChangeEvents(false);
		animSelectBox.addListener(properties.getSharedSelectBoxChangeListener());
		animSelectBox.addListener(new VisChangeListener((event, actor) -> createCommonAnimationsList()));

		VisTable animTable = new VisTable(true);
		animTable.add(new VisLabel("Animation"));
		animTable.add(animSelectBox);
		animTable.add(warningImage);

		add(preview).row();
		add(scaleTable).row();
		add(animTable).row();
	}

	@Override
	public void updateUIValues () {
		super.updateUIValues();

		ImmutableArray<EntityProxy> proxies = properties.getSelectedEntities();

		Tooltip.removeTooltip(warningImage);
		warningImage.setVisible(false);

		EntityUtils.setCommonCheckBoxState(proxies, preview, (Entity entity) -> entity.getComponent(SpinePreview.class).previewEnabled);

		createCommonAnimationsList();
		String commonAnimation = EntityUtils.getCommonString(proxies, NO_COMMON_ANIMATION, (Entity entity) -> entity.getComponent(VisSpine.class).getDefaultAnimation());

		if (commonAnimation.equals(NO_COMMON_ANIMATION)) {
			animSelectBox.getItems().add(NO_COMMON_ANIMATION);
			animSelectBox.setItems(animSelectBox.getItems().toArray());
			animSelectBox.setSelected(NO_COMMON_ANIMATION);
		} else
			animSelectBox.setSelected(commonAnimation);

		scaleField.setText(EntityUtils.getCommonFloatValue(proxies, (Entity entity) -> entity.getComponent(SpineScale.class).scale));
	}

	private void createCommonAnimationsList () {
		ImmutableArray<EntityProxy> proxies = properties.getSelectedEntities();

		int animationCounter = 0;
		HashSet<String> commonAnimations = new HashSet<>();
		Array<HashSet<String>> allAnimNames = new Array<>();

		for (EntityProxy proxy : proxies) {
			VisSpine visSpine = proxy.getComponent(VisSpine.class);
			Array<Animation> animations = visSpine.getSkeleton().getData().getAnimations();

			HashSet<String> animNames = new HashSet<>(animations.size);
			for (Animation anim : animations) {
				commonAnimations.add(anim.getName());
				animNames.add(anim.getName());
				animationCounter++;
			}

			allAnimNames.add(animNames);
		}

		for (HashSet<String> animationsNames : allAnimNames)
			commonAnimations.retainAll(animationsNames);

		if (commonAnimations.size() != animationCounter) {
			warningImage.setVisible(true);
			onlyCommonAnimationTooltip.setTarget(warningImage);
		}

		if (commonAnimations.size() == 0) {
			animSelectBox.setDisabled(true);
			animSelectBox.setItems(NO_COMMON_ANIMATION);

			warningImage.setVisible(true);
			onlyCommonAnimationTooltip.detach();
			noCommonAnimationTooltip.setTarget(warningImage);
		} else {
			animSelectBox.setDisabled(false);
			animSelectBox.setItems(commonAnimations.toArray(new String[commonAnimations.size()]));
		}
	}

	@Override
	public void setValuesToEntities () {
		super.setValuesToEntities();

		for (EntityProxy proxy : properties.getSelectedEntities()) {
			VisSpine visSpine = proxy.getComponent(VisSpine.class);
			SpinePreview previewComponent = proxy.getComponent(SpinePreview.class);
			SpineScale scaleComponent = proxy.getComponent(SpineScale.class);

			if (animSelectBox.getSelection().first().equals(NO_COMMON_ANIMATION) == false) {
				visSpine.setDefaultAnimation(animSelectBox.getSelection().first());
				previewComponent.updateAnimation = true;
			}

			if (preview.isIndeterminate() == false) {
				if (previewComponent.previewEnabled != preview.isChecked()) {
					previewComponent.updateAnimation = true;
					previewComponent.previewEnabled = preview.isChecked();
				}
			}

			float scale = FieldUtils.getFloat(scaleField, scaleComponent.scale);
			if (scale != scaleComponent.scale) {
				scaleComponent.scale = scale;
				scaleComponent.updateScale = true;
			}
		}

	}
}
