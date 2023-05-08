package net.fabricmc.notnotmelonclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.notnotmelonclient.misc.FavoriteItem;
import net.fabricmc.notnotmelonclient.util.DevUtil;
import net.fabricmc.notnotmelonclient.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractScheduledService.Scheduler;

public class Main implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("notnotmelonclient");
	public static Path configDir;
	public final Scheduler scheduler = new Scheduler();

	@Override
	public void onInitializeClient() {
		DevUtil.logMethodDescriptor();

		registerHotkeys();
		registerCyclic();

		configDir = FabricLoader.getInstance().getConfigDir().resolve("notnotmelonclient");
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            LOGGER.error("Failed to create config dir", e);
        }
	}

	public static void registerHotkeys() {
		FavoriteItem.addHotkey();
	}

	public static void registerCyclic() {
		scheduler.scheduleCyclic(Util::locationTracker, 20);
	}

	//ClientCommandRegistrationCallback.EVENT.register(Main::registerCommands);
	/*public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        ProtectItem.register(dispatcher);
    }*/
}
