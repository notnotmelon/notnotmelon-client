package net.fabricmc.notnotmelonclient.misc;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public class CursorResetFix {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static double[] mousePosition;
    public static long tick;
	public static void onCloseScreen() {
        if (!Config.getConfig().fixCursorReset) return;
        
		mousePosition = Util.getMousePosition();
        tick = Util.getGametick();
        long handler = client.getWindow().getHandle();
        GLFW.glfwSetInputMode(handler, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
	}

    public static void onOpenScreen(HandledScreen<?> screen) {
        if (!Config.getConfig().fixCursorReset) return;

        if (mousePosition != null && tick != -1 && tick == Util.getGametick()) {
            Util.setMousePosition(mousePosition[0], mousePosition[1]);
        }
	}
}
