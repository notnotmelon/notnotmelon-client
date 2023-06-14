package net.fabricmc.notnotmelonclient.config.categories;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.config.Config.getConfig;
import static net.fabricmc.notnotmelonclient.config.Config.getDefaults;

public class Slayer {
	public static ConfigCategory category() {
		return ConfigCategory.createBuilder()
			.name(Text.literal("Slayer"))

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Slayer"))
				.option(minibossPing())
				.build())

			.build();
	}

	public static Option<?> minibossPing() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Miniboss Ping"))
			.tooltip(Text.of("Shows a message whenever a slayer miniboss spawns near your player."))
			.binding(
				getDefaults().minibossPing,
				() -> getConfig().minibossPing,
				v -> getConfig().minibossPing = v
			)
			.controller(TickBoxController::new)
			.build();
	}
}
