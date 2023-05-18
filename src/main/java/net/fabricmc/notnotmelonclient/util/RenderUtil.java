package net.fabricmc.notnotmelonclient.util;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.notnotmelonclient.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;

public class RenderUtil {
	private final static MinecraftClient client = Main.client;

    public static void drawBoxOutline(Box box, float lineWidth, Color color1, Color color2) {
        if (!getFrustum().isVisible(box)) return;
        Vec3d minVector = MathUtil.minVector(box);
        double distance = client.player.getPos().distanceTo(minVector);
        if (distance > 200) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        MatrixStack matrices = matrixOf(box.minX, box.minY, box.minZ);

        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        lineWidth = 2 * (float) Math.max(1, lineWidth / Math.max(2, distance));
        RenderSystem.lineWidth(lineWidth);
		RenderSystem.disableCull();

		Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        
        box = box.offset(minVector.negate());
		float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

        new Line(x1, y1, z1, x2, y1, z1).consumeVertices(buffer, matrices, color1, color2);
		new Line(x2, y1, z2, x1, y1, z2).consumeVertices(buffer, matrices, color1, color2);
		new Line(x1, y1, z1, x1, y2, z1).consumeVertices(buffer, matrices, color1, color2);
		new Line(x2, y1, z2, x2, y2, z2).consumeVertices(buffer, matrices, color1, color2);
		new Line(x2, y2, z1, x2, y2, z2).consumeVertices(buffer, matrices, color1, color2);
		new Line(x1, y2, z2, x1, y2, z1).consumeVertices(buffer, matrices, color1, color2);
		new Line(x2, y1, z1, x2, y1, z2).consumeVertices(buffer, matrices, color2, color1);
		new Line(x1, y1, z2, x1, y1, z1).consumeVertices(buffer, matrices, color2, color1);
		new Line(x1, y1, z2, x1, y2, z2).consumeVertices(buffer, matrices, color2, color1);
		new Line(x2, y1, z1, x2, y2, z1).consumeVertices(buffer, matrices, color2, color1);
		new Line(x1, y2, z1, x2, y2, z1).consumeVertices(buffer, matrices, color2, color1);
		new Line(x2, y2, z2, x1, y2, z2).consumeVertices(buffer, matrices, color2, color1);
        tessellator.draw();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

	public static void drawBoxOutline(BlockPos blockPos, float lineWidth, Color color) {
        drawBoxOutline(blockPos, lineWidth, color, color);
    }

    public static PointList<Integer> boxedPoints = new PointList<Integer>();
    public static long lastBoxedTick;
    public static void drawBoxOutline(BlockPos blockPos, float lineWidth, Color color1, Color color2) {
        long tick = Util.getGametick();
        if (lastBoxedTick != tick) {
            lastBoxedTick = tick;
            boxedPoints = new PointList<Integer>();
        }
        
        boxedPoints.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        drawBoxOutline(new Box(blockPos), lineWidth, color1, color2);
    }

    public static void drawRainbowBoxOutline(BlockPos blockPos, float lineWidth, int period) {
        float hue1 = (Util.getGametick() % period + 1) / (float) period;
        float hue2 = (float) (hue1 + 0.25) % 1;
        drawBoxOutline(blockPos, lineWidth, Color.getHSBColor(hue1, 1, 1), Color.getHSBColor(hue2, 1, 1));
    }

    public static MatrixStack matrixOf(double x, double y, double z) {
        MatrixStack matrices = new MatrixStack();

        Camera camera = client.gameRenderer.getCamera();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
		Vec3d cameraPosition = camera.getPos();
        matrices.translate(x - cameraPosition.x, y - cameraPosition.y, z - cameraPosition.z);

        return matrices;
    }

	public static Frustum getFrustum() {
        return client.worldRenderer.frustum;
    }

	public static Vec3d interpolationVector(Entity e) {
        Vec3d position = e.getPos();
        double tickDelta = client.getTickDelta();
        return new Vec3d(
			position.x - MathHelper.lerp(tickDelta, e.lastRenderX, position.x),
			position.y - MathHelper.lerp(tickDelta, e.lastRenderY, position.y),
			position.z - MathHelper.lerp(tickDelta, e.lastRenderZ, position.z)
		);
    }
}
