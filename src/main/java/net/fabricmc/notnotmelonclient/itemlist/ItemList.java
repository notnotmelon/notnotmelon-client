package net.fabricmc.notnotmelonclient.itemlist;

import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.misc.ScrollableTooltips;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.List;

import static net.fabricmc.notnotmelonclient.Main.client;

public class ItemList {
	public static int xOffset = 0;
	public static int yOffset = 18;
	public static int lastMouseX = -1;
	public static int lastMouseY = -1;
	public static int pageNumber = 2;
	public static final int step = 18;
	public static int pageSize;
	public static int startIndex;
	public static int endIndex;
	public static Text pageNumberText;
	public static int textRenderX;

	public static void render(HandledScreen<?> screen, MatrixStack matrices, int mouseX, int mouseY) {
		if (!NeuRepo.isDownloaded) return;
		ItemRenderer itemRenderer = client.getItemRenderer();
		mouseX -= xOffset;
		int targetMouseX = mouseX - Math.abs(mouseX) % step + xOffset;
		int targetMouseY = mouseY - Math.abs(mouseY) % step;
		boolean renderedTooltip = false;

		for (int i = startIndex; i < endIndex; i++) {
			ItemListIcon icon = NeuRepo.itemListIcons.get(i);
			itemRenderer.renderInGui(matrices, icon.stack, icon.x, icon.y);
			if (!renderedTooltip && targetMouseX == icon.x && targetMouseY == icon.y) {
				screen.renderTooltip(matrices, screen.getTooltipFromItem(icon.stack), icon.stack.getTooltipData(), mouseX + xOffset, mouseY);
				renderedTooltip = true;
			}
		}

		if (renderedTooltip && (targetMouseX != lastMouseX || targetMouseY != lastMouseY)) ScrollableTooltips.reset();
		lastMouseX = targetMouseX;
		lastMouseY = targetMouseY;

		RenderUtil.drawCenteredText(matrices, client, textRenderX, 7, pageNumberText, -1);
	}

	public static void onOpenScreen(HandledScreen<?> screen) {
		cacheItemList(screen);
	}

	public static void cacheItemList(HandledScreen<?> screen) {
		if (!NeuRepo.isDownloaded) return;
		List<ItemListIcon> icons = NeuRepo.itemListIcons;

		Rectangle rectangle = new Rectangle(screen.x - step, screen.y - step, screen.backgroundWidth + step, screen.backgroundWidth + step);
		int maxX = Config.getConfig().itemListWidth * step;
		int maxY = client.getWindow().getScaledHeight() - step;
		int x = 0;
		int y = yOffset;
		xOffset = Math.max(0, (screen.x - maxX) / 2);

		boolean freezePageSize = false;
		pageSize = 0;
		for (int i = 0; i < NeuRepo.itemListIcons.size();) {
			if (!rectangle.contains(x, y)) {
				if (!freezePageSize) pageSize++;
				icons.get(i).setLocation(x + xOffset, y);
				if (icons.get(i).children != null) Util.print(icons.get(i).stack.getName());
				i++;
			}
			x += step;
			if (x >= maxX) {
				x = 0;
				y += step;
				if (y >= maxY) {
					freezePageSize = true;
					y = yOffset;
				}
			}
		}

		startIndex = pageSize * pageNumber;
		endIndex = Math.min(pageSize * (pageNumber + 1), icons.size());
		pageNumberText = Text.of(pageNumber + "/" + (icons.size() / pageSize));
		textRenderX = Math.min(maxX / 2 + xOffset, screen.x);
	}
}
