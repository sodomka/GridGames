package normalformsolver;

import java.util.Arrays;

import sequentialgame.AbstractAction;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class BimatrixCorrelatedEqmSolver<A extends AbstractAction> extends BimatrixNormalFormSolver<A> {

	
	// pi[a1][a2][...] : probability of some joint action (decision variables)
	// R[i][a1][a2][...] : player i's reward for some joint action (constant -- payoff matrix)
	
	// for each player i, action a_i, and other action a'_i:
	//   sum(expected payoffs when playing a_i, given signal a_i) >=
	//   sum(expected payoffs when playing a'_i, given signal a_i)
	//   --Equation 2 in http://www.cs.brown.edu/research/pubs/pdfs/2005/Greenwald-2005-CL.pdf
	
	// for each player i, action a_i:
	// pi[a1][a2][...] >= 0
	// ^ Defined upon variable creation
	
	// sum(all pi[a1][a2][...]) == 1
	
	IloCplex cplex;
	
	public BimatrixCorrelatedEqmSolver() {
		try {
			cplex = new IloCplex();
			cplex.setOut(null); // suppress output
		} catch (IloException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Returns a joint distribution over actions for player 1 and 2, given their payoffs.
	 * Returned solution is a correlated equilibrium following some specified objective.
	 * @param player1Payoffs
	 * @param player2Payoffs
	 * @return
	 */
	public double[][] solve(double[][] player1Payoffs, double[][] player2Payoffs) {
		double[][] jointProbsDouble = null;
		try {
			cplex.clearModel();
			
			// Create solver for a two-player game
			int numP1Actions = player1Payoffs.length;
			int numP2Actions = player1Payoffs[0].length;

			
			// Create decision variables to hold the joint probabiliity.
			IloNumVar[][] jointActionProbability = new IloNumVar[numP1Actions][numP2Actions];
			for (int a1=0; a1<numP1Actions; a1++) {
				for (int a2=0; a2<numP2Actions; a2++) {
					jointActionProbability[a1][a2] = cplex.numVar(0, 1);
				}
			}
			
			// For each player (1 or 2), each action, and other action:
			// [Start with player 1]
			for (int a1=0; a1<numP1Actions; a1++) {
				for (int a1Prime=0; a1Prime<numP1Actions; a1Prime++) {
					// Add constraint about how expected profit for taking action a1 is 
					// >= expected profit for taking action a1Prime.
					if (a1==a1Prime) continue;
					IloLinearNumExpr expectedPayoffForActionGivenActionSignal = cplex.linearNumExpr();
					IloLinearNumExpr expectedPayoffForOtherActionGivenActionSignal = cplex.linearNumExpr();					
					// Sum over other opponent's actions 
					// (from the perspective of the outer loop player)
					for (int a2=0; a2<numP2Actions; a2++) {
						expectedPayoffForActionGivenActionSignal.addTerm(player1Payoffs[a1][a2], jointActionProbability[a1][a2]);
						expectedPayoffForOtherActionGivenActionSignal.addTerm(player1Payoffs[a1Prime][a2], jointActionProbability[a1][a2]);
					}
					// Add constraint
					cplex.addGe(expectedPayoffForActionGivenActionSignal, expectedPayoffForOtherActionGivenActionSignal);
				}
			}

			// [Do the same thing for player 2]
			for (int a2=0; a2<numP2Actions; a2++) {
				for (int a2Prime=0; a2Prime<numP2Actions; a2Prime++) {
					// Add constraint about how expected profit for taking action a1 is 
					// >= expected profit for taking action a1Prime.
					if (a2==a2Prime) continue;
					IloLinearNumExpr expectedPayoffForActionGivenActionSignal = cplex.linearNumExpr();
					IloLinearNumExpr expectedPayoffForOtherActionGivenActionSignal = cplex.linearNumExpr();					
					// Sum over other opponent's actions 
					// (from the perspective of the outer loop player)
					for (int a1=0; a1<numP1Actions; a1++) {
						expectedPayoffForActionGivenActionSignal.addTerm(player2Payoffs[a1][a2], jointActionProbability[a1][a2]);
						expectedPayoffForOtherActionGivenActionSignal.addTerm(player2Payoffs[a1][a2Prime], jointActionProbability[a1][a2]);
					}
					// Add constraint
					cplex.addGe(expectedPayoffForActionGivenActionSignal, expectedPayoffForOtherActionGivenActionSignal);
				}
			}
			
			// sum(all pi[a1][a2][...]) == 1
			IloLinearNumExpr sumProbs = cplex.linearNumExpr();
			for (int a1=0; a1<numP1Actions; a1++) {
				for (int a2=0; a2<numP2Actions; a2++) {
					sumProbs.addTerm(1, jointActionProbability[a1][a2]);
				}
			}
			cplex.addEq(1, sumProbs);
			
			
			
			// Add some objective:
			IloLinearNumExpr objective = cplex.linearNumExpr();
			// Maximize sum of players expected rewards:
			for (int a1=0; a1<numP1Actions; a1++) {
				for (int a2=0; a2<numP2Actions; a2++) {
					objective.addTerm(player1Payoffs[a1][a2], jointActionProbability[a1][a2]);
					objective.addTerm(player2Payoffs[a1][a2], jointActionProbability[a1][a2]);					
				}
			}
			cplex.addMaximize(objective);

			
			
			// Solve and get result
			if ( cplex.solve() ) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value = " + cplex.getObjValue());
				cplex.output().println("Objective function = " + cplex.getObjective());
				double objectiveVal = cplex.getObjValue();
				jointProbsDouble = new double[numP1Actions][numP2Actions];
				//Create double array to return
				for (int a1=0; a1<numP1Actions; a1++) {
					for (int a2=0; a2<numP2Actions; a2++) {
						jointProbsDouble[a1][a2] = cplex.getValue(jointActionProbability[a1][a2]);
					}
				}


				
			}			
			
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jointProbsDouble;
	}
	
	
	public static void main(String[] args) {
		BimatrixCorrelatedEqmSolver solver = new BimatrixCorrelatedEqmSolver();
		double[][] player1Payoffs = {{6,2},{7,0}};
		double[][] player2Payoffs = {{6,7},{2,0}};
		double[][] jointProbs = solver.solve(player1Payoffs, player2Payoffs);
		// Print result
		for (int a1=0; a1<jointProbs.length; a1++) {
			System.out.println(Arrays.toString(jointProbs[a1]));
		}
		
	}
}
