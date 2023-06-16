package net.fabricmc.notnotmelonclient.mixin;

import net.fabricmc.notnotmelonclient.misc.FavoriteItem;
import net.fabricmc.notnotmelonclient.misc.ScrollableTooltips;
import net.fabricmc.notnotmelonclient.util.ItemUtil;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    @Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("HEAD"), cancellable = true)
    private void notnotmelonclient$renderGuiItemModelHead(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, int z, CallbackInfo ci) {
		if (!Util.isSkyblock) return;

        // don't render the skyblock menu if it's in the cursor stack
        if (ItemUtil.isSkyblockMenu(stack)) {
            ItemStack cursorStack = ItemUtil.getCursorStack();
            if (cursorStack != null && cursorStack.equals(stack)) ci.cancel();
        }
    }

	@Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("TAIL"))
    private void notnotmelonclient$renderGuiItemModelTail(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, int z, CallbackInfo ci) {
		if (!Util.isSkyblock) return;
             
        // draw star icon for favorited items
        if (!stack.isEmpty() && FavoriteItem.isKeyPressed() && FavoriteItem.isProtected(stack))
            FavoriteItem.renderStar((DrawContext) (Object) this, x, y);
    }

	@Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.AFTER))
	public void notnotmelonclient$drawTooltip(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, CallbackInfo ci) {
		if (CONFIG.scrollableTooltips)
			((DrawContext) (Object) this).getMatrices().translate(ScrollableTooltips.x, ScrollableTooltips.y, 0);
	}
}
