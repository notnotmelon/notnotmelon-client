package net.fabricmc.notnotmelonclient.config.categories;

import com.google.gson.*;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.ListOption;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import net.fabricmc.notnotmelonclient.config.KeybindWithTextElement;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Type;

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
				.initial(new CommandKeybind("", GLFW.GLFW_KEY_UNKNOWN))
				.build())

			.build();
	}

	public record CommandKeybind(String command, int keyBind) implements JsonSerializer<CommandKeybind>, JsonDeserializer<CommandKeybind>  {
		@Override
		public CommandKeybind deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonArray array = jsonElement.getAsJsonArray();
			return new CommandKeybind(array.get(0).getAsString(), array.get(1).getAsInt());
		}

		@Override
		public JsonElement serialize(CommandKeybind commandKeybind, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonArray array = new JsonArray();
			array.add(commandKeybind.command);
			array.add(commandKeybind.keyBind);
			return array;
		}
	}

	public record KeybindController(Option<CommandKeybind> option) implements Controller<CommandKeybind> {
		@Override
		public Text formatValue() {
			return Text.of("/" + getCommand());
		}

		@Override
		public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
			return new KeybindWithTextElement(this, screen, widgetDimension);
		}

		public String getCommand() {
			String command = option().pendingValue().command;
			return command == null ? "" : command;
		}

		public void setCommand(String command) {
			option.requestSet(new CommandKeybind(
				command,
				option.pendingValue().keyBind
			));
		}
	}
}
