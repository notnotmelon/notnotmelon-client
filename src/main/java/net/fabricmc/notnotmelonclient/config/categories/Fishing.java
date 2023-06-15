package net.fabricmc.notnotmelonclient.config.categories;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.config.Config.*;

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
			.tooltip(Text.of("Display !!! and play a sound whenever a fish nibbles your bobber."))
			.binding(
				getDefaults().showWhenToReel,
				() -> CONFIG.showWhenToReel,
				v -> CONFIG.showWhenToReel = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	public static Option<?> legendaryCatchWarning() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Legendary Catch Warning"))
			.tooltip(Text.of("Shows a warning whenever you catch a legendary fish. Also works for golden fish and plhlegblast."))
			.binding(
				getDefaults().legendaryCatchWarning,
				() -> CONFIG.legendaryCatchWarning,
				v -> CONFIG.legendaryCatchWarning = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	public static Option<?> hideOtherPlayersFishing() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Hide Other Players Fishing"))
			.tooltip(Text.of("Hides other player's fishing lines and bobbers."))
			.binding(
				getDefaults().hideOtherPlayersFishing,
				() -> CONFIG.hideOtherPlayersFishing,
				v -> CONFIG.hideOtherPlayersFishing = v
			)
			.controller(TickBoxController::new)
			.build();
	}

	public static Option<?> bobberTimer() {
		return Option.createBuilder(boolean.class)
			.name(Text.of("Bobber Timer"))
			.tooltip(Text.of("Displays a timer above your bobber. Can help with slugfishing."))
			.binding(
				getDefaults().bobberTimer,
				() -> CONFIG.bobberTimer,
				v -> CONFIG.bobberTimer = v
			)
			.controller(TickBoxController::new)
			.build();
	}
}
