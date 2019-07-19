package main.ProgramOutput;

import java.util.ArrayList;
import java.util.List;

import main.GlobalObjects.*;

public class KukaOut {
// takes a path of DrawingEntities and spits out a program to run on a kuka robot.
	
	private String programName;
	
	public KukaOut(){};
	
	public List<String> generateSRC(List<DrawingEntity> path) {
		// generates the .src file for output
		List<String> program = new ArrayList<String>();
		int pathPointIdx = 0;
		
		program.addAll(generateBoilerplate());
		program.add("");
		program.add(  "$VEL.CP = 2\n"
					+ "$APO.CDIS = 0\n"
					+ "$TOOL = TOOL_DATA[1]\n"
					+ "$BASE = BASE_DATA[1]\n"
					+ "PTP XHOME\n"); // set motion system variables and run the robot home
		program.add("PTP pathPoint0 : {x -25, y -50, z 50, a 0, b 0, c 0} #TOOL\n"); // move robot to a pounce position offset from the start of the path w/r/t the tool frame
		
		program.add(  "$VEL.CP = 0.22\n"
				    + "$APO.CDIS = 0.15\n");
		
		pathPointIdx = 1;
		for (DrawingEntity de : path) {
			if (de instanceof Line) {
				program.add("LIN pathPoint" + Integer.toString(pathPointIdx) + " C_DIS");
			} else if (de instanceof Arc) {
				program.add("CIRC pathPoint" + Integer.toString(pathPointIdx) + ", pathPoint" + Integer.toString(pathPointIdx + 1) + " C_DIS");
				pathPointIdx++;
			}
			pathPointIdx++;
		}
		
		program.add("\nLIN $POS_ACT : {x -25, y 50, z 50, a 0, b 0, c 0} #TOOL\n");
		
		program.add(  "$VEL.CP = 2\n"
				+ "PTP XHOME\n");
		
		return program;
	}
	
	public List<String> generateDAT(List<DrawingEntity> path) {
		// generates the .dat file (similar to a C .h file) to accompany the .src file
		List<String> datFile = new ArrayList<String>();
		int pathPointIdx = 1;
		
		datFile.add("&ACCESS RVO\r\n" + 
				    "&REL 17\r\n" + 
				    "&COMMENT generated dat file");
		datFile.add("DEFDAT");
		
		// the start position is the first point of the first element
		if (path.get(0) instanceof Line) {
			datFile.add(  "DECL E6POS "
					    + "pathPoint0 {"
					    + "X " + ((Line) path.get(0)).x1 + ", "
					    + "Y " + ((Line) path.get(0)).y1 + ", "
					    + "Z 0, "
					    + "A 0, "
					    + "B 0, "
					    + "C " + ((Line) path.get(0)).normalAngle
					    + "}");
		} else if (path.get(0) instanceof Arc) {
			datFile.add(  "DECL E6POS "
						+ "pathPoint0 {"
					    + "X " + ((Arc) path.get(0)).x1 + ", "
					    + "Y " + ((Arc) path.get(0)).y1 + ", "
					    + "Z 0, "
					    + "A 0, "
					    + "B 0, "
					    + "C " + ((Arc) path.get(0)).startAngle
					    + "}");
		}

		// the rest of the positions are the end points (or mid and end for arcs) of each element
		for (DrawingEntity de : path) {
			if (de instanceof Line) {
				datFile.add(  "DECL E6POS "
						    + "pathPoint" + pathPointIdx + " {"
						    + "X " + ((Line) de).x2 + ", "
						    + "Y " + ((Line) de).y2 + ", "
						    + "Z 0, "
						    + "A 0, "
						    + "B 0, "
						    + "C " + ((Line) de).normalAngle
						    + "}");
				
				pathPointIdx++;
			} else if (de instanceof Arc) {
				datFile.add(  "DECL E6POS "
							+ "pathPoint" + pathPointIdx + " {"
						    + "X " + ((Arc) de).midx + ", "
						    + "Y " + ((Arc) de).midy + ", "
						    + "Z 0, "
						    + "A 0, "
						    + "B 0, "
						    + "C " + ((Arc) de).midAngle
						    + "}");
				datFile.add(  "DECL E6POS "
							+ "pathPoint" + (pathPointIdx+1) + " {"
						    + "X " + ((Arc) de).x2 + ", "
						    + "Y " + ((Arc) de).y2 + ", "
						    + "Z 0, "
						    + "A 0, "
						    + "B 0, "
						    + "C " + ((Arc) de).endAngle
						    + "}");
				
				pathPointIdx += 2;
			}
		}
		
		datFile.add("ENDDAT");
		
		return datFile;
	}
	
	private List<String> generateBoilerplate() {
		// generates the boilerplate stuff that goes at the top of the .src file
		List<String> header = new ArrayList<String>();
		
		// this stuff doesn't actually matter afaik
		header.add("&ACCESS RVO");
		header.add("&REL 1");
		header.add("&COMMENT " + this.programName);
		// this stuff does matter but it's always the same
		header.add("DEF " + this.programName + "( )");
		header.add(";FOLD INI");
		header.add("	;FOLD BASISTECH INI");
		header.add("		GLOBAL INTERRUPT DECL 3 WHEN $STOPMESS==TRUE DO IR_STOPM ( )");
		header.add("		INTERRUPT ON 3");
		header.add("		BAS (#INITMOV,0)");
		header.add("	;ENDFOLD (BASISTECH INI)");
		header.add("	;FOLD USER INI");
		header.add("		;Make your modifications here");
		header.add("");
		header.add("	;ENDFOLD (USER INI)");
		header.add(";ENDFOLD (INI)");
		header.add("");
		
		return header;
	}
	
}
