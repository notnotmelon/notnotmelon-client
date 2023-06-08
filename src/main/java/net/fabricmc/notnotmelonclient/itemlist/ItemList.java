package net.fabricmc.notnotmelonclient.itemlist;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.misc.ScrollableTooltips;
import net.fabricmc.notnotmelonclient.util.Rect;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;

import static net.fabricmc.notnotmelonclient.Main.client;
import static net.fabricmc.notnotmelonclient.util.RenderUtil.itemRenderer;

public class ItemList {
	private static final Identifier ARROWS = new Identifier(Main.NAMESPACE, "textures/gui/arrows.png");
	private static final Identifier ASTERISK = new Identifier(Main.NAMESPACE, "textures/gui/asterisk.png");
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
	public static int gridWidth;
	public static int gridHeight;
	public static Rect playground;
	public static ItemListIcon parent;

	public static void render(HandledScreen<?> screen, MatrixStack matrices, int mouseX, int mouseY) {
		if (!NeuRepo.isDownloaded) return;
		Arrow.draw(matrices, mouseX, mouseY);
		RenderUtil.drawCenteredText(matrices, client, textRenderX, 7, pageNumberText, -1);
		int offsetMouseX = mouseX - xOffset;
		int targetMouseX = offsetMouseX - Math.abs(offsetMouseX) % STEP + xOffset;
		int targetMouseY = mouseY - Math.abs(mouseY) % STEP;
		boolean renderedTooltip = false;

		if (playground != null)
			if (playground.aabb(mouseX, mouseY))
				renderedTooltip = renderPlayground(matrices, screen, targetMouseX, targetMouseY, mouseX, mouseY, renderedTooltip);
			else {
				playground = null;
				parent = null;
			}

		for (int i = startIndex; i < endIndex; i++) {
			ItemListIcon icon = NeuRepo.itemListIcons.get(i);
			int x = icon.x;
			int y = icon.y;
			boolean isVisible = playground == null || icon == parent || !playground.aabb(x, y);
			if (isVisible) itemRenderer.renderInGui(matrices, icon.stack, x, y);

			if (icon.children != null) {
				if (isVisible && icon != parent) drawAsterisk(matrices, x, y);
				if (playground == null && targetMouseX == x && targetMouseY == y) {
					parent = icon;
					parent.calculateChildrenPositions();
					playground = parent.playground;
					renderedTooltip = renderPlayground(matrices, screen, targetMouseX, targetMouseY, mouseX, mouseY, renderedTooltip);
				}
			}
			if (isVisible && !renderedTooltip && targetMouseX == x && targetMouseY == y) {
				screen.renderTooltip(matrices, screen.getTooltipFromItem(icon.stack), icon.stack.getTooltipData(), mouseX, mouseY);
				renderedTooltip = true;
			}
		}

		if (renderedTooltip && (targetMouseX != lastMouseX || targetMouseY != lastMouseY)) ScrollableTooltips.reset();
		lastMouseX = targetMouseX;
		lastMouseY = targetMouseY;
	}

	private static void drawAsterisk(MatrixStack matrices, int x, int y) {
		matrices.push();
		matrices.translate(0, 0, 200);
		RenderSystem.setShaderTexture(0, ASTERISK);
		DrawableHelper.drawTexture(matrices, x + 13, y + 1, 0, 0, 4, 4, 4, 4);
		matrices.pop();
	}

	public static boolean renderPlayground(MatrixStack matrices, HandledScreen<?> screen, int targetMouseX, int targetMouseY, int mouseX, int mouseY, boolean renderedTooltip) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		TooltipBackgroundRenderer.render(
			DrawableHelper::fillGradient,
			matrix4f,
			bufferBuilder,
			playground.x + 2,
			playground.y + 2,
			playground.width - 6,
			playground.height - 6,
			0
		);
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		for (ItemListIcon child : parent.children) {
			itemRenderer.renderInGui(matrices, child.stack, child.x, child.y);
			if (!renderedTooltip && targetMouseX == child.x && targetMouseY == child.y) {
				screen.renderTooltip(matrices, screen.getTooltipFromItem(child.stack), child.stack.getTooltipData(), mouseX, mouseY);
				renderedTooltip = true;
			}
		}
		return renderedTooltip;
	}

	public static class Arrow {
		public static final int WIDTH = 7;
		public static final int HEIGHT = 11;
		public static final int Y = 5;
		public static int leftX;
		public static int rightX;

		public static void draw(MatrixStack matrices, int mouseX, int mouseY) {
			RenderSystem.disableDepthTest();
			RenderSystem.setShaderTexture(0, ARROWS);
			int v = hoveredLeft(mouseX, mouseY) ? HEIGHT : 0;
			int vv = hoveredRight(mouseX, mouseY) ? HEIGHT : 0;
			DrawableHelper.drawTexture(matrices, leftX, Y, 0, v, WIDTH, HEIGHT, 14, 22);
			DrawableHelper.drawTexture(matrices, rightX, Y, WIDTH, vv, WIDTH, HEIGHT, 14, 22);
		}

		public static boolean hoveredLeft(int mouseX, int mouseY) {
			leftX = textRenderX - 21 - WIDTH;
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
		gridWidth = Config.getConfig().itemListWidth;
		int maxX = gridWidth * STEP;
		int maxY = client.getWindow().getScaledHeight() - STEP;
		int gridX = 0;
		int gridY = 0;
		int x = 0;
		int y = yOffset;
		xOffset = Math.max(0, (screen.x - maxX) / 2);

		boolean freezePageSize = false;
		pageSize = 0;
		for (int i = 0; i < NeuRepo.itemListIcons.size();) {
			if (!rectangle.contains(x, y)) {
				if (!freezePageSize) pageSize++;
				ItemListIcon icon = icons.get(i);
				icon.setLocation(x + xOffset, y);
				icon.setGridLocation(gridX, gridY);
				i++;
			}
			x += STEP;
			gridX++;
			if (x >= maxX) {
				x = 0;
				gridX = 0;
				y += STEP;
				gridY++;
				if (y >= maxY) {
					freezePageSize = true;
					y = yOffset;
					gridY = 0;
				}
			}
		}

		maxPageNumber = icons.size() / pageSize;
		pageNumber = Math.min(maxPageNumber, pageNumber);
		startIndex = pageSize * pageNumber;
		endIndex = Math.min(pageSize * (pageNumber + 1), icons.size());
		pageNumberText = Text.of((pageNumber + 1) + "/" + (maxPageNumber + 1));
		textRenderX = Math.min(maxX / 2 + xOffset, screen.x);
		gridHeight = pageSize / gridWidth;
	}

	public static void sort() {
		if (!NeuRepo.isDownloaded) return;
		NeuRepo.itemListIcons.sort(Config.getConfig().sortStrategy.sortFunction);
		if (client.currentScreen instanceof HandledScreen<?>)
			cacheItemList((HandledScreen<?>) client.currentScreen);
	}
}
