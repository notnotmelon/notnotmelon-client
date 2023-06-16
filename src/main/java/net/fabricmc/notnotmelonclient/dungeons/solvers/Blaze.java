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
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

import static net.fabricmc.notnotmelonclient.Main.client;
import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;

public class Blaze {
	public static List<ArmorStandEntity> puzzleBlazes;
	public static void render(WorldRenderContext wrc) {
		RenderSystem.disableDepthTest();
		if (puzzleBlazes == null || puzzleBlazes.isEmpty()) return;
		ArmorStandEntity blaze = puzzleBlazes.get(0);
		while (blaze.isRemoved()) {
			puzzleBlazes.remove(0);
			if (puzzleBlazes.isEmpty()){
				puzzleBlazes = null;
				return;
			}
			blaze = puzzleBlazes.get(0);
		}
		Vec3d pos = blaze.getBoundingBox().getCenter();
		Box box = new Box(pos.x - 0.6, pos.y - 2, pos.z - 0.6, pos.x + 0.6, pos.y, pos.z + 0.6);
		RenderUtil.drawRainbowBoxOutline(box, 12, 60);

		if (puzzleBlazes.size() == 1) return;
		renderLine(blaze, puzzleBlazes.get(1));
	}

	public static void renderLine(ArmorStandEntity blazeA, ArmorStandEntity blazeB) {
		Vec3d vectorA = blazeA.getBoundingBox().getCenter();
		Vec3d vectorB = blazeB.getBoundingBox().getCenter();
		if (vectorA.y > vectorB.y)
			vectorA = new Vec3d(vectorA.x, vectorA.y - 2, vectorA.z);
		else
			vectorB = new Vec3d(vectorB.x, vectorB.y - 2, vectorB.z);

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
		RenderSystem.lineWidth(3);
		RenderSystem.disableCull();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		MatrixStack matrices = RenderUtil.matrixOf(0, 0, 0);
		new Line(vectorA, vectorB).consumeVertices(buffer, matrices, Color.RED, Color.RED);
		tessellator.draw();
		RenderSystem.enableCull();
		RenderSystem.disableBlend();
		RenderSystem.enableDepthTest();
	}

	public static void onEntitySpawned(Entity entity) {
		if (!CONFIG.blaze || !(entity instanceof ArmorStandEntity blaze)) return;
		if (puzzleBlazes != null || !Util.isDungeons() || !isPuzzleBlaze(blaze)) return;
		Box roomBB = Dungeons.getRoomBounds();
		if (!roomBB.contains(entity.getPos())) return;
		ClientWorld world = client.world;
		assert world != null;
		List<ArmorStandEntity> blazes = world.getEntitiesByClass(ArmorStandEntity.class, Dungeons.getRoomBounds(), Blaze::isPuzzleBlaze);
		if (blazes.size() == 10) solve(blazes);
	}

	public static void onChangeRoom() {
		if (!CONFIG.blaze || puzzleBlazes != null) return;
		ClientWorld world = client.world;
		assert world != null;
		List<ArmorStandEntity> blazes = world.getEntitiesByClass(ArmorStandEntity.class, Dungeons.getRoomBounds(), Blaze::isPuzzleBlaze);
		if (blazes.isEmpty()) return;
		solve(blazes);
	}

	public static boolean isPuzzleBlaze(ArmorStandEntity blaze) {
		String name = blaze.getName().getString();
		return blaze.hasCustomName() && !blaze.isRemoved() && name.contains("Blaze") && name.contains("/");
	}

	public enum PuzzleVariant {LOWEST_TO_HIGHEST, HIGHEST_TO_LOWEST}
	public static PuzzleVariant getVariant() {
		ClientWorld world = client.world;
		assert world != null;
		double[] center = Dungeons.getRoomCenter();
		BlockPos blockPos = new BlockPos(MathHelper.floor(center[0]), 19, MathHelper.floor(center[1]));
		Util.print(blockPos);
		return world.getBlockState(blockPos).getBlock() == Blocks.ANDESITE ? PuzzleVariant.HIGHEST_TO_LOWEST : PuzzleVariant.LOWEST_TO_HIGHEST;
	}

	public static void solve(List<ArmorStandEntity> blazes) {
		try {
			PuzzleVariant variant = getVariant();
			Util.print(variant.name());
			Util.print(blazes.size());
			blazes.sort(variant == PuzzleVariant.HIGHEST_TO_LOWEST ? comparator.reversed() : comparator);
			puzzleBlazes = blazes;
		} catch (Exception e) {
			Util.print("Failed to solve blaze puzzle! Please report this.");
			Util.print(e);
		}
	}

	public static Comparator<ArmorStandEntity> comparator = Comparator.comparing(blaze -> {
		String name = Formatting.strip(blaze.getName().getString());
		Util.print(name);
		return Integer.parseInt(name.substring(name.indexOf('/') + 1, name.length() - 1));
	});
}
