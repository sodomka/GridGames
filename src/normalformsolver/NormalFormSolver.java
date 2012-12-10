package normalformsolver;

import sequentialgame.AbstractAction;
import normalformgame.NormalFormGame;

/**
 * An interface for a solver of normal form games. 
 * A solver simply takes a normal form game as input
 * and outputs a solution. This solution could be
 * a Nash equilibrium or any other solution concept
 * (e.g., coco values).
 * 
 * @author sodomka
 *
 * @param <A>
 */
public interface NormalFormSolver<A extends AbstractAction> {
	
	public GameSolution<A> solve(NormalFormGame<A> normalFormGame);
	
}
