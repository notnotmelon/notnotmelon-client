package net.fabricmc.notnotmelonclient.config;

import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.fabricmc.notnotmelonclient.config.categories.*;
import net.fabricmc.notnotmelonclient.itemlist.SortStrategies;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Config {
	public static Config CONFIG;

	public static Config getDefaults() {
		return new Config();
	}

	public YetAnotherConfigLib build() {
		return YetAnotherConfigLib.createBuilder()
			.title(Text.literal("Notnotmelon Client Config Options"))
			.save(JsonLoader.jsonInterface::save)
			.category(Qol.category())
			.category(ItemList.category())
			.category(Removals.category())
			.category(Dungeons.category())
			.category(Slayer.category())
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
	@ConfigEntry public int itemListWidth = 6;
	@ConfigEntry public SortStrategies sortStrategy = SortStrategies.Value;
	@ConfigEntry public int pageNumber = 0;
	@ConfigEntry public String searchQuery = "";
	@ConfigEntry public boolean reversed = false;
	@ConfigEntry public Boolean hideRecipeBook = true;
	@ConfigEntry public boolean minibossPing = true;
	@ConfigEntry public boolean ticTacToe = true;
	@ConfigEntry public boolean creeperBeam = true;
	@ConfigEntry public boolean threeWeirdos = true;
	@ConfigEntry public boolean blaze = true;
	@ConfigEntry public boolean effigyWaypoints = true;
}