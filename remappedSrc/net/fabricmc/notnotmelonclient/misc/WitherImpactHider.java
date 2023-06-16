package net.fabricmc.notnotmelonclient.misc;

import net.fabricmc.notnotmelonclient.util.MathUtil;
import net.fabricmc.notnotmelonclient.util.PointList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.entity.player.PlayerEntity;

public class WitherImpactHider {
	private static final MinecraftClient client = MinecraftClient.getInstance();

	public static Boolean isWitherImpactParticle(ExplosionLargeParticle particle) {
		Double x = particle.x;
		Double y = particle.y;
		Double z = particle.z;

		if (!particleCandidatesPerTile.contains(x, y, z)) return false;

		// wither impact always creates 8 explosions on the same tile.
		return particleCandidatesPerTile.get(x, y, z) >= 8;
	}

	private static PointList<Double> particleCandidatesPerTile = new PointList<>();
	private static long ageOfParticleCandidatesPerTile = -1; // particleCandidatesPerTile is cleared if the gametick changes
	public static void registerExplosionCreation(ExplosionLargeParticle particle) {
		if (client.world == null) return;

		long currentTick = client.world.getTime();
		if (ageOfParticleCandidatesPerTile != currentTick) {
			ageOfParticleCandidatesPerTile = currentTick;
			particleCandidatesPerTile = new PointList<>();
		}

		Double x = particle.x;
		Double y = particle.y;
		Double z = particle.z;

		particleCandidatesPerTile.add(x, y, z);
	}

	// attribute-based filters to check for hyperion particles
	public static Boolean initialParticleFilter(ExplosionLargeParticle particle) {
		// The particle max lifespan is between 6 and 9 ticks (randomized)
		if (particle.getMaxAge() < 6 || particle.getMaxAge() > 9) return false;

		boolean coordsCheck = false;
		if (
			Math.abs(particle.x - Math.rint(particle.x)) == 0.5 &&
			Math.abs(particle.z - Math.rint(particle.z)) == 0.5 &&
			particle.y - Math.rint(particle.y) == 0
		) coordsCheck = true;

		else {
			PlayerEntity player = client.player;
			if (
				player != null &&
				MathUtil.difference(player.getX(), particle.x) < 0.5 &&
				MathUtil.difference(player.getZ(), particle.z) < 0.5
			) coordsCheck = true;
		}
		
		if (!coordsCheck) return false;

		// The particle color is randomized between 0.5 and 1, but is always in greyscale
		return particle.red == particle.blue && particle.blue == particle.green && particle.alpha == 1;
	}
}
