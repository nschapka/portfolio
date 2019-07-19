package main.DataParsing;

import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;

import main.GlobalObjects.LineEquation;
import main.GlobalObjects.Point;

public class RayCasting {
	/*
	 * This class takes a point cloud and finds points that belong to a local concavity in the larger shape
	 */
	
	public RayCasting(){}
	
	public List<Point> FindLargestPolygon (List<Point> pointCloud) throws MalformedInputException {
		/*
		 * Takes a point cloud generated from the whole cad drawing and finds the subset of four of those points which describe the largest polygon
		 * This will be used as the boundary when raycasting later.
		 * 
		 * The formula used to calculate polygon size from points is not accurate for self-intersecting shapes (eg. something that looks like an hourglass)
		 * however, that inaccuracy reports those shapes as being smaller than reality, so that problem can be safely ignored, becuase we want a non self intersecting bounding polygon.
		 */
		List<Point> boundingPolygon = new ArrayList<Point>();
		double maxSize = 0;
		double polygonSize = 0;
		int[] maxSizeIndex = new int[4];
		
		if (pointCloud.size() < 4) { // need at least four points
			throw new MalformedInputException(pointCloud.size());
		}
		
		for (int p1 = 0; p1 < pointCloud.size(); p1++) {
			for (int p2 = 1; p2 < pointCloud.size(); p2++) {
				for (int p3 = 2; p3 < pointCloud.size(); p3++) {
					for (int p4 = 3; p4 < pointCloud.size(); p4++) {
						polygonSize = Math.abs( (   (pointCloud.get(p1).x * pointCloud.get(p2).y - pointCloud.get(p1).y * pointCloud.get(p2).x)
												  + (pointCloud.get(p2).x * pointCloud.get(p3).y - pointCloud.get(p2).y * pointCloud.get(p3).x)
												  + (pointCloud.get(p3).x * pointCloud.get(p4).y - pointCloud.get(p3).y * pointCloud.get(p4).x)
												  + (pointCloud.get(p4).x * pointCloud.get(p1).y - pointCloud.get(p4).y * pointCloud.get(p1).x)
												) / 2);
						if (polygonSize > maxSize) {
							maxSize = polygonSize;
							maxSizeIndex[0] = p1;
							maxSizeIndex[1] = p2;
							maxSizeIndex[2] = p3;
							maxSizeIndex[3] = p4;
						}
					}
				}
			}
		}
		
		for (int i : maxSizeIndex) {
			boundingPolygon.add(pointCloud.get(i));
		}
		
		return boundingPolygon;
	}
	
	public List<Point> FindInteriorPoints (List<Point> pointCloud, List<Point> boundingPolygon) {
		List<Point> interiorPoints = new ArrayList<Point>();
		int tempIntersections = 0;
		List<LineEquation> eqs = new ArrayList<LineEquation>();
		try {
			eqs = generateEquations(boundingPolygon);
		} catch (MalformedInputException e) {
			System.err.println("error occured in line equation generation - not enough points to make any lines");
			e.printStackTrace();
			System.exit(1);
		}
		
		for (Point p : pointCloud) {
			if (!boundingPolygon.contains(p)) { // points that are part of the bounding polygon are necessarily not interior points.
				tempIntersections = 0;
				for (LineEquation le : eqs) {
					if (!p.equals(le.point1) && !p.equals(le.point2)) {
						if (RayCastIntersects(p, le)) {
							tempIntersections++;
						}
					}
				}
				
				if (tempIntersections % 2 == 1) { // odd number of intersections means the point was within the lines
					interiorPoints.add(p);
				}
			}
		}
		
		return interiorPoints;
	}

	private boolean RayCastIntersects(Point testPoint, LineEquation lineEq) {
		if (lineEq.VerticalLine()) {
			if (lineEq.InYSpan(testPoint) && (lineEq.point1.x > testPoint.x)) { // if a ray cast from the testPoint in the positive x direction intersects with the line described by lineEq
				return true;
			}
		} else if (lineEq.InYSpan(testPoint) && (lineEq.XAtGivenY(testPoint.y) > testPoint.x) ) { // for nonvertical lines, we need to do a little more math
			return true;
		} 
		return false;
	}
	
	private List<LineEquation> generateEquations(List<Point> boundingPolygon) throws MalformedInputException {
		List<LineEquation> eqs = new ArrayList<LineEquation>();
		
		// there should only be four points in the bounding polygon, but this accepts n points in case that ever changes.
		if (boundingPolygon.size() < 2) { // can't make any lines with fewer than two points
			throw new MalformedInputException(0);
		}
		
		for (int i = 0; i < boundingPolygon.size() - 1; i++) {
			eqs.add(new LineEquation(boundingPolygon.get(i), boundingPolygon.get(i+1)));
		} 
		eqs.add(new LineEquation(boundingPolygon.get(boundingPolygon.size() - 1) ,boundingPolygon.get(0)));
		
		return eqs;
	}

}
