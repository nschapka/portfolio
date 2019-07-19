package main.GlobalObjects;

public class LineEquation {
	// stores coefficients of a linear equation for use in the ray casting methods
	public Point point1;
	public Point point2;
	private double slope;
	private double yIntercept;
	
	
	public LineEquation(Point p1, Point p2) {
		this.point1 = p1;
		this.point2 = p2;
		
		this.slope = (this.point2.y - this.point1.y) / (this.point2.x - this.point1.x);
		this.yIntercept = this.point1.y - (this.point1.x * this.slope);
		
	}
	
	public boolean InYSpan(Point inPoint) {
		return (   inPoint.y >= this.point1.y && inPoint.y <= this.point2.y
				|| inPoint.y <= this.point1.y && inPoint.y >= this.point2.y);
	}

	public double XAtGivenY(double y) {
		double x = (y - this.yIntercept) / this.slope; // rearranged y = mx + b -> (y - b)/m = x
		
		return x;
	}
	
	public String toString() {
		return "y = " + this.slope + "x + " + this.yIntercept;
	}
	
	public boolean VerticalLine() {
		return Math.abs(this.slope) == Double.POSITIVE_INFINITY; 
	}
}
