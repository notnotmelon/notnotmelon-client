package net.fabricmc.notnotmelonclient.mixin;

import net.fabricmc.notnotmelonclient.misc.ScrollableTooltips;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "close", at = @At("HEAD"))
	public void notnotmelonclient$close(CallbackInfo info) {
		ScrollableTooltips.reset();
	}
}
