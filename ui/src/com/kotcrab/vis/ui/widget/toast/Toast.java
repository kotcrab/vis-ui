package com.kotcrab.vis.ui.widget.toast;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Base class for all toast. To create your own toast you should extend {@link ToastTable} class.
 * <p>
 * If you want further customization and modify other aspects of toast (such as close button) override
 * {@link #createMainTable()}.
 * @author Kotcrab
 * @see MessageToast
 * @see ToastTable
 * @since 1.1.0
 */
public class Toast {
	private ToastStyle style;

	private ToastManager toastManager;

	private VisTable mainTable;
	private VisTable contentTable;

	/** @param content table content, preferably instance of {@link ToastTable} */
	public Toast (VisTable content) {
		this("default", content);
	}

	/** @param content table content, preferably instance of {@link ToastTable} */
	public Toast (String styleName, VisTable content) {
		this(VisUI.getSkin().get(styleName, ToastStyle.class), content);
	}

	/** @param content table content, preferably instance of {@link ToastTable} */
	public Toast (ToastStyle style, VisTable content) {
		this.style = style;
		this.contentTable = content;
		if (content instanceof ToastTable) {
			((ToastTable) content).setToast(this);
		}
		createMainTable();
	}

	protected void createMainTable () {
		mainTable = new VisTable();
		mainTable.setBackground(style.background);

		VisImageButton closeButton = new VisImageButton(style.closeButtonStyleName);
		closeButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				close();
			}
		});

		mainTable.add(contentTable).pad(3).fill().expand();
		mainTable.add(closeButton).top();
	}

	/** Called when close button was pressed by default call {@link #fadeOut()} */
	protected void close () {
		fadeOut();
	}

	public void fadeOut () {
		mainTable.addAction(Actions.sequence(Actions.fadeOut(VisWindow.FADE_TIME, Interpolation.fade), new Action() {
			@Override
			public boolean act (float delta) {
				toastManager.remove(Toast.this);
				return true;
			}
		}, Actions.removeActor()));
	}

	public VisTable fadeIn () {
		mainTable.setColor(1, 1, 1, 0);
		mainTable.addAction(Actions.fadeIn(VisWindow.FADE_TIME, Interpolation.fade));
		return mainTable;
	}

	public VisTable getContentTable () {
		return contentTable;
	}

	public VisTable getMainTable () {
		return mainTable;
	}

	public void setToastManager (ToastManager toastManager) {
		this.toastManager = toastManager;
	}

	public ToastManager getToastManager () {
		return toastManager;
	}

	public static class ToastStyle {
		public Drawable background;
		public String closeButtonStyleName;

		public ToastStyle () {
		}

		public ToastStyle (Drawable background, String closeButtonStyle) {
			this.background = background;
			this.closeButtonStyleName = closeButtonStyle;
		}
	}
}
