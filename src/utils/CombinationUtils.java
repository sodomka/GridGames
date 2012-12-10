package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for generating exhaustive combinations/permutations of items.
 * 
 * @author sodomka
 *
 */
public class CombinationUtils {

	/**
	 * Gets all possible permutations of n items (indexed 0...(n-1))
	 * when k are chosen, either with or without replacement.
	 * @param n
	 * @return
	 */
	public static List<List<Integer>> getAllPermutations(int n, int k, boolean withReplacement) {
		List<Integer> numEdgesPerLevel = new ArrayList<Integer>();
		for (int i=0; i<k; i++) {
			numEdgesPerLevel.add(n);
		}
		return getAllPaths(numEdgesPerLevel, withReplacement);
	}
	
	
	
	public static List<List<Integer>> getAllPaths(List<Integer> numEdgesPerLevel) {
		boolean allowDuplicateEdgeIndicesOnPath = true;
		return getAllPaths(numEdgesPerLevel, allowDuplicateEdgeIndicesOnPath);
	}
	
	/**
	 * Returns all possible paths through a tree, given a fixed number of edges
	 * at each level of the tree. Tree edges are indexed starting with 0.
	 * @param numEdgesPerLevel
	 * @return
	 */
	public static List<List<Integer>> getAllPaths(List<Integer> numEdgesPerLevel, boolean allowDuplicateEdgeIndicesOnPath) {
		List<Integer> currentPath = new ArrayList<Integer>();
		List<List<Integer>> allPaths = new ArrayList<List<Integer>>();
		createAllPaths(currentPath, numEdgesPerLevel, allPaths, allowDuplicateEdgeIndicesOnPath);
		return allPaths;
	}
	
	private static void createAllPaths(List<Integer> currentPath, List<Integer> numEdgesPerLevel, List<List<Integer>> allPaths, boolean allowDuplicateEdgeIndicesOnPath) {
		int currentDepth = currentPath.size();
		if (currentDepth == numEdgesPerLevel.size()) {
			allPaths.add(new ArrayList<Integer>(currentPath));
			return;
		}
		for (int edgeIdx=0; edgeIdx<numEdgesPerLevel.get(currentDepth); edgeIdx++) {
			// If we don't want to take the same edge index and this has been considered already, skip this index.
			if (!allowDuplicateEdgeIndicesOnPath && currentPath.contains(edgeIdx)) {
				continue;
			}
			currentPath.add(edgeIdx);
			createAllPaths(currentPath, numEdgesPerLevel, allPaths, allowDuplicateEdgeIndicesOnPath);
			currentPath.remove(currentPath.size()-1);
		}
		return;
	}
	
	
	public static void main(String[] args) {
		
		List<Integer> numEdgesPerLevel = new ArrayList<Integer>();
		numEdgesPerLevel.add(3);
		numEdgesPerLevel.add(3);
		numEdgesPerLevel.add(3);
		List<List<Integer>> allPaths = getAllPaths(numEdgesPerLevel, false);
		for (List<Integer> path : allPaths) {
			System.out.println(path);
		}
	}
	
}

