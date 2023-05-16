package net.fabricmc.notnotmelonclient.commands;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.util.ChatTrigger;
import net.fabricmc.notnotmelonclient.util.Scheduler;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.brigadier.Command;

public class Reparty implements ChatTrigger {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private String[] players;
    private int foundPlayers = 0;
    private boolean repartying = false;

    public Reparty() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) ->
            dispatcher.register(ClientCommandManager.literal("rp")
                .executes(ctx -> {
                    if (client.player == null || !Util.isSkyblock || repartying) return Command.SINGLE_SUCCESS;
                    client.player.networkHandler.sendCommand("p list");
                    this.repartying = true;
                    return Command.SINGLE_SUCCESS;
                })
            )
        );
    }

    private static final Pattern PLIST = Pattern.compile("^Party (?:Members|Moderators)(?: \\((\\d+)\\)|:( .*))$");
    private static final Pattern PLAYER = Pattern.compile(" ([a-zA-Z0-9_]{2,16}) ●");
    private static final Pattern EXTRA_STATS = Pattern.compile("^§r                             §6> §e§lEXTRA STATS §6<$");
    public ActionResult onMessage(Text text, String asString) {
        if (Config.getConfig().autoExtraStats && EXTRA_STATS.matcher(asString).matches()) {
            Util.sendDelayedCommand("showextrastats", 10);
            if (Config.getConfig().autoRepartyAccept) {
                Util.sendDelayedCommand("p list", 20);
                this.repartying = true;
                return ActionResult.FAIL;
            }
        }

        if (Config.getConfig().autoRepartyAccept && autoReparty(text, asString)) return ActionResult.PASS;
        if (!repartying) return ActionResult.PASS;

        if (asString.equals("-----------------------------------------------------"))
            return ActionResult.FAIL;

        if (asString.equals("You are not currently in a party.")) {
            this.reset();
            return ActionResult.PASS;
        }

        Matcher plist = PLIST.matcher(asString);
        if (!plist.matches()) return ActionResult.PASS;
        if (plist.group(1) != null) {
            this.foundPlayers = 0;
            int partySize = Integer.parseInt(plist.group(1));
            this.players = new String[partySize - 1];
        } else if (plist.group(2) != null) {
            Matcher player = PLAYER.matcher(plist.group(2));
            while (player.find()) {
                this.players[foundPlayers] = player.group(1);
                foundPlayers++;
            }
        }
        if (this.foundPlayers == this.players.length) reparty();
        return ActionResult.PASS;
    }

    private String previousPartyLeader;
    private static final Pattern DISBAND = Pattern.compile("^(\\[.+?\\] )?([a-zA-Z0-9_]{2,16}) has disbanded the party!$");
    private static final Pattern INVITE = Pattern.compile("^-----------------------------------------------------\\n(\\[.+?\\] )?([a-zA-Z0-9_]{2,16}) has invited you to join their party!\\nYou have 60 seconds to accept. Click here to join!\\n-----------------------------------------------------$");
    private boolean autoReparty(Text text, String asString) {
        Matcher invite = INVITE.matcher(asString);
        if (invite.matches()) {
            if (previousPartyLeader != null) {
                Util.sendDelayedCommand("p accept " + previousPartyLeader, 10);
                previousPartyLeader = null;
            }
            return true;
        }

        Matcher disband = DISBAND.matcher(asString);
        if (disband.matches()) {
            previousPartyLeader = disband.group(2);
            Scheduler.getInstance().schedule(() -> this.previousPartyLeader = null, 20 * 10);
            return true;
        }

        return false;
    }

    private void reparty() {
        Util.sendDelayedCommand("p disband", 5);
        int delay = 15;
        for (String player : this.players) {
            delay += 5;
            Util.sendDelayedCommand("p invite " + player, delay);
        }
        Scheduler.getInstance().schedule(this::reset, delay + 20);
    }

    private void reset() {
        this.repartying = false;
        this.players = null;
        this.foundPlayers = 0;
    }
}