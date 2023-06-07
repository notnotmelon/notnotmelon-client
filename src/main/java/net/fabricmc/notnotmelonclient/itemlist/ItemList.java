package net.fabricmc.notnotmelonclient.itemlist;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.misc.ScrollableTooltips;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.List;

import static net.fabricmc.notnotmelonclient.Main.client;

public class ItemList {
	private static final Identifier ITEMLIST = new Identifier(Main.NAMESPACE, "textures/gui/itemlist.png");
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
		pageNumber = 1;
		if (!NeuRepo.isDownloaded) return;
		ItemRenderer itemRenderer = client.getItemRenderer();
		mouseX -= xOffset;
		int targetMouseX = mouseX - Math.abs(mouseX) % step + xOffset;
		int targetMouseY = mouseY - Math.abs(mouseY) % step;
		boolean renderedTooltip = false;

		matrices.push();
		matrices.translate(0, 0, -0.01);
		for (int i = startIndex; i < endIndex; i++) {
			ItemListIcon icon = NeuRepo.itemListIcons.get(i);
			int x = icon.x;
			int y = icon.y;
			itemRenderer.renderInGui(matrices, icon.stack, x, y);
			if (icon.children != null) {
				RenderSystem.setShaderTexture(0, ITEMLIST);
				matrices.push();
				matrices.translate(0, 0, 0.01);
				DrawableHelper.drawTexture(matrices, x + 12, y + 4, 7, 0, 7, 11, 54, 40);
				matrices.pop();
			}
			if (!renderedTooltip && targetMouseX == x && targetMouseY == y) {
				screen.renderTooltip(matrices, screen.getTooltipFromItem(icon.stack), icon.stack.getTooltipData(), mouseX + xOffset, mouseY);
				renderedTooltip = true;
			}
		}
		matrices.pop();

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
