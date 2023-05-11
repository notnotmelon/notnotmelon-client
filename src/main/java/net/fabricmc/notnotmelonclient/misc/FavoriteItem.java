package net.fabricmc.notnotmelonclient.misc;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.isxander.yacl.config.ConfigEntry;
import dev.isxander.yacl.config.GsonConfigInstance;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.util.ItemUtil;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Unique;

public class FavoriteItem {
    public static GsonConfigInstance<FavoriteItem> jsonInterface;
    public static Path configDir;
    public static void loadConfig() {
        configDir = FabricLoader.getInstance().getConfigDir().resolve("notnotmelonclient/favoriteditems.properties");
        jsonInterface = new GsonConfigInstance<FavoriteItem>(FavoriteItem.class, configDir);
        jsonInterface.load();
    }

    @ConfigEntry public Set<String> itemsToProtect = new HashSet<String>();
    private static Set<String> itemsToProtect() {
        return jsonInterface.getConfig().itemsToProtect;
    }

    @Unique private static final Identifier STAR_TEXTURE = new Identifier(Main.NAMESPACE, "textures/gui/star.png");
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
        NbtCompound extraAttributes = ItemUtil.getExtraAttributes(stack);
        if (extraAttributes == null) return false;

        if (extraAttributes.contains("uuid"))
            if (itemsToProtect().contains(extraAttributes.getString("uuid")))
                return true;

        String itemId = ItemUtil.getSkyBlockItemID(stack);
        if (itemId != null && itemsToProtect().contains(itemId))
            return true;
            
        return false;
    }

    public static void printProtectMessage(ItemStack stack, String action) {
        Util.print(Text.literal("§cPrevented you from " + action + " §r").append(stack.getName()));
    }

    public static void toggleFavorited(ItemStack stack) {
        if (ItemUtil.isSkyblockMenu(stack)) return;
        NbtCompound extraAttributes = ItemUtil.getExtraAttributes(stack);
        if (extraAttributes == null) return;

        String protectionString = null;
        if (extraAttributes.contains("uuid")) {
            protectionString = extraAttributes.getString("uuid");
        } else {
            protectionString = ItemUtil.getSkyBlockItemID(stack);
        }

        if (protectionString == null) return;

        if (itemsToProtect().contains(protectionString)) {
            itemsToProtect().remove(protectionString);
        } else {
            itemsToProtect().add(protectionString);
        }

        jsonInterface.save();
    }
}
