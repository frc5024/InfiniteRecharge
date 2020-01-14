package frc.lib5k.kinematics;

public class Error2D {
	private double x, y = 0.0;

	/**
	 * Create a new Error2D
	 * 
	 * @param x X error
	 * @param y Y error
	 */
	public Error2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Rotate the Error by and angle (in degrees)
	 * 
	 * @param angle Angle to rotate goal by
	 */
	public void rotateBy(double angle) {
		// Note to self: this works perfectly fine. Don't question it
		angle = Math.toRadians(angle);

		double _x = x * Math.cos(angle) - y * Math.sin(angle);
		double _y = x * Math.sin(angle) + y * Math.cos(angle);

		this.x = _x;
		this.y = _y;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String toString() {
		return String.format("(%.2f, %.2f)", x, y);
	}
}