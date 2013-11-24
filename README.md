#Odd

This was a class project for McGill's COMP 424 (Artificial Intelligence). The task was to create an AI that would play the game of [Odd](http://nickbentleygames.wordpress.com/2010/02/11/one-of-my-better-games-odd/) intelligently, with no more than 5 seconds of "thinking" allotted per turn. This project deliverable was given 100%.

## Introduction

Odd is a 2-player game on a hexagonal board, played with white and black stones. Each player may place either a white or a black stone on any unoccupied spot on the board. Players alternate, placing stones until the board is full. A cluster is defined to be a group of 5 or more adjacent, same-color stones. Player 1 wins of there are an odd number of clusters (irrespective of color) on the board, and player 2 wins if there are an even number of clusters. The full rules and details can be found here: http://nickbentleygames.wordpress.com/2010/02/11/one-of-my-better-games-odd/

## Intelligent Player

The player uses a standard Monte Carlo Tree Search strategy with Upper Confidence Bounds. Upon initiation of the game, it spins off a separate thread in which it continually expands the Monte Carlo Tree. Expansion is performed by traversing the tree to the best leaf based on UCB calculations. When the opponent makes a move, the player re-adjusts the root node to reflect the current state of the game. When required to make a move, the player determines the best immediate child of the root based on gathered statistics on win rates of all children, and re-adjusts the root to reflect the current state of the game. Further details are provided in the report.

## Playing

The main class is `boardgame.ServerGUI`. 

To play, launch the above class. Then, through the Launch dropdown, choose `launch  server`. Then, launch the `odd.MonteCarloPlayer` and `human player` in any order desired (launched as players 1 and 2 respectively).