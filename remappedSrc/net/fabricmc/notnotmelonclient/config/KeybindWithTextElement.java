package net.fabricmc.notnotmelonclient.config;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.fabricmc.notnotmelonclient.config.categories.CommandKeybinds;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

// This is bloated. I don't like this lib

public class KeybindWithTextElement extends ControllerWidget<CommandKeybinds.KeybindController> {
	public KeybindWithTextElement(CommandKeybinds.KeybindController control, YACLScreen screen, Dimension<Integer> dim) {
		super(control, screen, dim);
		inputField = control.getCommand();
		control.option().addListener((opt, val) -> inputField = control.getCommand());
		setDimension(dim);
	}

	protected boolean instantApply;
	protected String inputField;
	protected int selectionLength = 0;
	protected int renderOffset;
	protected float ticks;
	private final Text emptyText = Text.literal("/command...").formatted(Formatting.GRAY);

	private final int buttonWidth = 80;
	protected Dimension<Integer> buttonBounds;
	private boolean buttonHovered;
	private boolean buttonFocused;

	protected int caretPos;
	private boolean textFieldHovered;
	protected boolean textFieldFocused = false;
	protected Dimension<Integer> textFieldBounds;

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		textFieldHovered = isMouseOver(mouseX, mouseY);
		buttonHovered = buttonBounds.isPointInside(mouseX, mouseY);

		Text name = control.option().changed() ? modifiedOptionName : control.option().name();
		Text shortenedName = Text.literal(GuiUtils.shortenString(name.getString(), textRenderer, getDimension().width() - getControlWidth() - getXPadding() - 7, "...")).setStyle(name.getStyle());

		drawButtonRect(context, getDimension().x(), getDimension().y(), getDimension().xLimit() - buttonWidth, getDimension().yLimit(), isTextFieldHovered(), isAvailable());
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getDimension().x() + getXPadding(), getTextY(), 0);
		context.drawText(textRenderer, shortenedName, 0, 0, getValueColor(), true);
		matrices.pop();

		drawValueText(context, mouseX, mouseY, delta);
		if (isTextFieldHovered()) {
			drawHoveredControl(context, mouseX, mouseY, delta);
		}

		drawButton(context);
	}

	private static final MutableText notBound = Text.literal("Not Bound").formatted(Formatting.GRAY);
	protected void drawButton(DrawContext context) {
		drawButtonRect(context, getDimension().xLimit() - buttonWidth, getDimension().y(), getDimension().xLimit(), getDimension().yLimit(), isButtonHovered(), isAvailable());
		Text text = buttonText();
		int width = textRenderer.getWidth(text);
		float x = buttonBounds.centerX() - width / 2f;

		MatrixStack matrices = context.getMatrices();
		if (buttonFocused) {
			matrices.push();
			matrices.translate(x, textFieldBounds.yLimit(), 0);
			context.fill(0, 0, width, 1, -1);
			context.fill(1, 1, width + 1, 2, 0xFF404040);
			matrices.pop();
		}

		matrices.push();
		matrices.translate(x, getTextY(), 0);
		context.drawText(textRenderer, text, 0, 0, 0xFFFFFFFF, true);
		matrices.pop();
	}

	protected Text buttonText() {
		int keyBind = control.getKeyBind();
		if (keyBind == GLFW.GLFW_KEY_UNKNOWN)
			return notBound;

		InputUtil.Type inputType = keyBind > 7 ? InputUtil.Type.KEYSYM : InputUtil.Type.MOUSE;
		return inputType.createFromCode(keyBind).getLocalizedText();
	}

	@Override
	protected void drawValueText(DrawContext context, int mouseX, int mouseY, float delta) {
		Text valueText = getValueText();
		if (!isTextFieldHovered()) valueText = Text.literal(GuiUtils.shortenString(valueText.getString(), textRenderer, getMaxUnwrapLength(), "...")).setStyle(valueText.getStyle());

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		int textX = getDimension().xLimit() - textRenderer.getWidth(valueText) + renderOffset - getXPadding() - buttonWidth;
		matrices.translate(textX, getTextY(), 0);
		context.enableScissor(textFieldBounds.x(), textFieldBounds.y() - 2, textFieldBounds.width() + 1, textFieldBounds.height() + 4);
		context.drawText(textRenderer, valueText, 0, 0, getValueColor(), true);
		matrices.pop();

		if (isTextFieldHovered()) {
			ticks += delta;

			String text = getValueText().getString();

			context.fill(textFieldBounds.x(), textFieldBounds.yLimit(), textFieldBounds.xLimit(), textFieldBounds.yLimit() + 1, -1);
			context.fill(textFieldBounds.x() + 1, textFieldBounds.yLimit() + 1, textFieldBounds.xLimit() + 1, textFieldBounds.yLimit() + 2, 0xFF404040);

			if (textFieldFocused || focused) {
				if (caretPos > text.length())
					caretPos = text.length();

				int caretX = textX + textRenderer.getWidth(text.substring(0, caretPos)) - 1;
				if (text.isEmpty())
					caretX = textFieldBounds.x() + textFieldBounds.width() / 2;

				if (ticks % 20 <= 10) {
					context.fill(caretX, textFieldBounds.y(), caretX + 1, textFieldBounds.yLimit(), -1);
				}

				if (selectionLength != 0) {
					int selectionX = textX + textRenderer.getWidth(text.substring(0, caretPos + selectionLength));
					context.fill(caretX, textFieldBounds.y() - 1, selectionX, textFieldBounds.yLimit(), 0x803030FF);
				}
			}
		}
		RenderSystem.disableScissor();
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		Dimension<Integer> dim = getDimension();
		if (dim == null) return false;
		return dim.withWidth(dim.width() - buttonWidth).isPointInside((int) mouseX, (int) mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!isAvailable()) return false;
		boolean wasFocused = buttonFocused;
		textFieldFocused = textFieldHovered;
		buttonFocused = buttonHovered;

		if (textFieldFocused) {
			clickTextField(mouseX, mouseY);
			return true;
		}

		if (buttonFocused && wasFocused) {
			keyPressedButtonInput(button);
			return true;
		}

		return buttonFocused;
	}

	public void clickTextField(double mouseX, double mouseY) {
		if (!textFieldBounds.isPointInside((int) mouseX, (int) mouseY)) {
			caretPos = getDefaultCaretPos();
		} else {
			// gets the appropriate caret position for where you click
			int textX = (int) mouseX - (textFieldBounds.xLimit() - textRenderer.getWidth(getValueText()));
			int pos = -1;
			int currentWidth = 0;
			for (char ch : inputField.toCharArray()) {
				pos++;
				int charLength = textRenderer.getWidth(String.valueOf(ch));
				if (currentWidth + charLength / 2 > textX) { // if more than halfway past the characters select in front of that char
					caretPos = pos;
					break;
				} else if (pos == inputField.length() - 1) {
					// if we have reached the end and no matches, it must be the second half of the char so the last position
					caretPos = pos + 1;
				}
				currentWidth += charLength;
			}

			selectionLength = 0;
		}
	}

	protected int getDefaultCaretPos() {
		return inputField.length();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (textFieldFocused)
			return keyPressedTextInput(keyCode);
		if (buttonFocused)
			return keyPressedButtonInput(keyCode);

		return false;
	}

	private boolean keyPressedButtonInput(int keyCode) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE)
			keyCode = GLFW.GLFW_KEY_UNKNOWN;
		control.setKeyBind(keyCode);
		unfocus();
		return true;
	}

	private boolean keyPressedTextInput(int keyCode) {
		switch (keyCode) {
			case InputUtil.GLFW_KEY_ESCAPE, InputUtil.GLFW_KEY_ENTER -> {
				unfocus();
				return true;
			}
			case InputUtil.GLFW_KEY_LEFT -> {
				if (Screen.hasShiftDown()) {
					if (Screen.hasControlDown()) {
						int spaceChar = findSpaceIndex(true);
						selectionLength += caretPos - spaceChar;
						caretPos = spaceChar;
					} else if (caretPos > 0) {
						caretPos--;
						selectionLength += 1;
					}
					checkRenderOffset();
				} else {
					if (caretPos > 0) {
						if (selectionLength != 0)
							caretPos += Math.min(selectionLength, 0);
						else
							caretPos--;
					}
					checkRenderOffset();
					selectionLength = 0;
				}

				return true;
			}
			case InputUtil.GLFW_KEY_RIGHT -> {
				if (Screen.hasShiftDown()) {
					if (Screen.hasControlDown()) {
						int spaceChar = findSpaceIndex(false);
						selectionLength -= spaceChar - caretPos;
						caretPos = spaceChar;
					} else if (caretPos < inputField.length()) {
						caretPos++;
						selectionLength -= 1;
					}
					checkRenderOffset();
				} else {
					if (caretPos < inputField.length()) {
						if (selectionLength != 0)
							caretPos += Math.max(selectionLength, 0);
						else
							caretPos++;
						checkRenderOffset();
					}
					selectionLength = 0;
				}

				return true;
			}
			case InputUtil.GLFW_KEY_BACKSPACE -> {
				doBackspace();
				return true;
			}
			case InputUtil.GLFW_KEY_DELETE -> {
				doDelete();
				return true;
			}
		}

		if (Screen.isPaste(keyCode)) {
			return doPaste();
		} else if (Screen.isCopy(keyCode)) {
			return doCopy();
		} else if (Screen.isCut(keyCode)) {
			return doCut();
		} else if (Screen.isSelectAll(keyCode)) {
			return doSelectAll();
		}

		return false;
	}

	protected boolean doPaste() {
		this.write(client.keyboard.getClipboard());
		return true;
	}

	protected boolean doCopy() {
		if (selectionLength != 0) {
			client.keyboard.setClipboard(getSelection());
			return true;
		}
		return false;
	}

	protected boolean doCut() {
		if (selectionLength != 0) {
			client.keyboard.setClipboard(getSelection());
			this.write("");
			return true;
		}
		return false;
	}

	protected boolean doSelectAll() {
		caretPos = inputField.length();
		checkRenderOffset();
		selectionLength = -caretPos;
		return true;
	}

	protected void checkRenderOffset() {
		if (textRenderer.getWidth(inputField) < getUnshiftedLength()) {
			renderOffset = 0;
			return;
		}

		int textX = getDimension().xLimit() - textRenderer.getWidth(inputField) - getXPadding();
		int caretX = textX + textRenderer.getWidth(inputField.substring(0, caretPos)) - 1;

		int minX = getDimension().xLimit() - getXPadding() - getUnshiftedLength();
		int maxX = minX + getUnshiftedLength();

		if (caretX + renderOffset < minX) {
			renderOffset = minX - caretX;
		} else if (caretX + renderOffset > maxX) {
			renderOffset = maxX - caretX;
		}
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (!textFieldFocused)
			return false;

		write(Character.toString(chr));

		return true;
	}

	protected void doBackspace() {
		if (selectionLength != 0) {
			write("");
		} else if (caretPos > 0) {
			modifyInput(builder -> builder.deleteCharAt(caretPos - 1));
			caretPos--;
			checkRenderOffset();
		}
	}

	protected void doDelete() {
		if (selectionLength != 0) {
			write("");
		} else if (caretPos < inputField.length()) {
			modifyInput(builder -> builder.deleteCharAt(caretPos));
		}
	}

	public void write(String string) {
		if (selectionLength == 0) {
			modifyInput(builder -> builder.insert(caretPos, string));
			caretPos += string.length();
			checkRenderOffset();
		} else {
			int start = getSelectionStart();
			int end = getSelectionEnd();

			modifyInput(builder -> builder.replace(start, end, string));
			caretPos = start + string.length();
			selectionLength = 0;
			checkRenderOffset();
		}
	}

	public void modifyInput(Consumer<StringBuilder> consumer) {
		StringBuilder temp = new StringBuilder(inputField);
		consumer.accept(temp);
		inputField = temp.toString();
		if (instantApply) updateControl();
	}

	public int getUnshiftedLength() {
		if (optionNameString.isEmpty())
			return getDimension().width() - getXPadding() * 2;
		return getDimension().width() / 8 * 5;
	}

	public int getMaxUnwrapLength() {
		if (optionNameString.isEmpty())
			return getDimension().width() - getXPadding() * 2;
		return getDimension().width() / 2;
	}

	public int getSelectionStart() {
		return Math.min(caretPos, caretPos + selectionLength);
	}

	public int getSelectionEnd() {
		return Math.max(caretPos, caretPos + selectionLength);
	}

	protected String getSelection() {
		return inputField.substring(getSelectionStart(), getSelectionEnd());
	}

	protected int findSpaceIndex(boolean reverse) {
		int i;
		int fromIndex = caretPos;
		if (reverse) {
			if (caretPos > 0)
				fromIndex -= 1;
			i = this.inputField.lastIndexOf(" ", fromIndex);

			if (i == -1) i = 0;
		} else {
			if (caretPos < inputField.length())
				fromIndex += 1;
			i = this.inputField.indexOf(" ", fromIndex);

			if (i == -1) i = inputField.length();
		}

		return i;
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
	}

	@Override
	public void unfocus() {
		super.unfocus();
		textFieldFocused = false;
		buttonFocused = false;
		renderOffset = 0;
		if (!instantApply) updateControl();
	}

	@Override
	public void setDimension(Dimension<Integer> dim) {
		super.setDimension(dim);

		int width = Math.max(6, Math.min(textRenderer.getWidth(getValueText()), getUnshiftedLength()));
		textFieldBounds = Dimension.ofInt(dim.xLimit() - getXPadding() - width - buttonWidth, dim.centerY() - textRenderer.fontHeight / 2, width, textRenderer.fontHeight);
		buttonBounds = Dimension.ofInt(dim.xLimit() - buttonWidth + 1, dim.y(), buttonWidth - 2, dim.height());
	}

	public boolean isTextFieldHovered() {
		return textFieldFocused || textFieldHovered;
	}

	public boolean isButtonHovered() {
		return buttonFocused || buttonHovered;
	}

	protected void updateControl() {
		control.setCommand(inputField);
	}

	@Override
	protected int getUnhoveredControlWidth() {
		return !isTextFieldHovered() ? Math.min(getHoveredControlWidth(), getMaxUnwrapLength()) : getHoveredControlWidth();
	}

	@Override
	protected int getHoveredControlWidth() {
		return Math.min(textRenderer.getWidth(getValueText()), getUnshiftedLength());
	}

	@Override
	protected Text getValueText() {
		if (!textFieldFocused && inputField.isEmpty())
			return emptyText;

		return instantApply || !textFieldFocused ? control.formatValue() : Text.literal(inputField);
	}
}