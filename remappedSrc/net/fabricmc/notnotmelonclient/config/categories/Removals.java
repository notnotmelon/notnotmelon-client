package net.fabricmc.notnotmelonclient.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.gui.controllers.TickBoxController;
import dev.isxander.yacl3.api.OptionDescription;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;
import static net.fabricmc.notnotmelonclient.config.Config.getDefaults;

public class Removals {
	public static ConfigCategory category() {
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

	public static Option<?> logSpamFix() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Log Spam Fix"))
			.description(OptionDescription.of(Text.of("Fixes certian messages being spammed to your log files while on Hypixel.")))
			.binding(
				getDefaults().logSpamFix,
				() -> CONFIG.logSpamFix,
				v -> CONFIG.logSpamFix = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> potionEffectsGui() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Hide Potion Effect GUI"))
			.description(OptionDescription.of(Text.of("Hides the list of active potion effects from showing in your inventory.")))
			.binding(
				getDefaults().potionEffectsGui,
				() -> CONFIG.potionEffectsGui,
				v -> CONFIG.potionEffectsGui = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> hideFireOverlay() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Hide Fire Overlay"))
			.description(OptionDescription.of(Text.of("Prevents the first-person fire overlay from blocking your vision.")))
			.binding(
				getDefaults().hideFireOverlay,
				() -> CONFIG.hideFireOverlay,
				v -> CONFIG.hideFireOverlay = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> hideGearScore() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Hide Gear Score"))
			.description(OptionDescription.of(Text.of("Removes gear score from dungeon equipment tooltips.")))
			.binding(
				getDefaults().hideGearScore,
				() -> CONFIG.hideGearScore,
				v -> CONFIG.hideGearScore = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> hideUnbreakable() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Hide Unbreakable Tag"))
			.description(OptionDescription.of(Text.of("Hides the \"Unbreakable\" tag found on every Skyblock item description.")))
			.binding(
				getDefaults().hideUnbreakable,
				() -> CONFIG.hideUnbreakable,
				v -> CONFIG.hideUnbreakable = v
			)
			.customController(TickBoxController::new)
			.build();
	}
}
