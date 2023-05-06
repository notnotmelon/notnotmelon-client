package net.fabricmc.notnotmelonclient.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.notnotmelonclient.Util;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class ProtectItem {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("protectitem").executes(ctx -> execute(ctx.getSource())));
    }

	private static int execute(FabricClientCommandSource source) {
        for (String s : Util.getSidebar())
            Util.print(s);
		return Command.SINGLE_SUCCESS;
    }
}
