package sequentialgame.grid;

import static org.junit.Assert.*;

import org.junit.Test;

import props.DiscreteDistribution;

public class SimpleBoardTest {

	@Test
	public void testNextPositionDistributionNoWalls() {
		// Create board with no walls.
		// Setup test.
		int numX = 3;
		int numY = 3;
		SimpleBoard board = new SimpleBoard(numX, numY);
		DiscreteDistribution<Position> nextPositionDistribution;
		GridAction up = new GridAction("UP");
		GridAction down = new GridAction("Down");
		GridAction left = new GridAction("Left");
		GridAction right = new GridAction("right");
		GridAction stick = new GridAction("stick");
		GridAction invalid = new GridAction("invalidActionName");
		DiscreteDistribution<Position> expectedNextPositionDistribution; // holds expected results.
		Position currentPlayerPosition = new Position(1,1);
				
		// Test up movement
		nextPositionDistribution = board.getNextPositionDistribution(currentPlayerPosition, up);
		expectedNextPositionDistribution = new DiscreteDistribution<Position>();
		expectedNextPositionDistribution.add(new Position(1,2), 1);
		System.out.println("Testing up: " + nextPositionDistribution);
		assertEquals(expectedNextPositionDistribution, nextPositionDistribution);
		
		// Test down movement
		nextPositionDistribution = board.getNextPositionDistribution(currentPlayerPosition, down);
		expectedNextPositionDistribution = new DiscreteDistribution<Position>();
		expectedNextPositionDistribution.add(new Position(1,0), 1);
		System.out.println("Testing down: " + nextPositionDistribution);
		assertEquals(expectedNextPositionDistribution, nextPositionDistribution);
		
		// Test left movement
		nextPositionDistribution = board.getNextPositionDistribution(currentPlayerPosition, left);
		expectedNextPositionDistribution = new DiscreteDistribution<Position>();
		expectedNextPositionDistribution.add(new Position(0,1), 1);
		System.out.println("Testing left: " + nextPositionDistribution);
		assertEquals(expectedNextPositionDistribution, nextPositionDistribution);

		// Test right movement
		nextPositionDistribution = board.getNextPositionDistribution(currentPlayerPosition, right);
		expectedNextPositionDistribution = new DiscreteDistribution<Position>();
		expectedNextPositionDistribution.add(new Position(2,1), 1);
		System.out.println("Testing right: " + nextPositionDistribution);
		assertEquals(expectedNextPositionDistribution, nextPositionDistribution);

		// Test stick movement
		nextPositionDistribution = board.getNextPositionDistribution(currentPlayerPosition, stick);
		expectedNextPositionDistribution = new DiscreteDistribution<Position>();
		expectedNextPositionDistribution.add(new Position(1,1), 1);
		System.out.println("Testing stick: " + nextPositionDistribution);
		assertEquals(expectedNextPositionDistribution, nextPositionDistribution);

		// Test invalid movement
		nextPositionDistribution = board.getNextPositionDistribution(currentPlayerPosition, invalid);
		expectedNextPositionDistribution = new DiscreteDistribution<Position>();
		expectedNextPositionDistribution.add(new Position(1,1), 1);
		System.out.println("Testing invalid: " + nextPositionDistribution);
		assertEquals(expectedNextPositionDistribution, nextPositionDistribution);

		//------ Test boundary cases
		Position leftBoundaryPlayerPosition = new Position(0,1);
		Position rightBoundaryPlayerPosition = new Position(numX-1,1);
		Position upBoundaryPlayerPosition = new Position(1,numY-1);
		Position downBoundaryPlayerPosition = new Position(1,0);

		// Test up movement on grid boundary
		nextPositionDistribution = board.getNextPositionDistribution(upBoundaryPlayerPosition, up);
		expectedNextPositionDistribution = new DiscreteDistribution<Position>();
		expectedNextPositionDistribution.add(upBoundaryPlayerPosition, 1);
		System.out.println("Testing up: " + nextPositionDistribution);
		assertEquals(expectedNextPositionDistribution, nextPositionDistribution);
		
		// Test down movement on grid boundary
		nextPositionDistribution = board.getNextPositionDistribution(downBoundaryPlayerPosition, down);
		expectedNextPositionDistribution = new DiscreteDistribution<Position>();
		expectedNextPositionDistribution.add(downBoundaryPlayerPosition, 1);
		System.out.println("Testing down: " + nextPositionDistribution);
		assertEquals(expectedNextPositionDistribution, nextPositionDistribution);
		
		// Test left movement on grid boundary
		nextPositionDistribution = board.getNextPositionDistribution(leftBoundaryPlayerPosition, left);
		expectedNextPositionDistribution = new DiscreteDistribution<Position>();
		expectedNextPositionDistribution.add(leftBoundaryPlayerPosition, 1);
		System.out.println("Testing left: " + nextPositionDistribution);
		assertEquals(expectedNextPositionDistribution, nextPositionDistribution);

		// Test right movement on grid boundary
		nextPositionDistribution = board.getNextPositionDistribution(rightBoundaryPlayerPosition, right);
		expectedNextPositionDistribution = new DiscreteDistribution<Position>();
		expectedNextPositionDistribution.add(rightBoundaryPlayerPosition, 1);
		System.out.println("Testing right: " + nextPositionDistribution);
		assertEquals(expectedNextPositionDistribution, nextPositionDistribution);		
	}

}
