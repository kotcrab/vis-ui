package com.kotcrab.vis.usl.lang;

import com.kotcrab.vis.usl.CollectionUtils;

import java.util.ArrayList;

public class GroupIdentifier extends Identifier {
	public ArrayList<String> inherits = new ArrayList<String>();
	public ArrayList<Identifier> content = new ArrayList<Identifier>();

	public GroupIdentifier () {
	}

	public GroupIdentifier (GroupIdentifier other) {
		super(other);
		this.inherits = new ArrayList<String>(other.inherits);

		for (Identifier id : other.content) {
			if (id instanceof BasicIdentifier)
				this.content.add(new BasicIdentifier((BasicIdentifier) id));

			if (id instanceof GroupIdentifier)
				this.content.add(new GroupIdentifier((GroupIdentifier) id));
		}
	}

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		GroupIdentifier that = (GroupIdentifier) o;

		if (!CollectionUtils.isEqualCollection(inherits, that.inherits)) return false;
		return CollectionUtils.isEqualCollection(content, that.content);

	}

	@Override
	public int hashCode () {
		int result = super.hashCode();
		result = 31 * result + inherits.hashCode();
		result = 31 * result + content.hashCode();
		return result;
	}
}
