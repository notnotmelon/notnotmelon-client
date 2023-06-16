package net.fabricmc.notnotmelonclient.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.gui.controllers.TickBoxController;
import dev.isxander.yacl3.api.OptionDescription;
import net.fabricmc.notnotmelonclient.dungeons.map.ThreeWeirdos;
import net.fabricmc.notnotmelonclient.dungeons.solvers.CreeperBeam;
import net.fabricmc.notnotmelonclient.dungeons.solvers.TicTacToe;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;
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
			.description(OptionDescription.of(Text.of("If you are the party leader in dungeons, automatically run /rp at the end of a dungeon run. Otherwise, automatically accepts reparty requests from the party leader.")))
			.binding(
				getDefaults().autoRepartyAccept,
				() -> CONFIG.autoRepartyAccept,
				v -> CONFIG.autoRepartyAccept = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> autoExtraStats() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Extra Stats"))
			.description(OptionDescription.of(Text.of("Runs /showextrastats after completing a dungeon run. This command shows score, time taken, deaths, secrets, and other values.")))
			.binding(
				getDefaults().autoExtraStats,
				() -> CONFIG.autoExtraStats,
				v -> CONFIG.autoExtraStats = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> dungeonMap() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Dungeon Map"))
			.description(OptionDescription.of(Text.of("Shows a map of the dungeon on your HUD.")))
			.binding(
				getDefaults().dungeonMap,
				() -> CONFIG.dungeonMap,
				v -> CONFIG.dungeonMap = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> oldMasterStars() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Old Master Stars"))
			.description(OptionDescription.of(Text.of("Use the old master star format for item names. Replaces ... with ...")))
			.binding(
				getDefaults().oldMasterStars,
				() -> CONFIG.oldMasterStars,
				v -> CONFIG.oldMasterStars = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> ticTacToe() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Tic Tac Toe"))
			.description(OptionDescription.of(Text.of("Highlights the next button during the Tic Tac Toe puzzle in dungeons.")))
			.binding(
				getDefaults().ticTacToe,
				() -> CONFIG.ticTacToe,
				v -> { CONFIG.ticTacToe = v; TicTacToe.bestMoveIndicator = null; }
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> creeperBeam() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Creeper Beam"))
			.description(OptionDescription.of(Text.of("Draws lines between valid sea lanterns in the creeper beam puzzle. A total of five lines are drawn, including the obvious line.")))
			.binding(
				getDefaults().creeperBeam,
				() -> CONFIG.creeperBeam,
				v -> { CONFIG.creeperBeam = v; CreeperBeam.lines = null; }
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> threeWeirdos() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Three Weirdos"))
			.description(OptionDescription.of(Text.of("Draws a box around the correct chest in the three weirdos puzzle.")))
			.binding(
				getDefaults().threeWeirdos,
				() -> CONFIG.threeWeirdos,
				v -> { CONFIG.threeWeirdos = v; ThreeWeirdos.correctChest = null; }
			)
			.customController(TickBoxController::new)
			.build();
	}
}
