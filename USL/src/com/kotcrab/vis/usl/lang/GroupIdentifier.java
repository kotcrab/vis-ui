package com.kotcrab.vis.usl.lang;

import java.util.ArrayList;

public class GroupIdentifier extends Identifier {
	public ArrayList<String> inherits = new ArrayList<String>();
	public ArrayList<Identifier> content = new ArrayList<Identifier>();
}
