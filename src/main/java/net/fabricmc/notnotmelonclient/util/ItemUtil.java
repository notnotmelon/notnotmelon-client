package net.fabricmc.notnotmelonclient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;

public class ItemUtil {
	public static NbtCompound getExtraAttributes(ItemStack stack) {
        if (stack == null || stack.getNbt() == null) return null;
        return stack.getSubNbt("ExtraAttributes");
    }

    private static final String SKYBLOCK_MENU_NAME = "SkyBlock Menu (Click)";
    public static Boolean isSkyblockMenu(ItemStack stack) {
        return !stack.isEmpty() && stack.getName().getString().equals(SKYBLOCK_MENU_NAME);
    }

    // returns the "hover stack" used in GUIs
    public static ItemStack getCursorStack() {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen == null || !(screen instanceof HandledScreen)) return null;
        ScreenHandler handler = ((HandledScreen) screen).getScreenHandler();
        return handler.getCursorStack();
    }
}
