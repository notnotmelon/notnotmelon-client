package net.fabricmc.notnotmelon_client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Util {
	public static void print(Text t) {
		try (MinecraftClient client = MinecraftClient.getInstance()) {
			client.player.sendMessage(t);
		}
	}

	public static void print(String s) {
		if (s == null)
			print("null");
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
        try (MinecraftClient client = MinecraftClient.getInstance()) {
            if (client.player == null) return new ArrayList<String>();
            Scoreboard scoreboard = client.player.getScoreboard();
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
            return new ArrayList<String>(); // This can fail silently. Risky?
        }
    }
}
