package net.fabricmc.notnotmelonclient.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.gui.controllers.TickBoxController;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;
import static net.fabricmc.notnotmelonclient.config.Config.getDefaults;

public class Slayer {
	public static ConfigCategory category() {
		return ConfigCategory.createBuilder()
			.name(Text.literal("Slayer"))

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Slayer"))
				.option(minibossPing())
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Riftstalker Bloodfiend"))
				.option(effigyWaypoints())
				.build())

			.build();
	}

	private static Option<?> effigyWaypoints() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Miniboss Ping"))
			.description(OptionDescription.of(Text.of("Displays waypoints to any inactive blood effigies. Each effigy grants +2 rift damage for 20 minutes.")))
			.binding(
				getDefaults().effigyWaypoints,
				() -> CONFIG.effigyWaypoints,
				v -> CONFIG.effigyWaypoints = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> minibossPing() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Miniboss Ping"))
			.description(OptionDescription.of(Text.of("Shows a message whenever a slayer miniboss spawns near your player.")))
			.binding(
				getDefaults().minibossPing,
				() -> CONFIG.minibossPing,
				v -> CONFIG.minibossPing = v
			)
			.customController(TickBoxController::new)
			.build();
	}
}
