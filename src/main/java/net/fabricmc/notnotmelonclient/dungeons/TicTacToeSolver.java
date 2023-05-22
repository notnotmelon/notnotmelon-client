package net.fabricmc.notnotmelonclient.dungeons;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// The AI can only start in the corner or center.
// The AI is X. We are O

public class TicTacToeSolver {
	private static final MinecraftClient client = Main.client;

	public static void render(WorldRenderContext wrc) {
		RenderUtil.drawRainbowBoxOutline(new BlockPos(0, 100, 0), 6, 60);
	}

	public static void onChangeRoom() {
		ClientWorld world = client.world;
		List<ItemFrameEntity> maps = new ArrayList<ItemFrameEntity>();
		Iterable<Entity> entities = world.getEntities();
		for (Entity entity : entities) {
			if (entity instanceof ItemFrameEntity && Dungeons.getRoomBounds().contains(entity.getPos())) {
				ItemFrameEntity map = (ItemFrameEntity) entity;
				ItemStack stack = map.getHeldItemStack();
				if (stack != null && stack.getItem() instanceof FilledMapItem)
					maps.add(map);
			}
		}

		if (maps.size() >= 9) return;

		for (ItemFrameEntity map : maps) {
			MapState mapState = FilledMapItem.getMapState(map.getHeldItemStack(), (World) world);
			Util.print(getTeam(mapState));
		}
	}

	public static char getTeam(MapState mapState) {
		int red = mapState.colors[16384 / 2] & 255; // calcuate the RED value of the map's center pixel
		if (red == 114) {
			return 'X';
		} else if (red == 33) {
			return 'O';
		} else {
			return '?';
		}
	}

	public static int evaluate(char[][] board) {
    	// columns
    	for (int i = 0; i < 3; i++)
        	if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i])
            	return board[0][i] == 'X' ? -10 : 10;
		
    	// rows
    	for (int i = 0; i < 3; i++)
        	if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2])
            	return board[i][0] == 'X' ? -10 : 10;
		
    	// diagonal
    	if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2])
        	return board[0][0] == 'X' ? -10 : 10;
    	if (board[2][0] != ' ' && board[2][0] == board[1][1] && board[1][1] == board[0][2])
        	return board[2][0] == 'X' ? -10 : 10;
       	
    	return 0;
	}

	public static int miniMax(char[][] board, boolean max, int emptySquares) {
		if (emptySquares == 0) return 0;
		int evaluation = evaluate(board);
		if (evaluation == 10 || evaluation == -10) return evaluation;

		if (max) {
			int bestScore = -Integer.MIN_VALUE;
			for (int row = 0; row < 3; row++) {
				for (int col = 0; col < 3; col++) {
					if (board[row][col] == ' ') {
						board[row][col] = 'O';
						bestScore = Math.max(bestScore, miniMax(board, !max, emptySquares + 1));
						board[row][col] = ' ';
					}
				}
			}
			return bestScore - emptySquares;
		} else {
			int bestScore = Integer.MAX_VALUE;
			for (int row = 0; row < 3; row++) {
				for (int col = 0; col < 3; col++) {
					if (board[row][col] == ' ') {
						board[row][col] = 'X';
						bestScore = Math.min(bestScore, miniMax(board, !max, emptySquares + 1));
						board[row][col] = ' ';
					}
				}
			}
			return bestScore + emptySquares;
		}
	}

	public static int[] bestMove(char[][] board) {
        int[] bestMove = new int[]{-1, -1};

		int emptySquares = 0;
		for (int i = 0; i < 3; i++)
        	for (int j = 0; j < 3; j++)
				if (board[i][j] == ' ')
					emptySquares++;

		int bestValue = Integer.MAX_VALUE;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[i][j] == ' ') {
					board[i][j] = 'O';
					int moveValue = miniMax(board, false, emptySquares);
					board[i][j] = ' ';
					if (moveValue < bestValue) {
						bestMove[0] = i;
						bestMove[1] = j;
						bestValue = moveValue;
					}
				}
			}
		}
        
        return bestMove;
    }
}
