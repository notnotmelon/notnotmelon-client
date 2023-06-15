package net.fabricmc.notnotmelonclient.itemlist;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

import static net.fabricmc.notnotmelonclient.Main.client;
import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;

public class SearchBar extends TextFieldWidget {
	public int distanceFromBottom;
	public ItemList parent;
	public static ItemSearchPattern searchPattern;

	public SearchBar(TextRenderer textRenderer, int width, int height, ItemList parent) {
		super(textRenderer, 0, 0, width, height, Text.empty());
		distanceFromBottom = height + 3;
		setText(CONFIG.searchQuery);
		searchPattern = calculateSearchPattern();
		this.parent = parent;
	}

	public void reposition(int centerX) {
		setPosition(centerX - width / 2, client.getWindow().getScaledHeight() - distanceFromBottom);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		String old = getText();
		if (!super.charTyped(chr, modifiers)) return false;
		if (!old.equals(getText())) doSearch();
		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		String old = getText();
		if (!super.keyPressed(keyCode, scanCode, modifiers)) return false;
		if (!old.equals(getText())) doSearch();
		return true;
	}

	public void doSearch() {
		searchPattern = calculateSearchPattern();
		parent.buildIconPositions();
		CONFIG.searchQuery = getText();
	}

	public static boolean matches(ItemListIcon icon) {
		return searchPattern == null || searchPattern.matches(icon);
	}

	public static boolean matches(ItemStack stack) {
		return searchPattern == null || searchPattern.matches(stack);
	}

	public ItemSearchPattern calculateSearchPattern() {
		if (getText().isBlank()) return ItemSearchPattern.EMPTY;
		ArrayList<ArrayList<String>> result = new ArrayList<>();
		for (String or : getText().toLowerCase().split("\\|")) {
			ArrayList<String> and = new ArrayList<>();
			for (String query : or.split("&")) {
				query = query.trim();
				if (!query.isBlank()) and.add(query);
			}
			if (!and.isEmpty()) result.add(and);
		}
		return new ItemSearchPattern(result);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!isVisible()) return false;
		boolean bl = mouseX >= this.getX() && mouseX < (this.getX() + this.width) && mouseY >= this.getY() && mouseY < (this.getY() + this.height);
		if (focusUnlocked) setFocused(bl);
		if (this.isFocused() && bl) {
			if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
				int i = MathHelper.floor(mouseX) - this.getX();
				if (drawsBackground) i -= 4;
				String string = textRenderer.trimToWidth(getText().substring(firstCharacterIndex), this.getInnerWidth());
				setCursor(textRenderer.trimToWidth(string, i).length() + firstCharacterIndex);
				doubleClick();
				return true;
			} else if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
				setText("");
				doSearch();
				return true;
			}
		}
		return false;
	}

	protected long lastClicked = -1;
	public static boolean yellowMode = false;
	protected void doubleClick() {
		long time = System.currentTimeMillis();
		if (lastClicked != -1 && lastClicked + 750 >= time) {
			yellowMode = !yellowMode;
			lastClicked = -1;
		} else {
			lastClicked = time;
		}
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.renderButton(matrices, mouseX, mouseY, delta);
		if (yellowMode && isVisible() && drawsBackground) {
			DrawableHelper.drawBorder(matrices, x - 1, y - 1, width + 2, height + 2, 0xFFFFC105);
		}
	}
}
