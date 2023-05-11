package net.fabricmc.notnotmelonclient.config;

import java.util.HashMap;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class Config {
	public static HashMap<String, Object> options = new HashMap<String, Object>();

	public static Object get(String id) { return options.get(id); }

	public static YetAnotherConfigLib build() {
		return YetAnotherConfigLib.createBuilder()
			.title(Text.literal("Used for narration. Could be used to render a title in the future."))
			.category(ConfigCategory.createBuilder()
				.name(Text.literal("Name of the category"))
				.tooltip(Text.literal("This text will appear as a tooltip when you hover or focus the button with Tab. There is no need to add \n to wrap as YACL will do it for you."))
				.group(OptionGroup.createBuilder()
					.name(Text.literal("Name of the group"))
					.tooltip(Text.literal("This text will appear when you hover over the name or focus on the collapse button with Tab."))
					.option(new ConfigOption<Boolean>(Boolean.class, "fancybars", true, Text.of("Fancy Bars"), Text.of("meow"), TickBoxController::new).option)
					.build())
				.build())
			.build();
	}

	public static void draw() {
		MinecraftClient.getInstance().setScreen(build().generateScreen(MinecraftClient.getInstance().currentScreen));
	}
}
