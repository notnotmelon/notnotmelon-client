package net.fabricmc.notnotmelonclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.misc.WitherImpactHider;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;

@Mixin(BillboardParticle.class)
public abstract class BillboardParticleMixin {
	@Inject(at = @At("HEAD"), method = "buildGeometry", cancellable = true)
	private void buildGeometry(CallbackInfo ci) {
		if (!Util.isSkyblock) return;
		if (!((Object) this instanceof ExplosionLargeParticle)) return;
		if (!Config.getConfig().witherImpactHider) return;
		ExplosionLargeParticle particle = (ExplosionLargeParticle) (Object) this;
		if (!WitherImpactHider.initalParticleFilter(particle)) return;
		
		if (WitherImpactHider.isWitherImpactParticle(particle)) {
			particle.markDead();
			ci.cancel();
		}
	}
}
