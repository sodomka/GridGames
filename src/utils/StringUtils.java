package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringUtils {
	
	
	/**
	 * Parse a string that represents multiple key-value pairs, where keys/values and key-value pairs
	 * have given strings that separate them.
	 * 
	 * Assumptions: 
	 *  1) The character that separates keys and values is unique (i.e., it is not
	 *     contained elsewhere in the key or the value).
	 *  2) The character that separates complete (key, value) pairs does not appear in the key.
	 * @param line
	 * @param keyValueSeparator
	 * @param pairSeparator
	 * @return
	 */
	public static Map<String, String> ParseKeyValuePairs(String line, String keyValueSeparator, String pairSeparator) {
		
		List<String> keys = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		String[] valueKeys = line.split(keyValueSeparator);
		for (int i=0; i<valueKeys.length; i++) {
			String valueKey = valueKeys[i];
			if (i==0) {
				// Just a key
				keys.add(valueKey);
			} else if (i==valueKeys.length-1) {
				// Just a value
				values.add(valueKey);
			} else {
				int pairSeparatorIdx =  valueKey.lastIndexOf(pairSeparator);
				String value = valueKey.substring(0, pairSeparatorIdx);
				String key = valueKey.substring(pairSeparatorIdx+pairSeparator.length(), valueKey.length());
				values.add(value);
				keys.add(key);
			}
		}
		
		assert (keys.size() == values.size());
		Map<String, String> pairs = new HashMap<String, String>();		
		for (int i=0; i<keys.size(); i++) {
			pairs.put(keys.get(i), values.get(i));
		}
		
		return pairs;
	}
	
	
	/**
	 * Parse a string representation of an array that is of the form [x1, x2, x3, ..., xn].
	 * Return the corresponding array.
	 * @param line
	 * @return
	 */
	public static String[] parseArrayString(String line, String elementSeparator) {
		return line.substring(1, line.length()-1).split(elementSeparator);
	}
	
	
	
	/**
	 * Converts an array of strings into an array of doubles.
	 * @param doublesAsStrings
	 * @return
	 */
	public static Double[] stringArrayToDoubleArray(String[] doublesAsStrings) {
		Double[] outputArray = new Double[doublesAsStrings.length];
		for (int i=0; i<doublesAsStrings.length; i++) {
			String doubleAsString = doublesAsStrings[i];
			outputArray[i] = Double.parseDouble(doubleAsString);			
		}
		return outputArray;
	}
	
	
	
	/**
	 * Converts an array of strings into an array of primitive doubles.
	 * @param doublesAsStrings
	 * @return
	 */
	public static double[] stringArrayToPrimitiveDoubleArray(String[] doublesAsStrings) {
		double[] outputArray = new double[doublesAsStrings.length];
		for (int i=0; i<doublesAsStrings.length; i++) {
			String doubleAsString = doublesAsStrings[i];
			outputArray[i] = Double.parseDouble(doubleAsString);			
		}
		return outputArray;
	}
	
	
	/**
	 * Converts an array of strings into an array of primitive ints.
	 * @param intsAsStrings
	 * @return
	 */
	public static int[] stringArrayToPrimitiveIntegerArray(String[] intsAsStrings) {
		int[] outputArray = new int[intsAsStrings.length];
		for (int i=0; i<intsAsStrings.length; i++) {
			String intAsString = intsAsStrings[i];
			outputArray[i] = Integer.parseInt(intAsString);			
		}
		return outputArray;
	}
	
	
}
