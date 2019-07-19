package main.GlobalObjects;

public class Circle extends DrawingEntity {
	// circles, described in cad data as a center point and a radius.
	public double x;
	public double y;
	public double rad;
	private int fieldsInit; 
	
	public Circle() {
		this.fieldsInit = 0b000;
		this.point2 = null;
	}
	
	public Circle(double x, double y, double rad) {
		this.x = x;
		this.y = y;
		this.rad = rad;
		this.fieldsInit = 0b111;
		
		this.point1 = new Point(this.x, this.y);
		this.point2 = null;
	}

	@Override
	public Circle SetPoints() {
		
		if (this.AllFieldsSet()) {
			this.point1 = new Point(this.x, this.y);
		} // TODO: else, throw an error.
		
		return this;
	}
	
	@Override
	public Circle UpdateCoords() {
		// if the arc had to be flipped, we need to update the raw coordinate data as well.
		// this shouldn't ever need to be used, as it only has one point, but I may as well implement it for all the subclasses of DE anyway.
		this.x = this.point1.x;
		this.y = this.point1.y;

		return this;
	}
	
	public boolean AllFieldsSet() {
		if (this.fieldsInit == 0b111) {
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		String s = "Circle: [";
		
		s += "Center x: " + this.x + ", ";
		s += "Center y: " + this.y + ", ";
		s += "Radius: " + this.rad + "]";
		
		return s;
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
		this.fieldsInit |= 0b100;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
		this.fieldsInit |= 0b010;
	}

	public double getRad() {
		return rad;
	}

	public void setRad(double rad) {
		this.rad = rad;
		this.fieldsInit |= 0b001;
	}
	
	
}
