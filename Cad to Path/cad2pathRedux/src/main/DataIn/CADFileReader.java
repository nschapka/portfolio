package main.DataIn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// handles reading data in from the .DXF file
public class CADFileReader {
BufferedReader br;
List<String> fileText = new ArrayList<String>();
	
	// blank constructor for now
	public CADFileReader(){}
	
	public List<String> CADRead(String filePath) {
		List<String> fileText = new ArrayList<String>();
		String s = "";
		// open up a reader for the provided file
		try {
			br = new BufferedReader( new FileReader(filePath));
			
			// now that it's open, read the whole thing into memory and close it.
			while ((s = br.readLine()) != null){
				fileText.add(s);
			}
			
			// close the filestream
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return fileText;
	}
	
}
