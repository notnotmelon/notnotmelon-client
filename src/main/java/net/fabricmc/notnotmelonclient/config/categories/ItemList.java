package net.fabricmc.notnotmelonclient.config.categories;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.string.number.IntegerFieldController;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.config.Config.getConfig;
import static net.fabricmc.notnotmelonclient.config.Config.getDefaults;

public class ItemList {
	public static ConfigCategory category() {
		return ConfigCategory.createBuilder()
			.name(Text.literal("Item List"))

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Item List"))
				.option(itemList())
				.option(itemListWidth())
				.option(includeEntities())
				.build())

			.build();
	}

	public static Option<?> itemList() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Item List"))
			.tooltip(Text.of("Displays a list of all Skyblock items and their recipes. Powered by the NEU API."))
			.binding(
				getDefaults().itemList,
				() -> getConfig().itemList,
				v -> getConfig().itemList = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	public static Option<?> itemListWidth() {
		return Option.createBuilder(int.class)
			.name(Text.of("Item List Width"))
			.tooltip(Text.of("How many columns are displayed in the item list? If you are lagging inside GUIs, try lowering this value."))
			.binding(
				getDefaults().itemListWidth,
				() -> getConfig().itemListWidth,
				v -> getConfig().itemListWidth = v
			)
			.controller(IntegerFieldController::new)
			.build();
	}

	public static Option<?> includeEntities() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Include Entities"))
			.tooltip(Text.of("Should NPCs, bosses, and mobs be included in the item list?"))
			.binding(
				getDefaults().includeEntities,
				() -> getConfig().includeEntities,
				v -> getConfig().includeEntities = v
			)
			.controller(TickBoxController::new)
			.build();
	}
}
