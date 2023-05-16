package net.fabricmc.notnotmelonclient.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		if(sidebarLines != null) {
			for (String sidebarLine : sidebarLines) {
				if (sidebarLine.contains("⏣")) location = sidebarLine;
			}
			if (location == null) location = "Unknown";
			location = location.replace('⏣', ' ').strip();
		}
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
        List<String> sidebar;

        if (client.world == null || client.isInSingleplayer() || (sidebar = getSidebar()) == null) {
            isSkyblock = false;
            isDungeons = false;
            return;
        }

        if (sidebar.isEmpty()) return;
        String objective = sidebar.get(0);
        if (objective.equals("SKYBLOCK") || objective.equals("SKIBLOCK")) {
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
			textRenderer.draw(matrices, text, (float) (x + i), (float) y, alpha);
			textRenderer.draw(matrices, text, (float) x, (float) (y + i), alpha);
		}
		textRenderer.draw(matrices, text, (float) x, (float) y, color);
	}

    public static void drawCenteredText(MatrixStack matrices, MinecraftClient client, float x, float y, Text text, int color) {
        TextRenderer textRenderer = client.textRenderer;
        x -= textRenderer.getWidth(text) / 2;
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
}
