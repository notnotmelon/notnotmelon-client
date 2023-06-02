package net.fabricmc.notnotmelonclient.mixin;

import net.fabricmc.notnotmelonclient.config.categories.CommandKeybinds;
import net.fabricmc.notnotmelonclient.misc.ScrollableTooltips;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
	@Inject(method = "onMouseScroll", at = @At("HEAD"))
    public void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo info) {
        long handle = MinecraftClient.getInstance().getWindow().getHandle();
		if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT))
        	ScrollableTooltips.scrollHorizontal(vertical);
        else
            ScrollableTooltips.scrollVertical(vertical);
    }

	@Inject(method = "onMouseButton", at = @At("TAIL"))
	public void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
		CommandKeybinds.onKeyPress(button, action);
	}
}
