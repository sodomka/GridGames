package sequentialsolver;

import java.util.Map;

import props.Joint;
import sequentialgame.AbstractAction;
import sequentialgame.AbstractState;

public class PolicyAndTransfers<S extends AbstractState, A extends AbstractAction> {

	private JointPolicy<S,A> policy;
	private Map<S,Joint<Double>> transfers;
	
	public PolicyAndTransfers(JointPolicy<S,A> policy, Map<S,Joint<Double>> transfers) {
		this.policy = policy;
		this.transfers = transfers;
	}
	
	public JointPolicy<S,A> getPolicy() {
		return policy;
	}
	
	public Map<S,Joint<Double>> getTransfers() {
		return transfers;
	}
}
