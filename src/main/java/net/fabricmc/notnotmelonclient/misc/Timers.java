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

    private static int threeMinutes = 1000 * 60 * 3;
    public static void tick() {
        renderables.clear();
        if (!Util.isSkyblock) return;

        if (Config.getConfig().goldenFishTimer && Fishing.goldenFishTimer != -1) {
            Util.print(Fishing.goldfishStreak);
            Util.print(System.currentTimeMillis());m
            if (Fishing.goldfishStreak + threeMinutes >= System.currentTimeMillis()) {
                Util.print("q");
                Fishing.goldenFishTimer = -1;
            } else {
                Util.print("g");
                Text text;
                long seconds = Fishing.goldenFishTimer / 1000;
                long minutes = seconds / 60;
                if (minutes >= 15) {
                    long chance = Math.min(100, 100 * (seconds - 60 * 15) / (60 * 20));
                    text = Text.of(chance + "%");
                } else {
                    text = Text.of(String.format("%d:%02d", minutes, seconds));
                }

                renderables.add(new RenderableTimer(
                        text,
                        64,
                        0xFFFFFFFF
                ));
            }
        }
    }

    private static final ArrayList<RenderableTimer> renderables = new ArrayList<>();
    public static void render(MatrixStack matrices) {
        if (renderables.size() == 0 || !Util.isSkyblock) return;

        Window window = client.getWindow();
        int scaledWidth = window.getScaledWidth();
        int scaledHeight = window.getScaledHeight();
        int x = scaledWidth / 2 - 91;
        int y = scaledHeight - 33;

        RenderSystem.setShaderTexture(0, ICONS);
        for (RenderableTimer timer : renderables) {
            timer.render(matrices, x, y);
            y -= 33;
        }
    }

    private static class RenderableTimer {
        private final Text text;
        private final int iconOffset;
        private final int color;

        public RenderableTimer(Text text, int iconOffset, int color) {
            this.text = text;
            this.iconOffset = iconOffset;
            this.color = color;
        }

        public void render(MatrixStack matrices, int x, int y) {
            DrawableHelper.drawTexture(matrices, x, y, 0, iconOffset, 64, 64, 64, 64);
            x += 32;
            RenderUtil.drawText(matrices, client, x, y, text, color);
        }
    }
}
