package net.fabricmc.notnotmelonclient.config.categories;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.config.Config.*;

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
			.tooltip(Text.of("Fixes certian messages being spammed to your log files while on Hypixel."))
			.binding(
				getDefaults().logSpamFix,
				() -> getConfig().logSpamFix,
				v -> getConfig().logSpamFix = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	public static Option<?> potionEffectsGui() {
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

	public static Option<?> hideFireOverlay() {
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

	public static Option<?> hideGearScore() {
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

	public static Option<?> hideUnbreakable() {
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
}
