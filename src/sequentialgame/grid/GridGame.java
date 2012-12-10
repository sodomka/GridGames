package sequentialgame.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import props.DiscreteDistribution;
import props.Joint;
import sequentialgame.SequentialGame;
import utils.CombinationUtils;

/**
 * An N-player sequential game that is played in a grid world (i.e., on a "board").
 * Each player has a set of goal positions that it receives reward for reaching.
 * Players simultaneously take (up, down, left, right, stick) actions until
 * a certain time limit or a player reaches its goal. If multiple players
 * reach their goal at the same time, they both receive the reward. See
 * the following paper for a full game description:
 * http://arxiv.org/pdf/1206.3277.pdf
 * 
 * @author sodomka
 *
 */
public class GridGame implements SequentialGame<GridState, GridAction> {

	int numPlayers;
	Board staticBoard;
	
	List<GridState> possibleStates;
	List<Joint<GridAction>> possibleJointActions;
	
	public GridGame(int numPlayers, Board staticBoard) {
		this.numPlayers = numPlayers;
		this.staticBoard = staticBoard;
		possibleStates = computePossibleStates(staticBoard, numPlayers);
		possibleJointActions = computePossibleJointActions(staticBoard.getAllowableActions(), numPlayers);		
	}
	
	/**
	 * Creates and returns a list of possible game states.
	 * @param staticBoard
	 * @param numPlayers
	 * @return
	 */
	private static List<GridState> computePossibleStates(Board staticBoard, int numPlayers) {		
		// If the board is static, a state only consists of different player positions.
		// Get list of every possible position.
		List<Position> occupiablePositions = staticBoard.getOccupiablePositions();
		List<GridState> jointPositions = getStatesFromOccupiablePositions(occupiablePositions, numPlayers);
		return jointPositions;
	}
	
	/**
	 * Returns a list of all possible states. Assumptions:
	 * 1) Each player must be located on an occupiable position.
	 * 2) No two players can be located on the same position.
	 * The result is a set of (numOccupiablePositions choose numPlayers
	 * without replacement) states.
	 * @param occupiablePositions
	 * @param numPlayers
	 * @return
	 */
	private static List<GridState> getStatesFromOccupiablePositions(
			List<Position> occupiablePositions, int numPlayers) {
		int numOccupiablePositions = occupiablePositions.size();
		List<GridState> possibleStates = new ArrayList<GridState>();
		List<List<Integer>> possiblePlayerPositionIndices = CombinationUtils.getAllPermutations(numOccupiablePositions, numPlayers, false); // Without replacement.
		// Iterate through every possible combination of player position indices
		for (List<Integer> playerPositionIndices : possiblePlayerPositionIndices) {
			Joint<Position> playerPositions = new Joint<Position>();
			for (int playerIdx=0; playerIdx<numPlayers; playerIdx++) {
				playerPositions.add(occupiablePositions.get(playerPositionIndices.get(playerIdx)));
			}
			// Add this combination of player positions to the list
			possibleStates.add(new GridState(playerPositions));
		}
		return possibleStates;
	}

	/**
	 * Creates and returns a list of possible joint actions, 
	 * Assumptions:
	 * 1) the action space for all players is the same
	 * 2) the action space is the same for all board positions.
	 * The result is a set of (numPossiblePlayerActions choose numPlayers with replacement)
	 * joint actions.
	 * 
	 * @param possiblePlayerActions
	 * @return
	 */
	public static List<Joint<GridAction>> computePossibleJointActions(List<GridAction> possiblePlayerActions, int numPlayers) {
		int numActions = possiblePlayerActions.size();
		List<Joint<GridAction>> possibleJointActions = new ArrayList<Joint<GridAction>>();
		List<List<Integer>> possibleActionIndices = CombinationUtils.getAllPermutations(numActions, numPlayers, true); // With replacement.
		// Iterate through every possible combination of actions.
		for (List<Integer> actionIndices : possibleActionIndices) {
			List<GridAction> actions = new ArrayList<GridAction>();
			for (int playerIdx=0; playerIdx<numPlayers; playerIdx++) {
				actions.add(possiblePlayerActions.get(actionIndices.get(playerIdx)));
			}
			// Add this combination of actions to the list.
			possibleJointActions.add(new Joint<GridAction>(actions));
		}
		return possibleJointActions;
	}
	
	
	@Override
	public int getNumPlayers() {
		return numPlayers;
	}
	
	@Override
	public List<GridState> getPossibleStates() {
		return possibleStates;
	}

	@Override
	public List<Joint<GridAction>> getPossibleJointActions() {
		return possibleJointActions;
	}


	/**
	 * Returns a distribution over next states, given the game is in some state and a particular
	 * joint set of actions (one per player) are taken. Only states with nonzero transition 
	 * probability are included in the returned distribution.
	 * @param state
	 * @param jointAction
	 * @return
	 */
	public DiscreteDistribution<GridState> getTransitionProbabilities(GridState state, Joint<GridAction> jointAction) {
		// At a high level, this method is implemented as follows:
		// 1) Get a distribution over each player's next position, assuming they are the only ones on the board
		//    (i.e., ignoring any collisions with other players).
		// 2) Modify the distribution by correcting for any collisions. 
		// 
		// The collision correction gets a bit complex with more players, but the idea is as follows:
		// 2a) Make sure no players are switching places (i.e., passing through each other)
		// 2b) Handle tiebreakers, which occur when multiple players try to reach the same location.
		// 2c) Handle "displacements", which occur when a player tries to move onto a location that
		//     is already occupied. With more than two players, there can be chain reactions of displacements.

		// Create the distribution that we'll ultimately return.
		DiscreteDistribution<GridState> transitionProbabilities = new DiscreteDistribution<GridState>();
		
		// For each player, get a distribution over that player's next possible positions
		// (defined by the board setup, e.g., locations of walls/semi-walls)
		Joint<DiscreteDistribution<Position>> nextPositionDistributionForPlayers = new Joint<DiscreteDistribution<Position>>();
		for (int playerIdx=0; playerIdx<numPlayers; playerIdx++) {
			Position currentPlayerPosition = state.getPlayerPosition(playerIdx);
			GridAction playerAction = jointAction.getForPlayer(playerIdx);
			DiscreteDistribution<Position> nextPositionDistributionForPlayer = staticBoard.getNextPositionDistribution(currentPlayerPosition, playerAction);
			nextPositionDistributionForPlayers.add(nextPositionDistributionForPlayer);
		}
		
		//Get all combinations of next player positions (allowing players to be on the same position) 
		List<Joint<Position>> possibleEndPositionsPerPlayer = getPossibleJointPositions(nextPositionDistributionForPlayers);
		
		// Get probabilities for each joint position, considering player collisions.
		Joint<Position> startPositionPerPlayer = state.getPlayerPositions();
		for (Joint<Position> endPositionPerPlayer : possibleEndPositionsPerPlayer) {
			// Compute the probability of the joint position, assuming no collisions.
			double probForJointPosition = 1;
			for (int playerIdx=0; playerIdx<numPlayers; playerIdx++) {
				Position playerPosition = endPositionPerPlayer.getForPlayer(playerIdx);
				double probForPlayerPosition = nextPositionDistributionForPlayers.get(playerIdx).get(playerPosition);
				probForJointPosition *= probForPlayerPosition;
			}
			
			// Correct for any pass-throughs (two players that are trying to switch spots by passing through each other).
			// Pass-throughs are not allowed. Any players trying to pass through another should remain at their starting position.
			modifyEndPositionsForPassThroughs(startPositionPerPlayer, endPositionPerPlayer);
			
			// Correct for any collisions (N players that are trying to move to the same location).
			// This gets tricky...
			
			// Get a list of all players that are definitely going to be displaced, since they are trying to move
			// to a location where a player remained in place.
			List<Integer> displacedPlayers = new ArrayList<Integer>();
			for (int playerIdx=0; playerIdx<numPlayers; playerIdx++) {
				for (int otherPlayerIdx=0; otherPlayerIdx<numPlayers; otherPlayerIdx++) {
					if (playerIdx==otherPlayerIdx) continue;
					Position otherPlayerStart = startPositionPerPlayer.getForPlayer(otherPlayerIdx);
					Position otherPlayerEnd = endPositionPerPlayer.getForPlayer(otherPlayerIdx);
					Position playerEnd = endPositionPerPlayer.getForPlayer(playerIdx);
					// If the player is moving onto the position of another player who did not actually move,
					// the first player will definitely be displaced.
					if (playerEnd.equals(otherPlayerEnd) && otherPlayerStart.equals(otherPlayerEnd)) {
						displacedPlayers.add(playerIdx);
					}
				}
			}
//			System.out.println("displacedPlayers:" + displacedPlayers);

			// Get a list of all players that *might* (but not necessarily) be displaced at each position, 
			// since there are multiple players trying to move
			// to the same location (and there isn't already a player staying put in that location).
			Map<Position, List<Integer>> tiebreakerPlayersByEndingPosition = new HashMap<Position, List<Integer>>();
			for (int playerIdx=0; playerIdx<numPlayers; playerIdx++) {
				// This player *might* be displaced if
				// (1) it is moving to a different location
				// (2) it is not already guaranteed to be displaced
				// (3) it has the same ending location as another player.
				Position playerStart = startPositionPerPlayer.getForPlayer(playerIdx);
				Position playerEnd = endPositionPerPlayer.getForPlayer(playerIdx);
				if (playerStart.equals(playerEnd)) continue;
				if (displacedPlayers.contains(playerIdx)) continue;
				boolean tiebreakerSituationFound = false;
				for (int otherPlayerIdx=0; otherPlayerIdx<numPlayers && !tiebreakerSituationFound; otherPlayerIdx++) {
					if (playerIdx==otherPlayerIdx) continue;
					Position otherPlayerEnd = endPositionPerPlayer.getForPlayer(otherPlayerIdx);
					if (playerEnd.equals(otherPlayerEnd)) {
						// Add a list of tiebreaker players for this position if it doesn't already exist.
						if (tiebreakerPlayersByEndingPosition.get(playerEnd) == null) {
							tiebreakerPlayersByEndingPosition.put(playerEnd, new ArrayList<Integer>());
						}
						List<Integer> tiebreakerPlayers = tiebreakerPlayersByEndingPosition.get(playerEnd);
						tiebreakerPlayers.add(playerIdx);
						tiebreakerSituationFound = true;
					}
				}
			}
			// Create set of all tiebreaker players across ending positions.
			Set<Integer> tiebreakerPlayers = new HashSet<Integer>();
			for (List<Integer> tiebreakerPlayersForPosition : tiebreakerPlayersByEndingPosition.values()) {
				tiebreakerPlayers.addAll(tiebreakerPlayersForPosition);
			}
//			System.out.println("tiebreakerPlayersByEndingPosition:" + tiebreakerPlayersByEndingPosition);
//			System.out.println("tiebreakerPlayers:" + tiebreakerPlayers);
			
			// Compute the probability of each tiebreaker outcome (i.e., choosing a winner for each tiebreaker).
			// Since tiebreakers are decided with equal probability on each player, all outcomes have the same
			// probability of occurring.
			double jointTiebreakerOutcomeProbability = 1;
			for (Position tiebreakerPosition : tiebreakerPlayersByEndingPosition.keySet()) {
				int numPossibleTiebreakerWinners = tiebreakerPlayersByEndingPosition.get(tiebreakerPosition).size();
				jointTiebreakerOutcomeProbability *= (1.0 / numPossibleTiebreakerWinners); 
			}

			// Update the transition probability for the likelihood of each outcome occurring.
			probForJointPosition *= jointTiebreakerOutcomeProbability;
			
			
			// Get a list of all possible tiebreaker outcomes. Each possible tiebreaker outcome
			// specifies the winning player for each tiebreaker position.
			List<Position> positionsWithTiebreakers = new ArrayList<Position>(tiebreakerPlayersByEndingPosition.keySet()); 
			List<Integer> numPossibleWinnersPerPosition = new ArrayList<Integer>();
			for (Position tiebreakerPosition : positionsWithTiebreakers) {
				int numPossibleWinners = tiebreakerPlayersByEndingPosition.get(tiebreakerPosition).size();
				numPossibleWinnersPerPosition.add(numPossibleWinners);
			}
			// The first List is over possibilities. The second List is over Positions with tiebreakers. Element gives the winning player.
			List<List<Integer>> possibleTiebreakerWinners = CombinationUtils.getAllPaths(numPossibleWinnersPerPosition);

//			System.out.println("possible tiebreaker winners: " + possibleTiebreakerWinners);
			for (List<Integer> tiebreakerOutcomeWinners : possibleTiebreakerWinners) {
				// For the given tiebreaker outcome, get a list of all the players that lost a tie.
				List<Integer> displacedPlayersFromTiebreaker = new ArrayList<Integer>(tiebreakerPlayers);
				displacedPlayersFromTiebreaker.removeAll(tiebreakerOutcomeWinners);
				
				// Add the list of players that are already known to be displaced. 
				displacedPlayersFromTiebreaker.addAll(displacedPlayers);
				
				// Make a copy of the player end positions, since it will be modified for this possible tiebreaker outcome.
				Joint<Position> endPositionPerPlayerGivenTiebreakers = new Joint<Position>();
				endPositionPerPlayerGivenTiebreakers.addAll(endPositionPerPlayer);
				
				// Modify end positions to account for tiebreaker outcomes and any displacements caused
				// by players moving onto already occupied positions.
				modifyEndPositionsForDisplacements(startPositionPerPlayer, endPositionPerPlayerGivenTiebreakers, displacedPlayersFromTiebreaker);
				
				// Get the state corresponding to these agent positions.
				GridState nextState = getStateFromPlayerPositions(endPositionPerPlayerGivenTiebreakers);
				transitionProbabilities.add(nextState, probForJointPosition);
			}
		}
		return transitionProbabilities;
	}

	
	private GridState getStateFromPlayerPositions(Joint<Position> playerPositions) {
		return new GridState(playerPositions);
	}

	
	/**
	 * Given a distribution over each player's possible next position, gets
	 * the space of possible next joint positions (one per player).
	 * @param nextPositionDistributionForPlayers
	 * @return
	 */
	private List<Joint<Position>> getPossibleJointPositions(
			Joint<DiscreteDistribution<Position>> nextPositionDistributionForPlayers) {
		// Get list of next possible positions for each player,
		// as well as the number of possible positions.
		Joint<List<Position>> possibleNextPositionsPerPlayer = new Joint<List<Position>>();
		Joint<Integer> numPossibleNextPositionsPerPlayer = new Joint<Integer>();
		for (DiscreteDistribution<Position> nextPositionDistributionForPlayer : nextPositionDistributionForPlayers) {
			List<Position> nextPossiblePositionsForPlayer = new ArrayList<Position>(nextPositionDistributionForPlayer.keySet());
			possibleNextPositionsPerPlayer.add(nextPossiblePositionsForPlayer);
			numPossibleNextPositionsPerPlayer.add(nextPossiblePositionsForPlayer.size());
		}
		
		// Get the list of all possible joint position indices.
		List<List<Integer>> possiblePositionIndices = CombinationUtils.getAllPaths(numPossibleNextPositionsPerPlayer);
		
		// Get the list of all possible joint positions.
		List<Joint<Position>> possibleJointPositions = new ArrayList<Joint<Position>>();
		for (List<Integer> positionIndices : possiblePositionIndices) {
			Joint<Position> jointPositions = new Joint<Position>();
			for (int playerIdx=0; playerIdx<numPlayers; playerIdx++) {
				Integer indexForPosition = positionIndices.get(playerIdx);
				Position positionForIndex = possibleNextPositionsPerPlayer.getForPlayer(playerIdx).get(indexForPosition);
				jointPositions.add(positionForIndex);
			}
			possibleJointPositions.add(jointPositions);
		}
		return possibleJointPositions;
	}

	
	/**
	 * Takes in player starting and ending positions. Updates endPositionPerPlayer list to account for any pass-throughs.
	 * A pass-through is an illegal move where the two players try to switch positions. It is resolved by having both 
	 * players remain in their starting position.
	 * @param startPositionPerPlayer
	 * @param endPositionPerPlayer
	 */
	public static void modifyEndPositionsForPassThroughs(Joint<Position> startPositionPerPlayer, Joint<Position> endPositionPerPlayer) {
		assert (startPositionPerPlayer.size() == endPositionPerPlayer.size());
		int numPlayers = startPositionPerPlayer.size();
		// Check pair-wise to make sure no two players are trying to pass through each other.
		for (int playerIdx=0; playerIdx<numPlayers; playerIdx++) {
			Position playerStart = startPositionPerPlayer.getForPlayer(playerIdx);
			Position playerEnd = endPositionPerPlayer.getForPlayer(playerIdx);
			for (int otherPlayerIdx=playerIdx+1; otherPlayerIdx<numPlayers; otherPlayerIdx++) {
				Position otherPlayerStart = startPositionPerPlayer.getForPlayer(otherPlayerIdx);
				Position otherPlayerEnd = endPositionPerPlayer.getForPlayer(otherPlayerIdx);
				// If players are trying to pass through each other, make them stay in place instead.
				if (playerStart.equals(otherPlayerEnd) && playerEnd.equals(otherPlayerStart)) {
					endPositionPerPlayer.set(playerIdx, playerStart);
					endPositionPerPlayer.set(otherPlayerIdx, otherPlayerStart);
				}
			}
		}
	}
	
	/**
	 * Takes in player starting and ending positions, as well as a list of players that were displaced from their desired
	 * ending position due to tiebreakers. Updates the endPositionPerPlayer list to reflect these displacement
	 * as well as any additional displacements that occur by chain reaction.
	 * @param startPositionPerPlayer
	 * @param endPositionPerPlayer
	 * @param displacedPlayerIndices
	 */
	public static void modifyEndPositionsForDisplacements(
			Joint<Position> startPositionPerPlayer, Joint<Position> endPositionPerPlayer, List<Integer> displacedPlayerIndices) {
		assert (startPositionPerPlayer.size() == endPositionPerPlayer.size());
		int numPlayers = startPositionPerPlayer.size();
		while (displacedPlayerIndices.size() > 0) {
			// Any player that is displaced ends at their starting position.
			for (Integer playerIdx : displacedPlayerIndices) {
				endPositionPerPlayer.set(playerIdx, startPositionPerPlayer.get(playerIdx));
			}
			// Check to see if there are any new displacements that occur, now that the currently
			// displaced player changed their ending position.
			List<Integer> nextDisplacedPlayerIndices = new ArrayList<Integer>();
			for (Integer playerIdx : displacedPlayerIndices) {
				Position displacedPlayerPosition = endPositionPerPlayer.get(playerIdx);
				for (int otherPlayerIdx=0; otherPlayerIdx<numPlayers; otherPlayerIdx++) {
					Position otherPlayerPosition = endPositionPerPlayer.get(otherPlayerIdx);
					if (otherPlayerIdx != playerIdx && displacedPlayerPosition.equals(otherPlayerPosition)) {
						nextDisplacedPlayerIndices.add(otherPlayerIdx);
					}
				}
			}
			// Update the list of displaced players for the next pass through the loop.
			displacedPlayerIndices = nextDisplacedPlayerIndices;
		}
	}
	

	@Override
	public Joint<Double> getImmediateRewards(GridState state,
			Joint<GridAction> jointAction, GridState nextState) {
		// A player's immediate reward depends only on 
		// 1) the action it took (e.g., movement costs)
		// 2) the player's next position (e.g., whether it reached a goal)
		// Have the board give its costs/rewards for each player.
		Joint<Double> immediateRewardPerPlayer = new Joint<Double>();
		for (int playerIdx=0; playerIdx<numPlayers; playerIdx++) {
			GridAction playerAction = jointAction.getForPlayer(playerIdx);
			Position playerPosition = nextState.getPlayerPosition(playerIdx);
			double playerActionReward = staticBoard.getActionReward(playerAction);
			double playerGoalReward = staticBoard.getGoalReward(playerPosition, playerIdx);
			immediateRewardPerPlayer.add(playerActionReward+playerGoalReward);
		}
		return immediateRewardPerPlayer;
	}

	@Override
	public List<GridAction> getPossibleActionsForPlayer(int playerIdx) {
		return staticBoard.getAllowableActions();
	}

	@Override
	public boolean isTerminalState(GridState state) {
		for (int playerIdx=0; playerIdx<numPlayers; playerIdx++) {
			Position position = state.getPlayerPosition(playerIdx);
			if (staticBoard.hasGoalForPlayer(position, playerIdx)) {
				return true;
			}
		}
		return false;
	}



}
