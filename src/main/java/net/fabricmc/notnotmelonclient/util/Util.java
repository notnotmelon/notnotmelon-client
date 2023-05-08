package net.fabricmc.notnotmelonclient.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.fabricmc.notnotmelonclient.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
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

    public static String getDescriptorForClass(final Class c)
    {
        if(c.isPrimitive())
        {
            if(c==byte.class)
                return "B";
            if(c==char.class)
                return "C";
            if(c==double.class)
                return "D";
            if(c==float.class)
                return "F";
            if(c==int.class)
                return "I";
            if(c==long.class)
                return "J";
            if(c==short.class)
                return "S";
            if(c==boolean.class)
                return "Z";
            if(c==void.class)
                return "V";
            throw new RuntimeException("Unrecognized primitive "+c);
        }
        if(c.isArray()) return c.getName().replace('.', '/');
        return ('L'+c.getName()+';').replace('.', '/');
    }

    public static String getMethodDescriptor(Method m)
    {
        String s=m.getName() + "(";
        for(final Class c: m.getParameterTypes())
            s+=getDescriptorForClass(c);
        s+=')';
        return s+getDescriptorForClass(m.getReturnType());
    }

    public static boolean showDescriptor = false;
    public static void logMethodDescriptor()
    {
        if (showDescriptor) {
            try {
                Main.LOGGER.info("**** METHOD DESCRIPTOR");
                Main.LOGGER.info(Util.getMethodDescriptor(ItemRenderer.class.getMethod(
                    "renderGuiItemModel",
                    ItemStack.class, ModelTransformationMode.class, boolean.class, MatrixStack.class, VertexConsumerProvider.class, int.class, int.class, BakedModel.class
                )));
            } catch (NoSuchMethodException e) {}
        }
    }
}
