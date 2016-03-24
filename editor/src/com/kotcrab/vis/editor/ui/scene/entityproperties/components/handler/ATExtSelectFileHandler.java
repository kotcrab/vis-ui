package com.kotcrab.vis.editor.ui.scene.entityproperties.components.handler;

import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.runtime.util.ImmutableArray;

/**
 * Provides extended select file handler functionality with API that is only available from editor.
 * @author Kotcrab
 */
public interface ATExtSelectFileHandler {
	String resolveExtension (ImmutableArray<EntityProxy> selectedEntities);
}
