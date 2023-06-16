package net.fabricmc.notnotmelonclient.dungeons.solvers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.notnotmelonclient.dungeons.Dungeons;
import net.fabricmc.notnotmelonclient.util.Line;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

import static net.fabricmc.notnotmelonclient.Main.client;
import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;

public class Blaze {
	public static List<BlazeEntity> puzzleBlazes;
	public static void render(WorldRenderContext wrc) {
		if (puzzleBlazes == null) return;
		if (puzzleBlazes.isEmpty()) {
			puzzleBlazes = null;
			return;
		}

		BlazeEntity blaze = puzzleBlazes.get(0);
		if (blaze.isDead()) {
			puzzleBlazes.remove(0);
			return;
		}
		RenderUtil.drawRainbowBoxOutline(blaze.getBoundingBox(), 12, 60);

		if (puzzleBlazes.size() == 1) return;
		BlazeEntity nextBlaze = puzzleBlazes.get(1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
		RenderSystem.lineWidth(3);
		RenderSystem.disableCull();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		MatrixStack matrices = RenderUtil.matrixOf(0, 0, 0);
		Line line = new Line(blaze.getBoundingBox().getCenter(), nextBlaze.getBoundingBox().getCenter());
		line.color = Color.RED;
		line.consumeVertices(buffer, matrices);
		tessellator.draw();
		RenderSystem.enableCull();
		RenderSystem.disableBlend();
	}

	public static void onChangeRoom() {
		puzzleBlazes = null;
		if (!CONFIG.blaze) return;
		ClientWorld world = client.world;
		assert world != null;
		List<BlazeEntity> blazes = world.getEntitiesByClass(BlazeEntity.class, Dungeons.getRoomBounds(), blaze -> true);
		if (blazes.isEmpty()) return;
		solve(blazes);
	}

	public enum PuzzleVariant {LOWEST_TO_HIGHEST, HIGHEST_TO_LOWEST}
	public static PuzzleVariant getVariant(List<BlazeEntity> blazes) {
		ClientWorld world = client.world;
		assert world != null;
		double[] center = Dungeons.getRoomCenter();
		BlockPos blockPos = new BlockPos(MathHelper.floor(center[0]), 19, MathHelper.floor(center[1]));
		Util.print(blockPos);
		return world.getBlockState(blockPos).getBlock() == Blocks.ANDESITE ? PuzzleVariant.HIGHEST_TO_LOWEST : PuzzleVariant.LOWEST_TO_HIGHEST;
	}

	public static void solve(List<BlazeEntity> blazes) {
		PuzzleVariant variant = getVariant(blazes);
		Util.print(variant.name());
		blazes.sort(variant == PuzzleVariant.HIGHEST_TO_LOWEST ? comparator.reversed() : comparator);
		puzzleBlazes = blazes;
	}

	public static Comparator<BlazeEntity> comparator = Comparator.comparing(blaze -> {
		String blazeName = Formatting.strip(blaze.getName().getString());
		if (blazeName == null) return Integer.MIN_VALUE;
		return Integer.parseInt(blazeName.substring(blazeName.indexOf('/') + 1, blazeName.length() - 1));
	});
}
