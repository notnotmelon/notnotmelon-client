package net.fabricmc.notnotmelonclient.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Util {
	public static void print(MutableText t) {
        MutableText prefixed = Text.literal("§d[nnc]§r ").append(t);
		MinecraftClient.getInstance().player.sendMessage(prefixed);
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
            ClientPlayerEntity client = MinecraftClient.getInstance().player;
            if (client == null) return new ArrayList<String>();
            Scoreboard scoreboard = client.getScoreboard();
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
}
