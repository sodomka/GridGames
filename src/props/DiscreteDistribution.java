package props;

import java.util.HashMap;

/**
 * A distribution over a discrete set of possible outcomes.
 * 
 * @author sodomka
 *
 */
@SuppressWarnings("serial")
public class DiscreteDistribution<K> extends HashMap<K, Double> {

	/**
	 * Adds some probability to the given key.
	 * @param key
	 * @param additionalProb
	 */
	public void add(K key, double additionalProb) {
		if (this.containsKey(key)) {
			Double previousProb = this.get(key);
			this.put(key, previousProb + additionalProb);
		} else {
			this.put(key,  additionalProb);
		}
	}
}
