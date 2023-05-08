package net.fabricmc.notnotmelonclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.notnotmelonclient.misc.FavoriteItem;
import net.fabricmc.notnotmelonclient.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("notnotmelonclient");
	public static Path configDir;

	@Override
	public void onInitializeClient() {
		Util.logMethodDescriptor();

		FavoriteItem.addHotkey();

		configDir = FabricLoader.getInstance().getConfigDir().resolve("notnotmelonclient");
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            LOGGER.error("Failed to create config dir", e);
        }
	}

	//ClientCommandRegistrationCallback.EVENT.register(Main::registerCommands);
	/*public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        ProtectItem.register(dispatcher);
    }*/
}
