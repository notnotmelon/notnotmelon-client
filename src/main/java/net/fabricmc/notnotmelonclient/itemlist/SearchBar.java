package net.fabricmc.notnotmelonclient.itemlist;

import net.fabricmc.notnotmelonclient.config.Config;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

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
		if (!super.charTyped(chr, modifiers)) return false;
		parent.buildIconPositions();
		Config.getConfig().searchQuery = getText();
		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!super.keyPressed(keyCode, scanCode, modifiers)) return false;
		parent.buildIconPositions();
		Config.getConfig().searchQuery = getText();
		return true;
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
}
