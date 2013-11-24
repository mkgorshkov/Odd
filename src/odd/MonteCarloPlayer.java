package odd;

import boardgame.Board;
import boardgame.Move;
import boardgame.Player;
import boardgame.Server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.ArrayList;
import java.util.Random;

/**
 * My implementation of the player AI that is intended to work with the Odd package
 * for COMP 424 Winter 2013.
 * @author Maxim Gorshkov (260397155)
 * @date April 2013
 *
 */
public class MonteCarloPlayer extends Player {

	//List of moves that can be played by the player
	private ArrayList<Long> moveList;
	//Player ID
	private int ID;
	//Number of tries that the player should take - ex. Rollouts
	private int NumberOfTries;
	//Progressive rollout factor
	private int factor = 0;
	//Random number generator
	Random R = new Random();

	/**
	 * Constructor for player not given a rollout factor
	 */
	public MyPlayer() {
		super("MyPlayer");
		moveList = new ArrayList<Long>();
		NumberOfTries = 100;
	}
	
	/**
	 * Constructor for player given a rollout factor
	 * @param n - Rollout factor passed as an integer.
	 */
	public MyPlayer(int n) {
		super("MyPlayer");
		moveList = new ArrayList<Long>();
		NumberOfTries = n;
	}

	/**
	 * Choose which move to play given the state of the current board.
	 * @param board - Current state of the Board.
	 */
	public Move chooseMove(Board board) {
		// Get the valid moves that we can play
		OddBoard pb = (OddBoard) board;
		LinkedList<OddMove> validMoves = pb.getValidMoves();

		//Initialize the array taht will hold the wins
		int[] wins = new int[validMoves.size()];

		//Get the id of the player
		ID = board.getTurn();

		//If less than 5 left
		if (validMoves.size() > 110) {
			//Go through each of the moves and perform the rollouts using the quasi player
			for (int j = 0; j < NumberOfTries; j++) {
				for (int i = 0; i < validMoves.size(); i++) {
					OddMove temp = validMoves.get(i);
					OddBoard tempB = (OddBoard) board.clone();
					tempB.move(temp);

					if (RunSimulationQuasi(tempB)) {
						wins[i]++;
					}
				}
			}
		} else {
			//Otherwise we take into about the progressive rollout and the go though
			//and perform the rollouts over the random simulation
			factor += validMoves.size();
			for (int j = 0; j < NumberOfTries+factor; j++) {
				for (int i = 0; i < validMoves.size(); i++) {
					OddMove temp = validMoves.get(i);
					OddBoard tempB = (OddBoard) board.clone();
					tempB.move(temp);

					if (RunSimulation(tempB)) {
						wins[i]++;
					}
				}

			}
			
		}

		//Get the most wins from the game and the index where the most wins occur
		int largest = wins[0];
		int largestIndex = 0;

		for (int x = 0; x < wins.length; x++) {
			if (wins[x] > largest) {
				largest = wins[x];
				largestIndex = x;
			}
		}
		//Play the move that had the most wins
		return validMoves.get(largestIndex);
	}

	/**
	 * Runs the simulation using the quasi player
	 * @param b - clone of the current state b after playing the move in chooseMove
	 * @return boolean - whether following the game to end using quasi random moves won
	 */
	public boolean RunSimulationQuasi(Board b) {
		OddBoard Tboard = (OddBoard) b;
		LinkedList<OddMove> validMoves = Tboard.getValidMoves();
		// While we don't have a winner go through the quasiplayer
		while (Tboard.getWinner() == Tboard.NOBODY) {
			QRandom quasi = new QRandom((double) R.nextInt(100) / 100,
					(double) R.nextInt(100) / 100, validMoves.size());
			int t = quasi.getQuasi();
			Tboard.move(validMoves.get(t));
			
			validMoves = Tboard.getValidMoves();
		}

		if (Tboard.getWinner() == ID) {
			return true;
		}

		return false;
	}

	/**
	 * Runs the simulation using the random player
	 * @param b - clone of the current state b after playing the move in chooseMove
	 * @return boolean - whether following the game to end using random moves won
	 */
	public boolean RunSimulation(Board b) {
		OddBoard Tboard = (OddBoard) b;
		LinkedList<OddMove> validMoves = Tboard.getValidMoves();
		// While we don't have a winner go through the random player
		while (Tboard.getWinner() == Tboard.NOBODY) {
			Tboard.move(validMoves.get(R.nextInt(validMoves.size())));
			validMoves = Tboard.getValidMoves();

		}

		if (Tboard.getWinner() == ID) {
			return true;
		}

		return false;
	}

	/**
	 * Creates a new board.
	 * @return Board - an oddboard that we have created.
	 */
	public Board createBoard() {
		return new OddBoard();
	}
}

/**
 * Produces either a single Quasi-Random number given inputs such as the size of the sequence,
 * the percentage to skip, and percentage to ignore.
 * @author Maxim Gorshkov
 * @version 0.1
 * @date April 2013
 *
 */
class QRandom {
	// How many values does QRandom ignore from the beginning of sequence
	private int aIgnore;
	// How many values to skip between
	private int aSkip;
	// How many values are going to be in the sequence
	private int aSequenceSize;
	// Generate the values into here
	private ArrayList<Integer> aSequence;

	/**
	 * Constructor, we take in values and call to fill in the sequence.
	 * @param pIgnorePcnt - Percent of the values to ignore (0.0 - 1.0).
	 * @param pSkipPercent - Percent of the values to skip (0.0 - 1.0).
	 * @param pSequenceSize - The size of the sequence that will be generated at the beginning.
	 */
	public QRandom(double pIgnorePcnt, double pSkipPercent, int pSequenceSize) {
		// Set size.
		aSequenceSize = pSequenceSize;
		// Make the number to ignore a value rather than the doubles
		aIgnore = (int) Math.floor(aSequenceSize * pIgnorePcnt);
		// Make the number to skip a value rather than the doubles
		aSkip = (int) Math.floor(aSequenceSize * pSkipPercent);
		// Fill the sequence based on parameters
		fillSequence();
	}

	/**
	 * Generate the sequence given how many elements we want to have in the arrayList.
	 */
	private void fillSequence() {
		//Initialise sequence.
		aSequence = new ArrayList<Integer>();
		//Fill it up.
		for (int i = 0; i < aSequenceSize; i++) {
			aSequence.add(i);
		}
	}

	/**
	 * Swap around and scramble the elements inside the sequence.
	 */
	private void swap() {
		for (int i = 0; i < aSequenceSize; i++) {
			//We make sure that we're going until the halfway point.
			if (i < aSequenceSize / 2 && i % 2 == 0) {
				int temp = aSequence.get(i);
				aSequence.set(i, aSequence.get(aSequenceSize - 1 - i));
				aSequence.set(aSequenceSize - 1 - i, temp);
			}
		}

	}

	/**
	 * Ignore a certain amount of elements from the leftmost side onwards.
	 */
	private void ignore() {
		//If we're at the end of the list, no bother ignoring
		if(aIgnore == 0){
			return;
		}
		
		//If the amount we're ignoring is more than half of the the sequence, reduce it down.
		while(aIgnore >= aSequence.size()/2){
			aIgnore /= 2;
		}
		
		//Otherwise, start ignoring.
		for (int j = 0; j < aSequenceSize; j++) {
			if(j <= aIgnore){
				aSequence.remove(j);
				
			}
		}
	}

	/**
	 * Skip between a certain amount of elements.
	 */
	private void skip() {
		
		//If we're skipping more than how many elements we have, trim it down
		while(aSkip >= aSequence.size()){
			aSkip /= 2;
		}
		
		//If it's not at the first element of the sequence, last element or zero,
		//continue to skip
		if(aSkip != 1 && aSkip != aSequence.size() && aSkip != 0){
			for(int i = 0; i < aSequence.size(); i++){
				if(i % aSkip == 0){
					aSequence.remove(i);
				}
			}
		}
	}
	
	/**
	 * Get a random Quasi number from the ones that are left in sequence
	 * after we complete the swap, ignore, and skip.
	 * @return an integer which represents the move.
	 */
	public int getQuasi(){
		Random a = new Random();
		swap();
		ignore();
		skip();
		return aSequence.get(a.nextInt(aSequence.size()));
	}
}

