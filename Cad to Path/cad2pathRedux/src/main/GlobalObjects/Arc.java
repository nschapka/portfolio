package main.GlobalObjects;


public class Arc extends DrawingEntity {
	// an arc, descirbed in cad data as an origin point, a start and end angle, and a radius.
	public double x;
	public double y;
	public double startAngle;
	public double endAngle;
	public double radius;
	private int fieldsInit; // binary to keep track of which fields have been read from data
	// the above fields are read in from data, the following are calculated after the fact
	public double x1;
	public double y1;
	public double x2;
	public double y2;
	public double midx;
	public double midy;
	public double span;
	public double midAngle;
	private boolean addFieldsInit;
	
	public Arc() {
		this.fieldsInit = 0b00000;
	}
	
	public Arc(double x, double y, double startAngle, double endAngle, double radius) {
		this.x = x;
		this.y = y;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		this.radius = radius;
		this.fieldsInit = 0b11111;
	}
	
	public Arc CalcAddData() {
		// this method is called to take the base data read in from the .dxf file and extrapolate other necessary data
		if (this.endAngle < this.startAngle) { // then the span crosses zero
			this.span = Math.abs((this.endAngle + 360) - this.startAngle);
		} else {
			this.span = Math.abs(this.endAngle - this.startAngle);
		}
		this.midAngle = this.startAngle + (this.span/2);
		if (this.midAngle > 360) {
			this.midAngle -= 360;
		}
		// creating a right triangle using the start angle, the center point, and vertical and horizontal lines aligned with the axes of the origin
		// then doing some trig
		this.x1 = this.x + (Math.cos(Math.toRadians(this.startAngle)))*this.radius;
		this.y1 = this.y + (Math.sin(Math.toRadians(this.startAngle)))*this.radius;
		this.x2 = this.x + (Math.cos(Math.toRadians(this.endAngle)))*this.radius;
		this.y2 = this.y + (Math.sin(Math.toRadians(this.endAngle)))*this.radius;
		this.midx = this.x + (Math.cos(Math.toRadians(this.midAngle)))*this.radius;
		this.midy = this.y + (Math.sin(Math.toRadians(this.midAngle)))*this.radius;
		
		this.addFieldsInit = true;
		
		return this;
	}
	
	public void flipAngles() {
		double tempAngle = this.startAngle;
		
		this.startAngle = this.endAngle;
		this.endAngle = tempAngle;
	}
	
	@Override
	public Arc SetPoints() {
		
		if (this.addFieldsInit) {
			this.point1 = new Point(this.x1, this.y1);
			this.point2 = new Point(this.x2, this.y2);
		} // TODO: else, throw an error.
		
		return this;
	}
	
	@Override
	public Arc UpdateCoords() {
		// if the arc had to be flipped, we need to update the raw coordinate data as well.
		this.x1 = this.point1.x;
		this.y1 = this.point1.y;
		this.x2 = this.point2.x;
		this.y2 = this.point2.y;

		return this;
	}
	
	public boolean AllFieldsSet() {
		return (this.fieldsInit == 0b11111);	
	}
	
	public String toString() {
		String s = "Arc: [";
		
		if (addFieldsInit) {
			s += "Start x: " + this.x1 + ", ";
			s += "Start y: " + this.y1 + ", ";
			s += "End x: " + this.x2 + ", ";
			s += "End y: " + this.y2 + ", ";
			s += "Span: " + this.span + ", ";
		}
		
		s += "Center x: " + this.x + ", ";
		s += "Center y: " + this.y + ", ";
		s += "Radius: " + this.radius + ", ";
		s += "Start Angle: " + this.startAngle + ", ";
		s += "End Angle: " + this.endAngle;
		
		s += "]";
		
		return s;
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
		this.fieldsInit |= 0b10000;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
		this.fieldsInit |= 0b01000;
	}

	public double getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(double startAngle) {
		this.startAngle = startAngle;
		this.fieldsInit |= 0b00100;
	}

	public double getEndAngle() {
		return endAngle;
	}

	public void setEndAngle(double endAngle) {
		this.endAngle = endAngle;
		this.fieldsInit |= 0b00010;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
		this.fieldsInit |= 0b00001;
	}


}
