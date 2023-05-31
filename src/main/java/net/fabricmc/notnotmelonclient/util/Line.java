package net.fabricmc.notnotmelonclient.util;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.awt.*;

public record Line(float x1, float y1, float z1, float x2, float y2, float z2) {
	public void consumeVertices(VertexConsumer vertexConsumer, MatrixStack matrices, Color color1, Color color2) {
		Matrix4f model = matrices.peek().getPositionMatrix();
		Matrix3f normal = matrices.peek().getNormalMatrix();
		Vec3d vector = MathUtil.normalize(x1, y1, z1, x2, y2, z2);
		vertexConsumer.vertex(model, x1, y1, z1).color(color1.getRed(), color1.getBlue(), color1.getGreen(), color1.getAlpha()).normal(normal, (float) vector.x, (float) vector.y, (float) vector.z).next();
		vertexConsumer.vertex(model, x2, y2, z2).color(color2.getRed(), color2.getBlue(), color2.getGreen(), color2.getAlpha()).normal(normal, (float) vector.x, (float) vector.y, (float) vector.z).next();
	}
}
