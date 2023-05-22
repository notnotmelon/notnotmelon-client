package net.fabricmc.notnotmelonclient.dungeons;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.minecraft.util.math.BlockPos;

// The AI can only start in the corner or center.
// The AI is X. We are O

public class TicTacToeSolver {
	public static void render(WorldRenderContext wrc) {
		RenderUtil.drawRainbowBoxOutline(new BlockPos(0, 100, 0), 6, 60);
	}

	public static void onChangeRoom() {
		char[][] board = new char[][]{
        	{' ', ' ', ' '},
        	{' ', ' ', ' '},
        	{'X', ' ', ' '}
    	};
    	char team = 'O';
    	while (true) {
        	int[] bestMove = bestMove(board, team);
        	if (bestMove[0] == -1) break;
        	board[bestMove[0]][bestMove[1]] = team;
        	for (int i = 0; i < 3; i++) {
            	for (int j = 0; j < 3; j++) {
                	System.out.print(board[i][j]==' ' ? '.' : board[i][j]);
				}
				System.out.println();
			}
			System.out.println();
        	team = team == 'X' ? 'O' : 'X';
    	}
	}

	public static int evaluate(char[][] board) {
    	// columns
    	for (int i = 0; i < 3; i++)
        	if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i])
            	return board[0][i] == 'X' ? 1 : -1;
		
    	// rows
    	for (int i = 0; i < 3; i++)
        	if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2])
            	return board[i][0] == 'X' ? 1 : -1;
		
    	// diagonal
    	if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2])
        	return board[0][0] == 'X' ? 1 : -1;
    	if (board[2][0] != ' ' && board[2][0] == board[1][1] && board[1][1] == board[0][2])
        	return board[2][0] == 'X' ? 1 : -1;
       	
    	return 0;
	}

	public static int miniMax(char[][] board, char team) {
		int emptySquares = 0;
		for (int i = 0; i < 3; i++)
        	for (int j = 0; j < 3; j++)
				if (board[i][j] == ' ')
					emptySquares++;

		if (team == 'O') {
			return max(board, emptySquares);
		} else {
			return min(board, emptySquares);
		}
	}

	public static int max(char[][] board, int emptySquares) {
		int evaluation = evaluate(board);
		if (evaluation != 0 || emptySquares == 0) return evaluation;

		evaluation = Integer.MIN_VALUE;
    	for (int i = 0; i < 3; i++) {
        	for (int j = 0; j < 3; j++) {
            	if (board[i][j] == ' ') {
                	board[i][j] = 'O';
					evaluation = Math.max(evaluation, min(board, emptySquares - 1));
                	board[i][j] = ' ';
            	}
			}
		}
		return evaluation;
	}

	public static int min(char[][] board, int emptySquares) {
		int evaluation = evaluate(board);
		if (evaluation != 0 || emptySquares == 0) return evaluation;

		evaluation = Integer.MAX_VALUE;
    	for (int i = 0; i < 3; i++) {
        	for (int j = 0; j < 3; j++) {
            	if (board[i][j] == ' ') {
                	board[i][j] = 'X';
					evaluation = Math.min(evaluation, max(board, emptySquares - 1));
                	board[i][j] = ' ';
            	}
			}
		}
		return evaluation;
	}	

	public static int[] bestMove(char[][] board, char team) {
        int[] bestMove = new int[]{-1, -1};
		System.out.println("TURN: "+team);
		if (team == 'O') {
			int bestValue = Integer.MAX_VALUE;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (board[i][j] == ' ') {
						board[i][j] = team;
						int moveValue = miniMax(board, team);
						board[i][j] = ' ';
						System.out.print(moveValue == 1 ? " 1" : moveValue == 0 ? " 0" : moveValue);
						if (moveValue < bestValue) {
							bestMove[0] = i;
							bestMove[1] = j;
							bestValue = moveValue;
						}
					}else {System.out.print(" _");}
				}
				System.out.println();
			}
		} else if (team == 'X') {
			int bestValue = Integer.MIN_VALUE;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (board[i][j] == ' ') {
						board[i][j] = team;
						int moveValue = miniMax(board, team);
						board[i][j] = ' ';
						System.out.print(moveValue == 1 ? " 1" : moveValue == 0 ? " 0" : moveValue);
						if (moveValue > bestValue) {
							bestMove[0] = i;
							bestMove[1] = j;
							bestValue = moveValue;
						}
					}else {System.out.print(" _");}
				}
				System.out.println();
			}
		}
        
        return bestMove;
    }
}
