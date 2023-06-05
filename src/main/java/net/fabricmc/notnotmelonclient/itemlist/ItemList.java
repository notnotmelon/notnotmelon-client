package net.fabricmc.notnotmelonclient.itemlist;

import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.misc.ScrollableTooltips;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.fabricmc.notnotmelonclient.Main.client;

public class ItemList {
	public static final int xOffset = 5;
	public static int lastMouseX = -1;
	public static int lastMouseY = -1;
	public static int pageNumber = 2;
	public static final int step = 18;
	public static int pageSize;
	public static List<ItemListIcon> cachedIcons;
	public static int startIndex;
	public static int endIndex;
	public static Text pageNumberText;

	public static void render(HandledScreen<?> screen, MatrixStack matrices, int mouseX, int mouseY) {
		if (cachedIcons == null) return;
		ItemRenderer itemRenderer = client.getItemRenderer();
		mouseX -= xOffset;
		int targetMouseX = mouseX - mouseX % step + xOffset;
		int targetMouseY = mouseY - mouseY % step;
		boolean renderedTooltip = false;

		for (int i = startIndex; i < endIndex; i++) {
			ItemListIcon icon = cachedIcons.get(i);
			itemRenderer.renderInGui(matrices, icon.stack, icon.x, icon.y);
			if (!renderedTooltip && targetMouseX == icon.x && targetMouseY == icon.y) {
				screen.renderTooltip(matrices, screen.getTooltipFromItem(icon.stack), icon.stack.getTooltipData(), mouseX, mouseY);
				renderedTooltip = true;
			}
		}

		if (renderedTooltip && (targetMouseX != lastMouseX || targetMouseY != lastMouseY)) ScrollableTooltips.reset();
		lastMouseX = targetMouseX;
		lastMouseY = targetMouseY;

		RenderUtil.drawCenteredText(matrices, client, client.getWindow().getScaledWidth() / 6f, client.getWindow().getScaledHeight() - 10, pageNumberText, -1);
	}

	public static void onOpenScreen(HandledScreen<?> screen) {
		cacheItemList(screen);
	}

	public static void cacheItemList(HandledScreen<?> screen) {
		List<ItemStack> items = RepoParser.items;
		if (items == null) return;
		cachedIcons = new ArrayList<>();

		Rectangle rectangle = new Rectangle(screen.x - step, screen.y - step, screen.backgroundWidth + step, screen.backgroundWidth + step);
		int maxX = Config.getConfig().itemListWidth * step;
		int maxY = client.getWindow().getScaledHeight() - step;
		int x = 0;
		int y = 0;

		boolean freezePageSize = false;
		pageSize = 0;
		for (int i = 0; i < items.size();) {
			if (!rectangle.contains(x, y)) {
				if (!freezePageSize) pageSize++;
				cachedIcons.add(new ItemListIcon(items.get(i), x + xOffset, y));
				i++;
			}
			x += step;
			if (x >= maxX) {
				x = 0;
				y += step;
				if (y >= maxY) {
					freezePageSize = true;
					y = 0;
				}
			}
		}

		startIndex = pageSize * pageNumber;
		endIndex = Math.min(pageSize * (pageNumber + 1), cachedIcons.size());
		pageNumberText = Text.of(pageNumber + "/" + (cachedIcons.size() / pageSize));
	}
}
