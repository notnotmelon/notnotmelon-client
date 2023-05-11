package net.fabricmc.notnotmelonclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.notnotmelonclient.commands.ConfigCommand;
import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.misc.FavoriteItem;
import net.fabricmc.notnotmelonclient.util.DevUtil;
import net.fabricmc.notnotmelonclient.util.Scheduler;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.command.CommandRegistryAccess;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.CommandDispatcher;

public class Main implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("notnotmelonclient");
	public static final String NAMESPACE = "notnotmelonclient";
	public static Path configDir;

	public final Scheduler scheduler = new Scheduler();

	@Override
	public void onInitializeClient() {
		DevUtil.logMethodDescriptor();

		registerConfig();
		registerHotkeys();
		registerCyclic();
		registerCommands();
	}

	private void registerConfig() {
		Config.build();
	}

	private void registerHotkeys() {
		FavoriteItem.addHotkey();
	}

	private void registerCyclic() {
		scheduler.scheduleCyclic(Util::locationTracker, 23);
	}

	private void registerCommands() {
		ClientCommandRegistrationCallback.EVENT.register(Main::registerCommands);
	}

	private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        ConfigCommand.register(dispatcher);
    }
}
