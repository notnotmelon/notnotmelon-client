package net.fabricmc.notnotmelonclient.util;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.awt.*;

public class Line {
	public float x1;
	public float y1;
	public float z1;
	public float x2;
	public float y2;
	public float z2;
	public Color color;

	public Line(float x1, float y1, float z1, float x2, float y2, float z2) {
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}

	public Line(Vec3d centerA, Vec3d centerB) {
		this.x1 = (float) centerA.x;
		this.y1 = (float) centerA.y;
		this.z1 = (float) centerA.z;
		this.x2 = (float) centerB.x;
		this.y2 = (float) centerB.y;
		this.z2 = (float) centerB.z;
	}

	public void consumeVertices(VertexConsumer vertexConsumer, MatrixStack matrices, Color color1, Color color2) {
		Matrix4f model = matrices.peek().getPositionMatrix();
		Matrix3f normal = matrices.peek().getNormalMatrix();
		Vec3d vector = MathUtil.normalize(x1, y1, z1, x2, y2, z2);
		vertexConsumer.vertex(model, x1, y1, z1).color(color1.getRed(), color1.getBlue(), color1.getGreen(), color1.getAlpha()).normal(normal, (float) vector.x, (float) vector.y, (float) vector.z).next();
		vertexConsumer.vertex(model, x2, y2, z2).color(color2.getRed(), color2.getBlue(), color2.getGreen(), color2.getAlpha()).normal(normal, (float) vector.x, (float) vector.y, (float) vector.z).next();
	}

	public void consumeVertices(VertexConsumer vertexConsumer, MatrixStack matrices) {
		assert color != null;
		consumeVertices(vertexConsumer, matrices, color, color);
	}
}
