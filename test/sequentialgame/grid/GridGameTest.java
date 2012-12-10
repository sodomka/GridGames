package sequentialgame.grid;

import static org.junit.Assert.*;

import org.junit.Test;

import props.DiscreteDistribution;
import props.Joint;

/**
 * TODO: Add assertEquals instead of just printing output.
 * TODO: Add tests for multi-player interaction with (semi-)walls
 * TODO: Add more tests for 3 players.
 * TODO: Add tests for other elements besides transition probabilities.
 * @author sodomka
 *
 */
public class GridGameTest {

	@Test
	public void testGetTransitionProbabilities2Player() {
		
		// Setup the board, action space, etc.
		int numPlayers = 2;
		Board board = new SimpleBoard(2, 2);
		GridGame game = new GridGame(numPlayers, board);
		Position topLeft = new Position(0,1);
		Position bottomLeft = new Position(0,0);
		Position topRight = new Position(1,1);
		Position bottomRight = new Position(1,0);
		GridAction upAction = new GridAction("up");
		GridAction downAction = new GridAction("down");
		GridAction leftAction = new GridAction("left");
		GridAction rightAction = new GridAction("right");
		GridAction stickAction = new GridAction("stick");
		DiscreteDistribution<GridState> transitionProbs; // for holding results
		DiscreteDistribution<GridState> expectedTransitionProbs; // for holding expected results
		Joint<GridAction> jointAction; // for holding the current action
		GridState state; // for holding the current state
		Joint<Position> positions; // for holding the current player positions.
		
		// Test: Two players collide.
		// Create state.
		positions = new Joint<Position>();
		positions.add(topLeft); // player 1 position
		positions.add(bottomRight); // player 2 position
		state = new GridState(positions);
		// Create actions.
		jointAction = new Joint<GridAction>();
		jointAction.add(rightAction); // player 1 action
		jointAction.add(upAction); // player 2 action
		// Get transition probs.
		transitionProbs = game.getTransitionProbabilities(state, jointAction);
		System.out.println(transitionProbs);
		
		// Test: One player moves on top of another sticking player.
		// Create state.
		positions = new Joint<Position>();
		positions.add(topRight); // player 1 position
		positions.add(bottomRight); // player 2 position
		state = new GridState(positions);
		// Create actions.
		jointAction = new Joint<GridAction>();
		jointAction.add(stickAction); // player 1 action
		jointAction.add(upAction); // player 2 action
		// Get transition probs.
		transitionProbs = game.getTransitionProbabilities(state, jointAction);
		System.out.println(transitionProbs);
		
		// Test: One player moves on top of another effectively sticking player
		//       whose actual action would move the player off the board.
		// Create state.
		positions = new Joint<Position>();
		positions.add(topRight); // player 1 position
		positions.add(bottomRight); // player 2 position
		state = new GridState(positions);
		// Create actions.
		jointAction = new Joint<GridAction>();
		jointAction.add(rightAction); // player 1 action
		jointAction.add(upAction); // player 2 action
		// Get transition probs.
		transitionProbs = game.getTransitionProbabilities(state, jointAction);
		System.out.println(transitionProbs);
		
		// Test: Players try to pass through each other.
		// Create state.
		positions = new Joint<Position>();
		positions.add(topRight); // player 1 position
		positions.add(bottomRight); // player 2 position
		state = new GridState(positions);
		// Create actions.
		jointAction = new Joint<GridAction>();
		jointAction.add(downAction); // player 1 action
		jointAction.add(upAction); // player 2 action
		// Get transition probs.
		transitionProbs = game.getTransitionProbabilities(state, jointAction);
		System.out.println(transitionProbs);
		
		// Test: One player moves to another player's starting position,
		//       but with the other player has moved.
		// Create state.
		positions = new Joint<Position>();
		positions.add(topRight); // player 1 position
		positions.add(bottomRight); // player 2 position
		state = new GridState(positions);
		// Create actions.
		jointAction = new Joint<GridAction>();
		jointAction.add(leftAction); // player 1 action
		jointAction.add(upAction); // player 2 action
		// Get transition probs.
		transitionProbs = game.getTransitionProbabilities(state, jointAction);
		System.out.println(transitionProbs);

		// Test: Players don't cross paths at all.
		// Create state.
		positions = new Joint<Position>();
		positions.add(topRight); // player 1 position
		positions.add(bottomRight); // player 2 position
		state = new GridState(positions);
		// Create actions.
		jointAction = new Joint<GridAction>();
		jointAction.add(leftAction); // player 1 action
		jointAction.add(leftAction); // player 2 action
		// Get transition probs.
		transitionProbs = game.getTransitionProbabilities(state, jointAction);
		System.out.println(transitionProbs);
	}

	
	
	
	@Test
	public void testGetTransitionProbabilities3Player() {
		System.out.println("3 Player test.");
		
		// Setup the board, action space, etc.
		int numPlayers = 3;
		Board board = new SimpleBoard(3, 3);
		GridGame game = new GridGame(numPlayers, board);
		Position bottomLeft = new Position(0,0);
		Position middleLeft = new Position(0, 1);
		Position topLeft = new Position(0,2);
		Position bottomMiddle = new Position(1,0);
		Position middle = new Position(1,1);
		Position topMiddle = new Position(1,2);
		Position bottomRight = new Position(2,0);
		Position middleRight = new Position(2,1);
		Position topRight = new Position(2,2);
		GridAction upAction = new GridAction("up");
		GridAction downAction = new GridAction("down");
		GridAction leftAction = new GridAction("left");
		GridAction rightAction = new GridAction("right");
		GridAction stickAction = new GridAction("stick");
		DiscreteDistribution<GridState> transitionProbs; // for holding results
		DiscreteDistribution<GridState> expectedTransitionProbs; // for holding expected results
		Joint<GridAction> jointAction; // for holding the current action
		GridState state; // for holding the current state
		Joint<Position> positions; // for holding the current player positions.
		
		// Test: Three players collide.
		// Create state.
		positions = new Joint<Position>();
		positions.add(topMiddle); // player 1 position
		positions.add(middleLeft); // player 2 position
		positions.add(bottomMiddle); // player 3 position
		state = new GridState(positions);
		// Create actions.
		jointAction = new Joint<GridAction>();
		jointAction.add(downAction); // player 1 action
		jointAction.add(rightAction); // player 2 action
		jointAction.add(upAction); // player 3 action
		// Get transition probs.
		transitionProbs = game.getTransitionProbabilities(state, jointAction);
		System.out.println(transitionProbs);
		
		// Test: P1 and P2 collide; P3 tries to move to P1's old spot.
		// Create state.
		positions = new Joint<Position>();
		positions.add(topMiddle); // player 1 position
		positions.add(middleLeft); // player 2 position
		positions.add(topLeft); // player 3 position
		state = new GridState(positions);
		// Create actions.
		jointAction = new Joint<GridAction>();
		jointAction.add(downAction); // player 1 action
		jointAction.add(rightAction); // player 2 action
		jointAction.add(rightAction); // player 3 action
		// Get transition probs.
		transitionProbs = game.getTransitionProbabilities(state, jointAction);
		System.out.println(transitionProbs);
		// Expectation: 
		// With .5 probability, P1 wins the tiebreaker, displacing P2
		// and having P3's move be successful. 
		// With .5 probability, P2 wins the tiebreaker, displacing P1
		// which in turn displaces P3.
		
		
		
//		// Test: One player moves on top of another sticking player.
//		// Create state.
//		positions = new Joint<Position>();
//		positions.add(topRight); // player 1 position
//		positions.add(bottomRight); // player 2 position
//		state = new GridState(positions);
//		// Create actions.
//		jointAction = new Joint<GridAction>();
//		jointAction.add(stickAction); // player 1 action
//		jointAction.add(upAction); // player 2 action
//		// Get transition probs.
//		transitionProbs = game.getTransitionProbabilities(state, jointAction);
//		System.out.println(transitionProbs);
//		
//		// Test: One player moves on top of another effectively sticking player
//		//       whose actual action would move the player off the board.
//		// Create state.
//		positions = new Joint<Position>();
//		positions.add(topRight); // player 1 position
//		positions.add(bottomRight); // player 2 position
//		state = new GridState(positions);
//		// Create actions.
//		jointAction = new Joint<GridAction>();
//		jointAction.add(rightAction); // player 1 action
//		jointAction.add(upAction); // player 2 action
//		// Get transition probs.
//		transitionProbs = game.getTransitionProbabilities(state, jointAction);
//		System.out.println(transitionProbs);
//		
//		// Test: Players try to pass through each other.
//		// Create state.
//		positions = new Joint<Position>();
//		positions.add(topRight); // player 1 position
//		positions.add(bottomRight); // player 2 position
//		state = new GridState(positions);
//		// Create actions.
//		jointAction = new Joint<GridAction>();
//		jointAction.add(downAction); // player 1 action
//		jointAction.add(upAction); // player 2 action
//		// Get transition probs.
//		transitionProbs = game.getTransitionProbabilities(state, jointAction);
//		System.out.println(transitionProbs);
//		
//		// Test: One player moves to another player's starting position,
//		//       but with the other player has moved.
//		// Create state.
//		positions = new Joint<Position>();
//		positions.add(topRight); // player 1 position
//		positions.add(bottomRight); // player 2 position
//		state = new GridState(positions);
//		// Create actions.
//		jointAction = new Joint<GridAction>();
//		jointAction.add(leftAction); // player 1 action
//		jointAction.add(upAction); // player 2 action
//		// Get transition probs.
//		transitionProbs = game.getTransitionProbabilities(state, jointAction);
//		System.out.println(transitionProbs);
//
//		// Test: Players don't cross paths at all.
//		// Create state.
//		positions = new Joint<Position>();
//		positions.add(topRight); // player 1 position
//		positions.add(bottomRight); // player 2 position
//		state = new GridState(positions);
//		// Create actions.
//		jointAction = new Joint<GridAction>();
//		jointAction.add(leftAction); // player 1 action
//		jointAction.add(leftAction); // player 2 action
//		// Get transition probs.
//		transitionProbs = game.getTransitionProbabilities(state, jointAction);
//		System.out.println(transitionProbs);
	}

	
	
	
	
	
}
