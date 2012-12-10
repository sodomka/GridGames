package sequentialsolver;

import java.util.HashMap;
import props.DiscreteDistribution;
import props.Joint;
import sequentialgame.AbstractAction;
import sequentialgame.AbstractState;

/**
 * A container that holds a policy for a sequential game.
 * The policy consists of a mapping from game states to
 * a distribution over the joint actions taken by players.
 * 
 * @author sodomka
 *
 * @param <S>
 * @param <A>
 */
@SuppressWarnings("serial")
public class JointPolicy<S extends AbstractState, A extends AbstractAction> extends HashMap<S, DiscreteDistribution<Joint<A>>> {

}
