package net.fabricmc.notnotmelonclient.misc;

import net.fabricmc.notnotmelonclient.Util;
import net.minecraft.client.particle.ExplosionLargeParticle;

// Hyperion spawns six stacked explosion particles at the same location.

public class WitherImpactHider {
	public enum Confidence {
		NO,
		MAYBE,
		DEFINITELY
	}

	public static Confidence isWitherImpactParticle(ExplosionLargeParticle particle) {
		Util.print("explode!"+particle.x);
		return Confidence.DEFINITELY;
	}
}
