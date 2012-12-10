package sequentialgame;

import java.util.List;

import props.DiscreteDistribution;
import props.Joint;

/**
 * An interface for representing a sequential game, defined for
 * a particular type of states and actions. 
 * 
 * @author sodomka
 *
 * @param <S>
 * @param <A>
 */
public interface SequentialGame<S extends AbstractState, A extends AbstractAction> {

	public int getNumPlayers();
	
	public List<S> getPossibleStates();

	public boolean isTerminalState(S state);
	
	public List<A> getPossibleActionsForPlayer(int playerIdx);
	
	public List<Joint<A>> getPossibleJointActions();
	
	public DiscreteDistribution<S> getTransitionProbabilities(S state, Joint<A> jointAction);
	
	public Joint<Double> getImmediateRewards(S state, Joint<A> jointAction, S nextState);
	
}

