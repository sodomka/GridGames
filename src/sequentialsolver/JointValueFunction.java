package sequentialsolver;

import java.util.HashMap;
import props.Joint;
import sequentialgame.AbstractState;

/**
 * A container that holds a value function for a sequential game.
 * The value function is a mapping from states in the game
 * to a vector (one element per player) of expected payoffs for 
 * reaching that state.
 * 
 * @author sodomka
 *
 * @param <S>
 */
@SuppressWarnings("serial")
public class JointValueFunction<S extends AbstractState> extends HashMap<S, Joint<Double>> {

	public Joint<Double> getJointValuesForState(S state) {
		return this.get(state);
	}
		
}
