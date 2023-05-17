package net.fabricmc.notnotmelonclient.util;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MathUtil {
	public static double diffrence(double a, double b) {
		return Math.abs(a - b);
	}

    public static Vec3d normalize(float x1, float y1, float z1, float x2, float y2, float z2) {
        float x = x2 - x1;
        float y = y2 - y1;
        float z = z2 - z1;
        float scale = MathHelper.sqrt(z*z + y*y + x*x);
        return new Vec3d(x / scale, y / scale, z / scale);
    }

	public static Vec3d minVector(Box box) {
        return new Vec3d(box.minX, box.minY, box.minZ);
    }

	public static Vec3d maxVector(Box box) {
        return new Vec3d(box.maxX, box.maxY, box.maxZ);
    }
}
