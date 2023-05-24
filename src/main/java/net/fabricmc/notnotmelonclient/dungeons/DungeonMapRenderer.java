package net.fabricmc.notnotmelonclient.dungeons;

import net.fabricmc.notnotmelonclient.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import java.util.Objects;

public class DungeonMapRenderer extends MapRenderer {
    private static final MinecraftClient client = Main.client;

	private static DungeonMapRenderer instance;
	public static DungeonMapRenderer getInstance() {
		if (instance == null)
			instance = new DungeonMapRenderer(MinecraftClient.getInstance().getTextureManager());
		return instance;
	}

	public DungeonMapRenderer(TextureManager textureManager) {
		super(textureManager);
	}

    @Override public MapTexture getMapTexture(int id, MapState state) {
        return this.mapTextures.compute(id, (id2, texture) -> {
            if (texture == null) {
                return new DungeonMapTexture(id2, state);
            }
            texture.setState(state);
            return texture;
        });
    }

    class DungeonMapTexture extends MapRenderer.MapTexture {
        int ticksSinceTextureUpdate = 10;

        DungeonMapTexture(int id, MapState state) {
            super(id, state);
        }

        @Override public void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, boolean hidePlayerIcons, int light) {
            if (ticksSinceTextureUpdate-- == 0 || this.needsUpdate) {
                this.updateTexture();
                this.ticksSinceTextureUpdate = 13;
                this.needsUpdate = false;
            }
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.renderLayer);
            vertexConsumer.vertex(matrix4f, 0.0f, 128.0f, -0.01f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(light).next();
            vertexConsumer.vertex(matrix4f, 128.0f, 128.0f, -0.01f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(light).next();
            vertexConsumer.vertex(matrix4f, 128.0f, 0.0f, -0.01f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(light).next();
            vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, -0.01f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(light).next();
            int k = 0;
            for (MapIcon mapIcon : this.state.getIcons()) {
                if (hidePlayerIcons && !mapIcon.isAlwaysRendered()) continue;
                matrices.push();
                matrices.translate(0.0f + (float)mapIcon.getX() / 2.0f + 64.0f, 0.0f + (float)mapIcon.getZ() / 2.0f + 64.0f, -0.002f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)(mapIcon.getRotation() * 360) / 16.0f));
                matrices.scale(4.0f, 4.0f, 3.0f);
                matrices.translate(-0.125f, 0.125f, 0.0f);
                byte b = mapIcon.getTypeId();
                float g = (float)(b % 16) / 16.0f;
                float h = (float)(b / 16) / 16.0f;
                float l = (float)(b % 16 + 1) / 16.0f;
                float m = (float)(b / 16 + 1) / 16.0f;
                Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
                VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(MAP_ICONS_RENDER_LAYER);
                vertexConsumer2.vertex(matrix4f2, -1.0f, 1.0f, (float)k * -0.001f).color(255, 255, 255, 255).texture(g, h).light(light).next();
                vertexConsumer2.vertex(matrix4f2, 1.0f, 1.0f, (float)k * -0.001f).color(255, 255, 255, 255).texture(l, h).light(light).next();
                vertexConsumer2.vertex(matrix4f2, 1.0f, -1.0f, (float)k * -0.001f).color(255, 255, 255, 255).texture(l, m).light(light).next();
                vertexConsumer2.vertex(matrix4f2, -1.0f, -1.0f, (float)k * -0.001f).color(255, 255, 255, 255).texture(g, m).light(light).next();
                matrices.pop();
                if (mapIcon.getText() != null) {
                    TextRenderer textRenderer = client.textRenderer;
                    Text text = mapIcon.getText();
                    float o = textRenderer.getWidth(text);
                    float f2 = 25.0f / o;
                    Objects.requireNonNull(textRenderer);
                    float p = MathHelper.clamp(f2, 0.0f, 6.0f / 9.0f);
                    matrices.push();
                    matrices.translate(0.0f + (float)mapIcon.getX() / 2.0f + 64.0f - o * p / 2.0f, 0.0f + (float)mapIcon.getZ() / 2.0f + 64.0f + 4.0f, -0.025f);
                    matrices.scale(p, p, 1.0f);
                    matrices.translate(0.0f, 0.0f, -0.1f);
                    textRenderer.draw(text, 0.0f, 0.0f, -1, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, Integer.MIN_VALUE, light);
                    matrices.pop();
                }
                ++k;
            }
        }
    }
}
