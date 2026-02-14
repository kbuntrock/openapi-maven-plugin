package io.github.kbuntrock.sample.dto;

/**
 * An abstract shape dto
 *
 * @author Kévin Buntrock
 */
public sealed class ShapeDto permits CircleDto {

	/**
	 * Center of the shape on the X axis
	 */
	private int centerX;

	/**
	 * Center of the shape on the Y axis
	 */
	private int centerY;

	public int getCenterX() {
		return centerX;
	}

	public int getCenterY() {
		return centerY;
	}
}
