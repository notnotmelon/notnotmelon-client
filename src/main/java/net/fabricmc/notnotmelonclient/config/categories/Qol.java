package net.fabricmc.notnotmelonclient.config.categories;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.config.Config.*;

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
			.tooltip(Text.of("Adds custom bars for mana, health, and experience."))
			.binding(
				getDefaults().fancyBars,
				() -> getConfig().fancyBars,
				v -> getConfig().fancyBars = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	public static Option<?> scrollableTooltips() {
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

	public static Option<?> witherImpactHider() {
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

	public static Option<?> hideEmptyTooltips() {
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

	public static Option<?> priceTooltips() {
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

	public static Option<?> createdDate() {
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

	public static Option<?> fixCursorReset() {
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
}
