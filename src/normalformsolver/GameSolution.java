package normalformsolver;

import props.DiscreteDistribution;
import props.Joint;
import sequentialgame.AbstractAction;

/**
 * An interface for a solution to an N-player normal form game. 
 * A solution must specify:
 * 1) a distribution over joint player actions 
 *    (i.e., a distribution over outcomes)
 * 2) the expected payoff received by each player.
 * 
 * @author sodomka
 *
 * @param <A>
 */
public interface GameSolution<A extends AbstractAction> {

	// Returns a distribution over the joint space of player actions.
	DiscreteDistribution<Joint<A>> getJointActionDistribution();
	
	// Returns a list of expected payoffs, one per player.
	Joint<Double> getExpectedPayoffs();
	
	// Returns a list of transfer payments, one per player
	Joint<Double> getTransferPayments();
	
	Joint<Double> getExpectedPayoffsWithTransfer();
	
}
