package net.fabricmc.notnotmelonclient.dungeons;

import java.util.HashMap;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.util.math.BlockPos;

// The AI can only start in the corner or center.
// The AI is X. We are O

public class TicTacToeSolver {
	public static void render(WorldRenderContext wrc) {
		RenderUtil.drawRainbowBoxOutline(new BlockPos(0, 100, 0), 6, 60);
	}

	public static void onChangeRoom() {
		Util.print("room changed!");
	}

	
	private class TicTacToeBoard {
		private String board;

		TicTacToeBoard(String board) {
			this.board = board;
		}

		// Coding is hard. Let's just hardcode it!
		private static HashMap<String, Integer> nextMoves = new HashMap<String, Integer>();
		static {
			nextMoves.put("... .X. ...", 0);

			nextMoves.put("0X. .X. ...", 7);
			nextMoves.put("0.. .X. .X.", 1);




			
			nextMoves.put("X........", 4);
		}
	}
}
