package net.fabricmc.notnotmelonclient.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.slot.Slot;

public class ScrollableTooltips {
	public static int x = 0;
    public static int y = 0;
    private static int currentSlot = -999;

	public static double scrollSensitivity(double vertical) {
		MinecraftClient client = MinecraftClient.getInstance();
		vertical = client.options.getDiscreteMouseScroll().getValue() ? Math.signum(vertical) : vertical;
		return vertical * client.options.getMouseWheelSensitivity().getValue() * 6.5;
	}

    public static void scrollVertical(double vertical) {
        y += scrollSensitivity(vertical);
    }

    public static void scrollHorizontal(double vertical) {
        x += scrollSensitivity(vertical);
    }

    public static void reset() {
        x = 0;
        y = 0;
		currentSlot = -999;
    }

	public static void changeHoveredSlot(Slot slot) {
		if (slot == null) {
			reset();
		} else if (slot.id != currentSlot) {
			reset();
			currentSlot = slot.id;
		}
	}
}
