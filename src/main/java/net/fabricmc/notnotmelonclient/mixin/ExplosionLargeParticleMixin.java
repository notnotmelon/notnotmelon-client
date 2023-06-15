package net.fabricmc.notnotmelonclient.mixin;

import net.fabricmc.notnotmelonclient.misc.WitherImpactHider;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.particle.ExplosionLargeParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;

@Mixin(ExplosionLargeParticle.class)
public class ExplosionLargeParticleMixin {
	@Inject(at = @At("TAIL"), method = "<init>()V")
	private void init(CallbackInfo ci) {
		if (!Util.isSkyblock || !CONFIG.witherImpactHider) return;
		
		if (!((Object) this instanceof ExplosionLargeParticle particle)) return;
		if (!WitherImpactHider.initialParticleFilter(particle)) return;
		
		WitherImpactHider.registerExplosionCreation(particle);
	}
}
