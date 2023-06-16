package net.fabricmc.notnotmelonclient.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.gui.controllers.TickBoxController;
import dev.isxander.yacl3.gui.controllers.cycling.EnumController;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;
import net.fabricmc.notnotmelonclient.itemlist.SortStrategies;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;
import static net.fabricmc.notnotmelonclient.config.Config.getDefaults;

public class ItemList {
	public static ConfigCategory category() {
		return ConfigCategory.createBuilder()
			.name(Text.literal("Item List"))

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Item List"))
				.option(itemList())
				.option(itemListWidth())
				.option(sortStrategy())
				.option(reversed())
				.option(hideRecipeBook())
				.build())

			.build();
	}

	public static Option<?> itemList() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Item List"))
			.description(OptionDescription.of(Text.of("Displays a list of all Skyblock items and their recipes. Powered by the NEU API.")))
			.binding(
				getDefaults().itemList,
				() -> CONFIG.itemList,
				v -> CONFIG.itemList = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> itemListWidth() {
		return Option.createBuilder(int.class)
			.name(Text.of("Item List Width"))
			.description(OptionDescription.of(Text.of("How many columns are displayed in the item list? If you are lagging inside GUIs, try lowering this value.")))
			.binding(
				getDefaults().itemListWidth,
				() -> CONFIG.itemListWidth,
				v -> CONFIG.itemListWidth = v
			)
			.customController(opt -> new IntegerSliderController(opt, 1, 32, 1))
			.build();
	}

	public static Option<?> sortStrategy() {
		return Option.createBuilder(SortStrategies.class)
			.name(Text.of("Sorting Strategy"))
			.description(OptionDescription.of(Text.of("How should the item list be sorted?")))
			.binding(
				getDefaults().sortStrategy,
				() -> CONFIG.sortStrategy,
				v -> {
					CONFIG.sortStrategy = v;
					net.fabricmc.notnotmelonclient.itemlist.ItemList.sort();
				}
			)
			.customController(option -> new EnumController<>(option, SortStrategies.class))
			.build();
	}

	public static Option<?> reversed() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Reversed"))
			.description(OptionDescription.of(Text.of("Should the item list be sorted in reverse order?")))
			.binding(
				getDefaults().reversed,
				() -> CONFIG.reversed,
				v -> {
					CONFIG.reversed = v;
					net.fabricmc.notnotmelonclient.itemlist.ItemList.sort();
				}
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> hideRecipeBook() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Hide Recipe Book"))
			.description(OptionDescription.of(Text.of("Hides the vanilla recipe book from your inventory.")))
			.binding(
				getDefaults().hideRecipeBook,
				() -> CONFIG.hideRecipeBook,
				v -> CONFIG.hideRecipeBook = v
			)
			.customController(TickBoxController::new)
			.build();
	}
}
