package normalformsolver;

import java.util.List;

import props.DiscreteDistribution;
import props.Joint;

import normalformgame.NormalFormGame;
import sequentialgame.AbstractAction;

public class BimatrixCocoSolver<A extends AbstractAction> implements NormalFormSolver<A> {

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
		
		// Create cooperative and competitive payoff matrices.
		double[][] player1CooperativePayoffs = new double[numPlayer1Actions][numPlayer2Actions];
		double[][] player2CooperativePayoffs = new double[numPlayer1Actions][numPlayer2Actions];
		double[][] player1CompetitivePayoffs = new double[numPlayer1Actions][numPlayer2Actions];
		double[][] player2CompetitivePayoffs = new double[numPlayer1Actions][numPlayer2Actions];
		for (int player1ActionIdx=0; player1ActionIdx<numPlayer1Actions; player1ActionIdx++) {
			for (int player2ActionIdx=0; player2ActionIdx<numPlayer2Actions; player2ActionIdx++) {
				double player1Payoff = player1Payoffs[player1ActionIdx][player2ActionIdx];
				double player2Payoff = player2Payoffs[player1ActionIdx][player2ActionIdx];
				double cooperativePayoff = (player1Payoff + player2Payoff) / 2.0;
				double competitivePayoff = (player1Payoff - player2Payoff) / 2.0;
				player1CooperativePayoffs[player1ActionIdx][player2ActionIdx] = cooperativePayoff;
				player2CooperativePayoffs[player1ActionIdx][player2ActionIdx] = cooperativePayoff;
				player1CompetitivePayoffs[player1ActionIdx][player2ActionIdx] = competitivePayoff;
				player2CompetitivePayoffs[player1ActionIdx][player2ActionIdx] = -competitivePayoff;
			}
		}
		
		// Solve the cooperative game
		Joint<double[]> cooperativeMixedStrategyPerPlayer = BimatrixHuSolver.solveForMixedStrategies(player1CooperativePayoffs, player2CooperativePayoffs);
		double[] player1CooperativeStrategy = cooperativeMixedStrategyPerPlayer.getForPlayer(player1Idx);
		double[] player2CooperativeStrategy = cooperativeMixedStrategyPerPlayer.getForPlayer(player2Idx);
		double[][] jointCooperativeStrategy = BimatrixHuSolver.getDistributionOverJointActions(player1CooperativeStrategy, player2CooperativeStrategy);
		// Get expected payoffs if agents play these strategies. 
		double player1CooperativeExpectedPayoff = BimatrixHuSolver.getExpectedPayoffsForPlayer(player1CooperativePayoffs, jointCooperativeStrategy);
		double player2CooperativeExpectedPayoff = BimatrixHuSolver.getExpectedPayoffsForPlayer(player2CooperativePayoffs, jointCooperativeStrategy);
		
		// Solve the competitive game.
		Joint<double[]> competitiveMixedStrategyPerPlayer = BimatrixHuSolver.solveForMixedStrategies(player1CompetitivePayoffs, player2CompetitivePayoffs);
		double[] player1CompetitiveStrategy = competitiveMixedStrategyPerPlayer.getForPlayer(player1Idx);
		double[] player2CompetitiveStrategy = competitiveMixedStrategyPerPlayer.getForPlayer(player2Idx);
		double[][] jointCompetitiveStrategy = BimatrixHuSolver.getDistributionOverJointActions(player1CompetitiveStrategy, player2CompetitiveStrategy);
		// Get expected payoffs if agents play these strategies. 
		double player1CompetitiveExpectedPayoff = BimatrixHuSolver.getExpectedPayoffsForPlayer(player1CompetitivePayoffs, jointCompetitiveStrategy);
		double player2CompetitiveExpectedPayoff = BimatrixHuSolver.getExpectedPayoffsForPlayer(player2CompetitivePayoffs, jointCompetitiveStrategy);
		
		// Compute coco values
		double player1CoCoVal = player1CooperativeExpectedPayoff + player1CompetitiveExpectedPayoff;
		double player2CoCoVal = player2CooperativeExpectedPayoff + player2CompetitiveExpectedPayoff;

		// Compute agents' actual payoffs for following cooperative strategy
		double player1ActualExpectedPayoff = BimatrixHuSolver.getExpectedPayoffsForPlayer(player1Payoffs, jointCooperativeStrategy);
		double player2ActualExpectedPayoff = BimatrixHuSolver.getExpectedPayoffsForPlayer(player2Payoffs, jointCooperativeStrategy);
		Joint<Double> expectedPayoffs = new Joint<Double>();
		expectedPayoffs.add(player1ActualExpectedPayoff);
		expectedPayoffs.add(player2ActualExpectedPayoff);
		
		// Compute transfer payments
		double player1TransferPayments = player1CoCoVal - player1ActualExpectedPayoff;
		double player2TransferPayments = player2CoCoVal - player2ActualExpectedPayoff;
		Joint<Double> transferPayments = new Joint<Double>();
		transferPayments.add(player1TransferPayments);
		transferPayments.add(player2TransferPayments);
		
		// Solution is to follow the cooperative strategy.
		DiscreteDistribution<Joint<A>> jointStrategy = BimatrixHuSolver.getJointStrategyFromIndependentStrategies(normalFormGame, player1CooperativeStrategy, player2CooperativeStrategy);
		GameSolution<A> solution = new UncorrelatedGameSolution<A>(normalFormGame, jointStrategy, expectedPayoffs, transferPayments);

		return solution;
	}

}
