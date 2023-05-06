package net.fabricmc.notnotmelonclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.notnotmelonclient.misc.WitherImpactHider;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;

@Mixin(BillboardParticle.class)
public abstract class BillboardParticleMixin {
	@Inject(at = @At("HEAD"), method = "buildGeometry()V", cancellable = true)
	private void buildGeometry(CallbackInfo ci) {
		if (!((Object) this instanceof ExplosionLargeParticle)) return;
		ExplosionLargeParticle particle = (ExplosionLargeParticle) (Object) this;
		// Only render explosions if we are 100% sure they did not come from hyperion
		// Side effect: All explosions will not render for the first tick
		if (WitherImpactHider.isWitherImpactParticle(particle) != WitherImpactHider.Confidence.NO) ci.cancel();
	}
}
