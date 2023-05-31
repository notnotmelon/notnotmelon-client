package net.fabricmc.notnotmelonclient.misc;

import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;

public class CursorResetFix {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static double[] mousePosition;
    public static long tick;
	public static void onCloseScreen() {
        if (!Config.getConfig().fixCursorReset) return;
        
		mousePosition = Util.getMousePosition();
        tick = Util.getGametick();
		Window window = client.getWindow();
        long handler = window.getHandle();
        GLFW.glfwSetInputMode(handler, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		Util.setMousePosition(window.getWidth() / 2f, window.getHeight() / 2f);
	}

    public static void onOpenScreen(HandledScreen<?> screen) {
        if (!Config.getConfig().fixCursorReset) return;

        if (mousePosition != null && tick != -1 && tick == Util.getGametick()) {
            Util.setMousePosition(mousePosition[0], mousePosition[1]);
        }
	}
}
