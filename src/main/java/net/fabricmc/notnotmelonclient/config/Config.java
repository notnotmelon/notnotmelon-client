package net.fabricmc.notnotmelonclient.config;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.config.ConfigEntry;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class Config {
	public static final Config instance = new Config();
	public static Config getConfig() { return JsonLoader.jsonInterface.getConfig(); }
	public static Config getDefaults() { return JsonLoader.jsonInterface.getDefaults(); }

	public YetAnotherConfigLib build() {
		return YetAnotherConfigLib.createBuilder()
			.title(Text.literal("Notnotmelon Client Config Options"))
			.save(JsonLoader.jsonInterface::save)
			.category(Qol())
			.build();
	}

	public void draw() {
		MinecraftClient client = MinecraftClient.getInstance();
		client.setScreenAndRender(build().generateScreen(null));
	}

	public ConfigCategory Qol() {
		return ConfigCategory.createBuilder()
			.name(Text.literal("Quality of Life"))

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Visual"))
				.option(fancyBars())
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Item Features"))
				.option(scrollableTooltips())
				.option(witherImpactHider())
				.build())

			.build();
	}

	@ConfigEntry public boolean fancyBars = true;
	public Option<?> fancyBars() {
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

	@ConfigEntry public boolean scrollableTooltips = true;
	public Option<?> scrollableTooltips() {
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

	@ConfigEntry public boolean witherImpactHider = true;
	public Option<?> witherImpactHider() {
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
}