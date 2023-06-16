package net.fabricmc.notnotmelonclient.itemlist;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.misc.ScrollableTooltips;
import net.fabricmc.notnotmelonclient.util.Rect;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.fabricmc.notnotmelonclient.Main.client;
import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;
import static net.fabricmc.notnotmelonclient.util.RenderUtil.itemRenderer;

public class ItemList extends ClickableWidget implements Drawable {
	private static final Identifier ARROWS = new Identifier(Main.NAMESPACE, "textures/gui/arrows.png");
	private static final Identifier ASTERISK = new Identifier(Main.NAMESPACE, "textures/gui/asterisk.png");
	public static int lastMouseX = -1;
	public static int lastMouseY = -1;
	public int pageNumber = CONFIG.pageNumber;
	public int maxPageNumber;
	public static final int STEP = 18;
	public int pageSize;
	public int startIndex;
	public int endIndex;
	public static final Text nothingToRender = Text.of("0/0");
	public Text pageNumberText;
	public int textRenderX;
	public int gridWidth;
	public int gridHeight;
	public Rect playground;
	public ItemListIcon parent;
	public final SearchBar searchBar;
	public final HandledScreen<?> screen;
	public final List<ItemListIcon> iconsToRender = new ArrayList<>();
	protected Thread t;

	public ItemList() {
		super(0, 18, 0, 0, Text.empty());
		screen = (HandledScreen<?>) client.currentScreen;
		searchBar = new SearchBar(client.advanceValidatingTextRenderer, 120, 16, this);
		screen.addDrawableChild(searchBar);
		calculatePageSize();
		buildIconPositions();
		searchBar.reposition(textRenderX);
	}

	@Override
	public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
		if (!NeuRepo.isDownloaded) return;
		renderArrows(context, mouseX, mouseY);
		if (pageNumberText != null) RenderUtil.drawCenteredText(context, pageNumberText, textRenderX, 7, -1);
		int offsetMouseX = mouseX - x;
		int targetMouseX = offsetMouseX - Math.abs(offsetMouseX) % STEP + x;
		int targetMouseY = mouseY - Math.abs(mouseY) % STEP;
		boolean renderedTooltip = false;

		if (playground != null) {
			if (playground.aabb(mouseX, mouseY))
				renderedTooltip = renderPlayground(context, screen, targetMouseX, targetMouseY, mouseX, mouseY, renderedTooltip);
			else {
				playground = null;
				parent = null;
			}
		}

		int maxIndex = Math.min(endIndex, iconsToRender.size());
		for (int i = startIndex; i < maxIndex; i++) {
			ItemListIcon icon;
			try { icon = iconsToRender.get(i); }
			catch (NullPointerException | IndexOutOfBoundsException e) { break; }
			int x = icon.x;
			int y = icon.y;
			boolean isVisible = playground == null || icon == parent || !playground.aabb(x, y);
			if (isVisible) context.drawItem(icon.stack, x, y);

			if (icon.children != null) {
				if (isVisible && icon != parent) renderAsterisk(context, x, y);
				if (playground == null && targetMouseX == x && targetMouseY == y) {
					parent = icon;
					parent.calculateChildrenPositions(gridWidth, gridHeight);
					playground = parent.playground;
					renderedTooltip = renderPlayground(context, screen, targetMouseX, targetMouseY, mouseX, mouseY, renderedTooltip);
				}
			}
			if (isVisible && !renderedTooltip && targetMouseX == x && targetMouseY == y) {
				context.drawItemTooltip(client.textRenderer, icon.stack, mouseX, mouseY);
				renderedTooltip = true;
			}
		}

		if (renderedTooltip && (targetMouseX != lastMouseX || targetMouseY != lastMouseY)) ScrollableTooltips.reset();
		lastMouseX = targetMouseX;
		lastMouseY = targetMouseY;
	}

	private void renderAsterisk(DrawContext context, int x, int y) {
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(0, 0, 200);
		context.drawTexture(ASTERISK, x + 13, y + 1, 0, 0, 4, 4, 4, 4);
		matrices.pop();
	}

	public boolean renderPlayground(DrawContext context, HandledScreen<?> screen, int targetMouseX, int targetMouseY, int mouseX, int mouseY, boolean renderedTooltip) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
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
			context.drawItem(child.stack, child.x, child.y);
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
	public void renderArrows(DrawContext context, int mouseX, int mouseY) {
		RenderSystem.disableDepthTest();
		int v = hoveredLeftArrow(mouseX, mouseY) ? arrowHeight : 0;
		int vv = hoveredRightArrow(mouseX, mouseY) ? arrowHeight : 0;
		context.drawTexture(ARROWS, leftX, arrowY, 0, v, arrowWidth, arrowHeight, 14, 22);
		context.drawTexture(ARROWS, rightX, arrowY, arrowWidth, vv, arrowWidth, arrowHeight, 14, 22);
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
				updatePagination();
			}
		} else if (hoveredRightArrow(mouseX, mouseY)) {
			if (pageNumber != maxPageNumber) {
				pageNumber++;
				updatePagination();
			}
		}
		return true;
	}

	public void buildIconPositions() {
		if (!NeuRepo.isDownloaded) return;
		if (t != null && t.isAlive()) t.interrupt();
		iconsToRender.clear();
		playground = null;
		parent = null;
		t = new Thread(this::buildIconPositionsThread);
		t.setDaemon(true);
		t.start();
	}

	protected void buildIconPositionsThread() {
		List<ItemListIcon> icons = NeuRepo.itemListIcons;
		int gridX = 0;
		int gridY = 0;
		int x = 0;
		int y = this.y;
		Rectangle screenRectangle = screenDimensions();
		for (int i = 0; i < icons.size();) {
			if (Thread.interrupted()) return;
			if (!screenRectangle.contains(x, y)) {
				ItemListIcon icon = icons.get(i++);
				if (!SearchBar.matches(icon)) continue;
				if (Thread.interrupted()) return;
				icon.setLocation(x + this.x, y);
				icon.setGridLocation(gridX, gridY);
				iconsToRender.add(icon);
			}
			x += STEP;
			gridX++;
			if (x >= width) {
				x = 0;
				gridX = 0;
				y += STEP;
				gridY++;
				if (y >= height) {
					y = this.y;
					gridY = 0;
				}
			}
		}
		if (Thread.interrupted()) return;
		updatePagination();
	}

	public void updatePagination() {
		int numIcons = iconsToRender.size();
		maxPageNumber = pageSize == 0 ? 0 : (numIcons / pageSize);
		pageNumber = Math.min(maxPageNumber, pageNumber);
		CONFIG.pageNumber = pageNumber;
		startIndex = pageSize * pageNumber;
		endIndex = Math.min(pageSize * (pageNumber + 1), numIcons);
		pageNumberText = numIcons == 0 ? nothingToRender : Text.of((pageNumber + 1) + "/" + (maxPageNumber + 1));
	}

	public void calculatePageSize() {
		gridWidth = CONFIG.itemListWidth;
		width = gridWidth * STEP;
		height = client.getWindow().getScaledHeight() - STEP - searchBar.distanceFromBottom;
		Rectangle screenRectangle = screenDimensions();
		pageSize = 0;
		int x = 0;
		int y = this.y;
		this.x = Math.max(0, (screen.x - width) / 2);
		while (true) {
			if (!screenRectangle.contains(x, y)) pageSize++;
			x += STEP;
			if (x >= width) {
				x = 0;
				y += STEP;
				if (y >= height) break;
			}
		}
		gridHeight = pageSize / gridWidth;
		textRenderX = Math.min(width / 2 + this.x, screen.x);
	}

	public Rectangle screenDimensions() {
		return new Rectangle(screen.x - STEP, screen.y - STEP, screen.backgroundWidth + STEP, screen.backgroundWidth + STEP);
	}

	public static void sort() {
		if (!NeuRepo.isDownloaded) return;
		Comparator<ItemListIcon> sortFunction = CONFIG.sortStrategy.sortFunction;
		if (CONFIG.reversed) sortFunction = sortFunction.reversed();
		NeuRepo.itemListIcons.sort(sortFunction);
		if (client.currentScreen != null)
			for (Element widget : client.currentScreen.children())
				if (widget instanceof ItemList)
					((ItemList) widget).buildIconPositions();
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
}
