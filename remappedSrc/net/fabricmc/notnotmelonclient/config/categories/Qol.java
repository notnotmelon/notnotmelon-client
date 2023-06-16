package net.fabricmc.notnotmelonclient.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.gui.controllers.TickBoxController;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;
import static net.fabricmc.notnotmelonclient.config.Config.getDefaults;

public class Qol {
	public static ConfigCategory category() {
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
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Temp"))
				.option(fixCursorReset())
				.build())

			.build();
	}


	public static Option<?> fancyBars() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Fancy Bars"))
			.description(OptionDescription.of(Text.of("Adds custom bars for mana, health, and experience.")))
			.binding(
				getDefaults().fancyBars,
				() -> CONFIG.fancyBars,
				v -> CONFIG.fancyBars = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> scrollableTooltips() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Scrollable Tooltips"))
			.description(OptionDescription.of(Text.of("Allows you to scroll item tooltips with the scrollwheel. Hold SHIFT to scroll horizontally. Adjust the scroll sensitivity in your vanilla mouse settings.")))
			.binding(
				getDefaults().scrollableTooltips,
				() -> CONFIG.scrollableTooltips,
				v -> CONFIG.scrollableTooltips = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> witherImpactHider() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Wither Impact Hider"))
			.description(OptionDescription.of(Text.of("Hides the explosion particles created by Wither Impact.")))
			.binding(
				getDefaults().witherImpactHider,
				() -> CONFIG.witherImpactHider,
				v -> CONFIG.witherImpactHider = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> hideEmptyTooltips() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Hide Empty Tooltips"))
			.description(OptionDescription.of(Text.of("Hides empty item tooltips in Skyblock GUIs.")))
			.binding(
				getDefaults().hideEmptyTooltips,
				() -> CONFIG.hideEmptyTooltips,
				v -> CONFIG.hideEmptyTooltips = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> priceTooltips() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Price Tooltips"))
			.description(OptionDescription.of(Text.of("Shows NPC, bazaar, and lowest BIN prices on item tooltips. Press SHIFT for a full stack.")))
			.binding(
				getDefaults().priceTooltips,
				() -> CONFIG.priceTooltips,
				v -> CONFIG.priceTooltips = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> createdDate() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Obtained Date"))
			.description(OptionDescription.of(Text.of("Shows the date that an item was obtained date on its tooltip. Only works for items with a UUID.")))
			.binding(
				getDefaults().createdDate,
				() -> CONFIG.createdDate,
				v -> CONFIG.createdDate = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> fixCursorReset() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Fix Cursor Reset"))
			.description(OptionDescription.of(Text.of("Fixes that your cursor will reset to the center of the screen while navigating skyblock menus.")))
			.binding(
				getDefaults().fixCursorReset,
				() -> CONFIG.fixCursorReset,
				v -> CONFIG.fixCursorReset = v
			)
			.customController(TickBoxController::new)
			.build();
	}
}
