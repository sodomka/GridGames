package normalformsolver;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 * A linear programming solver for 2-player 
 * zero-sum games. See p.232 of Chvatal, "Linear Programming".
 * @author sodomka
 *
 */
public class ZeroSumBimatrixGameSolver {

	IloCplex cplex;
	
	public ZeroSumBimatrixGameSolver() {
		try {
			cplex = new IloCplex();
			cplex.setOut(null); // suppress output
		} catch (IloException e) {
			e.printStackTrace();
		}

	}
	
	
	public double solveForMinimaxValue(double[][] player1Payoffs) {
		
		int m = player1Payoffs.length; // num player 1 actions
		int n = player1Payoffs[0].length; // num player 2 actions
		
		double minZ = -1000;
		double maxZ = 1000;
		
		try {
			cplex.clearModel();
			IloNumVar z = cplex.numVar(minZ, maxZ);
			IloNumVar[] p1Probs = cplex.numVarArray(m, 0, 1);
			
			// ADD OBJECTIVE:
			// Maximize our expected payoffs
			cplex.addMaximize(z);
			
			// For each possible opponent action,
			for (int j=0; j<n; j++) {
				// Compute expected payoff when opponent plays that action
				IloLinearNumExpr expectedPayoffGivenOpponentAction = cplex.linearNumExpr();
				for (int i=0; i<m; i++) {
					expectedPayoffGivenOpponentAction.addTerm(player1Payoffs[i][j], p1Probs[i]);
				}
				// Add constraint saying total expected payoffs are at least this high.
				cplex.addGe(expectedPayoffGivenOpponentAction, z);
			}
			
			// Probs must sum to 1.
			IloLinearNumExpr sumProbs = cplex.linearNumExpr();
			for (int i=0; i<m; i++) {
				sumProbs.addTerm(1, p1Probs[i]);
			}
			cplex.addEq(1, sumProbs);
			
			// Each prob must be nonnegative
			for (int i=0; i<m; i++) {
				cplex.addGe(p1Probs[i], 0);
			}
			
			if ( cplex.solve() ) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value = " + cplex.getObjValue());
				cplex.output().println("Objective function = " + cplex.getObjective());

				double objectiveVal = cplex.getObjValue();
				return objectiveVal;

//				//Create double array to return
//				for (int a=0; a<numAgents; a++) {
//					for (int s=0; s<=a; s++) {
//						I_a_sDouble[a][s] = cplex.getValue(I_a_s[a][s]);
//					}
//				}

			}			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return Double.NaN;
	}
	
}
