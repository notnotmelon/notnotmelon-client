package net.fabricmc.notnotmelonclient.misc;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.notnotmelonclient.util.ItemUtil;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Unique;

public class FavoriteItem {
    @Unique private static final Identifier STAR_TEXTURE = new Identifier("notnotmelonclient", "textures/gui/star.png");
    public static KeyBinding keyBinding;

    public static boolean isKeyPressed() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), keyBinding.boundKey.getCode());
    }

    public static void addHotkey() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.favoriteItem",
            GLFW.GLFW_KEY_L,
            "key.categories.notnotmelonclient"
        ));
    }

    public static void renderStar(MatrixStack matrices, int x, int y) {
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, STAR_TEXTURE);

        // Draw shadow
        RenderSystem.setShaderColor(0f, 0f, 0f, 0.25f);
        matrices.push();
        matrices.translate(0.5f, 0.5f, 0f);
        DrawableHelper.drawTexture(matrices, x, y, 0, 0, 8, 8, 8, 8);
        matrices.pop();

        // Draw star
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        DrawableHelper.drawTexture(matrices, x, y, 0, 0, 8, 8, 8, 8);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static boolean isProtected(ItemStack stack) {
        return stack.getName().getString().equals("Jungle Log") || ItemUtil.getExtraAttributes(stack) != null;
    }

    public static void printProtectMessage(ItemStack stack, String action) {
        Util.print(Text.literal("§cPrevented you from " + action + " §r").append(stack.getName()));
    }

    private static 
    public static void toggleFavorited(ItemStack stack) {
        NbtCompound extraAttributes = ItemUtil.getExtraAttributes(stack);
        if (extraAttributes == null) return;
        if (extraAttributes.contains("uuid")) {
            String uuid = extraAttributes.getString("uuid");
        } else if (extraAttributes.contains())
    }
}
