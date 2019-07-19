package main.ProgramOutput;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

public class fileOutput {
	// spits out whatever files were generated earlier
	Writer wr = null;
	
	public fileOutput(){}
	
	public void writeFiles (List<String> text, String name, String fileExtention) {
		// open up a file stream
		try {
			wr = new BufferedWriter (new OutputStreamWriter(new FileOutputStream ("C:\\users\\nschapka\\desktop\\" + name + "." + fileExtention), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			for (String s : text) {
				wr.append(s);
				wr.append("\r\n");
				wr.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
