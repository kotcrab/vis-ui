package com.kotcrab.vis.usl.lang;

/** Represents identifier of USL lang, see subclasses. */
public abstract class Identifier {
	public String name;

	public Identifier () {
	}

	public Identifier (String name) {
		this.name = name;
	}

	public Identifier (Identifier other) {
		this.name = other.name;
	}

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Identifier that = (Identifier) o;

		return name.equals(that.name);

	}

	@Override
	public int hashCode () {
		return name.hashCode();
	}
}
