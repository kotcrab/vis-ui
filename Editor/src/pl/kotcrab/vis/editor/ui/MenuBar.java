
package pl.kotcrab.vis.editor.ui;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;

public class MenuBar extends Table {
	private Array<MenuItem> menus;
	private Stage stage;
	private Skin skin;

	private Menu lastDisplayedMenu;
	private boolean menuVisible = false;

	public MenuBar (Stage stage, Skin skin) {
		super(skin);
		this.skin = skin;
		this.stage = stage;

		menus = new Array<MenuItem>();

		stage.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (lastDisplayedMenu != null) {
					if (menuVisible) {
						Vector2 pos = lastDisplayedMenu.localToStageCoordinates(new Vector2(0, 0));
						Rectangle rect = new Rectangle(pos.x, pos.y, lastDisplayedMenu.getWidth(), lastDisplayedMenu.getHeight());

						if (rect.contains(x, y) == false) closeMenu();
						return true;
					} else
						menuVisible = true;
				}

				return false;
			}

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if (lastDisplayedMenu != null && menuVisible) closeMenu();
			}
		});
	}

	private void closeMenu () {
		lastDisplayedMenu.remove();
		lastDisplayedMenu = null;
		menuVisible = false;
	}

	public void addMenu (Menu menu) {
		menus.add(new MenuItem(menu));
		menu.clear();
	}

	private class MenuItem {
		public Menu menu;
		public TextButton menuOpenButton;

		public MenuItem (Menu menu) {
			this.menu = menu;
			menuOpenButton = new TextButton(menu.getTitle(), skin, "menu");
			add(menuOpenButton);

			menuOpenButton.addListener(new InputListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					if(lastDisplayedMenu != null) closeMenu();
					showMenu();
					return false;
				}
			});
		}

		private void showMenu () {
			Vector2 pos = menuOpenButton.localToStageCoordinates(new Vector2(0, 0));

			menu.setPosition(pos.x, pos.y - menu.getHeight());
			stage.addActor(menu);

			lastDisplayedMenu = menu;
		}
	}
}
