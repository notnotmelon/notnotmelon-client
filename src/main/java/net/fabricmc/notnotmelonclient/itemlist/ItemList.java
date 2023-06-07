package net.fabricmc.notnotmelonclient.itemlist;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.misc.ScrollableTooltips;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;

import static net.fabricmc.notnotmelonclient.Main.client;

public class ItemList {
	private static final Identifier ITEMLIST = new Identifier(Main.NAMESPACE, "textures/gui/itemlist.png");
	public static int xOffset = 0;
	public static int yOffset = 18;
	public static int lastMouseX = -1;
	public static int lastMouseY = -1;
	public static int pageNumber;
	public static int maxPageNumber;
	public static final int STEP = 18;
	public static int pageSize;
	public static int startIndex;
	public static int endIndex;
	public static Text pageNumberText;
	public static int textRenderX;

	public static void render(HandledScreen<?> screen, MatrixStack matrices, int mouseX, int mouseY) {
		if (!NeuRepo.isDownloaded) return;
		ItemRenderer itemRenderer = client.getItemRenderer();
		int offsetMouseX = mouseX - xOffset;
		int targetMouseX = offsetMouseX - Math.abs(offsetMouseX) % STEP + xOffset;
		int targetMouseY = mouseY - Math.abs(mouseY) % STEP;
		boolean renderedTooltip = false;

		for (int i = startIndex; i < endIndex; i++) {
			ItemListIcon icon = NeuRepo.itemListIcons.get(i);
			int x = icon.x;
			int y = icon.y;
			itemRenderer.renderInGui(matrices, icon.stack, x, y);
			if (icon.children != null) {
				matrices.push();
				matrices.translate(0, 0, 200);
				RenderSystem.setShaderTexture(0, ITEMLIST);
				DrawableHelper.drawTexture(matrices, x + 12, y + 4, 0, 0, 7, 11, 14, 22);
				matrices.pop();
			}
			if (!renderedTooltip && targetMouseX == x && targetMouseY == y) {
				screen.renderTooltip(matrices, screen.getTooltipFromItem(icon.stack), icon.stack.getTooltipData(), mouseX, mouseY);
				renderedTooltip = true;
			}
		}

		if (renderedTooltip && (targetMouseX != lastMouseX || targetMouseY != lastMouseY)) ScrollableTooltips.reset();
		lastMouseX = targetMouseX;
		lastMouseY = targetMouseY;

		RenderUtil.drawCenteredText(matrices, client, textRenderX, 7, pageNumberText, -1);
		RenderSystem.setShaderTexture(0, ITEMLIST);
		Arrow.draw(matrices, mouseX, mouseY);
	}

	public static class Arrow {
		public static final int WIDTH = 7;
		public static final int HEIGHT = 11;
		public static final int Y = 5;
		public static int leftX;
		public static int rightX;

		public static void draw(MatrixStack matrices, int mouseX, int mouseY) {
			int v = hoveredLeft(mouseX, mouseY) ? HEIGHT : 0;
			int vv = hoveredRight(mouseX, mouseY) ? HEIGHT : 0;
			DrawableHelper.drawTexture(matrices, leftX, Y, 0, v, WIDTH, HEIGHT, 14, 22);
			DrawableHelper.drawTexture(matrices, rightX, Y, WIDTH, vv, WIDTH, HEIGHT, 14, 22);
		}

		public static boolean hoveredLeft(int mouseX, int mouseY) {
			leftX = textRenderX - 22 - WIDTH;
			return leftX <= mouseX + 2 && mouseX - 2 < leftX + WIDTH && Y <= mouseY && mouseY <= Y + HEIGHT;
		}

		public static boolean hoveredRight(int mouseX, int mouseY) {
			rightX = textRenderX + 20;
			return rightX <= mouseX + 2 && mouseX - 2 < rightX + WIDTH && Y <= mouseY && mouseY <= Y + HEIGHT;
		}
	}

	public static void onClick(int button, double mouseXD, double mouseYD) {
		if (button != GLFW.GLFW_MOUSE_BUTTON_1) return;
		Screen screen = client.currentScreen;
		if (!(screen instanceof HandledScreen<?>)) return;
		int mouseY = (int) mouseYD;
		if (mouseY > Arrow.Y + Arrow.HEIGHT) return;
		int mouseX = (int) mouseXD;
		if (Arrow.hoveredLeft(mouseX, mouseY)) {
			if (pageNumber != 0) {
				pageNumber--;
				cacheItemList((HandledScreen<?>) screen);
			}
		} else if (Arrow.hoveredRight(mouseX, mouseY)) {
			if (pageNumber != maxPageNumber) {
				pageNumber++;
				cacheItemList((HandledScreen<?>) screen);
			}
		}
	}

	public static void onOpenScreen(HandledScreen<?> screen) {
		cacheItemList(screen);
	}

	public static void cacheItemList(HandledScreen<?> screen) {
		if (!NeuRepo.isDownloaded) return;
		List<ItemListIcon> icons = NeuRepo.itemListIcons;

		Rectangle rectangle = new Rectangle(screen.x - STEP, screen.y - STEP, screen.backgroundWidth + STEP, screen.backgroundWidth + STEP);
		int maxX = Config.getConfig().itemListWidth * STEP;
		int maxY = client.getWindow().getScaledHeight() - STEP;
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
			x += STEP;
			if (x >= maxX) {
				x = 0;
				y += STEP;
				if (y >= maxY) {
					freezePageSize = true;
					y = yOffset;
				}
			}
		}

		maxPageNumber = icons.size() / pageSize;
		pageNumber = Math.min(maxPageNumber, pageNumber);
		startIndex = pageSize * pageNumber;
		endIndex = Math.min(pageSize * (pageNumber + 1), icons.size());
		pageNumberText = Text.of((pageNumber + 1) + "/" + (maxPageNumber + 1));
		textRenderX = Math.min(maxX / 2 + xOffset, screen.x);
	}

	public static void sort() {
		if (!NeuRepo.isDownloaded) return;
		NeuRepo.itemListIcons.sort(Config.getConfig().sortStrategy.sortFunction);
		if (client.currentScreen instanceof HandledScreen<?>)
			cacheItemList((HandledScreen<?>) client.currentScreen);
	}
}
