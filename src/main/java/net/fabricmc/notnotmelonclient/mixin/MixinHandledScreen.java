package net.fabricmc.notnotmelonclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.fabricmc.notnotmelonclient.commands.ProtectItem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;


@Mixin(HandledScreen.class)
public class MixinHandledScreen<T extends ScreenHandler> extends Screen {
	@Inject(method = "drawItem", at = @At("TAIL"))
    private void renderSlotBookmark(MatrixStack matrices, ItemStack stack, int x, int y, String amountText, CallbackInfo ci) {
        ProtectItem.renderStar(matrices, x, y, 1f);
    }

    protected MixinHandledScreen(Text title) { super(title); }
}
