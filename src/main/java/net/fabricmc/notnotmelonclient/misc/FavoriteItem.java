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
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class FavoriteItem {
    public static GsonConfigInstance<FavoriteItem> jsonInterface;
    public static Path configDir;
    public static void loadConfig() {
        configDir = FabricLoader.getInstance().getConfigDir().resolve("notnotmelonclient/favoriteditems.properties");
        jsonInterface = new GsonConfigInstance<>(FavoriteItem.class, configDir);
        jsonInterface.load();
    }

    @ConfigEntry public final Set<String> itemsToProtect = new HashSet<>();
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
        return itemId != null && itemsToProtect().contains(itemId);
    }

    public static void printProtectMessage(ItemStack stack, Text action) {
        Util.print(Text.literal("§cPrevented you from " + action.getString() + " §r").append(stack.getName()));
    }

    public static void toggleFavorited(ItemStack stack) {
        if (ItemUtil.isSkyblockMenu(stack)) return;
        NbtCompound extraAttributes = ItemUtil.getExtraAttributes(stack);
        if (extraAttributes == null) return;

        String protectionString;
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

    public static void onSlotClick(Slot slot, int invSlot, HandledScreen<?> screen, SlotActionType actionType, CallbackInfo ci) {
        ScreenHandler handler = screen.getScreenHandler();

        if (FavoriteItem.isKeyPressed()) {
            ItemStack stack = slot.getStack();
            FavoriteItem.toggleFavorited(stack);
            ci.cancel();
        } else if (invSlot == -999 && actionType == SlotActionType.PICKUP) { // -999 is the slot id for clicking outside your inv
            ItemStack stack = handler.getCursorStack();
            protect(stack, Text.literal("dropping"), ci);
        } else if (slot != null && slot.hasStack() && actionType == SlotActionType.THROW) { // This handles pressing Q while hovering over an item
            protect(slot.getStack(), Text.literal("dropping"), ci);
        } else if (slot != null && slot.hasStack() && handler instanceof GenericContainerScreenHandler) {
            if (screen.getTitle().getString().equals("Salvage Item")) {
                protect(slot.getStack(), Text.literal("salvaging"), ci);
            } else if (isSellMenu((GenericContainerScreenHandler) handler)) {
                protect(slot.getStack(), Text.literal("selling"), ci);
            }
        }
    }

    private static void protect(ItemStack stack, Text action, CallbackInfo ci) {
        if (stack != null && isProtected(stack)) {
            printProtectMessage(stack, action);
            ci.cancel();
        }
    }

    public static boolean isSellMenu(GenericContainerScreenHandler chest) {
        ItemStack hopper = chest.getInventory().getStack(49);
        if (hopper.getName().getString().equals("Sell Item")) return true;
        for (Text lore : hopper.getTooltip(null, TooltipContext.BASIC)) {
            if (lore.getString().equals("Click to buyback!")) return true;
        }
        return false;
    }
}
