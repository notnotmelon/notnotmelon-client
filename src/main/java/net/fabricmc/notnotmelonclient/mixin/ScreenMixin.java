package net.fabricmc.notnotmelonclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.notnotmelonclient.misc.ScrollableTooltips;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public class ScreenMixin {
	@ModifyVariable(method = "renderTooltipFromComponents(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", ordinal = 6, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.BEFORE))
	public int modifyXOffset(int x) {
		return x + ScrollableTooltips.x;
	}

    @ModifyVariable(method = "renderTooltipFromComponents(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", ordinal = 7, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.BEFORE))
	public int modifyYOffset(int y) {
		return y + ScrollableTooltips.y;
	}

    @Inject(method = "close", at = @At("HEAD"))
	public void close(CallbackInfo info) {
		ScrollableTooltips.reset();
	}
}
