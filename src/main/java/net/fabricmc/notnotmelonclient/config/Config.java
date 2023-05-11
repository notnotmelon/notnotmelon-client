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
			.tooltip(Text.literal("QOL features that do not fit into any other category."))
			.option(fancyBars())
			.group(OptionGroup.createBuilder()
				.name(Text.literal("Item Features"))
				.tooltip(Text.literal("Features related to item tooltips or item effects."))
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
				JsonLoader.jsonInterface.getDefaults().fancyBars,
				() -> JsonLoader.jsonInterface.getConfig().fancyBars,
				v -> JsonLoader.jsonInterface.getConfig().fancyBars = v
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
				JsonLoader.jsonInterface.getDefaults().scrollableTooltips,
				() -> JsonLoader.jsonInterface.getConfig().scrollableTooltips,
				v -> JsonLoader.jsonInterface.getConfig().scrollableTooltips = v
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
				JsonLoader.jsonInterface.getDefaults().witherImpactHider,
				() -> JsonLoader.jsonInterface.getConfig().witherImpactHider,
				v -> JsonLoader.jsonInterface.getConfig().witherImpactHider = v
			)
			.controller(TickBoxController::new)
			.build();
	}
}