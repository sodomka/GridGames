package normalformgame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import props.Joint;
import sequentialgame.AbstractAction;

/**
 * An N-player normal form game, which is primarily defined by its payoff matrix: 
 * A mapping from vectors of actions (one per player) to vectors of
 * payoffs (one per player). 
 * TODO: Right now, the game takes in a list of actions for each player. We could 
 * abstract this away and instead express actions in terms of their indices.
 * 
 * @author sodomka
 *
 * @param <A>
 */
public class NormalFormGame<A extends AbstractAction> {
	int numPlayers;
	Map<Joint<A>, Joint<Double>> payoffs;
	Joint<List<A>> possibleActionsPerPlayer;	
	List<Joint<A>> possibleJointActions;

	public NormalFormGame(int numPlayers, 
			Joint<List<A>> possibleActionsPerPlayer,
			List<Joint<A>> possibleJointActions) {
		this.numPlayers = numPlayers;
		this.possibleActionsPerPlayer = possibleActionsPerPlayer;
		this.possibleJointActions = possibleJointActions;
		this.payoffs = new HashMap<Joint<A>, Joint<Double>>();
	}
	
	public void addPayoffsForJointAction(Joint<A> jointActions, Joint<Double> jointPayoffs) {
		assert(!payoffs.containsKey(jointActions));
		payoffs.put(jointActions, jointPayoffs);
	}
		
	public Joint<Double> getPayoffsForJointAction(Joint<A> actionsPerPlayer) {
		return payoffs.get(actionsPerPlayer);
	}
	
	public List<Joint<A>> getPossibleJointActions() {
		return possibleJointActions;
	}
	
	public List<A> getPossibleActionsForPlayer(int playerIdx) {
		return possibleActionsPerPlayer.getForPlayer(playerIdx);
	}
	
	public int getNumPlayers() {
		return numPlayers;
	}
	
	public String toString() {
		return payoffs.toString();
	}
}
