package sequentialsimulator;

import java.util.Map;

import normalformsolver.BimatrixCocoSolver;
import normalformsolver.BimatrixCorrelatedEqmSolver;
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
import sequentialsolver.PolicyAndTransfers;

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
	private int solver;
	
	public SimulateRun(String filename, int numPlayers, int numGames, 
			int maxSolverIter, int maxGameMoves, double gamma, int solver){
		this.filename =filename;
		this.numPlayers = numPlayers;
		this.numGames=numGames;
		this.maxSolverIter=maxSolverIter;
		this.maxGameMoves=maxGameMoves;
		this.gamma=gamma;
		this.solver = solver;
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimulateRun sim;
		if(args.length>7){
			sim = new SimulateRun(args[0],Integer.parseInt(args[1]), 
				Integer.parseInt(args[2]),Integer.parseInt(args[3]), 
				Integer.parseInt(args[4]), Double.parseDouble(args[5]), Integer.parseInt(args[6]));
		}else{
			String filenm = "./input/grid1.txt";
			int numPlay = 2;
			int numGame = 100;
			int maxIter = 500;
			int maxMove = 200;
			double gam = .9;
			int solve = 1;
			sim = new SimulateRun(filenm,numPlay,numGame,maxIter,maxMove,gam, solve);
		}
		sim.simulateRun();
		
	}
	public void  simulateRun() {
		NormalFormSolver<GridAction> normalFormSolver = new BimatrixHuSolver<GridAction>();
		if(solver ==1){
			normalFormSolver = new BimatrixHuSolver<GridAction>();
		}else if(solver == 2){
			normalFormSolver = new BimatrixCocoSolver<GridAction>();
		}else if(solver == 3){
			normalFormSolver = new BimatrixCorrelatedEqmSolver<GridAction>();
		}
		
		MultiAgentValueIteration<GridState,GridAction> valueIteration = new MultiAgentValueIteration<GridState,GridAction>(maxSolverIter, normalFormSolver, gamma);

		SimpleBoard board = new SimpleBoard(filename);
		//Board board = new SimpleBoard(2, 2);
		//Board board = new SimpleBoard(2, 2);
		SequentialGame<GridState, GridAction> game = new GridGame(numPlayers, board);
		PolicyAndTransfers<GridState, GridAction> policyAndTransfers = valueIteration.generatePolicyAndTransfers(game);
		JointPolicy<GridState,GridAction> policy = policyAndTransfers.getPolicy();
		Map<GridState,Joint<Double>> transfers = policyAndTransfers.getTransfers();
		
		System.out.println("POLICY:\n" + policy.toString(.001));
		System.out.println("TRANSFERS:\n" + transfers.toString());
		
		//@betsy basic testing
		System.out.println("Running:" +numGames+" games. "+maxGameMoves+" moves allowed.");
		Simulator<GridState,GridAction> testSim = new Simulator<GridState,GridAction>(policy, transfers, game, 1);
		
		Joint<Double> payoff = testSim.simulateAgents(numGames, maxGameMoves);
		System.out.println("Ran:" +numGames+" games "+maxGameMoves+" moves per game were allowed.");
		System.out.print("Average Rewards: ");
		for (int playerIdx=0; playerIdx <payoff.size(); playerIdx++) {
			System.out.printf("%.3f", payoff.get(playerIdx)/numGames);
			System.out.print(" ");
		}
		System.out.println();
		
	}
	

}
