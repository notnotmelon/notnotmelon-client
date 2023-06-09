package net.fabricmc.notnotmelonclient.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.gui.controllers.TickBoxController;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.config.Config.*;

public class Timers {
	public static ConfigCategory category() {
		return ConfigCategory.createBuilder()
			.name(Text.literal("Timers"))

			.option(goldenFishTimer())
			.option(darkAuctionTimer())
			.option(orbTimer())

			.build();
	}

	public static Option<?> goldenFishTimer() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Golden Fish Timer"))
			.description(OptionDescription.of(Text.of("Shows a 15 minute timer until the Golden Fish can appear on the Crimson Isle. After 15 minutes, shows the probability of catching the Golden Fish.")))
			.binding(
				getDefaults().goldenFishTimer,
				() -> CONFIG.goldenFishTimer,
				v -> CONFIG.goldenFishTimer = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> darkAuctionTimer() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Dark Auction Timer"))
			.description(OptionDescription.of(Text.of("Shows the remaining time until the next Dark Auction. Dark Auctions always happen on the 55th minute of every hour.")))
			.binding(
				getDefaults().darkAuctionTimer,
				() -> CONFIG.darkAuctionTimer,
				v -> CONFIG.darkAuctionTimer = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> orbTimer() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Orb Timer"))
			.description(OptionDescription.of(Text.of("Shows the amount of time remaining on nearby deployables. Always lists the highest priority deployable.")))
			.binding(
				getDefaults().orbTimer,
				() -> CONFIG.orbTimer,
				v -> CONFIG.orbTimer = v
			)
			.customController(TickBoxController::new)
			.build();
	}
}
