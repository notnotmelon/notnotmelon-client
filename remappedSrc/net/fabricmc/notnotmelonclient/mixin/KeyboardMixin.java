package net.fabricmc.notnotmelonclient.mixin;

import net.fabricmc.notnotmelonclient.config.categories.CommandKeybinds;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@Inject(at = @At("TAIL"), method = "onKey(JIIII)V")
	public void notnotmelonclient$onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		CommandKeybinds.onKeyPress(key, action);
	}
}
