package net.fabricmc.notnotmelonclient.mixin;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;

@Mixin(AbstractInventoryScreen.class)
public abstract class AbstractInventoryScreenMixin {
    @Inject(method = "drawStatusEffects", at = @At("HEAD"), cancellable = true)
    public void drawStatusEffects(CallbackInfo ci) {
        if (CONFIG.potionEffectsGui) ci.cancel();
    }
}