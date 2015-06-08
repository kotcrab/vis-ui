package com.kotcrab.vis.usl.lang;

/** Represents style identifier of USL lang */
public class StyleIdentifier extends GroupIdentifier {
	public boolean metaStyle;

	public StyleIdentifier () {
	}

	public StyleIdentifier (StyleIdentifier other) {
		super(other);
		this.metaStyle = other.metaStyle;
	}

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		StyleIdentifier that = (StyleIdentifier) o;

		return metaStyle == that.metaStyle;

	}

	@Override
	public int hashCode () {
		int result = super.hashCode();
		result = 31 * result + (metaStyle ? 1 : 0);
		return result;
	}
}
