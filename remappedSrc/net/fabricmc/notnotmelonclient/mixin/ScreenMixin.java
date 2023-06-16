package net.fabricmc.notnotmelonclient.mixin;

import net.fabricmc.notnotmelonclient.misc.ScrollableTooltips;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;

@Mixin(Screen.class)
public class ScreenMixin {
	@ModifyVariable(method = "renderTooltipFromComponents(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", ordinal = 6, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.BEFORE))
	public int notnotmelonclient$modifyXOffset(int x) {
		if ((Object) this instanceof HandledScreen && CONFIG.scrollableTooltips)
			if (!((Object) this instanceof CreativeInventoryScreen))
				return x + ScrollableTooltips.x;
		return x;
	}

    @ModifyVariable(method = "renderTooltipFromComponents(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", ordinal = 7, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.BEFORE))
	public int notnotmelonclient$modifyYOffset(int y) {
		if ((Object) this instanceof HandledScreen && CONFIG.scrollableTooltips)
			if (!((Object) this instanceof CreativeInventoryScreen))
				return y + ScrollableTooltips.y;
		return y;
	}

    @Inject(method = "close", at = @At("HEAD"))
	public void notnotmelonclient$close(CallbackInfo info) {
		ScrollableTooltips.reset();
	}
}
