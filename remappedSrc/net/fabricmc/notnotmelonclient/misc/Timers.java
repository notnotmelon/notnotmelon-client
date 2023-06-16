package net.fabricmc.notnotmelonclient.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.fishing.Fishing;
import net.fabricmc.notnotmelonclient.util.Scheduler;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;

public class Timers {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private static final Identifier ICONS = new Identifier(Main.NAMESPACE, "textures/gui/timers.png");

    public static void registerEvents() {
        Scheduler.scheduleCyclic(Timers::tick, 20);
    }

    private static final int minute = 1000 * 60;
    private static final int hour = 1000 * 60 * 60;
    public static void tick() {
        renderables.clear();
        if (!Util.isSkyblock) return;
        long currentTime = System.currentTimeMillis();

        if (CONFIG.darkAuctionTimer) {
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

        if (CONFIG.goldenFishTimer && Fishing.goldenFishTimer != -1) {
            if (Fishing.goldfishStreak + (minute * 3) < currentTime) {
                Util.print("Your Golden Fish timer was reset after 3 minutes of inactivity!");
                Fishing.resetGoldfish();
            } else {
                Text text;
                long milliseconds = (minute * 15) - (currentTime - Fishing.goldenFishTimer);
                if (milliseconds <= 0) {
                    double chance = Math.min(1, -milliseconds / (minute * 5d)) * 100;
                    text = Text.of(String.format("%.0f%%", chance));
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
    public static void render(DrawContext context) {
        if (renderables.size() == 0) return;

        Window window = client.getWindow();
        int scaledWidth = window.getScaledWidth();
        int x = scaledWidth / 2 - 91;
        int y = 2;


        for (RenderableTimer timer : renderables) {
            RenderSystem.enableBlend();
            timer.render(context, x, y);
            y += 18;
        }
    }

    private record RenderableTimer(Text text, int iconOffset, int color) {
        public void render(DrawContext context, int x, int y) {
            context.drawTexture(ICONS, x, y, iconOffset / 4f, 0, 16, 16, 128 / 4, 64 / 4);
            context.drawText(client.textRenderer, text, x + 18, y + 5, color, true);
        }
    }
}
