package main.GlobalObjects;

public class Line extends DrawingEntity {
	// lines, described in cad data as two points in space.
	public double x1;
	public double y1;
	public double x2;
	public double y2;
	private int fieldsInit;
	public double normalAngle;
	
	public Line() {
		this.fieldsInit = 0b0000;
	}
	
	public Line(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.fieldsInit = 0b1111;
		
		this.point1 = new Point(this.x1, this.y1);
		this.point2 = new Point(this.x2, this.y2);
	}
	
	public void calculateAngle() {
		// finds angles normal to the lines - this is important for the robot output
		// this is not to be executed until after the circuit is ordered and aligned, otherwise the line may be in the wrong direction
		
		normalAngle = Math.toDegrees(Math.tan((y2-y1)/(x2-x1))) - 90;
		
		if (normalAngle < 0) {
			normalAngle += 360;
		}
	}
	
	@Override
	public Line UpdateCoords() {
		// if the arc had to be flipped, we need to update the raw coordinate data as well.
		this.x1 = this.point1.x;
		this.y1 = this.point1.y;
		this.x2 = this.point2.x;
		this.y2 = this.point2.y;

		return this;
	}
	
	@Override
	public Line SetPoints() {
		// this method needs to be invoked for lines constructed as blank instances and filled out with the set/get methods
		if (this.AllFieldsSet()) {
			this.point1 = new Point(this.x1, this.y1);
			this.point2 = new Point(this.x2, this.y2);
		} // TODO: else, throw an error.
		return this;
	}
	
	public boolean AllFieldsSet() {
		return (this.fieldsInit == 0b1111);
	}
	
	public String toString() {
		String s = "Line: [";
		
		s += "x1: " + this.x1 + ", ";
		s += "y1: " + this.y1 + ", ";
		s += "x2: " + this.x2 + ", ";
		s += "y2: " + this.y2 + "]";
		
		return s;
		
	}

	public double getX1() {
		return x1;
	}

	public void setX1(double x1) {
		this.x1 = x1;
		this.fieldsInit |= 0b1000;
	}

	public double getY1() {
		return y1;
	}

	public void setY1(double y1) {
		this.y1 = y1;
		this.fieldsInit |= 0b0100;
	}

	public double getX2() {
		return x2;
	}

	public void setX2(double x2) {
		this.x2 = x2;
		this.fieldsInit |= 0b0010;
	}

	public double getY2() {
		return y2;
	}

	public void setY2(double y2) {
		this.y2 = y2;
		this.fieldsInit |= 0b0001;
	}
	
	
	
}
