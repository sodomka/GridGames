package normalformsolver;

import java.util.List;

import props.DiscreteDistribution;
import props.Joint;

import sequentialgame.AbstractAction;

import normalformgame.NormalFormGame;

/**
 * A wrapper class for handling the special case of 2-player games.
 * All the lists of actions and payoffs are unnecessary in 2-player games,
 * since they can easily be expressed in matrix form. 
 * @author sodomka
 *
 */
public abstract class BimatrixNormalFormSolver<A extends AbstractAction> implements NormalFormSolver<A> {

	static final int player1Idx = 0;
	static final int player2Idx = 1;
	
	@Override
	public GameSolution<A> solve(NormalFormGame<A> normalFormGame) {
		List<A> player1Actions = normalFormGame.getPossibleActionsForPlayer(player1Idx);
		List<A> player2Actions = normalFormGame.getPossibleActionsForPlayer(player2Idx);		
		int numPlayer1Actions = player1Actions.size();
		int numPlayer2Actions = player2Actions.size();
		
		// Extract the payoff matrices for players 1 and 2.
		double[][] player1Payoffs = BimatrixHuSolver.getPayoffMatrixForPlayer(normalFormGame, player1Idx);
		double[][] player2Payoffs = BimatrixHuSolver.getPayoffMatrixForPlayer(normalFormGame, player2Idx);

		double[][] jointActionProbs = solve(player1Payoffs, player2Payoffs);
				
		// Compute expected payoffs.
		double player1ActualExpectedPayoff = BimatrixHuSolver.getExpectedPayoffsForPlayer(player1Payoffs, jointActionProbs);
		double player2ActualExpectedPayoff = BimatrixHuSolver.getExpectedPayoffsForPlayer(player2Payoffs, jointActionProbs);
		Joint<Double> expectedPayoffs = new Joint<Double>();
		expectedPayoffs.add(player1ActualExpectedPayoff);
		expectedPayoffs.add(player2ActualExpectedPayoff);

		// Compute joint strategy.
		DiscreteDistribution<Joint<A>> jointStrategy = new DiscreteDistribution<Joint<A>>();
		for (int a1=0; a1<numPlayer1Actions; a1++) {
			for (int a2=0; a2<numPlayer2Actions; a2++) {
				Joint<A> jointAction = new Joint<A>();
				jointAction.add(player1Actions.get(a1));
				jointAction.add(player2Actions.get(a2));
				jointStrategy.add(jointAction, jointActionProbs[a1][a2]);
			}
		}
		
		// Assume no transfer payments. TODO: Have solver also return this.
		Joint<Double> transferPayments = new Joint<Double>();
		transferPayments.add(0.0);
		transferPayments.add(0.0);
		
		GameSolution<A> solution = new UncorrelatedGameSolution<A>(normalFormGame, jointStrategy, expectedPayoffs, transferPayments);
		return solution;
	}
	
	/**
	 * Given payoffs for player 1 and 2, return the joint over outcomes.
	 * @param player1Payoffs
	 * @param player2Payoffs
	 * @return
	 */
	public abstract double[][] solve(double[][] player1Payoffs, double[][] player2Payoffs);

}
