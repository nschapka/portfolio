package main.GlobalObjects;

public class Point {
	// just an x,y point
	
/*
 * for the moment, i'm truncating these values at 4 places right of the decimal.
 * there are some small inaccuracies in the floating point trig math that cause calculated points that 'should be' identical to drift apart past 6-8 decimal places, which causes problems when checking for equality.
 * however, in the real world, those differences are on the order of hundred thousandths of millimeters, much smaller than most robots' reported repeatability (~0.05mm)
 * truncating to ten-thousandths of inches (four places) is still a whole order of magnitude smaller than what the robot is able to affect.
 * Additionally, it's unlikely that the cad drawing would intentionally place points within millionths of meters of one another when working with this material.
 * 
 * Future work: research doing high accuracy trig in java, will likely cost performance.
 */
	public double x;
	public double y;
	private int fieldsInit;
	
	public Point() {
		this.fieldsInit = 0b00;
	}
	
	public Point(double x, double y) {
		this.x = Math.floor(x * 10000) / 10000;
		this.y = Math.floor(y * 10000) / 10000;
		this.fieldsInit = 0b11;
	}
	
	@Override
	public boolean equals (Object obj) {
		// these values should be truncated, so they can be checked simply for equality
		if (obj instanceof Point && this.x == ((Point) obj).x && this.y == ((Point) obj).y) { // instanceof check first to use the short circuit operator, protects against invalid cast
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		String out = "Point: [";
		
		out += "x: " + this.x + ", ";
		out += "y: " + this.y + "]";
		
		return out;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = Math.floor(x * 10000) / 10000;
		this.fieldsInit |= 0b10;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = Math.floor(y * 10000) / 10000;
		this.fieldsInit |= 0b01;
	}
	
	
	
}
