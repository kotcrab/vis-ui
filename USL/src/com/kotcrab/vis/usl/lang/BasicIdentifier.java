package com.kotcrab.vis.usl.lang;

public class BasicIdentifier extends Identifier {
	public String content;

	public BasicIdentifier () {
	}

	public BasicIdentifier (String name, String content) {
		super(name);
		this.content = content;
	}

	public BasicIdentifier (BasicIdentifier other) {
		super(other);
		this.content = other.content;
	}

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		BasicIdentifier that = (BasicIdentifier) o;

		return content.equals(that.content);

	}

	@Override
	public int hashCode () {
		int result = super.hashCode();
		result = 31 * result + content.hashCode();
		return result;
	}
}
