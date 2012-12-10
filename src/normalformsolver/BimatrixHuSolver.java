/**
 * Code to solve 2 player bimatrix games for Nash equilibria.
 * Modified from http://www.junlinghu.com/papers.html
 * 
 * @author sodomka
 * 
 * Applet for calculating Nash equilibria for two-person bimatrix game. 
 * Copyright (c) Junling Hu and Yilian Zhang, 2001 
 * This is an Applet that allows an user to enter the payoff matrices, the then caculated possible Nash equilibria for the bimatrix game. 
 * The algorithm is Lemke-Howson method decribed in the following book:    
 * @book{Cottle91,
 *   author = {Cottle, Richard W. and Pang, J.--S. and R. E. Stone},
 *   title =	 {The Linear Complementarity Problem},
 *   year =	 1992,
 *   publisher = {Academic Press},
 *   address = {New York}
 */
package normalformsolver;

import java.util.List;
import props.DiscreteDistribution;
import props.Joint;
import sequentialgame.AbstractAction;
import normalformgame.NormalFormGame;

public class BimatrixHuSolver<A extends AbstractAction> implements NormalFormSolver<A> {

	static final int player1Idx = 0;
	static final int player2Idx = 1;
	
	int row;
	int col;
	int dimM;
	double[][] player1Payoffs;
	double[][] player2Payoffs;
	double[][] Z;
	
//	public static void main(String[] args) {
//		int numPlayers = 2;
//		GridAction a1 = new GridAction("a1");
//		GridAction a2 = new GridAction("a2");
//		List<GridAction> actions = new ArrayList<GridAction>();
//		actions.add(a1);
//		actions.add(a2);
//		Joint<List<GridAction>>  possibleActionsPerPlayer = new Joint<List<GridAction>>();
//		possibleActionsPerPlayer.add(actions);
//		possibleActionsPerPlayer.add(actions);
//		List<Joint<GridAction>> possibleJointActions = GridGame.computePossibleJointActions(actions, numPlayers);
//		NormalFormGame<GridAction> normalFormGame = new NormalFormGame<GridAction>(numPlayers, possibleActionsPerPlayer, possibleJointActions);
//		Random random = new Random();
//		for (int i=0; i<possibleJointActions.size(); i++) {
//			Joint<GridAction> jointActions = possibleJointActions.get(i);
//			Joint<Double> jointPayoffs = new Joint<Double>();
//			jointPayoffs.add((double) random.nextInt(100));
//			jointPayoffs.add((double) random.nextInt(100));
//			normalFormGame.addPayoffsForJointAction(jointActions, jointPayoffs);
//		}
//		System.out.println(normalFormGame);
//		BimatrixHuSolver<GridAction> solver = new BimatrixHuSolver<GridAction>();
//		GameSolution<GridAction> solution = solver.solve(normalFormGame);
//		System.out.println(solution);
//		System.out.println(solution.getJointActionDistribution());
//		System.out.println(solution.getExpectedPayoffs());
//	}

	
	@Override
	public GameSolution<A> solve(NormalFormGame<A> normalFormGame) {
		int numPlayers = normalFormGame.getNumPlayers();
		
		// TODO: What error handling to do if more than 2 players? 
		if (numPlayers > 2) {
			System.err.println("Bimatrix solver being used for " + numPlayers + " player game.");
		}
		
		// Get list of player actions.
		List<A> player1Actions = normalFormGame.getPossibleActionsForPlayer(player1Idx);
		List<A> player2Actions = normalFormGame.getPossibleActionsForPlayer(player2Idx);
		int numPlayer1Actions = player1Actions.size();
		int numPlayer2Actions = player2Actions.size();
		
		// Create payoff matrix.
		player1Payoffs = new double[numPlayer1Actions][numPlayer2Actions];
		player2Payoffs = new double[numPlayer1Actions][numPlayer2Actions];
		for (int player1ActionIndex=0; player1ActionIndex<numPlayer1Actions; player1ActionIndex++) {
			for (int player2ActionIndex=0; player2ActionIndex<numPlayer2Actions; player2ActionIndex++) {
				A player1Action = player1Actions.get(player1ActionIndex);
				A player2Action = player2Actions.get(player2ActionIndex);
				Joint<A> jointAction = new Joint<A>();
				jointAction.add(player1Action);
				jointAction.add(player2Action);
				Joint<Double> payoffsPerPlayer = normalFormGame.getPayoffsForJointAction(jointAction);
				player1Payoffs[player1ActionIndex][player2ActionIndex] = payoffsPerPlayer.get(player1Idx); 
				player2Payoffs[player1ActionIndex][player2ActionIndex] = payoffsPerPlayer.get(player2Idx);			}
		}
				
		// Set other variables defined elsewhere.
		row = player1Payoffs.length;
		col = player1Payoffs[0].length;
		dimM = row + col;
		Z = new double[row][dimM];

		// Solve for equilibrium.
		getnash(player1Payoffs,player2Payoffs,Z);
		
		// Convert solution out of the Z matrix.
		// Return the joint strategy.
		DiscreteDistribution<Joint<A>> jointStrategy = new DiscreteDistribution<Joint<A>>();
		double[] player1StrategyArr = getEquilibriumStrategyForPlayer(player1Idx);
		double[] player2StrategyArr = getEquilibriumStrategyForPlayer(player2Idx);
		for (int player1ActionIndex=0; player1ActionIndex<numPlayer1Actions; player1ActionIndex++) {		
			for (int player2ActionIndex=0; player2ActionIndex<numPlayer2Actions; player2ActionIndex++) {
				double probAction1 = player1StrategyArr[player1ActionIndex];
				double probAction2 = player2StrategyArr[player2ActionIndex];
				A player1Action = player1Actions.get(player1ActionIndex);
				A player2Action = player2Actions.get(player2ActionIndex);
				Joint<A> jointAction = new Joint<A>();
				jointAction.add(player1Action);
				jointAction.add(player2Action);
				jointStrategy.add(jointAction, probAction1*probAction2);
			}
		}
		
		GameSolution<A> solution = new UncorrelatedGameSolution<A>(normalFormGame, jointStrategy);
		return solution;
	}

	
	public double[] getEquilibriumStrategyForPlayer(int playerIdx) {
		int equilibriumIdxToConsider = 0; // always consider the first equilibrium found.
		double[] eqmStrategyForPlayer = null;
		if (playerIdx==0) {
			eqmStrategyForPlayer = new double[row];
			for (int actionIdx=0; actionIdx<row; actionIdx++) {
				eqmStrategyForPlayer[actionIdx] = Z[equilibriumIdxToConsider][actionIdx];
			}
		} else if (playerIdx==1) {
			eqmStrategyForPlayer = new double[col];
			for (int actionIdx=0; actionIdx<col; actionIdx++) {
				eqmStrategyForPlayer[actionIdx] = Z[equilibriumIdxToConsider][row+actionIdx];
			}
		}
		return eqmStrategyForPlayer;
	}

	
    public void AllClear(int WList[][],int ZList[][],double Q[],double LZ[][],int k){
		// Define index matrix WList and ZList,Q
	
		for (int i=0;i<dimM;i++){
			WList[i][1] = i; WList[i][0] =1;  //"W"
			ZList[i][1] = i; ZList[i][0] =2;  // "z"
			Q[i] = -1;
			LZ[k][i] = 0;
		}
    }
	
    public void getnash(double A[][] , double B[][],double Z[][]){
		
		double LA[][];
		double LB[][];
		double LZ[][];
		double M[][];
		int WList[][],ZList[][];
		double Q[];
		
		
		
		// initial Q and Z, WList, ZList	
		LA = new double[row][col];
		LB = new double[row][col];
		M = new double[dimM][dimM];
		LZ = new double[row][dimM];
		WList = new int[dimM][2];
		ZList = new int[dimM][2];
		Q = new double[dimM];
		
		
		
		/*Transform the payoff matrix into cost matrix*/
		/* LA = -A , LB = -B */
		Multiple2(A,LA,row,col,-1); 
		Multiple2(B,LB,row,col,-1);  
		
		/* find more than one Nash equilibrium solution*/
		for(int k = 0;k<row;k++){
			AllClear(WList,ZList,Q,LZ,k);    // initialize 
			Comp(LA,LB,M);
			
			getonenash(M,Q,WList,ZList,k,LA,LB,LZ); // get one nash solution
			Multiple1(LZ,Z,dimM,1,k);
		}
		//QMprint(LB,row,col);
	}
	
	public void getonenash(double M[][],double Q[],int WList[][],int ZList[][],int k, double LA[][],double LB[][], double LZ[][]){
		int c=0,r=0,j1;	
		int oldWList[][];
		oldWList = new int[dimM][2];
	 		
		// find c be the best action agent 2 can take
		c=find_min_col(LB,k);
		
		// Pivot get new q, M and exchange-elements
		//QMprint(M,dimM,dimM);
		Pivot(Q,M,c+row,k);	    
		Exchange_element(WList,ZList,c+row,k);
		
		//if(k==0) QMprint(M,dimM,dimM);
		// find r is the best action agent 1 can take against c
		r = find_min_row(LA,c);		
	
		//Pivot get new q, M and exchange-elements
		Pivot(Q,M,r,c+row); 
		Exchange_element(WList,ZList,r,c+row);
		//if(k==2) QMprint(M,dimM,dimM);
		//System.out.print("c=" +c +"r="+r+"\n");
		// get Z for k
		if (r==k){  // we have the solution
			get_final_z(WList,Q,LZ,k);
		}
		else{
			j1 = find_min_ratio(Q,M,r);
			//System.out.println("j1="+j1);
			if(j1==-1) {              //we find the solution
				get_final_z(WList,Q,LZ,k);
			}
			else{
				// get to original step 
				Pivot(Q,M,j1,r);
				if (k==2) QMprint(M,dimM,dimM);
				while(j1!=-1&&WList[j1][1]!=k){
					
					//copy Wlist to oldWlist
					Multiple2(WList,oldWList,dimM,2,1);  
					Exchange_element(WList,ZList, j1,r);
					r = find_complement(j1,oldWList,ZList);
					
					// go back to step 1. r is the new driving variable
					j1 = find_min_ratio(Q,M,r); 
					if ( j1!=-1) { Pivot(Q,M,j1,r);
					}
					//System.out.print(j1);
				}
				if(j1!=-1 ) Exchange_element(WList,ZList,j1,r);
				get_final_z(WList,Q,LZ,k);
			}
		}	   
		normalize(LZ,k);
    }
	
    // calculate  A1 = alpha * A0 , scalar multiplication
    public void Multiple2(double A0[][], double A1[][],int s,int t,double p){
		
		for (int i=0;i<s;i++){
			for (int j=0;j<t;j++){
				A1[i][j] = p*A0[i][j];
			}
		}
    }
	
	// calculate  A1 = alpha * A0 , scalar multiply, A0 and A1 are integers
	public void Multiple2(int A0[][], int A1[][],int s,int t,int prod){
		
		for (int i=0;i<s;i++){
			for (int j=0;j<t;j++){
				A1[i][j] = prod*A0[i][j];
			}
		}
    }
	
    // calculate  A1[k] = alpha * A0[k], scalar multiply on one row
    public void Multiple1(double A0[][], double A1[][],int t,double product,int k)
    {
		for (int i=0;i<t;i++){ 
			A1[k][i] = product*A0[k][i];
		}
    }
    
	// construct the M matrix
    public void Comp(double A0[][],double A1[][],double M1[][]){
		// want M be a positive matrix at the beginning
		double MaxA,MaxB;
		MaxA = find_abs_max(A0);MaxB = find_abs_max(A1); 
				
		for (int i=0;i<dimM;i++){
			for (int j=0;j<dimM;j++){
				// M(i,j) = 0 , i<m&j<m, i>m&j>m 
				if ((i<row&&j<row)||(i>=row&&j>=row)){
					M1[i][j] = 0;
				}
				else if (j>=row&&i<row) {
					M1[i][j] = A0[i][j-row]+ MaxA +1 ;
				}
				else {
					M1[i][j] = A1[j][i-row]+ MaxB + 1;   // transpose
				}
			}
		}
    }
	
	// find the absolute maxium of Matrix A0
    public double find_abs_max(double A0[][]){
		double Max=0;
		double temp;
		
		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++){
				temp= Math.abs(A0[i][j]);
				if ( temp > Max) Max = temp;
			}
		}
		return Max;
	}
	
	public int find_min_col(double L1[][],int k){
		double min = 200000;
		int c=0;
   	    for(int j=0;j<col;j++){
			if (L1[k][j]<min) {
				min = L1[k][j];
				c = j;
			}
	    }
		return c;
	}
	
	public int find_min_row(double L1[][],int c){
		double min = 200000;
		int r=0;
		for(int i=0;i<row;i++){
				if (L1[i][c]<min) {
					min = L1[i][c];
					r = i;
				}
		}
		return r;
	}
	
	public void Pivot(double Q1[],double M1[][], int r1, int c1){
		
		double pPoint ;
		double MLocal[][];
		double QLocal[];
		MLocal = new double[dimM][dimM];
		QLocal = new double[dimM];
		
		// the pivot point
		pPoint = M1[r1][c1];
		
		if (Math.abs(pPoint) <= 0.000000001) {
			return ;}
		
		for (int i=0;i<dimM;i++){
			if (i==r1) QLocal[i] = -Q1[r1]/pPoint ;
			else QLocal[i] = Q1[i] - (Q1[r1]/pPoint)*M1[i][c1];
			
			for (int j=0;j<dimM;j++){
				if (i==r1){
					if(j==c1) MLocal[i][j] = 1.0/pPoint;
					else MLocal[i][j] = - M1[r1][j] /pPoint;
				}
				else {
		    if(j==c1) MLocal[i][j] = M1[i][c1]/pPoint;
		    else MLocal[i][j] = M1[i][j] -(M1[i][c1]/pPoint)*M1[r1][j];
				}
			}
		}
		for ( int i=0;i<dimM;i++) Q1[i] = QLocal[i];
		  Multiple2(MLocal,M1,dimM,dimM,1);
    }
	
	//exchange rth row of WList and cth row of ZList 
    public void  Exchange_element(int WList1[][],int ZList1[][],int r,int c){
		int temp1,temp2;	
		temp1 = WList1[r][0]; temp2  = WList1[r][1];
		WList1[r][0]= ZList1[c][0]; WList1[r][1]=ZList1[c][1];
		ZList1[c][0]= temp1; ZList1[c][1] = temp2;
    }

	// This function is modified, get the final solution
    public void  get_final_z(int WList1[][],double Q1[],double Z1[][],int k){
		
		int j;
		for (int i=0;i<dimM;i++){
	    if (WList1[i][0] == 2) { j=WList1[i][1]; Z1[k][j] = Q1[i];}
	    
		}
    }
	
	// minumum ratio test
    public int find_min_ratio(double Q1[],double M1[][], int r){
		double min;
		double R[];
		int j=-1;
		double PostZero = 20000000;
		R = new double[dimM];
		
		//System.out.print("loop in min_ratio   ");
		for (int i=0;i<dimM;i++){
	    if (M1[i][r]<-0.00001) R[i] = -Q1[i]/M1[i][r];
	    else R[i] = PostZero;
		}
		min = PostZero;
		for (int i=0;i<dimM;i++){
			if(R[i]!=PostZero&&R[i]<min){
		min = R[i]; j=i;
			}
		}
		return j;
    }
	
	// returns the position index of the complement of Wlistj in Zlist 
    public int find_complement(int j,int WList1[][],int ZList1[][]){
		int l = -1;
		int temp1,temp2;
		
		temp1 = WList1[j][0] ;
		temp1 = 3-temp1;   /* (w,a)-> (z,a)  (z,a) ->(w,a) */
		temp2 = WList1[j][1] ;
		
		for (int i=0;i<dimM;i++){
			if (ZList1[i][0]==temp1&&ZList1[i][1]==temp2){
				l = i;
				break;
			}
		}
		
		return l;
    }


    public void QMprint(double M[][],int row,int col){
//		for (int i=0;i<row;i++){
//			for (int j=0;j<col;j++){
//				System.out.print(M[i][j]+" ");
//			}
//			//	    System.out.print("     " +Q[i]);
//			System.out.print("\n");
//		}
    }
	
	public void QMprint(int M[][],int row,int col){
//		for (int i=0;i<row;i++){
//			for (int j=0;j<col;j++){
//				System.out.print(M[i][j]+" ");
//			}
//			//	    System.out.print("     " +Q[i]);
//			System.out.print("\n");
//		}
    }
	
	
    public void normalize(double LZ1[][],int k) {
		double sum1=0 ,sum2 = 0;
		for (int i=0;i<row;i++)
			sum1 = sum1+ Math.abs(LZ1[k][i]);
		for (int i=row;i<dimM;i++)
			sum2 = sum2+ Math.abs(LZ1[k][i]);
		for (int i=0;i<dimM;i++){
			if (i<row) LZ1[k][i]= Math.abs(LZ1[k][i])/sum1;
			else  LZ1[k][i]= Math.abs(LZ1[k][i])/sum2;
		}
    }
}
