package net.fabricmc.notnotmelonclient.config;

import dev.isxander.yacl.config.GsonConfigInstance;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.notnotmelonclient.Main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonLoader {
    public static GsonConfigInstance<Config> jsonInterface;
    public static Path configDir;
    public static void load() {
        configDir = FabricLoader.getInstance().getConfigDir().resolve("notnotmelonclient/config.properties");
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            Main.LOGGER.error("Failed to create config dir", e);
        }

        jsonInterface = new GsonConfigInstance<Config>(Config.class, configDir);
        jsonInterface.load();
    }
}