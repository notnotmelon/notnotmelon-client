package net.fabricmc.notnotmelonclient.config;

import dev.isxander.yacl.config.GsonConfigInstance;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class JsonLoader {
    public static GsonConfigInstance<Config> jsonInterface;
    public static Path configDir;
    public static void load() {
        configDir = FabricLoader.getInstance().getConfigDir().resolve("notnotmelonclient/config.properties");
        jsonInterface = new GsonConfigInstance<>(Config.class, configDir);
        jsonInterface.load();
    }
}