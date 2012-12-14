package normalformsolver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import props.Joint;
import sequentialgame.AbstractAction;

/**
 * Uses gambit command-line tools to solve a normal form game.
 * 
 * @author sodomka
 *
 */
public class BimatrixGambitSolver<A extends AbstractAction> extends BimatrixNormalFormSolver<A> {

	// See http://www.gambit-project.org/doc/tools.html
	private String gambitLocation = "/usr/local/bin/";
	private String solverToUse = "gambit-gnm";


	public double[][] solve(double[][] p1Payoffs, double[][] p2Payoffs) {
		Joint<double[]> mixedStrategies = solveForNash(p1Payoffs, p2Payoffs);
		double[] player1Mix = mixedStrategies.get(player1Idx);
		double[] player2Mix = mixedStrategies.get(player2Idx);
		double[][] jointOverOutcomes = getDistributionOverJointActions(player1Mix, player2Mix);
		return jointOverOutcomes;
	}


	/**
	 * Returns a solution to a normal-form game, in terms of a mixed strategy for each player.
	 * @param p1Payoffs
	 * @param p2Payoffs
	 * @return
	 */
	public Joint<double[]> solveForNash(double[][] p1Payoffs, double[][] p2Payoffs) {
		int numP1Actions = p1Payoffs.length;
		int numP2Actions = p1Payoffs[0].length;
		String gameString = createGameStringFromPayoffs(p1Payoffs, p2Payoffs);
		//System.out.println("gameString=" + gameString);
		List<String> output = executeCommand(getGambitCommandFromString(gambitLocation, solverToUse, gameString));
		//System.out.println("output=" + output);
		boolean stopAfterFirstNE = true;
		List<Joint<double[]>> profiles = getMixedStrategyProfilesFromGambitOutput(output, stopAfterFirstNE, numP1Actions, numP2Actions);
		//System.out.println("profiles=" + Arrays.toString(profiles.get(0).get(0)) + " ; " + Arrays.toString(profiles.get(0).get(1)));
		return profiles.get(0);
	}

	
	
	private static String createGameStringFromPayoffs(double[][] p1Payoffs, double[][] p2Payoffs) {
		int numPlayer1Actions = p1Payoffs.length;
		int numPlayer2Actions = p1Payoffs[0].length;
		StringBuffer sb = new StringBuffer();
		sb.append("NFG 1 R \"Title\"\n");
		sb.append("{\"P1\" \"P2\"} {" + numPlayer1Actions + " " + numPlayer2Actions + "}\n");
		for (int a2=0; a2<numPlayer2Actions; a2++) {
			for (int a1=0; a1<numPlayer1Actions; a1++) {
				sb.append(p1Payoffs[a1][a2] + " " + p2Payoffs[a1][a2] + " ");
			}
		}
		return sb.toString();
	}
	
	
	
	/**
	 * Executes the given command and returns anything that appears in standard output.
	 * @param command
	 * @return
	 */
	private static List<String> executeCommand(String[] command) {
		List<String> outputLines = new ArrayList<String>();
		Runtime rt = Runtime.getRuntime();
		Process pr;
		try {
			pr = rt.exec(command);
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));		
			String line=null;
			while((line=input.readLine()) != null) {
				outputLines.add(line);
			}
			int exitVal = pr.waitFor();
			//System.out.println("Exited with error code "+exitVal);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return outputLines;
	}


	/**
	 * Gets the execution command to run gambit, given gambit's location,
	 * a gambit solver, and a string that represents the game.
	 * @param gambitLocation
	 * @param solverToUse
	 * @param gameString
	 * @return
	 */
	private static String[] getGambitCommandFromString(String gambitLocation, String solverToUse, String gameString) {
		String[] cmd = {
				"/bin/sh",
				"-c",
				"echo '" + gameString + "'" + " | " + gambitLocation + solverToUse 
		};
		return cmd;
	}


	
	/**
	 * Gets the execution command to run gambit, given gambit's location, 
	 * a gambit solver name and a game file.
	 * For a description of game file formats, see:
	 * http://www.gambit-project.org/doc/formats.html#the-strategic-game-nfg-file-format-payoff-version
	 * @param solverToUse
	 * @param gameFile
	 * @return
	 */
	private static String[] getGambitCommandFromFile(String gambitLocation, String solverToUse, String gameFile) {
		// Read from file
		String[] cmd = {
				"/bin/sh",
				"-c",
				gambitLocation + solverToUse + " < " + gameFile
		};
		return cmd;
	}



	/**
	 * Returns a list of mixed strategy profiles, given output from gambit.
	 * @param output
	 * @param stopAfterFirstNE
	 * @param numP1Actions
	 * @param numP2Actions
	 * @return
	 */
	private static List<Joint<double[]>> getMixedStrategyProfilesFromGambitOutput(List<String> output, boolean stopAfterFirstNE, int numP1Actions, int numP2Actions) {
		List<Joint<double[]>> mixedStrategyProfiles = new ArrayList<Joint<double[]>>();
		for (String line : output) {
			String[] lineArr = line.split(",");
			if (lineArr[0].equals("NE")) {
				Joint<double[]> mixedStrategyProfile = new Joint<double[]>();
				double[] p1MixedStrategy = new double[numP1Actions];
				double[] p2MixedStrategy = new double[numP2Actions];
				int idx=1;
				for (int a1=0; a1<numP1Actions; a1++) {
					p1MixedStrategy[a1] = Double.parseDouble(lineArr[idx]);
					idx++;
				}
				for (int a2=0; a2<numP2Actions; a2++) {
					p2MixedStrategy[a2] = Double.parseDouble(lineArr[idx]);
					idx++;
				}
				mixedStrategyProfile.add(p1MixedStrategy);
				mixedStrategyProfile.add(p2MixedStrategy);
				mixedStrategyProfiles.add(mixedStrategyProfile);
				if (stopAfterFirstNE) break;
			}
		}
		return mixedStrategyProfiles;
	}	


}
