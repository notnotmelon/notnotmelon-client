package net.fabricmc.notnotmelonclient.config;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.GsonConfigInstance;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.notnotmelonclient.config.categories.CommandKeybinds;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;
import java.nio.file.Path;

public class JsonLoader {
    public static GsonConfigInstance<Config> jsonInterface;
    public static Path configDir;
    public static void load() {
        configDir = FabricLoader.getInstance().getConfigDir().resolve("notnotmelonclient/config.properties");
        jsonInterface = GsonConfigInstance.createBuilder(Config.class)
            .setPath(configDir)
            //.appendGsonBuilder(CommandKeybinds.CommandKeybindSerializer.serializer)   <- this causes infinite recursion inside yacl
            .overrideGsonBuilder(
                new GsonBuilder().setExclusionStrategies(new ConfigExclusionStrategy())
                    .registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
                    .registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
                    .registerTypeHierarchyAdapter(Color.class, new GsonConfigInstance.ColorTypeAdapter())
                    .registerTypeHierarchyAdapter(CommandKeybinds.CommandKeybind.class, new CommandKeybinds .CommandKeybindSerializer())
                    .serializeNulls()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create() // instead we need to do this silliness
            )
            .build();
        jsonInterface.load();

        Config.CONFIG = JsonLoader.jsonInterface.getConfig();
    }

    // ctrl+c ctrl+v from GsonConfigInstance. Original is private.
    private static class ConfigExclusionStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return fieldAttributes.getAnnotation(ConfigEntry.class) == null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> aClass) {
            return false;
        }
    }
}