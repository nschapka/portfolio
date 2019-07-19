package main.DataParsing;

import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;


import main.GlobalObjects.*;

public class DataManipulation {

	public DataManipulation(){}
	
	public List<Point> ParseUniquePoints(List<Point> allPoints) {
		 // every point pulled from the DrawingEntities should have a duplicate, so we pare down the list for later.
		List<Point> uniquePoints = new ArrayList<Point>();
		for (Point p : allPoints) {
			if (!PointInList(uniquePoints, p)) {
				uniquePoints.add(p);
			}
		}
		return uniquePoints;
	}
	
	private boolean PointInList (List<Point> allPoints, Point thisPoint) {
		for (Point p : allPoints) {
			if (thisPoint != p && thisPoint.equals(p)) { // check that the points contain the same value, but are not the same object
				return true;
			}
		}
		return false;
	}
	
	public Point OuterVertex(List<Point> points) {
		/*
		 * returns the point from the given list with the greatest displacement from the origin (wherever that may be).
		 * In cases where the part is two concentric shapes, this will identify a point on the outer perimeter (which we want to ignore)
		 * For a half part, this will identify a point on the only perimeter, but will still be useful as a jumping off point to process a circuit.
		 */
		Point outerP = new Point();
		double maxDisp = 0;
		double tempDisp = 0;
		
		// displacement from origin d = Sqrt( x^2 + y^2 )
		for (Point p : points) {
			if ((tempDisp = (Math.sqrt(Math.pow(p.x, 2) + Math.pow(p.y, 2)))) > maxDisp) {
				outerP = p;
				maxDisp = tempDisp;
			}
		}
		
		return outerP;
	}
	
	public List<DrawingEntity> OrderCircuit(List<DrawingEntity> ents, Point anchor) throws MalformedInputException {
		/*
		 * The drawing ents in cad data are not necessarily in order, so we need to reorder them to make a continuous outline.
		 * Starting from the outer vertex, we look for an entity e1 which shares a vertex with e0, and repeat
		 */
		List<DrawingEntity> circuit = new ArrayList<DrawingEntity>();
		Point nextPoint = anchor;
		Point tempPoint1 = new Point();
		Point tempPoint2 = new Point();
		boolean addToList = false;
		boolean loopedAround = false;
		
		while (!loopedAround) {
			for (DrawingEntity d : ents) {
				if (!(d instanceof Circle)) { // skipping over circles.  They are a closed circuit in and of themselves, and can't really be part of a larger circuit.
					tempPoint1 = d.point1;
					tempPoint2 = d.point2;
					
					addToList = true;
					// if we find an entity with a matching vertex, we update the searched-for point to that entity's other vertex to continue the circuit.
					if (nextPoint.equals(tempPoint1)) {
						nextPoint = tempPoint2;
					} else if (nextPoint.equals(tempPoint2)) {
						nextPoint = tempPoint1;
					} else {
						addToList = false;
					}
					
					if (addToList && circuit.contains(d)) {
						loopedAround = true;
					}
					
					if (addToList && !loopedAround) {
						if (circuit.size() > 0 && d == circuit.get(circuit.size() - 1)) { // first clause protects against indexOutOfBounds in the second
							throw new MalformedInputException(ents.indexOf(d));
							// the loop will check every other element before looping back around and matching an element to itself.  If that happens, we have a problem.
						}
						circuit.add(d);
					}
				}
			}
		}
		return circuit;
	}
	
	public boolean IsInnermostCircuit(List<DrawingEntity> circuit, List<DrawingEntity> allEnts) {
		/*
		 * the input cad drawing takes two general forms: a whole part that is an outline with an inner circuit, or a half part which is just an outline.
		 * in the former case, the inner circuit is our target, and there are two circuits. 
		 * in the latter case, there is only one circuit, but we need to do some more work to determine which parts of the outline are the target.
		 * if the circuit we find contains all the elements pulled in from cad, we're in the latter case.
		 */
		
		int numEnts = 0;
		
		for (DrawingEntity d : allEnts) {
			if (!(d instanceof Circle)) { // circles aren't part of the circuit
				numEnts++;
			}
		}
		
		return numEnts == circuit.size();
	}
	
	public List<DrawingEntity> AlignCircuit(List<DrawingEntity> circuit) {
		/*
		 * Some of the cad elements may be backwards (ie, their point1 -> point2 is counterclockwise w/r/t the circuit we're building, while others are clockwise)
		 * this method is here to make sure the circuit described by a list of entities passed in is continuous in one direction.
		 * 
		 * i'm pretty sure this biases a counterclockwise circuit, maybe more thought could go into proving/disproving that.
		 */
		List<Integer> flipFlags = new ArrayList<Integer>();
		Point tempPoint;
		
		do { // while flipflags.contains(1)
			flipFlags.clear();
			for (int i = 0; i < circuit.size() - 1; i++) {
				
				if (!circuit.get(i).point2.equals(circuit.get(i+1).point1)) {
					flipFlags.add(1);
				} else {
					flipFlags.add(0);
				}
				
			}
			
			// one last check for the looparound
			if (!circuit.get(circuit.size()-1).point2.equals(circuit.get(0).point1)) {
				flipFlags.add(1);
			} else {
				flipFlags.add(0);
			}
			
			// flip those entities flagged, except for the first of any group of more than two
			for (int i = 1; i < circuit.size() - 1; i++) {
				if (flipFlags.get(i) == 1 && flipFlags.get(i-1) == 1) {
					
					tempPoint = circuit.get(i).point1;
					circuit.get(i).point1 = circuit.get(i).point2;
					circuit.get(i).point2 = tempPoint;
					if (circuit.get(i) instanceof Arc) {
						((Arc) circuit.get(i)).flipAngles();
					}
				}
			}
			
			// looparound check
			if (flipFlags.get(0) == 1 && flipFlags.get(flipFlags.size()-1) == 1) {
					
				tempPoint = circuit.get(0).point1;
				circuit.get(0).point1 = circuit.get(0).point2;
				circuit.get(0).point2 = tempPoint;
				if (circuit.get(0) instanceof Arc) {
					((Arc) circuit.get(0)).flipAngles();
				}
			}
			
		} while (flipFlags.contains(1)); // we repeat the flag and flip until everything is a nicely ordered circuit.  As long as the elements are ordered properly by OrderCircuit, it will converge after a few cycles at worst.
		
		return circuit;
	}
	
	public Point anchorForInnerCircuit (List<DrawingEntity> outerCircuit, List<DrawingEntity> ents) {
		Point newAnchor = new Point();
		
		for (DrawingEntity de : ents) {
			if (!outerCircuit.contains(de)) {
				newAnchor = de.point1; // can be any old point from the other circuit.
				break;
			}
		}
		
		return newAnchor;
	}
	
	
}
