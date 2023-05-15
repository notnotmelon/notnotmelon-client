package net.fabricmc.notnotmelonclient.util;

import java.util.Locale;

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
	private static final MinecraftClient client = MinecraftClient.getInstance();

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
        Screen screen = client.currentScreen;
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
                NbtCompound attributes = extraAttributes.getCompound("attributes");
                String attribute = attributes.getKeys().stream().findFirst().get();
                result.append(attribute.toUpperCase(Locale.ENGLISH));
                result.append('-');
                result.append(attributes.getInt(attribute));

            } else if (base.equals("POTION")) {
                result.append(extraAttributes.getString("potion").toUpperCase(Locale.ENGLISH));
                result.append('-');
                result.append(extraAttributes.getInt("potion_level"));
                if (extraAttributes.contains("enhanced")) result.append("-ENHANCED");

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

    @Nullable public static String moulberryification(String itemID) {
        if (itemID.contains("PET-")) {
			return itemID.replace("PET-", "")
				.replace("COMMON", "0")
				.replace("UNCOMMON", "1")
				.replace("RARE", "2")
				.replace("EPIC", "3")
				.replace("LEGENDARY", "4")
				.replace("MYTHIC", "5")
				.replace('-', ';');
        } else if (itemID.contains("POTION-")) {
            return null;
		} else if (itemID.contains("ENCHANTED_BOOK-")) {
			return itemID.replace("ENCHANTED_BOOK-", "").replace('-', ';');
		} else if (itemID.contains("RUNE-")) {
			return itemID.replace('-', ';');
        } else if (itemID.contains("ATTRIBUTE_SHARD-")) {
			return null;
		} else {
			return itemID.replace(":", "-");
		}
    }
}
