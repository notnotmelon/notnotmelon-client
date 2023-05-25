package net.fabricmc.notnotmelonclient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Util {
	public static void print(Text t) {
        MutableText prefixed = Text.literal("§d[nnc]§r ").append(t);
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) player.sendMessage(prefixed);
	}

	public static void print(String s) {
		print(Text.literal(s));
	}

	public static void print(char c) {
		print(Text.literal(String.valueOf(c)));
	}

	public static void print(Object o) {
		print(o.toString());
	}

	public static List<String> getSidebar() {
        try {
            List<String> result = new ArrayList<>();
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            Scoreboard scoreboard = player.getScoreboard();
            ScoreboardObjective objective = scoreboard.getObjectiveForSlot(1);
            for (ScoreboardPlayerScore score : scoreboard.getAllPlayerScores(objective)) {
                Team team = scoreboard.getPlayerTeam(score.getPlayerName());
                String line = team.getPrefix().getString() + team.getSuffix().getString();
                line = Formatting.strip(line.trim());
                if (!line.isEmpty()) result.add(line);
            }
            result.add(objective.getDisplayName().getString());
            Collections.reverse(result);
            return result;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    private static String location;
    public static List<String> getTablist() {
        if (!isSkyblock) return new ArrayList<>();
        try {
            List<String> result = new ArrayList<>();
            PlayerListHud hud = MinecraftClient.getInstance().inGameHud.getPlayerListHud();
            for (PlayerListEntry playerEntry : hud.collectPlayerEntries()) {
                String playerName = hud.getPlayerName(playerEntry).getString().trim();
                if (!playerName.isEmpty()) {
                    result.add(playerName);
                    if (location == null && playerName.startsWith("Area: "))
                        location = playerName.replaceFirst("Area: ", "");
                }
            }
            Util.print(location);
            return result;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public static String getLocation() {
        if (!isSkyblock) return null;
        if (location == null) getTablist();
        return location;
    }

    public static boolean isSkyblock = false;
    public static boolean isDungeons = false;
    public static void locationTracker() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world == null || client.isInSingleplayer()) {
            isSkyblock = false;
            isDungeons = false;
            return;
        }

        List<String> sidebar = getSidebar();
        if (sidebar.isEmpty()) return;
        String objective = sidebar.get(0);
        if (objective.startsWith("SKYBLOCK") || objective.startsWith("SKIBLOCK")) {
            isSkyblock = true;
            isDungeons = sidebar.toString().contains("The Catacombs");
        } else if (isSkyblock) {
            isSkyblock = false;
            isDungeons = false;
        }
    }

    public static String orderedTextAsString(OrderedText orderedText) {
        StringBuilder sb = new StringBuilder();
        orderedText.accept((i, s, c) -> { sb.append(c); return true; } );
        return sb.toString();
    }

    public static void sendDelayedCommand(String command, int delay) {
        Scheduler.getInstance().schedule(() -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) player.networkHandler.sendCommand(command);
        }, delay);
    }

    public static double[] getMousePosition() {
        double[] mouseX = new double[1];
        double[] mouseY = new double[1];
        long handler = MinecraftClient.getInstance().getWindow().getHandle();
        GLFW.glfwGetCursorPos(handler, mouseX, mouseY);
        return new double[]{mouseX[0], mouseY[0]};
    }

    public static void setMousePosition(double x, double y) {
        MinecraftClient client = MinecraftClient.getInstance();
        long handler = client.getWindow().getHandle();
        GLFW.glfwSetInputMode(handler, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        GLFW.glfwSetCursorPos(handler, x, y);
        client.mouse.onCursorPos(handler, x, y);
    }

    public static long getGametick() {
        try {
            return MinecraftClient.getInstance().world.getTime();
        } catch(NullPointerException e) {
            return -1;
        }
    }
}
