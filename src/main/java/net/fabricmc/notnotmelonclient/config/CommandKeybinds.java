package net.fabricmc.notnotmelonclient.config;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.ListOption;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.gui.controllers.ControllerWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.config.Config.getConfig;
import static net.fabricmc.notnotmelonclient.config.Config.getDefaults;

public class CommandKeybinds {
	public static ConfigCategory category() {
		return ConfigCategory.createBuilder()
			.name(Text.literal("Command Keybinds"))

			.option(ListOption.createBuilder(CommandKeybind.class)
				.name(Text.of("Command Keybinds"))
				.tooltip(Text.of("Set custom keybinds for chat commands."))
				.binding(
					getDefaults().commandKeybinds,
					() -> getConfig().commandKeybinds,
					v -> getConfig().commandKeybinds = v
				)
				.controller(KeybindController::new)
				.build())

			.build();
	}

	public record CommandKeybind(String command, KeyBinding keyBind) {}

	private record KeybindController(Option<CommandKeybind> option) implements Controller<CommandKeybind> {
		@Override
		public Text formatValue() {
			return Text.empty();
		}

		@Override
		public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
			return new KeybindControllerElement(this, screen, widgetDimension);
		}
	}

	private static class KeybindControllerElement extends ControllerWidget<KeybindController> {
		public KeybindControllerElement(KeybindController control, YACLScreen screen, Dimension<Integer> dim) {
			super(control, screen, dim);
		}

		@Override
		protected int getHoveredControlWidth() {
			return 0;
		}
	}
}
