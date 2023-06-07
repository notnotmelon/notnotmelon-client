package net.fabricmc.notnotmelonclient.itemlist;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.notnotmelonclient.api.ApiRequests;
import net.fabricmc.notnotmelonclient.util.ItemUtil;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.item.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class NeuRepo {
	public static JsonObject petNums;
	public static boolean isDownloaded;
	public static ItemStack[] items;
	public static final Map<String, ItemStack> itemsByID = new HashMap<>();
	public static List<ItemListIcon> itemListIcons;

	public static void afterDownload() {
		isDownloaded = true;
		try {
			parsePetNums();
			parseItems();
			parseItemParents();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static Path resolve(String other) {
		return ApiRequests.neuRepo.outputPath.resolve(other);
	}

	private static JsonObject resolveAsJson(String other) throws IOException {
		Path path = resolve(other);
		return JsonParser.parseReader(Files.newBufferedReader(path)).getAsJsonObject();
	}

	public static void parsePetNums() throws IOException {
		petNums = resolveAsJson("constants/petnums.json");
	}

	public static void parseItems() throws IOException {
		Path path = resolve("items");
		File[] files = Objects.requireNonNull(path.toFile().listFiles());

		items = new ItemStack[files.length];
		itemsByID.clear();
		int i = 0;
		for (File file : files) {
			JsonObject json = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();
			ItemStack stack = ItemStackBuilder.parseJsonObj(json);
			items[i++] = stack;
			itemsByID.put(json.get("internalname").getAsString(), stack);
		}
	}

	public static void parseItemParents() throws IOException {
		JsonObject json = resolveAsJson("constants/parents.json");

		Map<String, String> childrenNames = new HashMap<>();
		for (Map.Entry<String, JsonElement> entry : json.entrySet())
			for (JsonElement child : entry.getValue().getAsJsonArray())
				childrenNames.put(child.getAsString(), entry.getKey());

		Map<String, List<ItemStack>> childrenStacks = new HashMap<>();
		itemListIcons = new ArrayList<>();
		for (Map.Entry<String, ItemStack> entry : itemsByID.entrySet()) {
			String stackName = entry.getKey();
			ItemStack stack = entry.getValue();
			if (childrenNames.containsKey(stackName)) {
				String parentName = childrenNames.get(stackName);
				Util.print(parentName+" "+stackName);
				if (!childrenStacks.containsKey(parentName))
					childrenStacks.put(parentName, new ArrayList<>());
				childrenStacks.get(parentName).add(stack);
			} else {
				itemListIcons.add(new ItemListIcon(stack));
			}
		}

		for (ItemListIcon icon : itemListIcons) {
			String itemName = ItemUtil.moulberryification(ItemUtil.getFullItemID(icon.stack));
			if (childrenStacks.containsKey(itemName))
				icon.setChildren(childrenStacks.get(itemName));
		}
	}
}
