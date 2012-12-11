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

	/**
	 * Get policy string for all joint actions where the probability of 
	 * playing that joint action is above epsilon.
	 * @param epsilon
	 * @return
	 */
	public String toString(double epsilon) {
		StringBuffer sb = new StringBuffer();
		for (S state : keySet()) {
			sb.append(state + " : " );
			DiscreteDistribution<Joint<A>> distribution = this.get(state);
			for (Joint<A> jointAction : distribution.keySet()) {
				double prob = distribution.get(jointAction);
				if (prob >= epsilon) {
					sb.append(jointAction + "=" + prob + ", ");
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public String policyAtStateToString(S state, double epsilon) {
		StringBuffer sb = new StringBuffer();
			sb.append(state + " : " );
			DiscreteDistribution<Joint<A>> distribution = this.get(state);
			for (Joint<A> jointAction : distribution.keySet()) {
				double prob = distribution.get(jointAction);
				if (prob >= epsilon) {
					sb.append(jointAction + "=" + prob + ", ");
				}
			}
		return sb.toString();		
	}
}
