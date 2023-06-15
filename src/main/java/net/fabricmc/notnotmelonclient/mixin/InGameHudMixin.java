package net.fabricmc.notnotmelonclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.dungeons.map.DungeonMap;
import net.fabricmc.notnotmelonclient.misc.StatusBars;
import net.fabricmc.notnotmelonclient.misc.Timers;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@ModifyVariable(method = "setOverlayMessage(Lnet/minecraft/text/Text;Z)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Text setOverlayMessage(Text message) {
        if (!Util.isSkyblock || !Config.getConfig().fancyBars) return message;
		return Text.literal(StatusBars.parseOverlayMessage(message.getString()));
    }

	@Inject(method = "renderMountHealth", at = @At("HEAD"), cancellable = true)
    private void renderMountHealth(CallbackInfo ci) {
        if (Util.isSkyblock && Config.getConfig().fancyBars) ci.cancel();
    }

	@Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void renderExperienceBar(CallbackInfo ci) {
        if (Util.isSkyblock && Config.getConfig().fancyBars) ci.cancel();
	}

	@Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void renderStatusBars(MatrixStack matrices, CallbackInfo ci) {
        if (!Util.isSkyblock) return;

		if (Util.isDungeons() && Config.getConfig().dungeonMap)
            DungeonMap.render(matrices);
        
        Timers.render(matrices);

        if (Config.getConfig().fancyBars) {
            StatusBars.draw(matrices);
            ci.cancel();
        }

        RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
	}
}
