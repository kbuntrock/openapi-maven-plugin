package io.github.kbuntrock.sample.dto;

/**
 * Circle shape object as a datatransfer object
 *
 * @author Kévin Buntrock
 */
public final class CircleDto extends ShapeDto {

	/**
	 * Radius of the circle in mm
	 */
	private double radius;

	public double getRadius() {
		return radius;
	}
}
