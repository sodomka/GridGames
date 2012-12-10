package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

	
	/**
	 * Returns a list of strings, one per line in the input file.
	 * Note that for large files, a less memory-intensive method should be used.
	 * @param filename
	 * @return
	 */
	public static List<String> readLines(String filename) {
		List<String> lines = new ArrayList<String>();
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}
	
	
	public static String getFileAsString(String filename) {
		StringBuffer sb = new StringBuffer();
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();		
	}
	
}
