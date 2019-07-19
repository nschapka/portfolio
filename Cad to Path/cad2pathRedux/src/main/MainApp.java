package main;

import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.DataIn.CADFileReader;
import main.DataIn.DataReader;
import main.DataParsing.DataManipulation;
import main.DataParsing.PathSetup;
import main.DataParsing.RayCasting;
import main.GlobalObjects.*;
import main.ProgramOutput.ABBOut;
import main.ProgramOutput.KukaOut;
import main.ProgramOutput.fileOutput;

// the overall top level class.  Houses the main method and runs other routines.
public class MainApp {
	
	public enum outputType {ABB, KUKA};
	
	public static void main(String args[]) {
		
		// data declarations:
		List<DrawingEntity> circuit            = new ArrayList<DrawingEntity>();
		List<String>        fileText           = new ArrayList<String>();
		List<String>        outputText         = new ArrayList<String>();
		List<DrawingEntity> entities           = new ArrayList<DrawingEntity>();
		List<Point>         pointCloud         = new ArrayList<Point>();
		List<Point>         uniquePoints       = new ArrayList<Point>();
		List<Point>         boundingPolygon    = new ArrayList<Point>();
		List<Point>         interiorPoints     = new ArrayList<Point>();
		List<DrawingEntity> partialPath        = new ArrayList<DrawingEntity>();
		List<DrawingEntity> orderedPartialPath = new ArrayList<DrawingEntity>();
		List<DrawingEntity> alignedPartialPath = new ArrayList<DrawingEntity>();
		Point               partialPathAnchor  = new Point();
		Point               outerPoint         = new Point();
		String              fileArg            = "";
		outputType          outputArg          = null;
		String              outputName         = "";
		String              filenamePattern    = ".+(?=(\\.dxf))"; // anything up until .dxf
		Pattern             filenameRegex      = Pattern.compile(filenamePattern);
		Matcher             filenameMatcher    = null;
		
		// instances
		CADFileReader    cfr = new CADFileReader();
		DataReader       dr  = new DataReader();
		DataManipulation dm  = new DataManipulation();
		RayCasting       rc  = new RayCasting();
		PathSetup        ps  = new PathSetup();
		KukaOut          ko  = new KukaOut();
		ABBOut           ao  = new ABBOut();
		fileOutput       fo  = new fileOutput();
		
		// CLI argument parsing
		if (args.length == 2) { // two mandatory args
			for (String arg : args) {
				filenameMatcher = (filenameRegex.matcher(arg));
				if (filenameMatcher.find()) { // if the regex query hits, this arg is likely the filename
					if (!outputName.equals("")) {
						System.err.println("duplicate filename argument detected");
						System.exit(1);
					}
					fileArg = arg;
					outputName = filenameMatcher.group(0);
				} else { // otherwise, it's probably the output type
					if (arg.toUpperCase().equals("ABB")) {
						if (outputArg != null) {
							System.err.println("duplicate output type argument detected");
							System.exit(1);
						}
						outputArg = outputType.ABB;
					} else if (arg.toUpperCase().equals("KUKA")) {
						if (outputArg != null) {
							System.err.println("duplicate output type argument detected");
							System.exit(1);
						}
						outputArg = outputType.KUKA;
					} else { // the argument didn't match a filename OR one of the output types, throw an error
						System.err.println("Invalid input argument");
						System.exit(1);
					}
				}
			}
		} else {
			System.err.println("invalid number of arguments.  Please enter a filename (*.dxf) and an output type (ABB || Kuka)");
			System.exit(1);
		}
		
		// operations
		fileText = cfr.CADRead(System.getProperty("user.dir") + "\\" + fileArg);
		entities = dr.DataRead(fileText);
		pointCloud = dr.generatePointCloud(entities);
		uniquePoints = dm.ParseUniquePoints(pointCloud);
		outerPoint = dm.OuterVertex(uniquePoints);
		
		try {
			circuit = dm.OrderCircuit(entities, outerPoint);
		} catch (MalformedInputException e) {
			e.printStackTrace();
		}
		
		if (!dm.IsInnermostCircuit(circuit, entities)) { // a part that has a fully enclosed circuit
			try {
				circuit = dm.OrderCircuit(entities, dm.anchorForInnerCircuit(circuit, entities));
				circuit = dm.AlignCircuit(circuit);
				
				for (DrawingEntity de : circuit) {
					if (de instanceof Line) {
						((Line) de).calculateAngle();
					}
				}
				
				if (outputArg == outputType.ABB) { 
					outputText = ao.generateMOD(circuit);
					fo.writeFiles(outputText, outputName, "mod");
				} else if (outputArg == outputType.KUKA) { 
					outputText = ko.generateDAT(circuit);
					fo.writeFiles(outputText, outputName, "dat");
					outputText = ko.generateSRC(circuit);
					fo.writeFiles(outputText, outputName, "src");
				}
			} catch (MalformedInputException e) {
				e.printStackTrace();
			}
				
		} else {
			// a part that's cut in half before processing.  Shaped like a U
			try {
				boundingPolygon = rc.FindLargestPolygon(uniquePoints);
				interiorPoints = rc.FindInteriorPoints(uniquePoints, boundingPolygon);
				partialPath = ps.PathToGenerate(interiorPoints, entities);
				partialPathAnchor = ps.findAnchorPoint(interiorPoints, partialPath);
				orderedPartialPath = ps.OrderHalfCircuit(partialPathAnchor, partialPath);
				alignedPartialPath = ps.AlignHalfCircuit(orderedPartialPath);
				
				for (DrawingEntity de : alignedPartialPath) {
					if (de instanceof Line) {
						((Line) de).calculateAngle();
					}
				}
				
				if (outputArg == outputType.ABB) {
					outputText = ao.generateMOD(alignedPartialPath);	
					fo.writeFiles(outputText, outputName, "mod");
				} else if (outputArg == outputType.KUKA) { 
					outputText = ko.generateDAT(alignedPartialPath);
					fo.writeFiles(outputText, outputName, "dat");
					outputText = ko.generateSRC(alignedPartialPath);
					fo.writeFiles(outputText, outputName, "src");
				}
				
				
			} catch (MalformedInputException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
