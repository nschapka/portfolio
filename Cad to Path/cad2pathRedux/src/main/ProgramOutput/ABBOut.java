package main.ProgramOutput;

import java.util.ArrayList;
import java.util.List;
import main.GlobalObjects.*;

public class ABBOut {
	// takes an ordered path of DrawingEntities and generates a program to guide an ABB robot around that path.
	
	public ABBOut() {};
	
	public List<String> generateMOD(List<DrawingEntity> path) {
		List<String> modFile = new ArrayList<String>();
		List<Double> quaternions = new ArrayList<Double>();
		int numPositions = getNumPositions(path);
		DrawingEntity currEnt = path.get(0);
		int pathIdx = 1;
		
		modFile.add("MODULE testfile\n");
		
		// using the start point of the first entity
		if (currEnt instanceof Line) {
			quaternions = toQuaternions(((Line) currEnt).normalAngle);
			modFile.add(  "CONST robtarget pathPoint0:=["
					    + "[" + currEnt.point1.x + "," + currEnt.point1.y + ",0],"
					    + "[" + quaternions.get(0) + ",0,0," + quaternions.get(3) + "],"
					    + "[0,0,0,1],"
					    + "[9E+09,9E+09,9E+09,9E+09,9E+09,9E+09]"
					    + "];");
		} else if (currEnt instanceof Arc) {
			quaternions = toQuaternions(((Arc) currEnt).startAngle);
			modFile.add(  "CONST robtarget pathPoint0:=["
					    + "[" + currEnt.point1.x + "," + currEnt.point1.y + ",0],"
					    + "[" + quaternions.get(0) + ",0,0," + quaternions.get(3) + "],"
					    + "[0,0,0,1],"
					    + "[9E+09,9E+09,9E+09,9E+09,9E+09,9E+09]"
					    + "];");
		}
		
		// using the endpoints of all further lines, and the mid + end points of all further arcs.
		for (DrawingEntity de : path) {
			if (de instanceof Line) {
				quaternions = toQuaternions(((Line) de).normalAngle);
				modFile.add(  "CONST robtarget pathPoint" + pathIdx + ":=["
						    + "[" + de.point2.x + "," + de.point2.y + ",0],"
						    + "[" + quaternions.get(0) + ",0,0," + quaternions.get(3) + "],"
						    + "[0,0,0,1],"
						    + "[9E+09,9E+09,9E+09,9E+09,9E+09,9E+09]"
						    + "];");
				pathIdx++;
			} else if (de instanceof Arc) {
				quaternions = toQuaternions(((Arc) de).midAngle);
				modFile.add(  "CONST robtarget pathPoint" + pathIdx + ":=["
						    + "[" + ((Arc) de).midx + "," + ((Arc) de).midy + ",0],"
						    + "[" + quaternions.get(0) + ",0,0," + quaternions.get(3) + "],"
						    + "[0,0,0,1],"
						    + "[9E+09,9E+09,9E+09,9E+09,9E+09,9E+09]"
						    + "];");
				pathIdx++;
				quaternions = toQuaternions(((Arc) de).endAngle);
				modFile.add(  "CONST robtarget pathPoint" + pathIdx + ":=["
						    + "[" + de.point2.x + "," + de.point2.y + ",0],"
						    + "[" + quaternions.get(0) + ",0,0," + quaternions.get(3) + "],"
						    + "[0,0,0,1],"
						    + "[9E+09,9E+09,9E+09,9E+09,9E+09,9E+09]"
						    + "];");
				pathIdx++;
			}
		}
		modFile.add("CONST speedData processSpeed:=[]");
		
		modFile.add("\nPROC processMotion\n");
		
		modFile.add("MoveAbsJ home, v2000, fine, tool0;");
		modFile.add("MoveJ RelTool(pathPoint0, -25,-50,50), v1000, fine, tool1, \\Wobj:=wobjPart;");
		modFile.add("MoveL pathPoint0, processSpeed, z5, tool1, \\Wobj:=wobjPart;");
		
		pathIdx = 1;
		for (DrawingEntity de : path) {
			if (de instanceof Line) {
				modFile.add("MoveL pathPoint" + pathIdx + ", processSpeed, z5, tool1, \\Wobj:=wobjPart;");
				pathIdx++;
			} else if (de instanceof Arc) {
				modFile.add("MoveC pathPoint" + pathIdx + ", pathPoint" + (pathIdx + 1) + ", processSpeed, z5, tool1, \\Wobj:=wobjPart;");
				pathIdx += 2;
			}
		}
		
		modFile.add("MoveL RelTool(pathPoint" + numPositions + ", -25,-50,50), v1000, fine, tool1, \\Wobj:=wobjPart;");
		modFile.add("MoveAbsJ home, v2000, fine, tool0;");
		
		modFile.add("\nENDPROC");
		modFile.add("\nENDMODULE");
		
		return modFile;
	}
	
	private int getNumPositions (List<DrawingEntity> path) {
		int output = 0;
		
		for (DrawingEntity de : path) {
			if (de instanceof Line) {
				output += 1;
			} else if (de instanceof Arc) {
				output += 2;
			}
		}
		
		return output;
	}
	
	private List<Double> toQuaternions (double normalAngle) {
		// the point's we're using will be flat with only a rotation about the z axis, so we can get away with just one argument.
		List<Double> quat = new ArrayList<Double>();
		
		quat.add(Math.floor(Math.cos(Math.toRadians(normalAngle)/2) * 1000000 ) / 1000000 ); // cos(normalAngle/2), truncated to 6 decimal places
		quat.add(0.0);
		quat.add(0.0);
		quat.add(Math.floor(Math.sin(Math.toRadians(normalAngle)/2) * 1000000 ) / 1000000 ); // sin(normalAngle/2), truncated to 6 decimal places
		
		return quat;
	}
}
