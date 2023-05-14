package net.fabricmc.notnotmelonclient.util;

import java.util.Locale;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;

public class ItemUtil {
    // skyblock stores most nbt in the ExtraAttributes tag
    @Nullable public static NbtCompound getExtraAttributes(ItemStack stack) {
        if (stack == null || stack.getNbt() == null)
            return null;
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
        if (screen == null || !(screen instanceof HandledScreen))
            return null;
        ScreenHandler handler = ((HandledScreen<?>) screen).getScreenHandler();
        return handler.getCursorStack();
    }

    // returns item IDs. for example SKYBLOCK_MENU or HYPERION
    @Nullable public static String getSkyBlockItemID(ItemStack stack) {
        if (stack == null)
            return null;
        NbtCompound extraAttributes = getExtraAttributes(stack);
        if (extraAttributes == null || !extraAttributes.contains("id"))
            return null;
        return extraAttributes.getString("id");
    }

    // Returns the full item ID. Usually this is the same as the Skyblock ID.
    // This ID is used in several 3rd party APIs such as tricked.pro
    @Nullable public static String getFullItemID(ItemStack stack) {
        String base = getSkyBlockItemID(stack);
        if (base == null) return null;
        NbtCompound extraAttributes = getExtraAttributes(stack);
        StringBuilder result = new StringBuilder(base);
        result.append('-');

        try {
            if (base.equals("PET")) {
                JsonObject petInfo = new Gson().fromJson(extraAttributes.getString("petInfo"), JsonObject.class);
                result.append(petInfo.get("type").getAsString());
                result.append('-');
                result.append(petInfo.get("tier").getAsString());

            } else if (base.equals("ENCHANTED_BOOK")) {
                NbtCompound enchants = extraAttributes.getCompound("enchantments");
                String enchant = enchants.getKeys().stream().findFirst().get();
                result.append(enchant.toUpperCase(Locale.ENGLISH));
                result.append('-');
                result.append(enchants.getInt(enchant));

            } else if (base.equals("ATTRIBUTE_SHARD")) {
                // TODO

            } else if (base.equals("POTION")) {
                result.append(extraAttributes.getString("potion").toUpperCase(Locale.ENGLISH));
                result.append('-');
                result.append(extraAttributes.getInt("potion_level"));
                if (extraAttributes.contains("enhanced")) result.append("-ENHANCED");
                if (extraAttributes.contains("extended")) result.append("-EXTENDED");
                if (extraAttributes.contains("splash")) result.append("-SPLASH");

            } else if (base.equals("RUNE")) {
                NbtCompound runes = extraAttributes.getCompound("runes");
                String rune = runes.getKeys().stream().findFirst().get();
                result.append(rune.toUpperCase(Locale.ENGLISH));
                result.append('-');
                result.append(runes.getInt(rune));

            } else return base;
        } catch (NullPointerException e) {
            return base;
        }
        
        return result.toString();
    }
}
