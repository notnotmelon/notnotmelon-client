package net.fabricmc.notnotmelonclient.dungeons;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.minecraft.util.math.BlockPos;

public class TicTacToeSolver {
	public static void render(WorldRenderContext wrc) {
		RenderUtil.drawRainbowBoxOutline(new BlockPos(0, -55, 0), 6, 60);
	}
}
