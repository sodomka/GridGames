package sequentialsolver;

import java.util.Iterator;
import java.util.Set;

import props.Joint;

import sequentialgame.SequentialGame;
import sequentialgame.grid.Board;
import sequentialgame.grid.GridAction;
import sequentialgame.grid.GridGame;
import sequentialgame.grid.GridState;
import sequentialgame.grid.SimpleBoard;
import sequentialsimulator.Simulator;
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

		String filename = "./input/game2.txt";
		SimpleBoard board = new SimpleBoard(filename);
		//Board board = new SimpleBoard(2, 2);
		int numPlayers = 2;
		SequentialGame<GridState, GridAction> game = new GridGame(numPlayers, board);
		JointPolicy<GridState,GridAction> policy = valueIteration.generatePolicy(game);
		System.out.println("POLICY:\n" + policy.toString(.001));
		
		//@betsy basic testing
		Simulator<GridState,GridAction> testSim = new Simulator<GridState,GridAction>(policy, game, 1);
//		Set<GridState> states = policy.keySet();
//		Iterator<GridState> iter = states.iterator();
//		GridState state = iter.next();
//		state = iter.next();
//		state = iter.next();
//		state = iter.next();
		//Joint<GridAction> action = testSim.samplePolicy(state);
		Joint<Double>rewards = testSim.simulateAgents(2, 10);
		System.out.println(rewards);
		
	}
	
	
}
