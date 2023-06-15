package net.fabricmc.notnotmelonclient.dungeons.solvers;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.dungeons.Dungeons;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.fabricmc.notnotmelonclient.util.Scheduler;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

// The AI can only start in the corner or center.
// The AI is X. We are O
// PUZZLE SOLVED! notnotmelon tied Tic Tac Toe! Good Job!

public class TicTacToe {
	private static final MinecraftClient client = Main.client;

	public static BlockPos bestMoveIndicator;

	public static void render(WorldRenderContext wrc) {
		if (bestMoveIndicator != null) RenderUtil.drawRainbowBoxOutline(bestMoveIndicator, 16, 60);
	}

	public static void onChangeRoom() {
		if (!Config.getConfig().ticTacToe) return;
		if (!updateBoard()) bestMoveIndicator = null;
	}

	public static void onEntitySpawned(Entity entity) {
		if (!Config.getConfig().ticTacToe) return;
		if (entity instanceof ItemFrameEntity itemFrame && Util.isDungeons() && Dungeons.getRoomBounds().contains(entity.getPos())) {
			Scheduler.schedule(() -> {
				char team = getTeam(itemFrame);
				if (team == 'X')
					updateBoard();
				else if (team == 'O')
					bestMoveIndicator = null;
			}, 1);
		}
	}

	public static boolean updateBoard() {
		ClientWorld world = client.world;
		List<ItemFrameEntity> itemFrames = world.getEntitiesByClass(ItemFrameEntity.class, Dungeons.getRoomBounds(), (ItemFrameEntity itemFrame) -> {
			ItemStack stack = itemFrame.getHeldItemStack();
			return stack != null && stack.getItem() instanceof FilledMapItem;
		});

		if (itemFrames.size() == 0 || itemFrames.size() >= 9) return false;

		BlockPos topLeft = null;
		Direction facing = null;
		char[][] board = new char[][]{
			{' ', ' ', ' '},
			{' ', ' ', ' '},
			{' ', ' ', ' '}
		};

		for (ItemFrameEntity itemFrame : itemFrames) {
			int x = (int) Math.floor(itemFrame.getX());
			int y = (int) Math.floor(itemFrame.getY());
			int z = (int) Math.floor(itemFrame.getZ());

			int row;
			if (y == 72) row = 0;
			else if (y == 71) row = 1;
			else if (y == 70) row = 2;
			else continue;

			int column;
			BlockPos mapPosition = new BlockPos(x, y, z);
			BlockPos left;
			BlockPos right;
			if (itemFrame.facing == Direction.EAST || itemFrame.facing == Direction.WEST) {
				left = mapPosition.add(0, 0, -1);
				right = mapPosition.add(0, 0, 1);
			} else {
				left = mapPosition.add(-1, 0, 0);
				right = mapPosition.add(1, 0, 0);
			}

			Block leftBlock = world.getBlockState(left).getBlock();
			Block rightBlock = world.getBlockState(right).getBlock();
			if (leftBlock == Blocks.POLISHED_ANDESITE) column = 0;
			else if (rightBlock == Blocks.POLISHED_ANDESITE) column = 2;
			else column = 1;

			if (itemFrame.facing == Direction.EAST || itemFrame.facing == Direction.NORTH)
				column = 2 - column;


			char team = getTeam(itemFrame);
			if (team == '?') continue;
			board[row][column] = team;

			if (topLeft == null) {
				facing = itemFrame.facing;
				if (facing == Direction.NORTH)
					topLeft = mapPosition.add(column, row, 1);
				else if (facing == Direction.SOUTH)
					topLeft = mapPosition.add(-column, row, -1);
				else if (facing == Direction.EAST)
					topLeft = mapPosition.add(-1, row, column);
				else if (facing == Direction.WEST)
					topLeft = mapPosition.add(1, row, -column);
			}
		}

		if (topLeft == null) return false;
		int[] bestMove = bestMove(board);
		if (bestMove[0] == -1 && bestMove[1] == -1) return false;

		if (facing == Direction.NORTH)
			bestMoveIndicator = topLeft.add(-bestMove[1], -bestMove[0], 0);
		else if (facing == Direction.SOUTH)
			bestMoveIndicator = topLeft.add(bestMove[1], -bestMove[0], 0);
		else if (facing == Direction.EAST)
			bestMoveIndicator = topLeft.add(0, -bestMove[0], -bestMove[1]);
		else if (facing == Direction.WEST)
			bestMoveIndicator = topLeft.add(0, -bestMove[0], bestMove[1]);
		return true;
	}

	public static char getTeam(ItemFrameEntity itemFrame) {
		MapState mapState = FilledMapItem.getMapState(itemFrame.getHeldItemStack(), client.world);
		if (mapState == null) return '?';
		int red = mapState.colors[8000] & 255;
		if (red == 114) {
			return 'X';
		} else if (red == 33) {
			return 'O';
		}
		return '?';
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
			int bestScore = Integer.MIN_VALUE;
			for (int row = 0; row < 3; row++) {
				for (int col = 0; col < 3; col++) {
					if (board[row][col] == ' ') {
						board[row][col] = 'O';
						bestScore = Math.max(bestScore, miniMax(board, false, emptySquares - 1));
						board[row][col] = ' ';
					}
				}
			}
			return bestScore + emptySquares;
		} else {
			int bestScore = Integer.MAX_VALUE;
			for (int row = 0; row < 3; row++) {
				for (int col = 0; col < 3; col++) {
					if (board[row][col] == ' ') {
						board[row][col] = 'X';
						bestScore = Math.min(bestScore, miniMax(board, true, emptySquares - 1));
						board[row][col] = ' ';
					}
				}
			}
			return bestScore - emptySquares;
		}
	}

	public static int[] bestMove(char[][] board) {
        int[] bestMove = new int[]{-1, -1};
		int bestValue = Integer.MIN_VALUE;

		int emptySquares = 0;
		for (int i = 0; i < 3; i++)
        	for (int j = 0; j < 3; j++)
				if (board[i][j] == ' ')
					emptySquares++;

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (board[row][col] != ' ') continue;
				board[row][col] = 'O';
				int score = miniMax(board, false, emptySquares - 1);
				System.out.print(score);
				board[row][col] = ' ';
				if (score > bestValue) {
					bestMove[0] = row;
					bestMove[1] = col;
					bestValue = score;
				}
			}
		}

        return bestMove;
    }
}
