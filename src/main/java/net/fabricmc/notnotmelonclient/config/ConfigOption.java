package net.fabricmc.notnotmelonclient.config;

import java.util.function.Function;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import net.minecraft.text.Text;

public class ConfigOption<T> {
	public Option<T> option;
	@SuppressWarnings("unchecked")
	ConfigOption(Class<T> typeClass, String id, T defaultValue, Text name, Text tooltip, Function<Option<T>, Controller<T>> controller) {
		option = Option.createBuilder(typeClass)
			.name(name)
			.tooltip(tooltip)
			.binding(
				defaultValue,
				() -> { return defaultValue; },
				v -> Config.options.put(id, v)
			)
			.controller(controller)
			.build();
	}
}
