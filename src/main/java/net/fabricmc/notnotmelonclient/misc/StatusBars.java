package net.fabricmc.notnotmelonclient.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

// this class renders stat bars for health, mana, and xp
// it uses the overlay message sent by Hypixel
// example overlay message:
// §64,029/3,729❤     §a536§a❈ Defense     §b2,512/2,512✎ §3600ʬ
// example without soulflow:
// §64,029/3,729❤     §a536§a❈ Defense     §b2,512/2,512✎ Mana§r

public class StatusBars {
    private static final Identifier HEALTH_BARS = new Identifier(Main.NAMESPACE, "textures/gui/health_bars.png");
    private static final Identifier MANA_BARS = new Identifier(Main.NAMESPACE, "textures/gui/mana_bars.png");
    private static final Identifier ORB = new Identifier(Main.NAMESPACE, "textures/gui/orb.png");

	private static final Pattern HEALTH_PATTERN = Pattern.compile("§[c6]<num>/<num>❤".replace("<num>", "(\\d+(,\\d\\d\\d)*)"));
    private static final Pattern DEFENSE_PATTERN = Pattern.compile("§a<num>§a❈ Defense".replace("<num>", "(\\d+(,\\d\\d\\d)*)"));
    private static final Pattern MANA_USE_PATTERN = Pattern.compile("§b-<num> Mana .*?\\)".replace("<num>", "(\\d+(,\\d\\d\\d)*)"));
    private static final Pattern MANA_PATTERN = Pattern.compile("§b<num>/<num>✎ (?:Mana|§3<num>ʬ)".replace("<num>", "(\\d+(,\\d\\d\\d)*)"));
	private static final Pattern OUT_OF_MANA_PATTERN = Pattern.compile("§c§lNOT ENOUGH MANA");
	private static final Pattern CLEANUP_PATTERN = Pattern.compile("     +");

	public static int playerMaxHealth;
	public static int playerHealth;
	public static int playerMaxMana;
	public static int playerMana;

	public static String parseOverlayMessage(String message) {
		Matcher healthMatcher = HEALTH_PATTERN.matcher(message);
		if (healthMatcher.find()) {
			playerHealth = Integer.valueOf(healthMatcher.group(1).replace(",", ""));
			playerMaxHealth = Integer.valueOf(healthMatcher.group(3).replace(",", ""));
			message = healthMatcher.replaceAll("");
		}

		// defense and mana use are mutually exclusive
		Matcher defenseMatcher = DEFENSE_PATTERN.matcher(message);
		Matcher manaUseMatcher = MANA_USE_PATTERN.matcher(message);
		if (manaUseMatcher.find()) {
			message = manaUseMatcher.replaceAll("");
		} else if (defenseMatcher.find()) {
			message = defenseMatcher.replaceAll("");
		}

		Matcher manaMatcher = MANA_PATTERN.matcher(message);
		Matcher outOfManaMatcher = OUT_OF_MANA_PATTERN.matcher(message);
		if (outOfManaMatcher.find()) {
			playerMana = 0;
		} else if (manaMatcher.find()) {
			int overflowMana = Integer.valueOf(manaMatcher.group(5).replace(",", ""));
			playerMana = Integer.valueOf(manaMatcher.group(1).replace(",", "")) + overflowMana;
			playerMaxMana = Integer.valueOf(manaMatcher.group(3).replace(",", ""));
			message = manaMatcher.replaceAll("");
		}
		
		return CLEANUP_PATTERN.matcher(message).replaceAll("     ").trim(); // reduce whitespace
	}

	public static void draw(MatrixStack matrices) {
		MinecraftClient client = MinecraftClient.getInstance();
		Window window = client.getWindow();
        int scaledWidth = window.getScaledWidth();
        int scaledHeight = window.getScaledHeight();
		int x = scaledWidth / 2 - 91;
		int y = scaledHeight - 33;

		drawOrb(matrices, client, x + 184, y + 15);
		RenderSystem.setShaderTexture(0, HEALTH_BARS);
		drawBar(matrices, client, x, y, playerHealth, playerMaxHealth, 0xFFFF5555);
		RenderSystem.setShaderTexture(0, MANA_BARS);
		drawBar(matrices, client, x + 92, y, playerMana, playerMaxMana, 0xFF55FFFF);
	}

	private static void drawBar(MatrixStack matrices, MinecraftClient client, int x, int y, int value, int maxValue, int color) {
		DrawableHelper.drawTexture(matrices, x, y, 0, 0, 90, 9, 90, 27);
		Text text = Text.literal(String.valueOf(value));

		int v = 9;
		while (value > 0 && v != 27) {
			if (value >= maxValue) { // draw a full bar
				DrawableHelper.drawTexture(matrices, x, y, 0, v, 90, 9, 90, 27);
			} else { // draw a partially filled bar
				double fill = (float) value / maxValue;
				DrawableHelper.drawTexture(matrices, x, y, 0, v, 11 + (int) Math.ceil(fill * 78), 9, 90, 27);
			}
			v += 9; // used for overflow bars. each overflow sprite is 9px lower on the spritesheet
			value -= maxValue;
		}

		Util.drawCenteredText(matrices, client, x + 50, y - 3, text, color);
	}

	private static void drawOrb(MatrixStack matrices, MinecraftClient client, int x, int y) {
		int level = client.player.experienceLevel;
		float progress = client.player.experienceProgress;
		String experience = String.valueOf(level) + "." + (int) (progress * 10);

		RenderSystem.setShaderTexture(0, ORB);
		DrawableHelper.drawTexture(matrices, x, y, 0, 0, 13, 13, 13, 13);
		Util.drawText(matrices, client, x + 7, y + 6, Text.literal(experience), 0xFFC8FF8F);
	}
}