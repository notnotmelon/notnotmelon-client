package net.fabricmc.notnotmelonclient.config.categories;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import net.fabricmc.notnotmelonclient.dungeons.map.ThreeWeirdos;
import net.fabricmc.notnotmelonclient.dungeons.solvers.CreeperBeam;
import net.fabricmc.notnotmelonclient.dungeons.solvers.TicTacToe;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.config.Config.getConfig;
import static net.fabricmc.notnotmelonclient.config.Config.getDefaults;

public class Dungeons {
	public static ConfigCategory category() {
		return ConfigCategory.createBuilder()
			.name(Text.literal("Dungeons"))

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Puzzle Solvers"))
				.option(ticTacToe())
				.option(creeperBeam())
				.option(threeWeirdos())
				.build())

			/*.group(OptionGroup.createBuilder()
				.name(Text.literal("Boss Fight"))
				.build())*/

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Map"))
				.option(dungeonMap())
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Other"))
				.option(autoRepartyAccept())
				.option(autoExtraStats())
				.option(oldMasterStars())
				.build())

			.build();
	}

	public static Option<?> autoRepartyAccept() {
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

	public static Option<?> autoExtraStats() {
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

	public static Option<?> dungeonMap() {
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

	public static Option<?> oldMasterStars() {
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

	public static Option<?> ticTacToe() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Tic Tac Toe"))
			.tooltip(Text.of("Highlights the next button during the Tic Tac Toe puzzle in dungeons."))
			.binding(
				getDefaults().ticTacToe,
				() -> getConfig().ticTacToe,
				v -> { getConfig().ticTacToe = v; TicTacToe.bestMoveIndicator = null; }
			)
			.controller(TickBoxController::new)
			.build();
	}

	public static Option<?> creeperBeam() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Creeper Beam"))
			.tooltip(Text.of("Draws lines between valid sea lanterns in the creeper beam puzzle. A total of five lines are drawn, including the obvious line."))
			.binding(
				getDefaults().creeperBeam,
				() -> getConfig().creeperBeam,
				v -> { getConfig().creeperBeam = v; CreeperBeam.lines = null; }
			)
			.controller(TickBoxController::new)
			.build();
	}

	public static Option<?> threeWeirdos() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Three Weirdos"))
			.tooltip(Text.of("Draws a box around the correct chest in the three weirdos puzzle."))
			.binding(
				getDefaults().threeWeirdos,
				() -> getConfig().threeWeirdos,
				v -> { getConfig().threeWeirdos = v; ThreeWeirdos.correctChest = null; }
			)
			.controller(TickBoxController::new)
			.build();
	}
}
