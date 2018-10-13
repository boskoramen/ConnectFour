/**
 *
 * @author isaiah.cruz
 */

package game_framework;

import java.util.ArrayList;

public class Move {
		private Board board;
		private int position;
		private ArrayList<Integer> victoryMoves;
		private ArrayList<Integer> uncheckedMoves;
		private final int tier;
		
		public Move(int a_position, Board a_board, int a_tier) {
			position = a_position;
			board = a_board;
			victoryMoves = new ArrayList<Integer>();
			uncheckedMoves = new ArrayList<Integer>();
			tier = a_tier;
			
			for(int i = 0; i < board.getXSIZE(); i++) {
				uncheckedMoves.add(i);
			}
		}
		public Move(int a_position, Move parentMove) {
			position = a_position;
			board = parentMove.board;
			victoryMoves = new ArrayList<Integer>();
			uncheckedMoves = new ArrayList<Integer>();
			tier = parentMove.tier + 1;
			
			for(int i = 0; i < board.getXSIZE(); i++) {
				uncheckedMoves.add(i);
			}
		}
		
		public void addVictoryMove(int position) {
			victoryMoves.add(position);
		}
		public void removeUncheckedMove(Integer pos) {
			uncheckedMoves.remove(pos);
		}
		public int getPosition() {
			return position;
		}
		public ArrayList<Integer> getVictoryMoves() {
			return victoryMoves;
		}
		public ArrayList<Integer> getUncheckedMoves() {
			return uncheckedMoves;
		}
		public int getTier() {
			return tier;
		}
	}