package net.fabricmc.notnotmelonclient.mixin;

import net.fabricmc.notnotmelonclient.misc.WitherImpactHider;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;

@Mixin(BillboardParticle.class)
public abstract class BillboardParticleMixin {
	@Inject(at = @At("HEAD"), method = "buildGeometry", cancellable = true)
	private void buildGeometry(CallbackInfo ci) {
		if (!Util.isSkyblock) return;
		if (!((Object) this instanceof ExplosionLargeParticle particle)) return;
		if (!CONFIG.witherImpactHider) return;
		if (!WitherImpactHider.initialParticleFilter(particle)) return;
		
		if (WitherImpactHider.isWitherImpactParticle(particle)) {
			particle.markDead();
			ci.cancel();
		}
	}
}
