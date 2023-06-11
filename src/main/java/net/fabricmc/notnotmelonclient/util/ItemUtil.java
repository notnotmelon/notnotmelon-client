package net.fabricmc.notnotmelonclient.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.notnotmelonclient.api.ApiRequests;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.NoSuchElementException;

import static net.fabricmc.notnotmelonclient.Main.client;

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
        Screen screen = client.currentScreen;
        if (!(screen instanceof HandledScreen))
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
        if (extraAttributes == null) return null;
        StringBuilder result = new StringBuilder(base);
        result.append('-');

        try {
            switch (base) {
                case "PET" -> {
                    JsonObject petInfo = new Gson().fromJson(extraAttributes.getString("petInfo"), JsonObject.class);
                    result.append(petInfo.get("type").getAsString());
                    result.append('-');
                    result.append(petInfo.get("tier").getAsString());
                }
                case "ENCHANTED_BOOK" -> {
                    NbtCompound enchants = extraAttributes.getCompound("enchantments");
                    String enchant = enchants.getKeys().stream().findFirst().get();
                    result.append(enchant.toUpperCase(Locale.ENGLISH));
                    result.append('-');
                    result.append(enchants.getInt(enchant));
                }
                case "ATTRIBUTE_SHARD" -> {
                    NbtCompound attributes = extraAttributes.getCompound("attributes");
                    String attribute = attributes.getKeys().stream().findFirst().get();
                    result.append(attribute.toUpperCase(Locale.ENGLISH));
                    result.append('-');
                    result.append(attributes.getInt(attribute));
                }
                case "POTION" -> {
                    result.append(extraAttributes.getString("potion").toUpperCase(Locale.ENGLISH));
                    result.append('-');
                    result.append(extraAttributes.getInt("potion_level"));
                    if (extraAttributes.contains("enhanced")) result.append("-ENHANCED");
                }
                case "RUNE" -> {
                    NbtCompound runes = extraAttributes.getCompound("runes");
                    String rune = runes.getKeys().stream().findFirst().get();
                    result.append(rune.toUpperCase(Locale.ENGLISH));
                    result.append('-');
                    result.append(runes.getInt(rune));
                }
                default -> {
                    return base;
                }
            }
        } catch (NullPointerException | NoSuchElementException e) {
            return base;
        }

        return result.toString();
    }

    public static String moulberryification(String itemID) {
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
            return itemID.replace("POTION-", "").replace("-ENCHANTED", "");
		} else if (itemID.contains("ENCHANTED_BOOK-")) {
			return itemID.replace("ENCHANTED_BOOK-", "").replace('-', ';').replaceFirst("-\\d", "");
		} else if (itemID.contains("RUNE-")) {
			return itemID.replace('-', ';');
        } else if (itemID.contains("ATTRIBUTE_SHARD-")) {
			return "ATTRIBUTE_SHARD";
		} else {
			return itemID.replace(":", "-");
		}
    }

    public static int getRarity(ItemStack stack) {
        try {
            NbtList nbtList = stack.getSubNbt(ItemStack.DISPLAY_KEY).getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE);

            for (int j = nbtList.size() - 1; j >= 0; --j) {
                Text t = Text.Serializer.fromJson(nbtList.getString(j));
                if (t == null) continue;
                String line = t.getString();
                line = line.replaceAll("§(k.|.)", "").trim();
                int i = line.indexOf(' ');
                String rarity = i == -1 ? line : line.substring(0, i);
                switch (rarity) {
                    case "COMMON": return 1;
                    case "UNCOMMON": return 2;
                    case "RARE": return 3;
                    case "EPIC": return 4;
                    case "LEGENDARY": return 5;
                    case "MYTHIC": return 6;
                    case "DIVINE", "SUPREME": return 7;
                    case "SPECIAL": return 8;
                    case "VERY SPECIAL": return 9;
                    case "ADMIN": return 10;
                };
            }
        } catch (Exception e) { return 0; }
        return 0;
    }

    public static float getValue(ItemStack stack) {
        String itemID = ItemUtil.getFullItemID(stack);
        if (itemID == null) return 0;

        String bazaarID = itemID.replace('-', '_').replace("ENCHANTED_BOOK", "ENCHANTMENT");
        JsonObject bazaarPrices = ApiRequests.bazaarPrices.getJSON();
        if (bazaarPrices != null && bazaarPrices.has(bazaarID)) {
            JsonObject jsonObject = bazaarPrices.getAsJsonObject(bazaarID);
            if (!jsonObject.get("sellPrice").isJsonNull()) return jsonObject.get("sellPrice").getAsFloat();
        }

        JsonObject lowestBins = ApiRequests.lowestBins.getJSON();
        if (lowestBins != null && lowestBins.has(itemID)) return lowestBins.get(itemID).getAsFloat();

        JsonObject averageBins = ApiRequests.averageBins.getJSON();
        String moulberryID = ItemUtil.moulberryification(itemID);
        if (averageBins != null && averageBins.has(moulberryID)) return averageBins.get(moulberryID).getAsFloat();

        JsonObject npcPrices = ApiRequests.npcPrices.getJSON();
        if (npcPrices != null && npcPrices.has(bazaarID)) return npcPrices.get(bazaarID).getAsFloat();

        return 0;
    }
}
