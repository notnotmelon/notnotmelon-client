package net.fabricmc.notnotmelonclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.notnotmelonclient.misc.FavoriteItem;
import net.fabricmc.notnotmelonclient.util.ItemUtil;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;


@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(method = "renderGuiItemModel", at = @At("HEAD"), cancellable = true)
    private void renderGuiItemModelHead(MatrixStack matrices, ItemStack stack, int x, int y, BakedModel model, CallbackInfo ci) {
		if (!Util.isSkyblock) return;

        // don't render the skyblock menu if its in the cursorstack
        if (ItemUtil.isSkyblockMenu(stack)) {
            ItemStack cursorStack = ItemUtil.getCursorStack();
            if (cursorStack != null && cursorStack.equals(stack)) ci.cancel();
        }
    }

	@Inject(method = "renderGuiItemModel", at = @At("TAIL"))
    private void renderGuiItemModelTail(MatrixStack matrices, ItemStack stack, int x, int y, BakedModel model, CallbackInfo ci) {   
		if (!Util.isSkyblock) return;
             
        // draw star icon for favorited items
        if (!stack.isEmpty() && FavoriteItem.isKeyPressed() && FavoriteItem.isProtected(stack))
            FavoriteItem.renderStar(matrices, x, y);
    }
}
