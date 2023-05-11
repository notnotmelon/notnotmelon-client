package net.fabricmc.notnotmelonclient.config;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.config.ConfigEntry;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class Config {
	public static final Config instance = new Config();

	public YetAnotherConfigLib build() {
		return YetAnotherConfigLib.createBuilder()
		.title(Text.literal("Notnotmelon Client Config Options"))
		.category(ConfigCategory.createBuilder()
			.name(Text.literal("Quality of Life"))
			.tooltip(Text.literal("QOL features that do not fit into any other category."))
			//.group(OptionGroup.createBuilder()
				//.name(Text.literal("Name of the group"))
				//.tooltip(Text.literal("This text will appear when you hover over the name or focus on the collapse button with Tab."))
				.option(statusBarOption())
				//.build())
			.build())
			.save(JsonLoader.jsonInterface::save)
		.build();
	}

	public void draw() { // TODO: fix
		MinecraftClient client = MinecraftClient.getInstance();
		client.setScreenAndRender(build().generateScreen(null));
	}

	@ConfigEntry public boolean fancyBars = true;
	public Option<?> statusBarOption() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Fancy Bars"))
			.tooltip(Text.of("Adds custom bars for mana, health, and experience."))
			.binding(
				JsonLoader.jsonInterface.getDefaults().fancyBars,
				() -> JsonLoader.jsonInterface.getConfig().fancyBars,
				v -> JsonLoader.jsonInterface.getConfig().fancyBars = v
			)
			.controller(TickBoxController::new)
			.build();
	}
}