package com.kotcrab.vis.ui.test;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisImageTextButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisWindow;

public class TestImageTextButtonOrientation extends VisWindow {
    public TestImageTextButtonOrientation() {
        super("text image button orientation");

        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();

        addVisWidgets();

        setSize(400, 250);
        centerWindow();
    }

    private void addVisWidgets () {
        Drawable icon = VisUI.getSkin().getDrawable("icon-folder");

        VisImageTextButton right = new VisImageTextButton("right", icon);
        VisImageTextButton left = new VisImageTextButton("left", icon);
        VisImageTextButton top = new VisImageTextButton("top", icon);
        VisImageTextButton bottom = new VisImageTextButton("bottom", icon);

        // 'text_right' is the default orientation for backwards compatibility, so no need to set it explicitly
        // right.setOrientation(VisImageTextButton.Orientation.text_right);
        left.setOrientation(VisImageTextButton.Orientation.TEXT_LEFT);
        top.setOrientation(VisImageTextButton.Orientation.TEXT_TOP);
        bottom.setOrientation(VisImageTextButton.Orientation.TEXT_BOTTOM);

        add(new VisLabel("VisImageTextButton TEXT_RIGHT (default)"));
        add(right).row();
        add(new VisLabel("VisImageTextButton TEXT_LEFT"));
        add(left).row();
        add(new VisLabel("VisImageTextButton TEXT_TOP"));
        add(top).row();
        add(new VisLabel("VisImageTextButton TEXT_BOTTOM"));
        add(bottom).padBottom(3f).row();
    }
}
