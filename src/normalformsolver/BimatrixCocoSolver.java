package normalformsolver;

import java.util.Arrays;
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
		
		System.out.println("player1Payoffs:\n" + print2dArr(player1Payoffs));
		System.out.println("player2Payoffs:\n" + print2dArr(player2Payoffs));
		System.out.println("player1CoopPayoffs:\n" + print2dArr(player1CooperativePayoffs));
		System.out.println("player2CoopPayoffs:\n" + print2dArr(player2CooperativePayoffs));
		System.out.println("player1CompPayoffs:\n" + print2dArr(player1CompetitivePayoffs));
		System.out.println("player2CompPayoffs:\n" + print2dArr(player2CompetitivePayoffs));

		
		// Solve the cooperative game
		Joint<double[]> cooperativeMixedStrategyPerPlayer = BimatrixHuSolver.solveForMixedStrategies(player1CooperativePayoffs, player2CooperativePayoffs);
		double[] player1CooperativeStrategy = cooperativeMixedStrategyPerPlayer.getForPlayer(player1Idx);
		double[] player2CooperativeStrategy = cooperativeMixedStrategyPerPlayer.getForPlayer(player2Idx);
		double[][] jointCooperativeStrategy = BimatrixHuSolver.getDistributionOverJointActions(player1CooperativeStrategy, player2CooperativeStrategy);
		// Get expected payoffs if agents play these strategies. 
		double player1CooperativeExpectedPayoff = BimatrixHuSolver.getExpectedPayoffsForPlayer(player1CooperativePayoffs, jointCooperativeStrategy);
		double player2CooperativeExpectedPayoff = BimatrixHuSolver.getExpectedPayoffsForPlayer(player2CooperativePayoffs, jointCooperativeStrategy);
		
		System.out.println("jointCooperativeStrategy:\n" + print2dArr(jointCooperativeStrategy));

		// Solve the competitive game.
		Joint<double[]> competitiveMixedStrategyPerPlayer = BimatrixHuSolver.solveForMixedStrategies(player1CompetitivePayoffs, player2CompetitivePayoffs);
		
		// Buggy. Use minmax / maxmin
		boolean minmaxHack = true;
		double[] player1CompetitiveStrategy;
		double[] player2CompetitiveStrategy;
		if (!minmaxHack) { 
			player1CompetitiveStrategy = competitiveMixedStrategyPerPlayer.getForPlayer(player1Idx);
			player2CompetitiveStrategy = competitiveMixedStrategyPerPlayer.getForPlayer(player2Idx);
		}
		
		////////////// START HACK
		else {
		// minmax
		int player1BestAction = -1;
		double player1BestVal = Double.NEGATIVE_INFINITY;
		for (int a1=0; a1<numPlayer1Actions; a1++) {
			// Get best a2 value for this action
			int bestA2 = -1;
			double bestA2Val = Double.NEGATIVE_INFINITY;			
			for (int a2=0; a2<numPlayer2Actions; a2++) {
				if (player2CompetitivePayoffs[a1][a2] > bestA2Val) {
					bestA2Val = player2CompetitivePayoffs[a1][a2];
					bestA2 = a2;
				}
			}
			// See if a1 is still better than any other action if a2 minimizes a1.
			if (player1CompetitivePayoffs[a1][bestA2] > player1BestVal) {
				player1BestVal = player1CompetitivePayoffs[a1][bestA2];
				player1BestAction = a1;
			}
		}
		player1CompetitiveStrategy = new double[numPlayer1Actions];
		player1CompetitiveStrategy[player1BestAction] = 1.0;
		
		// maxmin
		int player2BestAction = -1;
		double player2BestVal = Double.NEGATIVE_INFINITY;
		for (int a2=0; a2<numPlayer2Actions; a2++) {
			// Get best a1 value for this action
			int bestA1 = -1;
			double bestA1Val = Double.NEGATIVE_INFINITY;			
			for (int a1=0; a1<numPlayer1Actions; a1++) {
				if (player1CompetitivePayoffs[a1][a2] > bestA1Val) {
					bestA1Val = player1CompetitivePayoffs[a1][a2];
					bestA1 = a1;
				}
			}
			// See if a2 is still better than any other action if a1 minimizes a2.
			if (player2CompetitivePayoffs[bestA1][a2] > player2BestVal) {
				player2BestVal = player2CompetitivePayoffs[bestA1][a2];
				player2BestAction = a2;
			}
		}
		player2CompetitiveStrategy = new double[numPlayer2Actions];
		player2CompetitiveStrategy[player2BestAction] = 1.0;
		}
		//////////////////////// END HACK
				
		double[][] jointCompetitiveStrategy = BimatrixHuSolver.getDistributionOverJointActions(player1CompetitiveStrategy, player2CompetitiveStrategy);
		// Get expected payoffs if agents play these strategies. 
		
		
		
		
		double player1CompetitiveExpectedPayoff = BimatrixHuSolver.getExpectedPayoffsForPlayer(player1CompetitivePayoffs, jointCompetitiveStrategy);
		double player2CompetitiveExpectedPayoff = BimatrixHuSolver.getExpectedPayoffsForPlayer(player2CompetitivePayoffs, jointCompetitiveStrategy);

		
		
		System.out.println("jointCompetitiveStrategy:\n" + print2dArr(jointCompetitiveStrategy));

		
		
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

		System.out.println("p1Coco=" + player1CoCoVal + ", p2Coco=" + player2CoCoVal);
		System.out.println("p1ActualPayoff=" + player1ActualExpectedPayoff + ", p2ActualPayoff=" + player2ActualExpectedPayoff);
		System.out.println("p1Transfer=" + player1TransferPayments + ", p2Transfer=" + player2TransferPayments);
		
		// Solution is to follow the cooperative strategy.
		DiscreteDistribution<Joint<A>> jointStrategy = BimatrixHuSolver.getJointStrategyFromIndependentStrategies(normalFormGame, player1CooperativeStrategy, player2CooperativeStrategy);
		GameSolution<A> solution = new UncorrelatedGameSolution<A>(normalFormGame, jointStrategy, expectedPayoffs, transferPayments);

		return solution;
	}
	
	
	public static String print2dArr(double[][] arr) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<arr.length; i++) {
			sb.append(Arrays.toString(arr[i])+"\n");
		}
		return sb.toString();
	}

}
