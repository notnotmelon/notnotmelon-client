package net.fabricmc.notnotmelonclient.itemlist;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.Main.client;

public class SearchBar extends TextFieldWidget {
	public int distanceFromBottom;

	public SearchBar(TextRenderer textRenderer, int width, int height) {
		super(textRenderer, 0, 0, width, height, Text.empty());
		distanceFromBottom = height + 3;
	}

	public void reposition(int centerX) {
		setPosition(centerX - width / 2, client.getWindow().getScaledHeight() - distanceFromBottom);
	}
}
