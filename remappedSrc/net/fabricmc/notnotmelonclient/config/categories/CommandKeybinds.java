package net.fabricmc.notnotmelonclient.config.categories;

import com.google.gson.*;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.api.OptionDescription;
import net.fabricmc.notnotmelonclient.config.KeybindWithTextElement;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Type;
import java.util.Objects;

import static net.fabricmc.notnotmelonclient.Main.client;
import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;
import static net.fabricmc.notnotmelonclient.config.Config.getDefaults;

public class CommandKeybinds {
	public static ConfigCategory category() {
		return ConfigCategory.createBuilder()
			.name(Text.literal("Command Keybinds"))

			.option(ListOption.createBuilder(CommandKeybind.class)
				.name(Text.of("Command Keybinds"))
				.description(OptionDescription.of(Text.of("Set custom keybinds for chat commands.")))
				.binding(
					getDefaults().commandKeybinds,
					() -> CONFIG.commandKeybinds,
					v -> CONFIG.commandKeybinds = v
				)
				.customController(KeybindController::new)
				.initial(new CommandKeybind("", GLFW.GLFW_KEY_UNKNOWN))
				.build())

			.build();
	}

	public static void onKeyPress(int keyCode, int action) {
		if (action != GLFW.GLFW_PRESS) return;
		if (client.currentScreen != null) return;
		if (client.player == null) return;

		for (CommandKeybind commandKeybind : CONFIG.commandKeybinds)
			if (commandKeybind.keyBind == keyCode)
				client.player.networkHandler.sendCommand(commandKeybind.command);
	}

	public static class CommandKeybindSerializer implements JsonSerializer<CommandKeybind>, JsonDeserializer<CommandKeybind> {
		@Override
		public CommandKeybind deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonArray()) {
				JsonArray array = jsonElement.getAsJsonArray();
				return new CommandKeybind(array.get(0).getAsString(), array.get(1).getAsInt());
			}
			return new CommandKeybind("", GLFW.GLFW_KEY_UNKNOWN);
		}

		@Override
		public JsonElement serialize(CommandKeybind commandKeybind, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonArray array = new JsonArray();
			array.add(commandKeybind.command);
			array.add(commandKeybind.keyBind);
			return array;
		}

		//public static final UnaryOperator<GsonBuilder> serializer = builder -> builder.registerTypeHierarchyAdapter(CommandKeybind.class, new CommandKeybindSerializer());
	}

	public record CommandKeybind(String command, int keyBind) {
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof CommandKeybind that)) return false;
			return Objects.equals(this.command, that.command) && this.keyBind == that.keyBind;
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
			CommandKeybind previous = option.pendingValue();
			if (Objects.equals(previous.command, command)) return;
			option.requestSet(new CommandKeybind(
				command,
				previous.keyBind
			));
		}

		public int getKeyBind() {
			return option().pendingValue().keyBind;
		}

		public void setKeyBind(int keyBind) {
			CommandKeybind previous = option.pendingValue();
			if (previous.keyBind == keyBind) return;
			option.requestSet(new CommandKeybind(
				previous.command,
				keyBind
			));
		}
	}
}
