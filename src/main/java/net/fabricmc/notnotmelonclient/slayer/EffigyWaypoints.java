package net.fabricmc.notnotmelonclient.slayer;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.LinkedList;
import java.util.List;

public class EffigyWaypoints {
	BlockPos[] effigyPositions = new BlockPos[]{
		new BlockPos(150,94,95),
		new BlockPos(193, 87, 119),
		new BlockPos(235, 104, 147),
		new BlockPos(240, 123, 118),
		new BlockPos(262, 93, 94),
		new BlockPos(293, 90, 134)
	};

	public static List<Box> toRender = new LinkedList<>();

	public static void render(WorldRenderContext wrc) {
		for (Box box : toRender)
			RenderUtil.drawRainbowBoxOutline(box, 12, 60);
	}

	public static void calculateEffigyPositions() {
		if (!Util.isRift()) {
			toRender.clear();
			return;
		}

		List<String> sidebar = Util.getSidebar();
		for (String s : sidebar) {
			if (s.startsWith("Effigies: ")) {
				s = s.replaceFirst("Effigies: ", "");
				System.out.println(s);
				return;
			}
		}

		toRender.clear();
	}
}
