package normalformsolver;

import normalformgame.NormalFormGame;
import props.DiscreteDistribution;
import props.Joint;
import sequentialgame.AbstractAction;

/**
 * A game solution for which players' actions are uncorrelated 
 * (e.g., Nash equilibria).
 * 
 * @author sodomka
 *
 * @param <A>
 */
public class UncorrelatedGameSolution<A extends AbstractAction> implements GameSolution<A> {

	
	DiscreteDistribution<Joint<A>> jointActionDistribution;
	NormalFormGame<A> game;
	Joint<Double> expectedPayoffs;
	Joint<Double> transferPayments;
	
	
	public UncorrelatedGameSolution(NormalFormGame<A> game, DiscreteDistribution<Joint<A>> jointActionDistribution, Joint<Double> expectedPayoffs, Joint<Double> transferPayments) {
		this.game = game;
		this.jointActionDistribution = jointActionDistribution;
		this.expectedPayoffs = expectedPayoffs;
		this.transferPayments = transferPayments;		
	}
	
//	public UncorrelatedGameSolution(NormalFormGame<A> game, DiscreteDistribution<Joint<A>> jointActionDistribution, Joint<Double> transferPayments) {
//		this.game = game;
//		this.jointActionDistribution = jointActionDistribution;
//		this.transferPayments = transferPayments;
//	}
	
	
	// FIXME: Passing marginal distributions into the constructor currently 
	// is not implemented. Need to use the constructor that takes in 
	// a joint distribution.
	//	public UncorrelatedGameSolution(NormalFormGame<A> game, Joint<DiscreteDistribution<A>> strategyPerPlayer) {
	//		this.game = game;
	//		jointActionDistribution = getJointDistributionFromMarginals(strategyPerPlayer);
	//		
	//	}

	//	private DiscreteDistribution<Joint<A>> getJointDistributionFromMarginals(
	//			Joint<DiscreteDistribution<A>> strategyPerPlayer) {
	//		DiscreteDistribution<Joint<A>> jointActionDistribution = new DiscreteDistribution<Joint<A>>();		
	//		//TODO FIXME add here
	//		return null;
	//	}
	
	@Override
	public DiscreteDistribution<Joint<A>> getJointActionDistribution() {
		return jointActionDistribution;
	}

	@Override
	public Joint<Double> getExpectedPayoffsWithTransfer() {
		Joint<Double> totalPayoff = new Joint<Double>();
		for (int playerIdx=0; playerIdx<game.getNumPlayers(); playerIdx++) {
			//expectedReward.add(0.0);
			double reward = expectedPayoffs.getForPlayer(playerIdx);
			double transfer = transferPayments.getForPlayer(playerIdx);
			totalPayoff.add(reward+transfer);
		}
		return totalPayoff;
	}
	
	
	@Override
	public Joint<Double> getExpectedPayoffsWithoutTransfer() {
		return expectedPayoffs;
	}
	
	
//	@Override
//	public Joint<Double> getExpectedPayoffs() {
//
//		// Initialize expected payoffs to equal transfer payments for each player.
//		Joint<Double> expectedReward = new Joint<Double>();
//		for (int playerIdx=0; playerIdx<game.getNumPlayers(); playerIdx++) {
//			//expectedReward.add(0.0);
//			expectedReward.add(transferPayments.getForPlayer(playerIdx));
//		}
//		
//		// Compute expected payoffs
//		for (Joint<A> jointActions : game.getPossibleJointActions()) {
//			Joint<Double> payoffsForJointAction = game.getPayoffsForJointAction(jointActions);
//			double probAction = jointActionDistribution.get(jointActions);
//			for (int playerIdx=0; playerIdx<game.getNumPlayers(); playerIdx++) {
//				expectedReward.set(playerIdx, expectedReward.get(playerIdx) + 
//						probAction * payoffsForJointAction.getForPlayer(playerIdx));
//			}
//		}
//		
//		return expectedReward;
//	}


	@Override
	public Joint<Double> getTransferPayments() {
		return transferPayments;
	}

}
