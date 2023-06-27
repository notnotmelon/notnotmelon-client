package net.fabricmc.notnotmelonclient.mixin;

import net.fabricmc.notnotmelonclient.dungeons.map.DungeonMap;
import net.fabricmc.notnotmelonclient.misc.FancyBars;
import net.fabricmc.notnotmelonclient.misc.Timers;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@ModifyVariable(method = "setOverlayMessage(Lnet/minecraft/text/Text;Z)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Text notnotmelonclient$setOverlayMessage(Text message) {
        if (!Util.isSkyblock || !CONFIG.fancyBars) return message;
		return Text.literal(FancyBars.parseOverlayMessage(message.getString()));
    }

	@Inject(method = "renderMountHealth", at = @At("HEAD"), cancellable = true)
    private void notnotmelonclient$renderMountHealth(CallbackInfo ci) {
        if (Util.isSkyblock && CONFIG.fancyBars) ci.cancel();
    }

	@Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void notnotmelonclient$renderExperienceBar(CallbackInfo ci) {
        if (Util.isSkyblock && CONFIG.fancyBars) ci.cancel();
	}

	@Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void notnotmelonclient$renderStatusBars(DrawContext context, CallbackInfo ci) {
        if (!Util.isSkyblock) return;

		if (Util.isDungeons() && CONFIG.dungeonMap)
            DungeonMap.render(context);
        
        Timers.render(context);

        if (CONFIG.fancyBars) {
			boolean rift = Util.isRift();
            FancyBars.draw(context, rift);
            if (!rift) ci.cancel();
        }
	}

	@Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V", shift = At.Shift.AFTER), cancellable = true)
	private void notnotmelonclient$drawHeartsInRift(DrawContext context, CallbackInfo ci) {
		if (Util.isSkyblock && CONFIG.fancyBars) ci.cancel();
	}
}
