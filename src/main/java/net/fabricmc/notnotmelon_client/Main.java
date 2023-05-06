package net.fabricmc.notnotmelon_client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.notnotmelon_client.commands.ProtectItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.CommandDispatcher;

public class Main implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("notnotmelon-client");
	public static Path configDir;

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register(Main::registerCommands);

		configDir = FabricLoader.getInstance().getConfigDir().resolve("clientcommands");
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            LOGGER.error("Failed to create config dir", e);
        }
	}

	public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        ProtectItem.register(dispatcher);
    }
}
