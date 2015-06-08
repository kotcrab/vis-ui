package com.kotcrab.vis.usl.lang;

import java.util.ArrayList;

/** Represents StyleBlock in USL file */
public class StyleBlock {
	public String fullName;
	public StyleBlock extendsStyle;
	public ArrayList<StyleIdentifier> styles = new ArrayList<StyleIdentifier>();
}
