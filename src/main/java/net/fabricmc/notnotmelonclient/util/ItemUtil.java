package net.fabricmc.notnotmelonclient.util;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;

public class ItemUtil {
    // skyblock stores most nbt in the ExtraAttributes tag
	@Nullable public static NbtCompound getExtraAttributes(ItemStack stack) {
        if (stack == null || stack.getNbt() == null) return null;
        return stack.getSubNbt("ExtraAttributes");
    }

    // this also works in the rift
    private static final String SKYBLOCK_MENU_NAME = "SkyBlock Menu (Click)";
    public static Boolean isSkyblockMenu(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getName().getString().equals(SKYBLOCK_MENU_NAME);
    }

    // returns the "hover stack" used in GUIs
    @Nullable public static ItemStack getCursorStack() {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen == null || !(screen instanceof HandledScreen)) return null;
        ScreenHandler handler = ((HandledScreen) screen).getScreenHandler();
        return handler.getCursorStack();
    }

    // returns item IDs. for example SKYBLOCK_MENU or HYPERION
    @Nullable public static String getSkyBlockItemID(ItemStack stack) {
        if (stack == null) return null;
        NbtCompound extraAttributes = getExtraAttributes(stack);
        if (extraAttributes == null || !extraAttributes.contains("id")) return null;
        return extraAttributes.getString("id");
    }
}
