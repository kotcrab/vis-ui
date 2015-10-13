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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Animation;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.editor.ui.scene.entityproperties.NumberInputField;
import com.kotcrab.vis.editor.ui.scene.entityproperties.specifictable.SpecificUITable;
import com.kotcrab.vis.editor.util.scene2d.FieldUtils;
import com.kotcrab.vis.editor.util.scene2d.TableBuilder;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.plugin.spine.runtime.SpineComponent;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.HashSet;

public class SpineUITable extends SpecificUITable {
	private static final String NO_COMMON_ANIMATION = "<?>";

	private IndeterminateCheckbox playAnimationOnStart;
	private IndeterminateCheckbox preview;
	private VisSelectBox<String> animSelectBox;

	private NumberInputField scaleField;

	private Image warningImage;
	private Tooltip onlyCommonAnimationTooltip;
	private Tooltip noCommonAnimationTooltip;

	@Override
	protected void init () {
		preview = new IndeterminateCheckbox("Preview in editor");
		playAnimationOnStart = new IndeterminateCheckbox("Play animation on start");

		preview.addListener(properties.getSharedCheckBoxChangeListener());
		playAnimationOnStart.addListener(properties.getSharedCheckBoxChangeListener());

		scaleField = new NumberInputField(properties.getSharedFocusListener(), properties.getSharedChangeListener());
		scaleField.addValidator(new GreaterThanValidator(0));

		VisTable scaleTable = new VisTable(true);
		scaleTable.add("Scale:");
		scaleTable.add(scaleField).width(EntityProperties.FIELD_WIDTH);

		animSelectBox = new VisSelectBox<>();
		animSelectBox.setItems("<none>");
		animSelectBox.getSelection().setProgrammaticChangeEvents(false);
		animSelectBox.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				createCommonAnimationsList();
			}
		});
		animSelectBox.addListener(properties.getSharedSelectBoxChangeListener());

		warningImage = new Image(Icons.WARNING.drawable());

		onlyCommonAnimationTooltip = new Tooltip("Only showing animations that are common for all selected objects!");
		noCommonAnimationTooltip = new Tooltip("There isn't any common animation for selected objects");

		padTop(0);
		padLeft(3);
		left();
		defaults().left();
		add(preview).spaceBottom(0).row();
		add(playAnimationOnStart).spaceBottom(2).row();
		add(TableBuilder.build(new VisLabel("Animation:"), animSelectBox, warningImage)).row();
		add(scaleTable);
	}

	@Override
	public boolean isSupported (EntityProxy proxy) {
		return proxy.hasComponent(SpineComponent.class);
	}

	@Override
	public void updateUIValues () {
		Tooltip.removeTooltip(warningImage);
		warningImage.setVisible(false);

		Array<EntityProxy> proxies = properties.getProxies();

		EntityUtils.setCommonCheckBoxState(proxies, preview, (Entity entity) -> entity.getComponent(SpinePreviewComponent.class).previewEnabled);
		EntityUtils.setCommonCheckBoxState(proxies, playAnimationOnStart, (Entity entity) -> entity.getComponent(SpineComponent.class).isPlayOnStart());

		createCommonAnimationsList();
		String commonAnimation = EntityUtils.getCommonString(proxies, NO_COMMON_ANIMATION, (Entity entity) -> entity.getComponent(SpineComponent.class).getDefaultAnimation());

		if (commonAnimation.equals(NO_COMMON_ANIMATION)) {
			animSelectBox.getItems().add(NO_COMMON_ANIMATION);
			animSelectBox.setItems(animSelectBox.getItems().toArray());
			animSelectBox.setSelected(NO_COMMON_ANIMATION);
		} else
			animSelectBox.setSelected(commonAnimation);

		scaleField.setText(EntityUtils.getEntitiesCommonFloatValue(proxies, (Entity entity) -> entity.getComponent(SpineScaleComponent.class).scale));
	}

	private void createCommonAnimationsList () {
		Array<EntityProxy> proxies = properties.getProxies();

		int animationCounter = 0;
		HashSet<String> commonAnimations = new HashSet<>();
		Array<HashSet<String>> allAnimNames = new Array<>();

		for (EntityProxy proxy : proxies) {
			for (Entity entity : proxy.getEntities()) {
				SpineComponent spineComponent = entity.getComponent(SpineComponent.class);
				Array<Animation> animations = spineComponent.getSkeleton().getData().getAnimations();

				HashSet<String> animNames = new HashSet<>(animations.size);
				for (Animation anim : animations) {
					commonAnimations.add(anim.getName());
					animNames.add(anim.getName());
					animationCounter++;
				}

				allAnimNames.add(animNames);
			}
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
		for (EntityProxy proxy : properties.getProxies()) {
			for (Entity entity : proxy.getEntities()) {
				SpineComponent spineComponent = entity.getComponent(SpineComponent.class);
				SpinePreviewComponent previewComponent = entity.getComponent(SpinePreviewComponent.class);
				SpineScaleComponent scaleComponent = entity.getComponent(SpineScaleComponent.class);

				if (animSelectBox.getSelection().first().equals(NO_COMMON_ANIMATION) == false) {
					spineComponent.setDefaultAnimation(animSelectBox.getSelection().first());
					previewComponent.updateAnimation = true;
				}

				if (playAnimationOnStart.isIndeterminate() == false)
					spineComponent.setPlayOnStart(playAnimationOnStart.isChecked());
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
}
