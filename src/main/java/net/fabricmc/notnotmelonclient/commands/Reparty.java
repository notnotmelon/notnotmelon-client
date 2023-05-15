package net.fabricmc.notnotmelonclient.commands;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.notnotmelonclient.util.ChatTrigger;
import net.fabricmc.notnotmelonclient.util.Scheduler;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

public class Reparty implements ChatTrigger {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private String[] players;
    private int playersSoFar = 0;
    private boolean repartying = false;

    public Reparty(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("rp")
			.executes(ctx -> {
                if (!Util.isSkyblock || repartying || client.player == null) return Command.SINGLE_SUCCESS;
                client.player.networkHandler.sendCommand("p list");
                this.repartying = true;
                return Command.SINGLE_SUCCESS;
			})
        );
    }

    private static final Pattern PLIST = Pattern.compile("^Party (?:Members|Moderators)(?: \\((\\d+)\\)|:( .*))$");
    private static final Pattern PLAYER = Pattern.compile(" ([a-zA-Z0-9_]{2,16}) ●");
    public ActionResult onMessage(Text message, String asString) {
        if (!repartying) return ActionResult.PASS;

        if (asString.equals("-----------------------------------------------------")) {
            return ActionResult.FAIL;
        }

        if (asString.equals("You are not currently in a party.")) {
            this.reset();
            return ActionResult.PASS;
        }

        Matcher plist = PLIST.matcher(asString);
        if (!plist.matches()) return ActionResult.PASS;
        if (plist.group(1) != null) {
            try {
                this.playersSoFar = 0;
                this.players = new String[Integer.parseInt(plist.group(1)) - 1];
            } catch(NumberFormatException e) {
                Util.print("Reparty failed. Please report this!");
                this.reset();
                return ActionResult.PASS;
            }
        } else if (plist.group(2) != null) {
            Matcher player = PLAYER.matcher(plist.group(2));
            while (player.find()) {
                this.players[playersSoFar] = player.group(1);
                playersSoFar++;
            }
        }
        if (this.playersSoFar == this.players.length) reparty();
        return ActionResult.SUCCESS;
    }

    private void reparty() {
        Util.sendDelayedCommand("p disband", 5);
        int delay = 10;
        for (String player : this.players) {
            delay += 5;
            Util.sendDelayedCommand("p invite " + player, delay);
        }
        Scheduler.getInstance().schedule(this::reset, delay + 20);
    }

    private void reset() {
        this.repartying = false;
        this.players = null;
        this.playersSoFar = 0;
    }
}