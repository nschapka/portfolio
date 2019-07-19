package main.GlobalObjects;

import java.lang.reflect.Field;

// abstract superclass to lines, arcs, and circles.
public abstract class DrawingEntity {
	
	public Point point1;
	public Point point2;
	
	public abstract DrawingEntity UpdateCoords();
	public abstract DrawingEntity SetPoints();
	public abstract boolean AllFieldsSet();
	
	public void truncate() {
		/*
		 * truncates all double fields to four decimal places
		 * these fields are either read in from cad at excessive precision, or calculated afterward, with rounding error.
		 * doubles contain more precision than the robot is able to affect.  So, we chop it down to size (viz. cutting out the rounding error from calculation)
		 * seems to be safe when tested with a dummy class with no double fields, or no fields.
		 */
		
		for (Field f : this.getClass().getFields()) {
			if (f.getType() == double.class) {
				try {
					if (this instanceof DrawingEntity) {
						f.set(this, Math.floor(((double) f.get(this)) * 10000) / 10000);
					}
				} catch (IllegalArgumentException e) {
					System.out.println("illegal argument");
				} catch (IllegalAccessException e) {
					System.out.println("illegal access");
				}
			}
		}
	}
	
}
