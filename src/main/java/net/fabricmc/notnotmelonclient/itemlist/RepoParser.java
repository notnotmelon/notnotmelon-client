package net.fabricmc.notnotmelonclient.itemlist;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.notnotmelonclient.api.ApiRequests;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.item.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RepoParser {
	public static JsonObject petNums;
	public static List<ItemStack> items;

	public static void afterDownload() {
		calculatePetNums();
		items = new ArrayList<>();
		Path repoPath = ApiRequests.neuRepo.outputPath;
		File itemDirectory = repoPath.resolve("items").toFile();

		for (File itemFile : Objects.requireNonNull(itemDirectory.listFiles())) {
			try {
				JsonObject itemJson = JsonParser.parseReader(new FileReader(itemFile)).getAsJsonObject();
				ItemStack stack = ItemStackBuilder.parseJsonObj(itemJson);
				items.add(stack);
				Util.print(stack.getName());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void calculatePetNums() {
		try {
			Path path = ApiRequests.neuRepo.outputPath.resolve("constants/petnums.json");
			petNums = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
