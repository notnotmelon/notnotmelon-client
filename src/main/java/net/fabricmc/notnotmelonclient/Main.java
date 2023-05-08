package net.fabricmc.notnotmelonclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.notnotmelonclient.misc.FavoriteItem;
import net.fabricmc.notnotmelonclient.util.DevUtil;
import net.fabricmc.notnotmelonclient.util.Scheduler;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.command.CommandRegistryAccess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.CommandDispatcher;

public class Main implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("notnotmelonclient");
	public static Path configDir;

	public final Scheduler scheduler = new Scheduler();

	@Override
	public void onInitializeClient() {
		DevUtil.logMethodDescriptor();

		registerHotkeys();
		registerCyclic();
		ClientCommandRegistrationCallback.EVENT.register(Main::registerCommands);

		configDir = FabricLoader.getInstance().getConfigDir().resolve("notnotmelonclient");
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            LOGGER.error("Failed to create config dir", e);
        }
	}

	public void registerHotkeys() {
		FavoriteItem.addHotkey();
	}

	public void registerCyclic() {
		scheduler.scheduleCyclic(Util::locationTracker, 23);
	}

	public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        
    }

	public void onTick() {
		Util.print("zz");
        scheduler.tick();
    }
}
