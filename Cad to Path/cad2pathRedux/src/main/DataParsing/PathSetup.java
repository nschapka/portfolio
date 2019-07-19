package main.DataParsing;

import java.util.ArrayList;
import java.util.List;

import main.GlobalObjects.*;

public class PathSetup {

	public PathSetup() {}
	
	public List<DrawingEntity> PathToGenerate (List<Point> targetPoints, List<DrawingEntity> ents) {
		// this method is only needed when we execute the RayCasting.FindInnerPoints one. We need to re-map the found points to corresponding entities
		List<DrawingEntity> targetPath = new ArrayList<DrawingEntity>();
		
		for (DrawingEntity de : ents) {
			if (targetPoints.contains(de.point1) && targetPoints.contains(de.point2)) {
				targetPath.add(de);
			}
		}
		
		return targetPath;
	}
	
	public Point findAnchorPoint (List<Point> targetPoints, List<DrawingEntity> ents) {
		// finds a point at the end of the partial path (ie, a point that only belongs to one entity, rather than two)
		Point anchor = new Point();
		int numberOfParentEnts = 0;
		
		for (Point p : targetPoints) {
			numberOfParentEnts = 0;
			for (DrawingEntity de : ents) {
				if (de.point1.equals(p) || de.point2.equals(p)) {
					numberOfParentEnts++;
				}
			}
			if (numberOfParentEnts == 1) {
				anchor = p;
				break; // out of the Points iterator.
			}
		}
		
		return anchor;
	}
	
	public List<DrawingEntity> OrderHalfCircuit (Point anchor, List<DrawingEntity> ents) {
		List<DrawingEntity> orderedPath = new ArrayList<DrawingEntity>();
		Point anchorPoint = anchor;
		
		while (orderedPath.size() < ents.size()) {
			for (DrawingEntity de : ents) {
				if (!orderedPath.contains(de) && de.point1.equals(anchorPoint) || de.point2.equals(anchorPoint)) {
					orderedPath.add(de);
					if (anchorPoint.equals(de.point1)) {
						anchorPoint = de.point2;
					} else {
						anchorPoint = de.point1;
					}
				}
			}
		}
		
		return orderedPath;
	}
	
	public List<DrawingEntity> AlignHalfCircuit (List<DrawingEntity> path) {
		Point tempPoint;
		int lastEntity = path.size()-1;
		
		// if the start and end points are misaligned, we flip entity and update relevant data
		for (int i = 0; i < path.size() - 1; i++) {
			if (!path.get(i).point2.equals(path.get(i+1).point1)) {
				tempPoint = path.get(i).point1;
				path.get(i).point1 = path.get(i).point2;
				path.get(i).point2 = tempPoint;
				path.get(i).UpdateCoords();
				if (path.get(i) instanceof Arc) {
					((Arc) path.get(i)).flipAngles(); 
				}
			}
		}
		
		// last one looks behind rather than ahead
		if(!path.get(lastEntity).point1.equals(path.get(lastEntity-1).point2)) {
			tempPoint = path.get(lastEntity).point1;
			path.get(lastEntity).point1 = path.get(lastEntity).point2;
			path.get(lastEntity).point2 = tempPoint;
			path.get(lastEntity).UpdateCoords();
			if (path.get(lastEntity) instanceof Arc) {
				((Arc) path.get(lastEntity)).flipAngles(); 
			}
		}
		
		return path;
	}
	
}
