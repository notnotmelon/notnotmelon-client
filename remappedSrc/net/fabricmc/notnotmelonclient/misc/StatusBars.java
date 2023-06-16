package net.fabricmc.notnotmelonclient.misc;

import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			playerHealth = Integer.parseInt(healthMatcher.group(1).replace(",", ""));
			playerMaxHealth = Integer.parseInt(healthMatcher.group(3).replace(",", ""));
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
			int overflowMana = Integer.parseInt(manaMatcher.group(5).replace(",", ""));
			playerMana = Integer.parseInt(manaMatcher.group(1).replace(",", "")) + overflowMana;
			playerMaxMana = Integer.parseInt(manaMatcher.group(3).replace(",", ""));
			message = manaMatcher.replaceAll("");
		}
		
		return CLEANUP_PATTERN.matcher(message).replaceAll("     ").trim(); // reduce whitespace
	}

	public static void draw(DrawContext context) {
		MinecraftClient client = MinecraftClient.getInstance();
		Window window = client.getWindow();
        int scaledWidth = window.getScaledWidth();
        int scaledHeight = window.getScaledHeight();
		int x = scaledWidth / 2 - 91;
		int y = scaledHeight - 33;

		drawOrb(context, client, x + 184, y + 15);
		drawBar(context, HEALTH_BARS, x, y, playerHealth, playerMaxHealth, 0xFFFF5555);
		drawBar(context, MANA_BARS, x + 92, y, playerMana, playerMaxMana, 0xFF55FFFF);
	}

	private static void drawBar(DrawContext context, Identifier identifier, int x, int y, int value, int maxValue, int color) {
		context.drawTexture(identifier, x, y, 0, 0, 90, 9, 90, 27);
		Text text = Text.literal(String.valueOf(value));

		int v = 9;
		while (value > 0 && v != 27) {
			if (value >= maxValue) { // draw a full bar
				context.drawTexture(identifier, x, y, 0, v, 90, 9, 90, 27);
			} else { // draw a partially filled bar
				double fill = (float) value / maxValue;
				context.drawTexture(identifier, x, y, 0, v, 11 + (int) Math.ceil(fill * 78), 9, 90, 27);
			}
			v += 9; // used for overflow bars. each overflow sprite is 9px lower on the sprite sheet
			value -= maxValue;
		}

		RenderUtil.drawCenteredText(context, text, x + 50, y - 3, color);
	}

	private static void drawOrb(DrawContext context, MinecraftClient client, int x, int y) {
		int level = client.player.experienceLevel;
		float progress = client.player.experienceProgress;
		if (progress > 0.99 && level == 0 && Util.isDungeons()) {
			level += 1;
			progress = 0;
		}
		Text experience = Text.of(level + "." + (int) (progress * 10));

		context.drawTexture(ORB, x, y, 0, 0, 13, 13, 13, 13);
		context.drawText(client.textRenderer, experience, x + 7, y + 6, 0xFFC8FF8F, true);
	}
}