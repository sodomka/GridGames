package sequentialgame.grid;

import sequentialgame.AbstractAction;

/**
 * An action used in grid games. 
 * 
 * @author sodomka
 *
 */
public class GridAction implements AbstractAction {
	private String name;
	
	public GridAction(String name) {
		this.name = name.toLowerCase();
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GridAction)) return false; 

		GridAction that = (GridAction) o;
		if (!name.equals(that.name)) return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + name.hashCode();
		return result;
	}
	
	public String toString() {
		return name;
	}
	
	
}
