package net.fabricmc.notnotmelonclient.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.fishing.Fishing;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.fabricmc.notnotmelonclient.util.Scheduler;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class Timers {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private static final Identifier ICONS = new Identifier(Main.NAMESPACE, "textures/gui/timers.png");

    public static void registerEvents() {
        Scheduler.getInstance().scheduleCyclic(Timers::tick, 20);
    }

    private static final int minute = 1000 * 60;
    private static final int hour = 1000 * 60 * 60;
    public static void tick() {
        renderables.clear();
        if (!Util.isSkyblock) return;
        long currentTime = System.currentTimeMillis();

        if (Config.getConfig().darkAuctionTimer) {
            long milliseconds = currentTime % hour;
            if (milliseconds > minute * 55)
                milliseconds -= minute * 55;
            else
                milliseconds += minute * 5;

            renderables.add(new RenderableTimer(
                Text.of(formatTimer(hour - milliseconds)),
                0,
                0xFFFFFFFF
            ));
        }

        if (Config.getConfig().goldenFishTimer && Fishing.goldenFishTimer != -1) {
            if (Fishing.goldfishStreak + (minute * 3) < currentTime) {
                Util.print("Your Golden Fish timer was reset after 3 minutes of inactivity!");
                Fishing.resetGoldfish();
            } else {
                Text text;
                long milliseconds = (minute * 15) - (currentTime - Fishing.goldenFishTimer);
                if (milliseconds <= 0) {
                    double chance = Math.min(1, -milliseconds / (minute * 5)) * 100;
                    text = Text.of(String.format("%.1f%%", chance));
                } else {
                    text = Text.of(formatTimer(milliseconds));
                }

                renderables.add(new RenderableTimer(
                    text,
                    64,
                    0xFFFFFFFF
                ));
            }
        }
    }

    public static String formatTimer(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        return String.format("%d:%02d", minutes, seconds % 60);
    }

    private static final ArrayList<RenderableTimer> renderables = new ArrayList<>();
    public static void render(MatrixStack matrices) {
        if (renderables.size() == 0 || !Util.isSkyblock) return;

        Window window = client.getWindow();
        int scaledWidth = window.getScaledWidth();
        int scaledHeight = window.getScaledHeight();
        int x = scaledWidth / 2 - 91;
        int y = 2;

        RenderSystem.setShaderTexture(0, ICONS);
        for (RenderableTimer timer : renderables) {
            timer.render(matrices, x, y);
            y -= 33;
        }
    }

    private record RenderableTimer(Text text, int iconOffset, int color) {
        public void render(MatrixStack matrices, int x, int y) {
            DrawableHelper.drawTexture(matrices, x, y, iconOffset / 4f, 0, 16, 16, 128 / 4, 64 / 4);
            x += 18;
            y += 5;
            RenderUtil.drawText(matrices, client, x, y, text, color);
        }
    }
}
