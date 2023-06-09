package net.fabricmc.notnotmelonclient.dungeons.solvers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.notnotmelonclient.dungeons.Dungeons;
import net.fabricmc.notnotmelonclient.util.Line;
import net.fabricmc.notnotmelonclient.util.MathUtil;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.fabricmc.notnotmelonclient.Main.client;
import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;

public class CreeperBeam {
	public static List<Line> lines;
	public static void render(WorldRenderContext wrc) {
		if (lines == null) return;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
		RenderSystem.lineWidth(3);
		RenderSystem.disableCull();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		MatrixStack matrices = RenderUtil.matrixOf(0, 0, 0);
		for (Line line : lines)
			line.consumeVertices(buffer, matrices);
		tessellator.draw();
		RenderSystem.enableCull();
		RenderSystem.disableBlend();
	}

	public static void onChangeRoom() {
		lines = null;
		if (!CONFIG.creeperBeam) return;
		ClientWorld world = client.world;
		assert world != null;
		List<CreeperEntity> creepers = new ArrayList<>();
		world.collectEntitiesByType(TypeFilter.instanceOf(CreeperEntity.class),
			Dungeons.getRoomBounds(),
			CreeperBeam::isPuzzleCreeper,
			creepers,
			1);
		if (creepers.isEmpty()) return;
		CreeperEntity creeper = creepers.get(0);
		solve(creeper);
	}

	public static void onEntitySpawned(Entity entity) {
		if (CONFIG.creeperBeam && entity instanceof CreeperEntity creeper)
			if (lines == null && Util.isDungeons() && Dungeons.getRoomBounds().contains(entity.getPos()))
				if (isPuzzleCreeper(creeper))
					solve(creeper);
	}

	public static boolean isPuzzleCreeper(CreeperEntity creeper) {
		return !creeper.isInvisible() && creeper.getMaxHealth() == 20 && creeper.getHealth() == 20 && !creeper.hasCustomName();
	}

	public static final Color[] beamColors = new Color[]{
		Color.MAGENTA,
		Color.YELLOW,
		Color.RED,
		Color.BLUE,
		Color.GREEN,
	};

	public static void solve(CreeperEntity creeper) {
		Box creeperBB = creeper.getBoundingBox();
		Box targetBB = new Box(creeperBB.minX - 0.1, 75, creeperBB.minZ - 0.1, creeperBB.maxX + 0.1, 78, creeperBB.maxZ + 0.1);
		Box roomBB = new Box(creeperBB.minX - 14, 67, creeperBB.minZ - 13, creeperBB.maxX + 14, 87, creeperBB.maxZ + 13);
		List<BlockPos> lanterns = MathUtil.blocksInArea(roomBB, (BlockState blockState) -> {
			Block block = blockState.getBlock();
			return block == Blocks.PRISMARINE || block == Blocks.SEA_LANTERN;
		});

		lines = new ArrayList<>();
		Set<BlockPos> used = new HashSet<>();
		for (int i = 0; i < lanterns.size(); i++) {
			for (int j = i + 1; j < lanterns.size(); j++) {
				BlockPos a = lanterns.get(i);
				if (used.contains(a)) continue;
				BlockPos b = lanterns.get(j);
				if (used.contains(b)) continue;

				Vec3d centerA = a.toCenterPos();
				Vec3d centerB = b.toCenterPos();
				if (MathUtil.lineIntersectsWithBox(targetBB, centerA, centerB)) {
					used.add(a);
					used.add(b);
					Line line = new Line(centerA, centerB);
					line.color = beamColors[lines.size()];
					lines.add(line);
					if (lines.size() == 5) return;
				}
			}
		}
	}
}
