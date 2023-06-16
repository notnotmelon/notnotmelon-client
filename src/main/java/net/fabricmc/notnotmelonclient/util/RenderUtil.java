package net.fabricmc.notnotmelonclient.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;

import java.awt.*;

import static net.fabricmc.notnotmelonclient.Main.client;

public class RenderUtil {
    public static void drawBoxOutline(Box box, float lineWidth, Color color1, Color color2) {
        if (!getFrustum().isVisible(box)) return;
        double distance = client.player.getPos().distanceTo(box.getCenter());
        if (distance > 500) return;

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
        
        Vec3d minVector = MathUtil.minVector(box);
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
    
    public static void drawBoxOutline(Box box) {
        drawBoxOutline(box, 8, Color.WHITE, Color.WHITE);
    }

	public static void drawBoxOutline(BlockPos blockPos, float lineWidth, Color color) {
        drawBoxOutline(blockPos, lineWidth, color, color);
    }

    public static PointList<Integer> boxedPoints = new PointList<>();
    public static long lastBoxedTick;
    public static void drawBoxOutline(BlockPos blockPos, float lineWidth, Color color1, Color color2) {
        long tick = Util.getGametick();
        if (lastBoxedTick != tick) {
            lastBoxedTick = tick;
            boxedPoints = new PointList<>();
        }
        
        boxedPoints.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        boxedPoints.add(blockPos.getX() - 1, blockPos.getY(), blockPos.getZ());
        boxedPoints.add(blockPos.getX() + 1, blockPos.getY(), blockPos.getZ());
        boxedPoints.add(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ());
        boxedPoints.add(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
        boxedPoints.add(blockPos.getX(), blockPos.getY(), blockPos.getZ() - 1);
        boxedPoints.add(blockPos.getX(), blockPos.getY(), blockPos.getZ() + 1);
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
			MathHelper.lerp(tickDelta, e.lastRenderX, position.x),
			MathHelper.lerp(tickDelta, e.lastRenderY, position.y),
			MathHelper.lerp(tickDelta, e.lastRenderZ, position.z)
		);
    }

	static final int[] offsets = new int[]{-1, 1};
	public static void drawTextWithOutline(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color) {
		int alpha = color>>24<<24;
		for (int i : offsets) {
			context.drawText(textRenderer, text, x + i, y, alpha, false);
			context.drawText(textRenderer, text, x, y + i, alpha, false);
		}
		context.drawText(textRenderer, text, x, y, color, false);
	}

	public static void drawCenteredTextWithOutline(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color) {
		x -= textRenderer.getWidth(text) / 2;
		drawTextWithOutline(context, textRenderer, text, x, y, color);
	}
}
