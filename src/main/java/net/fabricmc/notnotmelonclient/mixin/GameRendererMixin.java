package net.fabricmc.notnotmelonclient.mixin;

import net.fabricmc.notnotmelonclient.config.Config;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Inject(at = @At("HEAD"), method = "bobView", cancellable = true)
	public void bobView(CallbackInfo ci) {
		if (Config.getConfig().disableViewBobbing) ci.cancel();
	}

	@Inject(at = @At("HEAD"), method = "tiltViewWhenHurt", cancellable = true)
	public void tiltViewWhenHurt(CallbackInfo ci) {
		if (Config.getConfig().disableViewBobbing) ci.cancel();
	}
}