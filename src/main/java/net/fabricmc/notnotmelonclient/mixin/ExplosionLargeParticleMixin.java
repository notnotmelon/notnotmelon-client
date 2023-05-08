package net.fabricmc.notnotmelonclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.notnotmelonclient.misc.WitherImpactHider;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.particle.ExplosionLargeParticle;

@Mixin(ExplosionLargeParticle.class)
public class ExplosionLargeParticleMixin {
	@Inject(at = @At("TAIL"), method = "<init>()V")
	private void init(CallbackInfo ci) {
		if (!Util.isSkyblock) return;
		
		if (!((Object) this instanceof ExplosionLargeParticle)) return;
		ExplosionLargeParticle particle = (ExplosionLargeParticle) (Object) this;
		if (!WitherImpactHider.initalParticleFilter(particle)) return;
		
		WitherImpactHider.registerExplosionCreation(particle);
	}
}
