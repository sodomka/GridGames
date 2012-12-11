package sequentialsimulator;

import props.DiscreteDistribution;
import props.Joint;
import sequentialgame.AbstractAction;
import sequentialgame.AbstractState;
import sequentialgame.SequentialGame;
import sequentialgame.grid.GridState;
import sequentialsolver.JointPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * A class for running agents with different policies and evaluating
 * agent payoffs.
 * @author betsy
 * @param <A>
 *
 * @param <S>
 * @param <A>
 */
public class Simulator<S extends AbstractState, A extends AbstractAction>{
	
	private int numGames;
	private JointPolicy<S,A> policy;
	private Map<S,Joint<Double>> transfers;
	SequentialGame<S,A> game;
	
	
	public Simulator(JointPolicy<S, A> policy, Map<S,Joint<Double>> transfers, 
			SequentialGame<S,A> game, int numGames){
		this.policy = policy;
		this.transfers = transfers;
		this.game = game;
		this.numGames = numGames;
		
		
	}
	/*
	 *  1) set up simulator
	 *  	- what policy, what game
	 *  	- how many games
	 *  
	 *  2) run a specific number of games
	 *  	- track how much of this?
	 *  3) tally results
	 *  	- average?
	 */
	
	/*
	 * @betsy
	 * 
	 * simulates games and totals the rewards for a set number of games
	 */
	public Joint<Double> simulateAgents(int numGames, int numIter){
		
		// initialize variables 
		Joint<Double> rewards = new Joint<Double>();
		for (int playerIdx=0; playerIdx<game.getNumPlayers(); playerIdx++) {
			rewards.add(0.0);
		}
		Joint<Double> rewardsTemp;
		
		//for the number of games specified, run a game and add the results to rewards
		for(int i=1;i<=numGames; i++){
			System.out.println("Game: "+i);
			rewardsTemp = playGame(numIter);
			for(int r =0;r<rewards.size();r++){
				rewards.set(r, rewards.get(r)+rewardsTemp.get(r));
			}
			System.out.println();
		}
		
		return rewards;
		
	}
	
	/*
	 * plays a game, the result is a Joint of total rewards over players
	 * 
	 * @betsy
	 */
	public Joint<Double> playGame(int numIterations){
		//initialize variables
		Joint<A> actionToPlay;
		DiscreteDistribution<S> transitionProb;
		S nextState;
		S state =  game.getStartingState();
		Joint<Double> rewards = new Joint<Double>();
		for (int playerIdx=0; playerIdx<game.getNumPlayers(); playerIdx++) {
			rewards.add(0.0);
		}
		Joint<Double> payoffs = new Joint<Double>();
		for (int playerIdx=0; playerIdx<game.getNumPlayers(); playerIdx++) {
			payoffs.add(0.0);
		}
		//System.out.println(state);
		int iteration = 0;
		//until the end of the game, agents take actions
		while(!game.isTerminalState(state) && iteration<numIterations){
			System.out.println("First "+state);
			System.out.println(transfers.get(state));
			actionToPlay = samplePolicy(state);
			transitionProb = game.getTransitionProbabilities(state, actionToPlay);
			nextState = sampleResultingState(transitionProb);
			Joint<Double> rewardsTemp = game.getImmediateRewards(state, actionToPlay, nextState);
			Joint<Double> payoffsTemp = transfers.get(state);
			
			for(int i =0;i<rewards.size();i++){
				rewards.set(i, rewards.get(i)+rewardsTemp.get(i));
				payoffs.set(i, rewards.get(i)+payoffsTemp.get(i));
				//System.out.println("up: "+ rewards.get(i));
				//System.out.println("up2:"+payoffsTemp.get(i));
			}
			System.out.println(payoffs);
			state = nextState;
			System.out.println("Next "+state);
			
			iteration+=1;
		}
		System.out.print("Game rewards: ");
		for (int playerIdx=0; playerIdx <rewards.size(); playerIdx++) {
			System.out.printf("%.3f", rewards.get(playerIdx));
			System.out.print(" ");
		}
		System.out.print("Game Payoffs: ");
		for (int playerIdx=0; playerIdx <rewards.size(); playerIdx++) {
			System.out.printf("%.3f", payoffs.get(playerIdx));
			System.out.print(" ");
		}
		System.out.println();
		return rewards;
	}
	
	//samples from a dist. of policies over states
	public Joint<A> samplePolicy(S state){
		
		DiscreteDistribution<Joint<A>> distActions = policy.get(state);
		Set <Joint<A>> possActions = distActions.keySet();
		double cumProb = 0.0;
		List<Double> probabilities = new ArrayList<Double>();
		List<Joint<A>> actions = new ArrayList<Joint<A>>();
		for(Joint<A> a : possActions){
			actions.add(a);
			cumProb +=distActions.get(a);
			probabilities.add(cumProb);
			
		}
		
		double epsilon = .001;
		if (Math.abs(cumProb-1) > epsilon) {
			System.err.println("Cumulative probability sums to " + cumProb);
		}
		
		Random rand = new Random();
		double randVal = rand.nextDouble();

		for(int i =0; i<actions.size();i++){
			if(randVal<=probabilities.get(i)){
				return actions.get(i);
			}
		}
		
		return actions.get(actions.size()-1);
	}
	
	//samples from a dist of next states given states and actions
	private S sampleResultingState(DiscreteDistribution<S> distStates) {
		
		Set <S> possStates = distStates.keySet();
		double cumProb = 0.0;
		List<Double> probabilities = new ArrayList<Double>();
		List<S> states = new ArrayList<S>();
		for(S s : possStates){
			states.add(s);
			cumProb +=distStates.get(s);
			probabilities.add(cumProb);
		}
		
		Random rand = new Random();
		double randVal = rand.nextDouble();
		
		for(int i =0; i<states.size();i++){
			if(randVal<=probabilities.get(i)){
				return states.get(i);
			}
		}
		return states.get(states.size()-1);
	}
	

}
