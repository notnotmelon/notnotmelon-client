package net.fabricmc.notnotmelonclient.dungeons;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;

public class DungeonMap {
	public static void render(MatrixStack matrices) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null && client.world == null) return;
        ItemStack item = client.player.getInventory().main.get(8);
        NbtCompound tag = item.getNbt();

        if (tag == null || !tag.contains("map")) return;
        
        int mapid = Integer.parseInt(StringUtils.substringBetween(tag.asString(), "map:", "}"));
        MapState state = FilledMapItem.getMapState(mapid, client.world);
        if (state == null) return;

        matrices.push();
        matrices.translate(2, 2, 0);
        VertexConsumerProvider.Immediate vertices = client.getBufferBuilders().getEffectVertexConsumers();
        MapRenderer map = DungeonMapRenderer.getInstance();
        map.draw(matrices, vertices, mapid, state, false, 15728880);
        vertices.draw();
        matrices.pop();
    }
}
