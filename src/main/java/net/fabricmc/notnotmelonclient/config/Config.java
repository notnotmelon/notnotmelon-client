package net.fabricmc.notnotmelonclient.config;

import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.config.ConfigEntry;
import dev.isxander.yacl.config.GsonConfigInstance;
import net.fabricmc.notnotmelonclient.config.categories.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Config {
	public static final Config instance = new Config();

	public static Config getConfig() {
		GsonConfigInstance<Config> jsonInterface = JsonLoader.jsonInterface;
		if (jsonInterface == null) return instance;
		return jsonInterface.getConfig();
	}
	public static Config getDefaults() {
		GsonConfigInstance<Config> jsonInterface = JsonLoader.jsonInterface;
		if (jsonInterface == null) return instance;
		return jsonInterface.getDefaults();
	}

	public YetAnotherConfigLib build() {
		return YetAnotherConfigLib.createBuilder()
			.title(Text.literal("Notnotmelon Client Config Options"))
			.save(JsonLoader.jsonInterface::save)
			.category(Qol.category())
			.category(ItemList.category())
			.category(Removals.category())
			.category(Dungeons.category())
			.category(Fishing.category())
			.category(Farming.category())
			.category(Timers.category())
			.category(CommandKeybinds.category())
			.build();
	}

	public void draw() {
		MinecraftClient client = MinecraftClient.getInstance();
		client.setScreenAndRender(build().generateScreen(null));
	}

	@ConfigEntry public boolean fancyBars = true;
	@ConfigEntry public boolean autoExtraStats = true;
	@ConfigEntry public boolean scrollableTooltips = true;
	@ConfigEntry public boolean witherImpactHider = true;
	@ConfigEntry public boolean logSpamFix = true;
	@ConfigEntry public boolean potionEffectsGui = true;
	@ConfigEntry public boolean hideEmptyTooltips = true;
	@ConfigEntry public boolean hideFireOverlay = true;
	@ConfigEntry public boolean hideGearScore = true;
	@ConfigEntry public boolean priceTooltips = true;
	@ConfigEntry public boolean createdDate = true;
	@ConfigEntry public boolean oldMasterStars = true;
	@ConfigEntry public boolean hideUnbreakable = true;
	@ConfigEntry public boolean autoRepartyAccept = true;
	@ConfigEntry public boolean dungeonMap = true;
	@ConfigEntry public boolean fixCursorReset = true;
	@ConfigEntry public boolean showWhenToReel = true;
	@ConfigEntry public boolean legendaryCatchWarning = true;
	@ConfigEntry public boolean hideOtherPlayersFishing = false;
	@ConfigEntry public boolean goldenFishTimer = true;
	@ConfigEntry public boolean darkAuctionTimer = false;
	@ConfigEntry public List<CommandKeybinds.CommandKeybind> commandKeybinds = new ArrayList<>();
	@ConfigEntry public boolean visitorGenerator = false;
	@ConfigEntry public boolean visitorProfit = false;
	@ConfigEntry public boolean orbTimer = false;
	@ConfigEntry public boolean bobberTimer = true;
	@ConfigEntry public boolean itemList = true;
	@ConfigEntry public int itemListWidth = 10;
	@ConfigEntry public boolean includeEntities = false;
}