package sequentialsimulator;

import normalformsolver.BimatrixHuSolver;
import normalformsolver.NormalFormSolver;
import props.Joint;
import sequentialgame.SequentialGame;
import sequentialgame.grid.GridAction;
import sequentialgame.grid.GridGame;
import sequentialgame.grid.GridState;
import sequentialgame.grid.SimpleBoard;
import sequentialsolver.JointPolicy;
import sequentialsolver.MultiAgentValueIteration;

public class SimulateRun {

	/**
	 * @param args
	 */
	private String filename;
	private int numPlayers;
	private int numGames;
	private int maxSolverIter;
	private int maxGameMoves;
	private double gamma;
	
	public SimulateRun(String filename, int numPlayers, int numGames, 
			int maxSolverIter, int maxGameMoves, double gamma){
		this.filename =filename;
		this.numPlayers = numPlayers;
		this.numGames=numGames;
		this.maxSolverIter=maxSolverIter;
		this.maxGameMoves=maxGameMoves;
		this.gamma=gamma;
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimulateRun sim;
		if(args.length>6){
			sim = new SimulateRun(args[0],Integer.parseInt(args[1]), 
				Integer.parseInt(args[2]),Integer.parseInt(args[3]), 
				Integer.parseInt(args[4]), Double.parseDouble(args[5]));
		}else{
			String filenm = "./input/grid4.txt";
			int numPlay = 2;
			int numGame = 100;
			int maxIter = 100;
			int maxMove = 2;
			double gam = .9;
			sim = new SimulateRun(filenm,numPlay,numGame,maxIter,maxMove,gam);
		}
		sim.simulateRun();
		
	}
	public void  simulateRun() {
		
		NormalFormSolver<GridAction> normalFormSolver = new BimatrixHuSolver<GridAction>();
		MultiAgentValueIteration<GridState,GridAction> valueIteration = new MultiAgentValueIteration<GridState,GridAction>(maxSolverIter, normalFormSolver, gamma);

		SimpleBoard board = new SimpleBoard(filename);
		//Board board = new SimpleBoard(2, 2);
		
		SequentialGame<GridState, GridAction> game = new GridGame(numPlayers, board);
		JointPolicy<GridState,GridAction> policy = valueIteration.generatePolicy(game);
		System.out.println("POLICY:\n" + policy.toString(.001));
		
		//@betsy basic testing
		System.out.println("Running:" +numGames+" games. "+maxGameMoves+" moves allowed.");
		Simulator<GridState,GridAction> testSim = new Simulator<GridState,GridAction>(policy, game, 1);
		
		Joint<Double>rewards = testSim.simulateAgents(numGames, maxGameMoves);
		System.out.println("Ran:" +numGames+" games "+maxGameMoves+" moves per game were allowed.");
		System.out.print("Average Rewards: ");
		for (int playerIdx=0; playerIdx <rewards.size(); playerIdx++) {
			System.out.printf("%.3f", rewards.get(playerIdx)/numGames);
			System.out.print(" ");
		}
		System.out.println();
		
	}
	

}
