package net.fabricmc.notnotmelonclient.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.util.Scheduler;

public class ConfigCommand {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(ClientCommandManager.literal("nnc")
			.executes(ctx -> {
				Scheduler.schedule(Config.instance::draw, 1);
				return Command.SINGLE_SUCCESS;
			})
        );
	}
}
