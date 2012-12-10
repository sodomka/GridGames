package sequentialgame.grid;

import props.Joint;
import sequentialgame.AbstractState;

/**
 * A grid game state consisting of the positions of each player.
 * 
 * @author sodomka
 *
 */
public class GridState implements AbstractState {
	private final Joint<Position> playerPositions;
	
	public GridState(Joint<Position> playerPositions) {
		this.playerPositions = playerPositions;
	}
	
	public Joint<Position> getPlayerPositions() {
		return playerPositions;
	}
	
	public Position getPlayerPosition(int playerIdx) {
		return playerPositions.get(playerIdx);
	}
	
	public String toString() {
		return playerPositions.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GridState)) return false; 

		GridState that = (GridState) o;
		if (!playerPositions.equals(that.playerPositions)) return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + playerPositions.hashCode();
		return result;
	}
	
}
