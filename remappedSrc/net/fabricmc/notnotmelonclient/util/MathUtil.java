package net.fabricmc.notnotmelonclient.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static net.fabricmc.notnotmelonclient.Main.client;

public class MathUtil {
	public static double difference(double a, double b) {
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

    public static Vec3d normalize(float angleRadians) {
        return new Vec3d(-MathHelper.sin(angleRadians), 0, MathHelper.cos(angleRadians));
    }

    public static Vec3d normalize(double angleRadians) {
        return new Vec3d(-Math.sin(angleRadians), 0, Math.cos(angleRadians));
    }

    public static boolean isLookingUpOrDown(Entity entity) {
        double pitch = entity.getPitch();
        return pitch == 90 || pitch == -90;
    }

    /**
     * @author qJake
     * @link <a href="https://stackoverflow.com/a/3235902">source</a>
     * <a href="https://creativecommons.org/licenses/by-sa/2.5/">licenses</a>
     * Modified
     */
    public static boolean lineIntersectsWithBox(Box box, Vec3d point1, Vec3d point2) {
        Vec3d minVec = minVector(box);
        Vec3d maxVec = maxVector(box);
        if (point2.x < minVec.x && point1.x < minVec.x) return false;
        if (point2.x > maxVec.x && point1.x > maxVec.x) return false;
        if (point2.y < minVec.y && point1.y < minVec.y) return false;
        if (point2.y > maxVec.y && point1.y > maxVec.y) return false;
        if (point2.z < minVec.z && point1.z < minVec.z) return false;
        if (point2.z > maxVec.z && point1.z > maxVec.z) return false;
        if (box.contains(point1)) return true;

        Holder<Vec3d> hitVec = new Holder<>();
        hitVec.value = new Vec3d(0, 0, 0);
        return (getIntersection(
            point1.x - minVec.x,
            point2.x - minVec.x,
            point1,
            point2,
            hitVec
        ) && isVecInYZ(box, hitVec.value))
            || (getIntersection(
            point1.y - minVec.y,
            point2.y - minVec.y,
            point1,
            point2,
            hitVec
        ) && isVecInXZ(box, hitVec.value))
            || (getIntersection(
            point1.z - minVec.z,
            point2.z - minVec.z,
            point1,
            point2,
            hitVec
        ) && isVecInXY(box, hitVec.value))
            || (getIntersection(
            point1.x - maxVec.x,
            point2.x - maxVec.x,
            point1,
            point2,
            hitVec
        ) && isVecInYZ(box, hitVec.value))
            || (getIntersection(
            point1.y - maxVec.y,
            point2.y - maxVec.y,
            point1,
            point2,
            hitVec
        ) && isVecInXZ(box, hitVec.value))
            || (getIntersection(
            point1.z - maxVec.z,
            point2.z - maxVec.z,
            point1,
            point2,
            hitVec
        ) && isVecInXY(box, hitVec.value));
    }

    /**
     * @author qJake
     * @link <a href="https://stackoverflow.com/a/3235902">source</a>
     * <a href="https://creativecommons.org/licenses/by-sa/2.5/">licenses</a>
     * Modified
     */
    private static boolean getIntersection(
        double dist1,
        double dist2,
        Vec3d point1,
        Vec3d point2,
        Holder<Vec3d> hitVec
    ) {
        if (dist1 == dist2 || (dist1 * dist2) >= 0) return false;
        hitVec.value = point1.add(point2.subtract(point1).multiply(-dist1 / (dist2 - dist1)));
        return true;
    }

    private static boolean isVecInYZ(Box box, Vec3d vec) {
        return vec.y >= box.minY && vec.y <= box.maxY && vec.z >= box.minZ && vec.z <= box.maxZ;
    }

    private static boolean isVecInXZ(Box box, Vec3d vec) {
        return vec.x >= box.minX && vec.x <= box.maxX && vec.z >= box.minZ && vec.z <= box.maxZ;

    }

    private static boolean isVecInXY(Box box, Vec3d vec) {
        return vec.x >= box.minX && vec.x <= box.maxX && vec.y >= box.minY && vec.y <= box.maxY;
    }

    private static class Holder<T>{ T value; }

    public static List<BlockPos> blocksInArea(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, @Nullable Predicate<BlockState> predicate) {
        assert client.world != null;
        List<BlockPos> result = new ArrayList<>();
        for (int x = minX; x <= maxX; x++)
            for (int y = maxY; y >= minY; y--)
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos blockPos = new BlockPos(x, y, z);
                    if (predicate == null || predicate.test(client.world.getBlockState(blockPos)))
                        result.add(blockPos);
                }
        return result;
    }

    public static List<BlockPos> blocksInArea(Box box, @Nullable Predicate<BlockState> predicate) {
        return blocksInArea(
            MathHelper.floor(box.minX),
            MathHelper.floor(box.minY),
            MathHelper.floor(box.minZ),
            MathHelper.ceil(box.maxX),
            MathHelper.ceil(box.maxY),
            MathHelper.ceil(box.maxZ),
            predicate
        );
    }
}
