package sequentialgame.grid;

import java.util.List;

import props.DiscreteDistribution;

/**
 * An interface for a 2-dimensional board on which
 * a grid game is played. A board primarily must specify a list
 * of allowable actions and define the dynamics (e.g., transition function
 * and rewards) for a single player operating on that board. 
 * 
 * @author sodomka
 *
 */
public interface Board {

	public List<GridAction> getAllowableActions();
	
	public List<Position> getOccupiablePositions();

	/**
	 * Returns a distribution over the player's next board position,
	 * should they take the given action from the given current position.
	 * @param currentPlayerPosition
	 * @param playerAction
	 * @return
	 */
	public DiscreteDistribution<Position> getNextPositionDistribution(
			Position currentPlayerPosition,
			GridAction playerAction);

	public double getActionReward(GridAction playerAction);

	public double getGoalReward(Position playerPosition, Integer playerIdx);
	
	public boolean hasGoalForPlayer(Position playerPosition, Integer playerIdx);
	
}
