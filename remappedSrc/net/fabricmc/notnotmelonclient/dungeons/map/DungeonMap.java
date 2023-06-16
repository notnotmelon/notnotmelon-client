package net.fabricmc.notnotmelonclient.dungeons.map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import org.apache.commons.lang3.StringUtils;
public class DungeonMap {
	public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null && client.world == null) return;
        ItemStack item = client.player.getInventory().main.get(8);
        NbtCompound tag = item.getNbt();

        if (tag == null || !tag.contains("map")) return;
        
        int mapId = Integer.parseInt(StringUtils.substringBetween(tag.asString(), "map:", "}"));
        MapState state = FilledMapItem.getMapState(mapId, client.world);
        if (state == null) return;

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(2, 2, 0);
        VertexConsumerProvider.Immediate vertices = client.getBufferBuilders().getEffectVertexConsumers();
        MapRenderer map = DungeonMapRenderer.getInstance();
        map.draw(matrices, vertices, mapId, state, false, 15728880);
        vertices.draw();
        matrices.pop();
    }
}
