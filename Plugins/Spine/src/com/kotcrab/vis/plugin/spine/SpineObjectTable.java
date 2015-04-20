package com.kotcrab.vis.plugin.spine;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Animation;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.editor.ui.scene.entityproperties.SpecificObjectTable;
import com.kotcrab.vis.editor.util.EntityUtils;
import com.kotcrab.vis.editor.util.gdx.TableBuilder;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;

import java.util.HashSet;

public class SpineObjectTable extends SpecificObjectTable {
	private static final String NO_COMMON_ANIMATION = "<?>";

	private IndeterminateCheckbox playAnimationOnStart;
	private IndeterminateCheckbox preivew;
	private VisSelectBox<String> animSelectBox;

	private Image warningImage;
	private Tooltip onlyCommonAnimationTooltip;
	private Tooltip noCommonAnimationTooltip;

	@Override
	protected void init () {
		preivew = new IndeterminateCheckbox("Preview in editor");
		playAnimationOnStart = new IndeterminateCheckbox("Play animation on start");

		preivew.addListener(properties.getSharedCheckBoxChangeListener());
		playAnimationOnStart.addListener(properties.getSharedCheckBoxChangeListener());

		animSelectBox = new VisSelectBox<>();
		animSelectBox.setItems("<none>");
		animSelectBox.getSelection().setProgrammaticChangeEvents(false);
		animSelectBox.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				createCommonAnimationsList();
			}
		});
		animSelectBox.addListener(properties.getSharedChangeListener());

		warningImage = new Image(Assets.getIcon(Icons.WARNING));

		onlyCommonAnimationTooltip = new Tooltip("Only showing animations that are common for all selected objects!");
		noCommonAnimationTooltip = new Tooltip("There isn't any common animation for selected objects");

		padTop(0);
		padLeft(3);
		left();
		defaults().left();
		add(preivew).spaceBottom(0).row();
		add(playAnimationOnStart).spaceBottom(2).row();
		add(TableBuilder.build(new VisLabel("Animation:"), animSelectBox, warningImage)).padBottom(3);
	}

	@Override
	public boolean isSupported (EditorObject entity) {
		return entity instanceof SpineObject;
	}

	@Override
	public void updateUIValues () {
		Tooltip.removeTooltip(warningImage);
		warningImage.setVisible(false);

		Array<EditorObject> entities = properties.getEntities();

		EntityUtils.setCommonCheckBoxState(entities, preivew, entity -> ((SpineObject) entity).isPreviewInEditor());
		EntityUtils.setCommonCheckBoxState(entities, playAnimationOnStart, entity -> ((SpineObject) entity).isPlayOnStart());

		createCommonAnimationsList();
		String commonAnimation = EntityUtils.getCommonString(entities, NO_COMMON_ANIMATION, entity -> ((SpineObject) entity).getDefaultAnimation());

		if (commonAnimation.equals(NO_COMMON_ANIMATION)) {
			animSelectBox.getItems().add(NO_COMMON_ANIMATION);
			animSelectBox.setItems(animSelectBox.getItems().toArray());
			animSelectBox.setSelected(NO_COMMON_ANIMATION);
		} else
			animSelectBox.setSelected(commonAnimation);
	}

	private void createCommonAnimationsList () {
		Array<EditorObject> entities = properties.getEntities();
		int animationCounter = 0;
		HashSet<String> commonAnimations = new HashSet<>();
		Array<HashSet<String>> allAnimNames = new Array<>();

		for (EditorObject obj : entities) {
			SpineObject spineObject = (SpineObject) obj;
			Array<Animation> animations = spineObject.getSkeleton().getData().getAnimations();

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
		Array<EditorObject> entities = properties.getEntities();
		for (EditorObject entity : entities) {
			SpineObject obj = (SpineObject) entity;

			if (animSelectBox.getSelection().first().equals(NO_COMMON_ANIMATION) == false)
				obj.setDefaultAnimation(animSelectBox.getSelection().first());

			if (playAnimationOnStart.isIndeterminate() == false) obj.setPlayOnStart(playAnimationOnStart.isChecked());
			if (preivew.isIndeterminate() == false) obj.setPreviewInEditor(preivew.isChecked());
		}
	}
}
