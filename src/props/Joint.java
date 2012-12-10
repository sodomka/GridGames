package props;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of elements, one per player.
 * This class is used to improve readability
 * over using a List. The added information is
 * that the number of elements is equal to the 
 * number of players in the game.
 * 
 * @author sodomka
 *
 * @param <E>
 */
@SuppressWarnings("serial")
public class Joint<E> extends ArrayList<E> {

	public Joint() {
	}
	
	public Joint(List<E> vals) {
		this.addAll(vals);
	}
	
	public E getForPlayer(int playerIdx) {
		return get(playerIdx);
	}
	
}
