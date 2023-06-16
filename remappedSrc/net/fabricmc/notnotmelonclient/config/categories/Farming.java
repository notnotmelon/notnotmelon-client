package net.fabricmc.notnotmelonclient.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.gui.controllers.TickBoxController;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;
import static net.fabricmc.notnotmelonclient.config.Config.getDefaults;

public class Farming {
	public static ConfigCategory category() {
		return ConfigCategory.createBuilder()
			.name(Text.literal("Farming"))

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Farming"))
				.option(visitorGenerator())
				.option(visitorProfit())
				.build())

			.build();
	}

	public static Option<?> visitorGenerator() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Visitor Generator [experimental]"))
			.description(OptionDescription.of(Text.of("Redirects your /warp commands to the garden whenever a visitor is ready to spawn. If the queue still has space, warps you back to the original destination after 1 second.")))
			.binding(
				getDefaults().visitorGenerator,
				() -> CONFIG.visitorGenerator,
				v -> CONFIG.visitorGenerator = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> visitorProfit() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Visitor Profit GUI"))
			.description(OptionDescription.of(Text.of("Shows a GUI with the estimated profit from a visitor trade. For bazaar items, uses buy order price. For auctionable items, uses the 1-day average BIN.")))
			.binding(
				getDefaults().visitorProfit,
				() -> CONFIG.visitorProfit,
				v -> CONFIG.visitorProfit = v
			)
			.customController(TickBoxController::new)
			.build();
	}
}
