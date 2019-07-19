package main.DataIn;

import java.util.ArrayList;
import java.util.List;

import main.GlobalObjects.Arc;
import main.GlobalObjects.Circle;
import main.GlobalObjects.DrawingEntity;
import main.GlobalObjects.Line;
import main.GlobalObjects.Point;

public class DataReader {

	public DataReader(){}
	
	public List<DrawingEntity> DataRead(List<String> fileText) {
		// reads data in from the the list of strings found in the cad file
		List<DrawingEntity> ents = new ArrayList<DrawingEntity>();
		// temporary entities to be built out and added to the list
		Arc ArcTemp;
		Line LineTemp;
		Circle CircleTemp;
		
		for (int idx = 0; idx < fileText.size(); idx++) {
			if (fileText.get(idx).equals("ARC")) {
				// grab five parameters, from fields 10, 20, 40, 50, and 51
				ArcTemp = new Arc();
				while (!ArcTemp.AllFieldsSet()) {
					idx++;
					switch (fileText.get(idx)) {
					case " 10":
						ArcTemp.setX(Double.parseDouble(fileText.get(idx+1)));
					break;
					case " 20":
						ArcTemp.setY(Double.parseDouble(fileText.get(idx+1)));
					break;
					case " 40":
						ArcTemp.setRadius(Double.parseDouble(fileText.get(idx+1)));
					break;
					case " 50":
						ArcTemp.setStartAngle(Double.parseDouble(fileText.get(idx+1)));
					break;
					case " 51":
						ArcTemp.setEndAngle(Double.parseDouble(fileText.get(idx+1)));
					break;
					}
				}
				// now that we're sure the arc is filled out, we add it to the list.
				// but first, we calculate the additional data we'll need later.
				ArcTemp = ArcTemp.CalcAddData();
				ArcTemp.truncate();
				ents.add(ArcTemp.SetPoints());
				
			}
			
			if (fileText.get(idx).equals("LINE")) {
				// grab four parameters from fields 10, 11, 20, 21
				LineTemp = new Line();
				while (!LineTemp.AllFieldsSet()) {
					idx++;
					switch (fileText.get(idx)) {
					case " 10":
						LineTemp.setX1(Double.parseDouble(fileText.get(idx+1)));
					break;
					case " 20":
						LineTemp.setY1(Double.parseDouble(fileText.get(idx+1)));
					break;
					case " 11":
						LineTemp.setX2(Double.parseDouble(fileText.get(idx+1)));
					break;
					case " 21":
						LineTemp.setY2(Double.parseDouble(fileText.get(idx+1)));
					break;
					}
				}
				LineTemp.truncate();
				ents.add(LineTemp.SetPoints());
			}
			
			if (fileText.get(idx).equals("CIRCLE")) {
				// grab three parameters from fields 10, 20, 40
				CircleTemp = new Circle();
				while (!CircleTemp.AllFieldsSet()) {
					idx++;
					switch (fileText.get(idx)) {
					case " 10":
						CircleTemp.setX(Double.parseDouble(fileText.get(idx+1)));
					break;
					case " 20":
						CircleTemp.setY(Double.parseDouble(fileText.get(idx+1)));
					break;
					case " 40":
						CircleTemp.setRad(Double.parseDouble(fileText.get(idx+1)));
					break;
					}
				}
				CircleTemp.truncate();
				ents.add(CircleTemp.SetPoints());
			}
		}
		
		return ents;
	}
	
	public List<Point> generatePointCloud(List<DrawingEntity> ents) {
		List<Point> pointCloud = new ArrayList<Point>();
		
		for (DrawingEntity d : ents) {
			pointCloud.add(d.point1);
			if (!(d instanceof Circle)) { // circles only have one point
				pointCloud.add(d.point2);
			}
		}
		
		return pointCloud;
	}
	
}
