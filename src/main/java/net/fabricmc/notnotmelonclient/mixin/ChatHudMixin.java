package net.fabricmc.notnotmelonclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;

@Mixin(ChatHud.class)
public class ChatHudMixin {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/OrderedText;FFI)I"), method = "render(Lnet/minecraft/client/util/math/MatrixStack;III)V")
    public int charTyped(TextRenderer textRenderer, MatrixStack matrices, OrderedText text, float x, float y, int color) {
		Util.drawText(matrices, textRenderer, x, y, text, color);
		return 1;
    }
}
