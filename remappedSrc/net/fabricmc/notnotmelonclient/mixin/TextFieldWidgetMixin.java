package net.fabricmc.notnotmelonclient.mixin;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin extends ClickableWidget {
	protected TextFieldWidgetMixin(int x, int y, int width, int height, Text message) {
		super(x, y, width, height, message);
	}

    @Inject(at = @At("HEAD"), method = "charTyped", cancellable = true)
    public void charTyped(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if (chr == '/' && ((TextFieldWidget) (ClickableWidget) this).getText().equals("/")) cir.setReturnValue(false);
    }
}