package net.fabricmc.notnotmelonclient;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.notnotmelonclient.api.ApiRequests;
import net.fabricmc.notnotmelonclient.commands.ConfigCommand;
import net.fabricmc.notnotmelonclient.commands.Reparty;
import net.fabricmc.notnotmelonclient.config.JsonLoader;
import net.fabricmc.notnotmelonclient.dungeons.Dungeons;
import net.fabricmc.notnotmelonclient.events.ChatTrigger;
import net.fabricmc.notnotmelonclient.fishing.Fishing;
import net.fabricmc.notnotmelonclient.misc.FavoriteItem;
import net.fabricmc.notnotmelonclient.misc.ItemPriceTooltip;
import net.fabricmc.notnotmelonclient.util.Scheduler;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Main implements ClientModInitializer {
	public static final MinecraftClient client = MinecraftClient.getInstance();
	public static final Logger LOGGER = LoggerFactory.getLogger("notnotmelonclient");
	public static final String NAMESPACE = "notnotmelonclient";
	public static Path configDir;
	public final Scheduler scheduler = new Scheduler();

	@Override
	public void onInitializeClient() {
		registerConfig();
		registerHotkeys();
		registerCyclic();
		registerCommands();
		registerEvents();
		registerChatTriggers();
	}

	private void registerConfig() {
		JsonLoader.load();
		FavoriteItem.loadConfig();
	}

	private void registerHotkeys() {
		FavoriteItem.addHotkey();
	}

	private void registerCyclic() {
		scheduler.scheduleCyclic(Util::locationTracker, 23);
		ApiRequests.init();
	}

	private void registerCommands() {
		ClientCommandRegistrationCallback.EVENT.register(this::registerCommands);
	}

	private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        ConfigCommand.register(dispatcher);
    }

	private void registerEvents() {
		ItemTooltipCallback.EVENT.register(ItemPriceTooltip::onInjectTooltip);
		Dungeons.registerEvents();
		Fishing.registerEvents();
	}

	private void registerChatTriggers() {
		for (ChatTrigger chatTrigger : new ChatTrigger[]{
			new Reparty()
		}) ChatTrigger.EVENT.register(chatTrigger);
	}
}
