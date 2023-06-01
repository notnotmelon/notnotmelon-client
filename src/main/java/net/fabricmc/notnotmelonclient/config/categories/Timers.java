package net.fabricmc.notnotmelonclient.config.categories;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.gui.controllers.TickBoxController;
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
			.tooltip(Text.of("Shows a 15 minute timer until the Golden Fish can appear on the Crimson Isle. After 15 minutes, shows the probability of catching the Golden Fish."))
			.binding(
				getDefaults().goldenFishTimer,
				() -> getConfig().goldenFishTimer,
				v -> getConfig().goldenFishTimer = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	public static Option<?> darkAuctionTimer() {
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

	public static Option<?> orbTimer() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Orb Timer"))
			.tooltip(Text.of("Shows the amount of time remaining on nearby deployables. Always lists the "))
			.binding(
				getDefaults().orbTimer,
				() -> getConfig().orbTimer,
				v -> getConfig().orbTimer = v
			)
			.controller(TickBoxController::new)
			.build();
	}
}
