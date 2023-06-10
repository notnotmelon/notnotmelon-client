package net.fabricmc.notnotmelonclient.itemlist;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.misc.ScrollableTooltips;
import net.fabricmc.notnotmelonclient.util.Rect;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.gui.widget.ClickableWidget;
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

public class ItemList extends ClickableWidget implements Drawable {
	private static final Identifier ARROWS = new Identifier(Main.NAMESPACE, "textures/gui/arrows.png");
	private static final Identifier ASTERISK = new Identifier(Main.NAMESPACE, "textures/gui/asterisk.png");
	public static int lastMouseX = -1;
	public static int lastMouseY = -1;
	public int pageNumber = Config.getConfig().pageNumber;
	public int maxPageNumber;
	public static final int STEP = 18;
	public int pageSize;
	public int startIndex;
	public int endIndex;
	public Text pageNumberText;
	public int textRenderX;
	public int gridWidth;
	public int gridHeight;
	public Rect playground;
	public ItemListIcon parent;
	public SearchBar searchBar;
	public HandledScreen<?> screen;

	public ItemList() {
		super(0, 18, 0, 0, Text.empty());
		screen = (HandledScreen<?>) client.currentScreen;
		searchBar = new SearchBar(client.advanceValidatingTextRenderer, 120, 16);
		screen.addDrawableChild(searchBar);
		cacheItemList();
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (!NeuRepo.isDownloaded) return;
		drawArrow(matrices, mouseX, mouseY);
		RenderUtil.drawCenteredText(matrices, client, textRenderX, 7, pageNumberText, -1);
		int offsetMouseX = mouseX - x;
		int targetMouseX = offsetMouseX - Math.abs(offsetMouseX) % STEP + x;
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
				if (isVisible && icon != parent) renderAsterisk(matrices, x, y);
				if (playground == null && targetMouseX == x && targetMouseY == y) {
					parent = icon;
					parent.calculateChildrenPositions(gridWidth, gridHeight);
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

	private void renderAsterisk(MatrixStack matrices, int x, int y) {
		matrices.push();
		matrices.translate(0, 0, 200);
		RenderSystem.setShaderTexture(0, ASTERISK);
		DrawableHelper.drawTexture(matrices, x + 13, y + 1, 0, 0, 4, 4, 4, 4);
		matrices.pop();
	}

	public boolean renderPlayground(MatrixStack matrices, HandledScreen<?> screen, int targetMouseX, int targetMouseY, int mouseX, int mouseY, boolean renderedTooltip) {
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

	public static final int arrowWidth = 7;
	public static final int arrowHeight = 11;
	public static final int arrowY = 5;
	public int leftX;
	public int rightX;
	public void drawArrow(MatrixStack matrices, int mouseX, int mouseY) {
		RenderSystem.disableDepthTest();
		RenderSystem.setShaderTexture(0, ARROWS);
		int v = hoveredLeftArrow(mouseX, mouseY) ? arrowHeight : 0;
		int vv = hoveredRightArrow(mouseX, mouseY) ? arrowHeight : 0;
		DrawableHelper.drawTexture(matrices, leftX, arrowY, 0, v, arrowWidth, arrowHeight, 14, 22);
		DrawableHelper.drawTexture(matrices, rightX, arrowY, arrowWidth, vv, arrowWidth, arrowHeight, 14, 22);
	}

	public boolean hoveredLeftArrow(int mouseX, int mouseY) {
		leftX = textRenderX - 21 - arrowWidth;
		return leftX <= mouseX + 2 && mouseX - 2 < leftX + arrowWidth && arrowY <= mouseY && mouseY <= arrowY + arrowHeight;
	}

	public boolean hoveredRightArrow(int mouseX, int mouseY) {
		rightX = textRenderX + 20;
		return rightX <= mouseX + 2 && mouseX - 2 < rightX + arrowWidth && arrowY <= mouseY && mouseY <= arrowY + arrowHeight;
	}

	@Override
	public boolean mouseClicked(double mouseXD, double mouseYD, int button) {
		if (button != GLFW.GLFW_MOUSE_BUTTON_1) return false;
		searchBar.onClick(mouseXD, mouseYD);
		int mouseY = (int) mouseYD;
		if (mouseY > arrowY + arrowHeight) return false;
		int mouseX = (int) mouseXD;
		if (hoveredLeftArrow(mouseX, mouseY)) {
			if (pageNumber != 0) {
				pageNumber--;
				cacheItemList();
			}
		} else if (hoveredRightArrow(mouseX, mouseY)) {
			if (pageNumber != maxPageNumber) {
				pageNumber++;
				cacheItemList();
			}
		}
		return true;
	}

	public void cacheItemList() {
		if (!NeuRepo.isDownloaded) return;
		List<ItemListIcon> icons = NeuRepo.itemListIcons;

		Rectangle rectangle = new Rectangle(screen.x - STEP, screen.y - STEP, screen.backgroundWidth + STEP, screen.backgroundWidth + STEP);
		gridWidth = Config.getConfig().itemListWidth;
		width = gridWidth * STEP;
		height = client.getWindow().getScaledHeight() - STEP - searchBar.distanceFromBottom;
		int gridX = 0;
		int gridY = 0;
		int x = 0;
		int y = this.y;
		this.x = Math.max(0, (screen.x - width) / 2);

		boolean freezePageSize = false;
		pageSize = 0;
		for (int i = 0; i < icons.size();) {
			if (!rectangle.contains(x, y)) {
				if (!freezePageSize) pageSize++;
				ItemListIcon icon = icons.get(i);
				icon.setLocation(x + this.x, y);
				icon.setGridLocation(gridX, gridY);
				i++;
			}
			x += STEP;
			gridX++;
			if (x >= width) {
				x = 0;
				gridX = 0;
				y += STEP;
				gridY++;
				if (y >= height) {
					freezePageSize = true;
					y = this.y;
					gridY = 0;
				}
			}
		}

		maxPageNumber = icons.size() / pageSize;
		pageNumber = Math.min(maxPageNumber, pageNumber);
		Config.getConfig().pageNumber = pageNumber;
		startIndex = pageSize * pageNumber;
		endIndex = Math.min(pageSize * (pageNumber + 1), icons.size());
		pageNumberText = Text.of((pageNumber + 1) + "/" + (maxPageNumber + 1));
		textRenderX = Math.min(width / 2 + this.x, screen.x);
		gridHeight = pageSize / gridWidth;
		searchBar.reposition(textRenderX);
	}

	public static void sort() {
		if (!NeuRepo.isDownloaded) return;
		NeuRepo.itemListIcons.sort(Config.getConfig().sortStrategy.sortFunction);
		if (client.currentScreen != null)
			for (Element widget : client.currentScreen.children())
				if (widget instanceof ItemList)
					((ItemList) widget).cacheItemList();
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
}
