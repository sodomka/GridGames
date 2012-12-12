package normalformsolver;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

public class CorrelatedEqmSolver {

	public static void main(String[] args) {
		try {
			IloCplex cplex = new IloCplex();
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
