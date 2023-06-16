package net.fabricmc.notnotmelonclient.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.gui.controllers.TickBoxController;
import dev.isxander.yacl3.api.OptionDescription;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;
import static net.fabricmc.notnotmelonclient.config.Config.getDefaults;

public class Fishing {
	public static ConfigCategory category() {
		return ConfigCategory.createBuilder()
			.name(Text.literal("Fishing"))

			.group(OptionGroup.createBuilder()
				.name(Text.literal("Fishing"))
				.option(showWhenToReel())
				.option(hideOtherPlayersFishing())
				.option(legendaryCatchWarning())
				.option(bobberTimer())
				.build())

			.build();
	}

	public static Option<?> showWhenToReel() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Show When to Reel-in"))
			.description(OptionDescription.of(Text.of("Display !!! and play a sound whenever a fish nibbles your bobber.")))
			.binding(
				getDefaults().showWhenToReel,
				() -> CONFIG.showWhenToReel,
				v -> CONFIG.showWhenToReel = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> legendaryCatchWarning() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Legendary Catch Warning"))
			.description(OptionDescription.of(Text.of("Shows a warning whenever you catch a legendary fish. Also works for golden fish and plhlegblast.")))
			.binding(
				getDefaults().legendaryCatchWarning,
				() -> CONFIG.legendaryCatchWarning,
				v -> CONFIG.legendaryCatchWarning = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> hideOtherPlayersFishing() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Hide Other Players Fishing"))
			.description(OptionDescription.of(Text.of("Hides other player's fishing lines and bobbers.")))
			.binding(
				getDefaults().hideOtherPlayersFishing,
				() -> CONFIG.hideOtherPlayersFishing,
				v -> CONFIG.hideOtherPlayersFishing = v
			)
			.customController(TickBoxController::new)
			.build();
	}

	public static Option<?> bobberTimer() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Bobber Timer"))
			.description(OptionDescription.of(Text.of("Displays a timer above your bobber. Can help with slugfishing.")))
			.binding(
				getDefaults().bobberTimer,
				() -> CONFIG.bobberTimer,
				v -> CONFIG.bobberTimer = v
			)
			.customController(TickBoxController::new)
			.build();
	}
}
