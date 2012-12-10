package sequentialsolver;

import sequentialgame.SequentialGame;
import sequentialgame.grid.Board;
import sequentialgame.grid.GridAction;
import sequentialgame.grid.GridGame;
import sequentialgame.grid.GridState;
import sequentialgame.grid.SimpleBoard;
import normalformsolver.BimatrixHuSolver;
import normalformsolver.NormalFormSolver;

/**
 * A class for running algorithms to generate policies for grid games.
 * 
 * @author sodomka
 *
 */
public class SolverRunner {

	
	public static void main(String[] args) {
		int numIterations = 1;
		NormalFormSolver<GridAction> normalFormSolver = new BimatrixHuSolver<GridAction>();
		double gamma = .9;
		MultiAgentValueIteration<GridState,GridAction> valueIteration = new MultiAgentValueIteration<GridState,GridAction>(numIterations, normalFormSolver, gamma);
		Board board = new SimpleBoard(2, 2);
		int numPlayers = 2;
		SequentialGame<GridState, GridAction> game = new GridGame(numPlayers, board);
		JointPolicy<GridState,GridAction> policy = valueIteration.generatePolicy(game);
		System.out.println("POLICY:" + policy);
	}
	
	
}
