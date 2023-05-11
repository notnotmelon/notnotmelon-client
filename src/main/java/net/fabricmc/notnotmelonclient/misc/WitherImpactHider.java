package net.fabricmc.notnotmelonclient.misc;

import java.util.HashMap;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.MinecraftClient;

public class WitherImpactHider {
	public static Boolean isWitherImpactParticle(ExplosionLargeParticle particle) {
		Double x = particle.x;
		Double y = particle.y;
		Double z = particle.z;

		if (!particleCanidatesPerTile.containsKey(x)) return false;
		if (!particleCanidatesPerTile.get(x).containsKey(y)) return false;
		if (!particleCanidatesPerTile.get(x).get(y).containsKey(z)) return false;

		// wither impact always creates 8 explosions on the same tile.
		return particleCanidatesPerTile.get(x).get(y).get(z) >= 8;
	}

	private static HashMap<Double, HashMap<Double, HashMap<Double, Integer>>> particleCanidatesPerTile = new HashMap<Double, HashMap<Double, HashMap<Double, Integer>>>();
	private static long ageOfParticleCanidatesPerTile = -1; // particleCanidatesPerTile is cleared if the gametick changes
	public static void registerExplosionCreation(ExplosionLargeParticle particle) {
		long currentTick = MinecraftClient.getInstance().world.getTime();
		if (ageOfParticleCanidatesPerTile != currentTick) {
			ageOfParticleCanidatesPerTile = currentTick;
			particleCanidatesPerTile = new HashMap<Double, HashMap<Double, HashMap<Double, Integer>>>();
		}

		Double x = particle.x;
		Double y = particle.y;
		Double z = particle.z;

		if (!particleCanidatesPerTile.containsKey(x))
			particleCanidatesPerTile.put(x, new HashMap<Double, HashMap<Double, Integer>>());

		if (!particleCanidatesPerTile.get(x).containsKey(y))
			particleCanidatesPerTile.get(x).put(y, new HashMap<Double, Integer>());

		if (!particleCanidatesPerTile.get(x).get(y).containsKey(z))
			particleCanidatesPerTile.get(x).get(y).put(z, 0);
			
		particleCanidatesPerTile.get(x).get(y).put(z, particleCanidatesPerTile.get(x).get(y).get(z) + 1);
	}

	// attribute-based filters to check for hyperion particles
	public static Boolean initalParticleFilter(ExplosionLargeParticle particle) {
		// The particle max lifespan is between 6 and 9 ticks (randomized)
		if (particle.getMaxAge() < 6 || particle.getMaxAge() > 9) return false;

		// All particles spawn at coords x:*.5 y:*.0 z:*.5
		if (Math.abs(particle.x - Math.rint(particle.x)) != 0.5) return false;
		if (particle.y - Math.rint(particle.y) != 0) return false;
		if (Math.abs(particle.z - Math.rint(particle.z)) != 0.5) return false;

		// The particle color is randomized between 0.5 and 1, but is always in greyscale
		if (particle.red == particle.blue && particle.blue == particle.green && particle.alpha == 1) return true;
		return false;
	}
}
