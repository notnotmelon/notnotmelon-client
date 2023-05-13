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
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Chat Spam"))
				.option(logSpamFix())
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
}