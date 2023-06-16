package net.fabricmc.notnotmelonclient.misc;

import com.google.gson.JsonObject;
import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.api.ApiRequests;
import net.fabricmc.notnotmelonclient.util.ItemUtil;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;

public class ItemPriceTooltip {
	protected static final Logger LOGGER = Main.LOGGER;
	private static final MinecraftClient client = MinecraftClient.getInstance();
	public static final Formatting priceColor = Formatting.AQUA;
	private static final Text UNKNOWN = Text.literal("UNKNOWN").formatted(Formatting.RED).formatted(Formatting.BOLD);
	public static final Pattern GEAR_SCORE_PATTERN = Pattern.compile("^Gear Score: .+");

	public static void onInjectTooltip(ItemStack stack, TooltipContext context, List<Text> lines) {
        if (!Util.isSkyblock || client.player == null) return;

		if (CONFIG.hideUnbreakable)
			lines.removeIf(text -> text.getString().equals("Unbreakable"));

		if (CONFIG.hideGearScore)
			lines.removeIf(text -> GEAR_SCORE_PATTERN.matcher(text.getString()).matches());

        String itemID = ItemUtil.getFullItemID(stack);
        if (itemID == null) return;
		
		try {
			if (CONFIG.priceTooltips) {
				addNPCPrice(stack, itemID, lines);
				if (!addBazaarPrice(stack, itemID, lines)) {
					addLowestBIN(stack, itemID, lines);
					addAveragePrice(stack, itemID, lines);
				}
			}
			if (CONFIG.createdDate)
				addDateObtained(stack, itemID, lines);
		} catch(Exception e) {
			lines.add(Text.literal("ERROR PARSING PRICE DATA! Please report this.").formatted(Formatting.BOLD).formatted(Formatting.DARK_RED));
			lines.add(Text.literal(e.getLocalizedMessage()).formatted(Formatting.BOLD).formatted(Formatting.DARK_RED));
			LOGGER.error("[nnc] Failed to parse price data!", e);
		}
	}

	private static boolean addBazaarPrice(ItemStack stack, String itemID, List<Text> lines) {
		JsonObject bazaarPrices = ApiRequests.bazaarPrices.getJSON();
		itemID = itemID.replace('-', '_').replace("ENCHANTED_BOOK", "ENCHANTMENT");
		if (bazaarPrices == null || !bazaarPrices.has(itemID)) return false;

		JsonObject getItem = bazaarPrices.getAsJsonObject(itemID);
		lines.add(Text.literal("Instabuy: ")
			.formatted(priceColor)
			.append(getItem.get("buyPrice").isJsonNull()
				? UNKNOWN : formatCoins(getItem.get("buyPrice").getAsFloat(), stack.getCount(), true)));

		lines.add(Text.literal("Instasell: ")
			.formatted(priceColor)
			.append(getItem.get("sellPrice").isJsonNull()
				? UNKNOWN : formatCoins(getItem.get("sellPrice").getAsFloat(), stack.getCount(), true)));

		return true;
	}

	private static boolean addAveragePrice(ItemStack stack, String itemID, List<Text> lines) {
		JsonObject averageBins = ApiRequests.averageBins.getJSON();
		itemID = ItemUtil.moulberryification(itemID);
		if (averageBins == null || itemID == null || !averageBins.has(itemID)) return false;

		lines.add(Text.literal("Average BIN: ")
			.formatted(priceColor)
			.append(averageBins.get(itemID) == null
				? UNKNOWN : formatCoins(averageBins.get(itemID).getAsFloat(), stack.getCount(), false)));

		return true;
	}

	private static boolean addLowestBIN(ItemStack stack, String itemID, List<Text> lines) {
		JsonObject lowestBins = ApiRequests.lowestBins.getJSON();
		if (lowestBins == null || !lowestBins.has(itemID)) return false;

		lines.add(Text.literal("Lowest BIN: ")
			.formatted(priceColor)
			.append(formatCoins(lowestBins.get(itemID).getAsFloat(), stack.getCount(), false)));

		return true;
	}

	private static boolean addNPCPrice(ItemStack stack, String itemID, List<Text> lines) {
		JsonObject npcPrices = ApiRequests.npcPrices.getJSON();
		itemID = itemID.replace('-', '_').replace("ENCHANTED_BOOK", "ENCHANTMENT");
		if (npcPrices == null || !npcPrices.has(itemID)) return false;
		float npcPrice = npcPrices.get(itemID).getAsFloat();
		if (npcPrice == 0) return false;

		lines.add(Text.literal("NPC Price: ")
			.formatted(priceColor)
			.append(formatCoins(npcPrice, stack.getCount(), true)));

		return true;
	}

	private static boolean addDateObtained(ItemStack stack, String itemID, List<Text> lines) {		
		String timestamp = getTimestamp(stack);
		if (timestamp == null || timestamp.length() == 0) return false;

		lines.add(Text.literal("Obtained: ")
			.append(Text.literal(timestamp))
			.formatted(Formatting.DARK_GRAY));
			
		return true;
	}

	private static final SimpleDateFormat inputDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
	private static final SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
	public static String getTimestamp(ItemStack stack) {
        NbtCompound extraAttributes = ItemUtil.getExtraAttributes(stack);
		if (extraAttributes == null || !extraAttributes.contains("timestamp", NbtCompound.STRING_TYPE)) return null;
		String result = extraAttributes.getString("timestamp");

		try {
			result = outputDateFormat.format(inputDateFormat.parse(result));
		} catch (ParseException ignored) {}

        return result;
    }

	private static Text formatCoins(double price, int count, boolean expandStack) {
		long handle = client.getWindow().getHandle();
		boolean multiply = InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT);
		if (expandStack && multiply && count == 1) count = 64;
		if (multiply) price *= count;
		String formatString = (price > 99 || ((int) price) == price) ? "%1$,.0f" : "%1$,.1f";
		String priceString = String.format(Locale.ENGLISH, formatString, price);
        MutableText result = Text.literal(priceString).formatted(Formatting.GOLD);

        if (multiply) {
            result.append(Text.literal(" ×" + count).formatted(Formatting.GRAY));
        }

		return result;
    }
}
