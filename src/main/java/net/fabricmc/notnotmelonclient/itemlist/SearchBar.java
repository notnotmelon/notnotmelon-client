package net.fabricmc.notnotmelonclient.itemlist;

import net.fabricmc.notnotmelonclient.config.Config;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

import static net.fabricmc.notnotmelonclient.Main.client;

public class SearchBar extends TextFieldWidget {
	public int distanceFromBottom;
	public ItemList parent;

	public SearchBar(TextRenderer textRenderer, int width, int height, ItemList parent) {
		super(textRenderer, 0, 0, width, height, Text.empty());
		distanceFromBottom = height + 3;
		setText(Config.getConfig().searchQuery);
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
		parent.buildIconPositions();
		Config.getConfig().searchQuery = getText();
	}

	public ItemSearchPattern searchPattern() {
		if (getText().isBlank()) return ItemSearchPattern.EMPTY;
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
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
				return true;
			} else if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
				setText("");
				doSearch();
				return true;
			}
		}
		return false;
	}
}
