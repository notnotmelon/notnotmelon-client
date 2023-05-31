package net.fabricmc.notnotmelonclient.config;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.config.ConfigEntry;
import dev.isxander.yacl.config.GsonConfigInstance;
import dev.isxander.yacl.gui.controllers.TickBoxController;
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
			.category(qol())
			.category(removals())
			.category(dungeons())
			.category(fishing())
			.category(CommandKeybinds.category())
			.build();
	}

	public void draw() {
		MinecraftClient client = MinecraftClient.getInstance();
		client.setScreenAndRender(build().generateScreen(null));
	}

	public ConfigCategory qol() {
		return ConfigCategory.createBuilder()
			.name(Text.literal("Quality of Life"))

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Visual"))
				.option(fancyBars())
				.option(hideEmptyTooltips())
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Item Features"))
				.option(scrollableTooltips())
				.option(witherImpactHider())
				.option(priceTooltips())
				.option(createdDate())
				.option(oldMasterStars())
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Temp"))
				.option(fixCursorReset())
				.option(darkAuctionTimer())
				.build())

			.build();
	}

	public ConfigCategory removals() {
		return ConfigCategory.createBuilder()
			.name(Text.literal("Removals"))

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Vanilla Feature Removal"))
				.option(potionEffectsGui())
				.option(hideFireOverlay())
				.option(hideUnbreakable())
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Chat Spam"))
				.option(logSpamFix())
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Other"))
				.option(hideGearScore())
				.build())

			.build();
	}

	public ConfigCategory dungeons() {
		return ConfigCategory.createBuilder()
			.name(Text.literal("Dungeons"))

			/*.group(OptionGroup.createBuilder()
				.name(Text.literal("Puzzle Solvers"))
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Bossfight"))
				.build())*/

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Map"))
				.option(dungeonMap())
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Other"))
				.option(autoRepartyAccept())
				.build())

			.build();
	}

	public ConfigCategory fishing() {
		return ConfigCategory.createBuilder()
				.name(Text.literal("Fishing"))

				.group(OptionGroup.createBuilder()
						.name(Text.literal("Fishing"))
						.option(showWhenToReel())
						.option(hideOtherPlayersFishing())
						.option(legendaryCatchWarning())
						.option(goldenFishTimer())
						.build())

				.build();
	}

	@ConfigEntry public boolean fancyBars = true;
	public Option<?> fancyBars() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Fancy Bars"))
			.tooltip(Text.of("Adds custom bars for mana, health, and experience."))
			.binding(
				getDefaults().fancyBars,
				() -> getConfig().fancyBars,
				v -> getConfig().fancyBars = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	@ConfigEntry public boolean scrollableTooltips = true;
	public Option<?> scrollableTooltips() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Scrollable Tooltips"))
			.tooltip(Text.of("Allows you to scroll item tooltips with the scrollwheel. Hold SHIFT to scroll horizontally. Adjust the scroll sensitivity in your vanilla mouse settings."))
			.binding(
				getDefaults().scrollableTooltips,
				() -> getConfig().scrollableTooltips,
				v -> getConfig().scrollableTooltips = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	@ConfigEntry public boolean witherImpactHider = true;
	public Option<?> witherImpactHider() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Wither Impact Hider"))
			.tooltip(Text.of("Hides the explosion particles created by Wither Impact."))
			.binding(
				getDefaults().witherImpactHider,
				() -> getConfig().witherImpactHider,
				v -> getConfig().witherImpactHider = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	@ConfigEntry public boolean logSpamFix = true;
	public Option<?> logSpamFix() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Log Spam Fix"))
			.tooltip(Text.of("Fixes certian messages being spammed to your log files while on Hypixel."))
			.binding(
				getDefaults().logSpamFix,
				() -> getConfig().logSpamFix,
				v -> getConfig().logSpamFix = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	@ConfigEntry public boolean potionEffectsGui = true;
	public Option<?> potionEffectsGui() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Hide Potion Effect GUI"))
			.tooltip(Text.of("Hides the list of active potion effects from showing in your inventory."))
			.binding(
				getDefaults().potionEffectsGui,
				() -> getConfig().potionEffectsGui,
				v -> getConfig().potionEffectsGui = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	@ConfigEntry public boolean hideEmptyTooltips = true;
	public Option<?> hideEmptyTooltips() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Hide Empty Tooltips"))
			.tooltip(Text.of("Hides empty item tooltips in Skyblock GUIs."))
			.binding(
				getDefaults().hideEmptyTooltips,
				() -> getConfig().hideEmptyTooltips,
				v -> getConfig().hideEmptyTooltips = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	@ConfigEntry public boolean hideFireOverlay = true;
	public Option<?> hideFireOverlay() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Hide Fire Overlay"))
			.tooltip(Text.of("Prevents the first-person fire overlay from blocking your vision."))
			.binding(
				getDefaults().hideFireOverlay,
				() -> getConfig().hideFireOverlay,
				v -> getConfig().hideFireOverlay = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	@ConfigEntry public boolean hideGearScore = true;
	public Option<?> hideGearScore() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Hide Gear Score"))
			.tooltip(Text.of("Removes gear score from dungeon equipment tooltips."))
			.binding(
				getDefaults().hideGearScore,
				() -> getConfig().hideGearScore,
				v -> getConfig().hideGearScore = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	@ConfigEntry public boolean priceTooltips = true;
	public Option<?> priceTooltips() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Price Tooltips"))
			.tooltip(Text.of("Shows NPC, bazaar, and lowest BIN prices on item tooltips. Press SHIFT for a full stack."))
			.binding(
				getDefaults().priceTooltips,
				() -> getConfig().priceTooltips,
				v -> getConfig().priceTooltips = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	@ConfigEntry public boolean createdDate = true;
	public Option<?> createdDate() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Obtained Date"))
			.tooltip(Text.of("Shows the date that an item was obtained date on its tooltip. Only works for items with a UUID."))
			.binding(
				getDefaults().createdDate,
				() -> getConfig().createdDate,
				v -> getConfig().createdDate = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	@ConfigEntry public boolean oldMasterStars = true;
	public Option<?> oldMasterStars() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Old Master Stars"))
			.tooltip(Text.of("Use the old master star format for item names. Replaces ... with ..."))
			.binding(
				getDefaults().oldMasterStars,
				() -> getConfig().oldMasterStars,
				v -> getConfig().oldMasterStars = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	@ConfigEntry public boolean hideUnbreakable = true;
	public Option<?> hideUnbreakable() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Hide Unbreakable Tag"))
			.tooltip(Text.of("Hides the \"Unbreakable\" tag found on every Skyblock item description."))
			.binding(
				getDefaults().hideUnbreakable,
				() -> getConfig().hideUnbreakable,
				v -> getConfig().hideUnbreakable = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	@ConfigEntry public boolean autoRepartyAccept = true;
	public Option<?> autoRepartyAccept() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Auto Reparty"))
			.tooltip(Text.of("If you are the party leader in dungeons, automatically run /rp at the end of a dungeon run. Otherwise, automatically accepts reparty requests from the party leader."))
			.binding(
				getDefaults().autoRepartyAccept,
				() -> getConfig().autoRepartyAccept,
				v -> getConfig().autoRepartyAccept = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	@ConfigEntry public boolean autoExtraStats = true;
	public Option<?> autoExtraStats() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Extra Stats"))
			.tooltip(Text.of("Runs /showextrastats after completing a dungeon run. This command shows score, time taken, deaths, secrets, and other values."))
			.binding(
				getDefaults().autoExtraStats,
				() -> getConfig().autoExtraStats,
				v -> getConfig().autoExtraStats = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	@ConfigEntry public boolean dungeonMap = true;
	public Option<?> dungeonMap() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Dungeon Map"))
			.tooltip(Text.of("Shows a map of the dungeon on your HUD."))
			.binding(
				getDefaults().dungeonMap,
				() -> getConfig().dungeonMap,
				v -> getConfig().dungeonMap = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	@ConfigEntry public boolean fixCursorReset = true;
	public Option<?> fixCursorReset() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Fix Cursor Reset"))
			.tooltip(Text.of("Fixes that your cursor will reset to the center of the screen while navigating skyblock menus."))
			.binding(
				getDefaults().fixCursorReset,
				() -> getConfig().fixCursorReset,
				v -> getConfig().fixCursorReset = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	@ConfigEntry public boolean showWhenToReel = true;
	public Option<?> showWhenToReel() {
		return Option.createBuilder(boolean.class)
				.name(Text.of("Show When to Reel-in"))
				.tooltip(Text.of("Display !!! and play a sound whenever a fish nibbles your bobber."))
				.binding(
						getDefaults().showWhenToReel,
						() -> getConfig().showWhenToReel,
						v -> getConfig().showWhenToReel = v
				)
				.controller(TickBoxController::new)
				.build();
	}

	@ConfigEntry public boolean legendaryCatchWarning = true;
	public Option<?> legendaryCatchWarning() {
		return Option.createBuilder(boolean.class)
				.name(Text.of("Legendary Catch Warning"))
				.tooltip(Text.of("Shows a warning whenever you catch a legendary fish. Also works for golden fish and plhlegblast."))
				.binding(
						getDefaults().legendaryCatchWarning,
						() -> getConfig().legendaryCatchWarning,
						v -> getConfig().legendaryCatchWarning = v
				)
				.controller(TickBoxController::new)
				.build();
	}

	@ConfigEntry public boolean hideOtherPlayersFishing = false;
	public Option<?> hideOtherPlayersFishing() {
		return Option.createBuilder(boolean.class)
				.name(Text.of("Hide Other Players Fishing"))
				.tooltip(Text.of("Hides other player's fishing lines and bobbers."))
				.binding(
						getDefaults().hideOtherPlayersFishing,
						() -> getConfig().hideOtherPlayersFishing,
						v -> getConfig().hideOtherPlayersFishing = v
				)
				.controller(TickBoxController::new)
				.build();
	}

	@ConfigEntry public boolean goldenFishTimer = true;
	public Option<?> goldenFishTimer() {
		return Option.createBuilder(boolean.class)
				.name(Text.of("Golden Fish Timer"))
				.tooltip(Text.of("Shows a 15 minute timer until the Golden Fish can appear on the Crimson Isle. After 15 minutes, shows the probability of catching the Golden Fish."))
				.binding(
						getDefaults().goldenFishTimer,
						() -> getConfig().goldenFishTimer,
						v -> getConfig().goldenFishTimer = v
				)
				.controller(TickBoxController::new)
				.build();
	}

	@ConfigEntry public boolean darkAuctionTimer = false;
	public Option<?> darkAuctionTimer() {
		return Option.createBuilder(boolean.class)
				.name(Text.of("Dark Auction Timer"))
				.tooltip(Text.of("Shows the remaining time until the next Dark Auction. Dark Auctions always happen on the 55th minute of every hour."))
				.binding(
						getDefaults().darkAuctionTimer,
						() -> getConfig().darkAuctionTimer,
						v -> getConfig().darkAuctionTimer = v
				)
				.controller(TickBoxController::new)
				.build();
	}

	@ConfigEntry public List<CommandKeybinds.CommandKeybind> commandKeybinds = new ArrayList<>();
}