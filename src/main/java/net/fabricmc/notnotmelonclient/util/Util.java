package net.fabricmc.notnotmelonclient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
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
	public static void print(MutableText t) {
        MutableText prefixed = Text.literal("§d[nnc]§r ").append(t);
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) player.sendMessage(prefixed);
	}

	public static void print(String s) {
		print(Text.literal(s));
	}

	public static void print(Object o) {
		print(o.toString());
	}

	public static String getLocation() {
        String location = null;
        List<String> sidebarLines = getSidebar();
        for (String sidebarLine : sidebarLines) {
            if (sidebarLine.contains("⏣")) location = sidebarLine;
        }
        if (location == null) location = "Unknown";
        location = location.replace('⏣', ' ').strip();
        return location;
    }

	// Source: https://github.com/SkyblockerMod/Skyblocker/blob/master/src/main/java/me/xmrvizzy/skyblocker/utils/Utils.java
	public static List<String> getSidebar() {
        try {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return new ArrayList<String>();
            Scoreboard scoreboard = player.getScoreboard();
            ScoreboardObjective objective = scoreboard.getObjectiveForSlot(1);
            List<String> lines = new ArrayList<String>();
            for (ScoreboardPlayerScore score : scoreboard.getAllPlayerScores(objective)) {
                Team team = scoreboard.getPlayerTeam(score.getPlayerName());
                if (team != null) {
                    String line = team.getPrefix().getString() + team.getSuffix().getString();
                    if (line.trim().length() > 0) {
                        String formatted = Formatting.strip(line);
                        lines.add(formatted);
                    }
                }
            }

            if (objective != null) {
                lines.add(objective.getDisplayName().getString());
                Collections.reverse(lines);
            }
            return lines;
        } catch (NullPointerException e) {
            return new ArrayList<String>();
        }
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

    static final int[] offsets = new int[]{-1, 1};
    public static void drawText(MatrixStack matrices, MinecraftClient client, float x, float y, Text text, int color) {
        TextRenderer textRenderer = client.textRenderer;
        int alpha = color>>24<<24;
		for (int i : offsets) {
			textRenderer.draw(matrices, text, x + i, y, alpha);
			textRenderer.draw(matrices, text, x, y + i, alpha);
		}
		textRenderer.draw(matrices, text, x, y, color);
	}

    public static void drawCenteredText(MatrixStack matrices, MinecraftClient client, float x, float y, Text text, int color) {
        TextRenderer textRenderer = client.textRenderer;
        x -= (float) textRenderer.getWidth(text) / 2;
        drawText(matrices, client, x, y, text, color);
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
