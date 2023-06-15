package net.fabricmc.notnotmelonclient.dungeons.map;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.dungeons.Dungeons;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

import static net.fabricmc.notnotmelonclient.Main.client;

public class ThreeWeirdos {
	public static BlockPos correctChest;
	public static void render(WorldRenderContext wrc) {
		if (correctChest == null) return;
		RenderUtil.drawRainbowBoxOutline(correctChest, 12, 60);
	}

	static String[] solutions = {
		"The reward is not in my chest!",
		"At least one of them is lying, and the reward is not in ",
		"My chest doesn't have the reward we are all telling the truth.",
		"My chest has the reward and I'm telling the truth!",
		"The reward isn't in any of our chests.",
		"Both of them are telling the truth. Also, "
	};

	public static ActionResult onMessage(Text text, String asString) {
		if (Config.getConfig().threeWeirdos && Util.isDungeons() && asString.startsWith("[NPC] "))
			for (String solution : solutions)
				if (asString.contains(solution))
					solve(asString);
		return ActionResult.PASS;
	}

	public static void solve(String message) {
		if (correctChest != null) return;
		String npcName = message.substring(message.indexOf("]") + 2, message.indexOf(":"));
		client.player.sendMessage(Text.literal(npcName + "§r has the blessing.").formatted(Formatting.BOLD));
		ClientWorld world = client.world;

		List<ArmorStandEntity> nameTags = new ArrayList<>();
		world.collectEntitiesByType(TypeFilter.instanceOf(ArmorStandEntity.class),
			Dungeons.getRoomBounds(),
			(ArmorStandEntity e) -> e.hasCustomName() && e.getCustomName().getString().contains(npcName),
			nameTags,
			1);
		if (nameTags.isEmpty()) return;
		ArmorStandEntity entity = nameTags.get(0);

		BlockPos pos = new BlockPos(entity.getBlockX(), 69, entity.getBlockZ());
		if (world.getBlockState(pos.north()).getBlock() == Blocks.CHEST) {
			correctChest = pos.north();
		} else if (world.getBlockState(pos.east()).getBlock() == Blocks.CHEST) {
			correctChest = pos.east();
		} else if (world.getBlockState(pos.south()).getBlock() == Blocks.CHEST) {
			correctChest = pos.south();
		} else if (world.getBlockState(pos.west()).getBlock() == Blocks.CHEST) {
			correctChest = pos.west();
		}
	}
}
